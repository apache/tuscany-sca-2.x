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

import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.core.conversation.ConversationExt;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.conversation.ConversationState;
import org.apache.tuscany.sca.core.factory.InstanceWrapper;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.core.scope.TargetDestructionException;
import org.apache.tuscany.sca.core.scope.TargetResolutionException;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ConversationEndedException;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireInvoker implements Invoker{
    protected ConversationManager conversationManager;
    protected boolean conversational;
    protected ConversationExt conversation;
    protected MessageFactory messageFactory;
    protected Object conversationID;
    protected Object callbackID;
    protected Object callbackObject;
    protected RuntimeWire wire;

    public RuntimeWireInvoker(MessageFactory messageFactory, ConversationManager conversationManager, RuntimeWire wire) {
        this.messageFactory = messageFactory;
        this.wire = wire;
        this.conversationManager = conversationManager;
        //init(wire);
    }

    /* TODO - EPR - not required for OASIS
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
    */
    
    /*
     * TODO - Introduced to allow the RuntimeWireInvoker to sit on the end of the 
     *        service binding chain. Runtime wire invoke needs splitting up into 
     *        separate conversation, callback interceptors etc.
     */
    public Message invoke(Message msg) {
                
        try {
            Object response = invoke(msg.getOperation(),msg);
            // Hack to put the response back in a message. 
            // shouldn't take it out of the response message in the first place
            msg.setBody(response);
        } catch (InvocationTargetException e) {
            throw new ServiceRuntimeException(e);
        }
        
        return msg;
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
        EndpointReference2 from = msg.getFrom();
        EndpointReference2 epFrom = wire.getEndpointReference();
        if (from != null) {
            from.setComponent(epFrom.getComponent());
            from.setReference(epFrom.getReference());
            from.setBinding(epFrom.getBinding());
            from.setInterfaceContract(epFrom.getInterfaceContract());
            from.setTargetEndpoint(epFrom.getTargetEndpoint());
            from.setCallbackEndpoint(epFrom.getCallbackEndpoint());

            // TODO - EPR - what's going on here?
            //from.mergeEndpoint(epFrom);
        } else {
            msg.setFrom(epFrom);
        }
        msg.setTo(wire.getEndpoint());

        Invoker headInvoker = chain.getHeadInvoker();
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);

        Message msgContext = ThreadMessageContext.getMessageContext();
        // TODO - EPR - no required for OASIS
        //Object currentConversationID = msgContext.getFrom().getReferenceParameters().getConversationID();

        ThreadMessageContext.setMessageContext(msg);
        try {
            // TODO - EPR - no required for OASIS
            //conversationPreinvoke(msg);
            // handleCallback(msg, currentConversationID);
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
                // TODO - EPR - no required for OASIS
                //conversationPostInvoke(msg);
            //} catch (TargetDestructionException e) {
            //    throw new ServiceRuntimeException(e);
            } finally {
                ThreadMessageContext.setMessageContext(msgContext);
            }
        }
    }

    /**
     * @param msgContext
     */
   /* TODO - EPR - no required for OASIS
    protected EndpointReference getCallbackEndpoint(Message msgContext) {
        EndpointReference from = msgContext.getFrom();
        return from == null ? null : from.getReferenceParameters().getCallbackReference();
    }
    */

    /**
     * Pre-invoke for the conversation handling
     * @param msg
     * @throws TargetResolutionException
     */
    /* TODO - EPR - no required for OASIS    
    private void conversationPreinvoke(Message msg) {
        if (conversational) {
            ReferenceParameters parameters = msg.getFrom().getReferenceParameters();
            // in some cases the ConversationID that should be used comes in with the 
            // message, e.g. when ws binding is in use.
            Object convID = parameters.getConversationID();
            if (convID != null) {
                conversationID = convID;
            }
            conversation = conversationManager.getConversation(conversationID);
            
            if (conversation == null || conversation.getState() == ConversationState.ENDED) {
                conversation = conversationManager.startConversation(conversationID);
                conversation.initializeConversationAttributes(wire.getTarget().getComponent());
            } else if (conversation.conversationalAttributesInitialized() == false) {
                conversation.initializeConversationAttributes(wire.getTarget().getComponent());
            } else if (conversation.isExpired()){
            	throw new ConversationEndedException("Conversation has expired.");
            }
            
            conversation.updateLastReferencedTime();
    
            parameters.setConversationID(conversation.getConversationID());
        }
    }
    */

    /**
     * Post-invoke for the conversation handling
     * @param wire
     * @param operation
     * @throws TargetDestructionException
     */
    /* TODO - EPR - no required for OASIS
    @SuppressWarnings("unchecked")
    private void conversationPostInvoke(Message msg) throws TargetDestructionException {
        if (conversational) {       
            Operation operation = msg.getOperation();
            ConversationSequence sequence = operation.getConversationSequence();
            if (sequence == ConversationSequence.CONVERSATION_END) {
                // in some cases the ConversationID that should be used comes in with the 
                // message, e.g. when ws binding is in use. 
                Object convID = msg.getFrom().getReferenceParameters().getConversationID();
                if (convID != null) {
                    conversationID = convID;
                }
                conversation = conversationManager.getConversation(conversationID);            
    
                // remove conversation id from scope container
                ScopeContainer scopeContainer = getConversationalScopeContainer(msg);
    
                if (scopeContainer != null) {
                    scopeContainer.remove(conversation.getConversationID());
                }
                
                conversation.end();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ScopeContainer getConversationalScopeContainer(Message msg) {
        ScopeContainer scopeContainer = null;

        RuntimeComponent component = (RuntimeComponent) msg.getTo().getComponent();

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent scopedRuntimeComponent = (ScopedRuntimeComponent)component;
            ScopeContainer container = scopedRuntimeComponent.getScopeContainer();

            if ((container != null) && (container.getScope() == Scope.CONVERSATION)) {
                scopeContainer = container;
            }
        }

        return scopeContainer;
    }
    */


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
