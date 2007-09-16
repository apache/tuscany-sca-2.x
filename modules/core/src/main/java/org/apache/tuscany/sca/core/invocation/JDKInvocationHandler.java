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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.core.assembly.RuntimeWireImpl;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.conversation.ConversationState;
import org.apache.tuscany.sca.core.conversation.ExtendedConversation;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.core.scope.TargetDestructionException;
import org.apache.tuscany.sca.core.scope.TargetResolutionException;
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
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -3366410500152201371L;

    protected boolean conversational;
    protected ExtendedConversation conversation;
    protected MessageFactory messageFactory;
    protected EndpointReference target;
    protected Object conversationID;
    protected Object callbackID;
    protected Object callbackObject;
    protected RuntimeWire wire;
    protected CallableReference<?> callableReference;
    protected Class<?> businessInterface;

    protected boolean fixedWire = true;
    protected transient Map<Method, InvocationChain> chains = new HashMap<Method, InvocationChain>();

    public JDKInvocationHandler(MessageFactory messageFactory, Class<?> businessInterface, RuntimeWire wire) {
        this.messageFactory = messageFactory;
        this.wire = wire;
        this.businessInterface = businessInterface;
        init(this.wire);
    }

    public JDKInvocationHandler(MessageFactory messageFactory, CallableReference<?> callableReference) {
        this.messageFactory = messageFactory;
        this.callableReference = callableReference;
        if (callableReference != null) {
            this.businessInterface = callableReference.getBusinessInterface();
            this.callbackID = callableReference.getCallbackID();
            this.conversation = (ExtendedConversation)callableReference.getConversation();
            this.wire = ((CallableReferenceImpl<?>)callableReference).getRuntimeWire();
            if (callableReference instanceof ServiceReference) {
                this.conversationID = ((ServiceReference)callableReference).getConversationID();
                this.callbackObject = ((ServiceReference)callableReference).getCallback();
            }
            if (wire != null) {
                init(wire);
            }
        }
    }

    protected void init(RuntimeWire wire) {
        if (wire != null) {
            try {
                // Clone the wire so that reference parameters can be changed
                this.wire = (RuntimeWire)wire.clone();
            } catch (CloneNotSupportedException e) {
                throw new ServiceRuntimeException(e);
            }
        }
        InterfaceContract contract = wire.getSource().getInterfaceContract();
        this.conversational = contract.getInterface().isConversational();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            return invokeObjectMethod(method, args);
        }
        if (wire == null) {
            throw new ServiceRuntimeException("No runtime wire is available");
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
     * Handle the methods on the Object.class
     * @param method
     * @param args
     */
    protected Object invokeObjectMethod(Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("toString".equals(name)) {
            return "[Proxy - " + toString() + "]";
        } else if ("equals".equals(name)) {
            Object obj = args[0];
            if (obj == null) {
                return false;
            }
            if (!Proxy.isProxyClass(obj.getClass())) {
                return false;
            }
            return equals(Proxy.getInvocationHandler(obj));
        } else if ("hashCode".equals(name)) {
            return hashCode();
        } else {
            return method.invoke(this);
        }
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

    protected synchronized InvocationChain getInvocationChain(Method method, RuntimeWire wire) {
        if (fixedWire && chains.containsKey(method)) {
            return chains.get(method);
        }
        InvocationChain found = null;
        for (InvocationChain chain : wire.getInvocationChains()) {
            Operation operation = chain.getSourceOperation();
            if (operation.isDynamic()) {
                operation.setName(method.getName());
                found = chain;
                break;
            } else if (match(operation, method)) {
                found = chain;
                break;
            }
        }
        if (fixedWire) {
            chains.put(method, found);
        }
        return found;
    }

    protected void setEndpoint(EndpointReference endpoint) {
        this.target = endpoint;
    }

    protected Object invoke(InvocationChain chain, Object[] args, RuntimeWire wire) throws Throwable {

        Message msg = messageFactory.createMessage();
        msg.setFrom(wire.getSource());
        if (target != null) {
            msg.setTo(target);
        } else {
            msg.setTo(wire.getTarget());
        }
        Invoker headInvoker = chain.getHeadInvoker();
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);
        msg.setBody(args);

        Message msgContext = ThreadMessageContext.getMessageContext();
        Object currentConversationID = msgContext.getTo().getReferenceParameters().getConversationID();

        conversationPreinvoke(msg, wire);
        handleCallback(msg, wire, currentConversationID);
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
            conversationPostInvoke(msg, wire);
            ThreadMessageContext.setMessageContext(msgContext);
        }
    }

    /**
     * @param msg
     * @param wire
     * @param interfaze
     * @throws TargetResolutionException
     */
    private void handleCallback(Message msg, RuntimeWire wire, Object currentConversationID)
        throws TargetResolutionException {
        ReferenceParameters parameters = msg.getTo().getReferenceParameters();
        parameters.setCallbackID(callbackID);
        if (wire.getSource() == null || wire.getSource().getCallbackEndpoint() == null) {
            return;
        }

        parameters.setCallbackReference(wire.getSource().getCallbackEndpoint());

        // If we are passing out a callback target
        // register the calling component instance against this 
        // new conversation id so that stateful callbacks will be
        // able to find it
        if (conversational && callbackObject == null) {
            // the component instance is already registered
            // so add another registration
            ScopeContainer<Object> scopeContainer = getConversationalScopeContainer(wire);

            if (scopeContainer != null) {
                scopeContainer.addWrapperReference(currentConversationID, conversation.getConversationID());
            }
        }

        Interface interfaze = msg.getOperation().getInterface();
        if (callbackObject != null) {
            if (callbackObject instanceof ServiceReference) {
                EndpointReference callbackRef = ((CallableReferenceImpl)callbackObject).getEndpointReference();
                parameters.setCallbackReference(callbackRef);
            } else {
                if (interfaze != null) {
                    if (!interfaze.isConversational()) {
                        throw new IllegalArgumentException(
                                                           "Callback object for stateless callback is not a ServiceReference");
                    } else {
                        ScopeContainer scopeContainer = getConversationalScopeContainer(wire);
                        if (scopeContainer != null) {
                            InstanceWrapper wrapper = new CallbackObjectWrapper(callbackObject);
                            scopeContainer.registerWrapper(wrapper, conversation.getConversationID());
                        }
                        parameters.setCallbackObjectID("java:" + System.identityHashCode(callbackObject));
                    }
                }
            }
        }
    }

    /**
     * Pre-invoke for the conversation handling
     * @param msg
     * @throws TargetResolutionException
     */
    private void conversationPreinvoke(Message msg, RuntimeWire wire) {
        if (!conversational) {
            // Not conversational or the conversation has been started
            return;
        }
        ConversationManager conversationManager = ((RuntimeWireImpl)wire).getConversationManager();
        if (conversation == null || conversation.getState() == ConversationState.ENDED) {
            conversation = conversationManager.startConversation(conversationID);
            if (callableReference != null) {
                ((CallableReferenceImpl)callableReference).attachConversation(conversation);
            }
        }
        // TODO - assuming that the conversation ID is a string here when
        //       it can be any object that is serializable to XML
        msg.getTo().getReferenceParameters().setConversationID(conversation.getConversationID());

    }

    /**
     * Post-invoke for the conversation handling
     * @param wire
     * @param operation
     * @throws TargetDestructionException
     */
    @SuppressWarnings("unchecked")
    private void conversationPostInvoke(Message msg, RuntimeWire wire) throws TargetDestructionException {
        Operation operation = msg.getOperation();
        ConversationSequence sequence = operation.getConversationSequence();
        if (sequence == ConversationSequence.CONVERSATION_END) {
            conversation.end();

            // remove conversation id from scope container
            ScopeContainer scopeContainer = getConversationalScopeContainer(wire);

            if (scopeContainer != null) {
                scopeContainer.remove(conversation.getConversationID());
            }
        }
    }

    private ScopeContainer<Object> getConversationalScopeContainer(RuntimeWire wire) {
        ScopeContainer<Object> scopeContainer = null;

        RuntimeComponent runtimeComponent = wire.getSource().getComponent();

        if (runtimeComponent instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent scopedRuntimeComponent = (ScopedRuntimeComponent)runtimeComponent;
            ScopeContainer<Object> tmpScopeContainer = scopedRuntimeComponent.getScopeContainer();

            if ((tmpScopeContainer != null) && (tmpScopeContainer.getScope() == Scope.CONVERSATION)) {
                scopeContainer = tmpScopeContainer;
            }
        }

        return scopeContainer;
    }

    /**
     * Creates a new conversational id
     * 
     * @return the conversational id
     */
    private Object createConversationID() {
        if (conversationID == null) {
            return UUID.randomUUID().toString();
        }
        return conversationID;
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

    /**
     * Minimal wrapper for a callback object contained in a ServiceReference
     */
    private static class CallbackObjectWrapper<T> implements InstanceWrapper<T> {

        private T instance;

        private CallbackObjectWrapper(T instance) {
            this.instance = instance;
        }

        public T getInstance() {
            return instance;
        }

        public void start() {
            // do nothing
        }

        public void stop() {
            // do nothing
        }

    }

}
