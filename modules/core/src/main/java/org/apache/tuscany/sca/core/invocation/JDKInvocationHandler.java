/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.core.invocation;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.context.ConversationImpl;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -3366410500152201371L;

    protected boolean conversational;
    protected ConversationImpl conversation;
    protected boolean conversationStarted;
    protected MessageFactory messageFactory;
    protected EndpointReference endpoint;
    protected Object callbackID;
    protected Object callbackObject;
    protected RuntimeWire wire;
    protected CallableReference<?> callableReference;

    public JDKInvocationHandler(MessageFactory messageFactory, RuntimeWire wire) {
        this.messageFactory = messageFactory;
        this.wire = wire;
        setConversational(wire);
    }
    
    public JDKInvocationHandler(MessageFactory messageFactory, CallableReference<?> callableReference) {
        this.messageFactory = messageFactory;
        this.callableReference = callableReference;
        if (callableReference != null) {
            this.callbackID = callableReference.getCallbackID();
            this.conversation = (ConversationImpl)callableReference.getConversation();
            this.wire = ((CallableReferenceImpl<?>)callableReference).getRuntimeWire();
            if (callableReference instanceof ServiceReference) {
                this.callbackObject = ((ServiceReference)callableReference).getCallback();
            }
            if (wire != null) {
                setConversational(wire);
            }
        }
    }

    protected void setConversational(RuntimeWire wire) {
        InterfaceContract contract = wire.getSource().getInterfaceContract();
        this.conversational = contract.getInterface().isConversational();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class) && "equals".equals(method.getName())) {
            Object obj = args[0];
            if (obj == null) {
                return false;
            }
            if (!Proxy.isProxyClass(obj.getClass())) {
                return false;
            }
            return equals(Proxy.getInvocationHandler(obj));
        } else if (Object.class.equals(method.getDeclaringClass()) && "hashCode".equals(method.getName())) {
            return hashCode();
        }
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("Destination for call is not known");
        }
        InvocationChain chain = getInvocationChain(method, wire);
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }

        // send the invocation down the wire
        Object result = invoke(chain, args, wire);

        return result;
    }

    /**
     * Determines if the given operation matches the given method
     * 
     * @return true if the operation matches, false if does not
     */
    @SuppressWarnings("unchecked")
    private static boolean match(Operation operation, Method method) {
        Class<?>[] params = method.getParameterTypes();
        DataType<List<DataType>> inputType = operation.getInputType();
        List<DataType> types = inputType.getLogical();
        boolean matched = true;
        if (types.size() == params.length && method.getName().equals(operation.getName())) {
            for (int i = 0; i < params.length; i++) {
                Class<?> clazz = params[i];
                if (!operation.getInputType().getLogical().get(i).getPhysical().isAssignableFrom(clazz)) {
                    matched = false;
                }
            }
        } else {
            matched = false;
        }
        return matched;

    }

    protected InvocationChain getInvocationChain(Method method, RuntimeWire wire) {
        for (InvocationChain chain : wire.getInvocationChains()) {
            Operation operation = chain.getSourceOperation();
            if (operation.isDynamic()) {
                operation.setName(method.getName());
                return chain;
            } else if (match(operation, method)) {
                return chain;
            }
        }
        return null;
    }

    public void setEndpoint(EndpointReference endpoint) {
        this.endpoint = endpoint;
    }

    public void setCallbackID(Object callbackID) {
        this.callbackID = callbackID;
    }

    protected Object invoke(InvocationChain chain, Object[] args, RuntimeWire wire) throws Throwable {

        Message msgContext = ThreadMessageContext.getMessageContext();
        Object msgContextConversationId = msgContext.getConversationID();

        Message msg = messageFactory.createMessage();

        // make sure that the conversation id is set so it can be put in the 
        // outgoing messages. The id can come from one of three places
        // 1 - Generated here (if the source is stateless)
        // 2 - Specified by the application (through a service reference)
        // 3 - from the message context (if the source is stateful)
        //
        // TODO - number 3 seems a little shaky as we end up propagating
        //        a conversationId through the source component. If we don't
        //        do this though we can't correlate the callback call with the
        //        current target instance. Currently specifying an application
        //        conversationId in this case also means that the callback
        //        can't be correlated with the source component instance 
        if (conversational) {
            if (conversation == null) {
                // this is a callback so create a conversation to 
                // hold onto the conversation state for the lifetime of the
                // stateful callback
                conversation = new ConversationImpl();
            }
            Object conversationId = conversation.getConversationID();

            // create a conversation id if one doesn't exist 
            // already, i.e. the conversation is just starting
            if ((conversationStarted == false) && (conversationId == null)) {

                // It the current component is already in a conversation
                // the use this just in case this message has a stateful 
                // callback. In which case the callback will come back
                // to the correct instance. 
                // TODO - we should always create a unique id here or
                //        take the application defined conversation id. 
                //        This implies we have to re-register the component 
                //        instance against this 
                if (msgContextConversationId == null) {
                    conversationId = createConversationID();
                } else {
                    conversationId = msgContextConversationId;
                }

                conversation.setConversationID(conversationId);
            }
            //TODO - assuming that the conversation ID is a string here when
            //       it can be any object that is serializable to XML
            msg.setConversationID((String)conversationId);
        }

        Invoker headInvoker = chain.getHeadInvoker();
        msg.setCorrelationID(callbackID);
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);
        Interface contract = operation.getInterface();
        if (contract != null && contract.isConversational()) {
            ConversationSequence sequence = operation.getConversationSequence();
            if (sequence == ConversationSequence.CONVERSATION_END) {
                msg.setConversationSequence(ConversationSequence.CONVERSATION_END);
                conversationStarted = false;
                if (conversation != null) {
                    conversation.setConversationID(null);
                }
            } else if (sequence == ConversationSequence.CONVERSATION_CONTINUE) {
                if (conversationStarted) {
                    msg.setConversationSequence(ConversationSequence.CONVERSATION_CONTINUE);
                } else {
                    conversationStarted = true;
                    msg.setConversationSequence(ConversationSequence.CONVERSATION_START);
                }
            }
        }
        msg.setBody(args);
        if (wire.getSource() != null && wire.getSource().getCallbackEndpoint() != null) {
            if (callbackObject != null) {
                if (callbackObject instanceof ServiceReference) {
                    msg.setFrom(((CallableReferenceImpl)callbackObject).getRuntimeWire().getTarget());
                } else {
                    if (contract != null) {
                        if (!contract.isConversational()) {
                            throw new NoRegisteredCallbackException(
                                                                    "Callback object for stateless callback is not a ServiceReference");
                        } else {
                            //FIXME: add callback object to scope container
                            msg.setFrom(wire.getSource().getCallbackEndpoint());
                        }
                    }
                }
            } else {
                //FIXME: check that the source component implements the callback interface
                msg.setFrom(wire.getSource().getCallbackEndpoint());
            }
        }
        if (endpoint != null) {
            msg.setTo(endpoint);
        } else {
            msg.setTo(wire.getTarget());
        }

        ThreadMessageContext.setMessageContext(msg);
        try {
            // dispatch the wire down the chain and get the response
            Message resp = headInvoker.invoke(msg);
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw (Throwable)body;
            }
            return body;
        } finally {
            ThreadMessageContext.setMessageContext(msgContext);
        }
    }

    /**
     * Creates a new conversational id
     * 
     * @return the conversational id
     */
    private String createConversationID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return the callableReference
     */
    public CallableReference<?> getCallableReference() {
        return callableReference;
    }

    /**
     * @param callableReference the callableReference to set
     */
    public void setCallableReference(CallableReference<?> callableReference) {
        this.callableReference = callableReference;
    }

}
