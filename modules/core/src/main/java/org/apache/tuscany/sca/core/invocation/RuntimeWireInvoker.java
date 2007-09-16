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

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

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
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireInvoker {
    protected ConversationManager conversationManager;
    protected boolean conversational;
    protected ExtendedConversation conversation;
    protected MessageFactory messageFactory;
    protected EndpointReference endpoint;
    protected Object conversationID;
    protected Object callbackID;
    protected Object callbackObject;
    protected RuntimeWire wire;

    public RuntimeWireInvoker(MessageFactory messageFactory, ConversationManager conversationManager, RuntimeWire wire) {
        this.messageFactory = messageFactory;
        this.wire = wire;
        this.conversationManager = conversationManager;
        init(wire);
    }

    protected void init(RuntimeWire wire) {
        if (wire != null) {
            ReferenceParameters parameters = wire.getSource().getReferenceParameters();
            this.callbackID = parameters.getCallbackID();
            this.callbackObject = parameters.getCallbackReference();
            this.conversationID = parameters.getConversationID();
            InterfaceContract contract = wire.getSource().getInterfaceContract();
            this.conversational = contract.getInterface().isConversational();
        }
    }

    public Object invoke(Operation operation, Message msg) throws InvocationTargetException {
        return invoke(wire, operation, msg);
    }

    public Object invoke(RuntimeWire wire, Operation operation, Message msg) throws InvocationTargetException {
        RuntimeWire runtimeWire = wire == null ? this.wire : wire;
        InvocationChain chain = runtimeWire.getInvocationChain(operation);
        return invoke(chain, msg, runtimeWire);
    }

    protected Object invoke(InvocationChain chain, Message msg, RuntimeWire wire) throws InvocationTargetException {

        msg.setFrom(wire.getSource());
        EndpointReference epTo = null;
        if (endpoint != null) {
            epTo = endpoint;
        } else {
            epTo = wire.getTarget();
        }
        if (msg.getTo() != null) {
            msg.getTo().mergeEndpoint(epTo);
        } else {
            msg.setTo(epTo);
        }
        Invoker headInvoker = chain.getHeadInvoker();
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);

        Message msgContext = ThreadMessageContext.getMessageContext();
        Object currentConversationID = msgContext.getTo().getReferenceParameters().getConversationID();

        ThreadMessageContext.setMessageContext(msg);
        try {
            conversationPreinvoke(msg);
            handleCallback(msg, currentConversationID);
            // dispatch the wire down the chain and get the response
            Message resp = headInvoker.invoke(msg);
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw new InvocationTargetException((Throwable)body);
            }
            return body;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        } finally {
            try {
                conversationPostInvoke(msg);
            } catch (TargetDestructionException e) {
                throw new ServiceRuntimeException(e);
            } finally {
                ThreadMessageContext.setMessageContext(msgContext);
            }
        }
    }

    /**
     * @param msgContext
     */
    protected EndpointReference getCallbackEndpoint(Message msgContext) {
        EndpointReference to = msgContext.getTo();
        return to == null ? null : to.getReferenceParameters().getCallbackReference();
    }

    /**
     * @param msg
     * @param wire
     * @param interfaze
     * @throws TargetResolutionException
     */
    @SuppressWarnings("unchecked")
    private void handleCallback(Message msg, Object currentConversationID) throws TargetResolutionException {
        EndpointReference from = msg.getFrom();
        EndpointReference to = msg.getTo();
        msg.getTo().getReferenceParameters().setCallbackID(callbackID);
        if (from == null || from.getCallbackEndpoint() == null) {
            return;
        }
        // If we are passing out a callback target
        // register the calling component instance against this 
        // new conversation id so that stateful callbacks will be
        // able to find it
        if (conversational && callbackObject == null) {
            // the component instance is already registered
            // so add another registration
            ScopeContainer scopeContainer = getConversationalScopeContainer(msg);

            if (scopeContainer != null) {
                scopeContainer.addWrapperReference(currentConversationID, to.getReferenceParameters()
                    .getConversationID());
            }
        }

        ReferenceParameters parameters = msg.getTo().getReferenceParameters();
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
                        ScopeContainer scopeContainer = getConversationalScopeContainer(msg);
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
    private void conversationPreinvoke(Message msg) {
        if (!conversational) {
            // Not conversational or the conversation has been started
            return;
        }
        if (conversation == null || conversation.getState() == ConversationState.ENDED) {
            conversation = conversationManager.startConversation(conversationID);
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
    private void conversationPostInvoke(Message msg) throws TargetDestructionException {
        Operation operation = msg.getOperation();
        ConversationSequence sequence = operation.getConversationSequence();
        if (sequence == ConversationSequence.CONVERSATION_END) {
            conversation.end();

            // remove conversation id from scope container
            ScopeContainer scopeContainer = getConversationalScopeContainer(msg);

            if (scopeContainer != null) {
                scopeContainer.remove(conversation.getConversationID());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ScopeContainer getConversationalScopeContainer(Message msg) {
        ScopeContainer scopeContainer = null;

        RuntimeComponent component = msg.getFrom().getComponent();

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent scopedRuntimeComponent = (ScopedRuntimeComponent)component;
            ScopeContainer container = scopedRuntimeComponent.getScopeContainer();

            if ((container != null) && (container.getScope() == Scope.CONVERSATION)) {
                scopeContainer = container;
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
