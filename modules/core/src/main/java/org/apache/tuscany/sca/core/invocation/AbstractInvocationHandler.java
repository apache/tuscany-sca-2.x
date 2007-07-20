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

import java.util.UUID;

import org.apache.tuscany.sca.core.component.ConversationImpl;
import org.apache.tuscany.sca.core.runtime.RuntimeWireImpl;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.Conversation;

/**
 * Base class for performing invocations on a wire. Subclasses are responsible for retrieving and supplying the
 * appropriate chain, target invoker, and invocation arguments.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractInvocationHandler {
    protected boolean conversational;
    protected ConversationImpl conversation;
    private boolean conversationStarted;
    private MessageFactory messageFactory;

    protected AbstractInvocationHandler(MessageFactory messageFactory, boolean conversational) {
        this.conversational = conversational;
        this.messageFactory = messageFactory;
    }

    public void setConversation(Conversation conversation){
        this.conversation = (ConversationImpl)conversation;
    }
    
    protected Object invoke(InvocationChain chain, Object[] args, RuntimeWire wire) throws Throwable {

        Message msgContext = ThreadMessageContext.getMessageContext();
        Message msg = messageFactory.createMessage();
               
        if (conversational) {
            if (conversation == null){
                // the conversation info is not being shared 
                // with a service reference object so create a
                // new one here
                conversation = new ConversationImpl();
            }
            Object conversationId = conversation.getConversationID();
            
            // create automatic conversation id if one doesn't exist 
            // already. This could be because we are in the middle of a
            // conversation or the conversation hasn't started but the
            if ((conversationStarted == false) && (conversationId == null)) {
                conversationId = createConversationID();
                conversation.setConversationID(conversationId);
            }
            //TODO - assuming that the conversation ID is a strin here when
            //       it can be any object that is serializable to XML
            msg.setConversationID((String)conversationId);
        }

        Invoker headInvoker = chain.getHeadInvoker();
        msg.setCorrelationID(msgContext.getCorrelationID());
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);
        Interface contract = operation.getInterface();
        if (contract != null && contract.isConversational()) {
            ConversationSequence sequence = operation.getConversationSequence();
            if (sequence == ConversationSequence.CONVERSATION_END) {
                msg.setConversationSequence(ConversationSequence.CONVERSATION_END);
                conversationStarted = false;
                if (conversation != null){
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
        msg.setFrom(wire.getSource());
        msg.setTo(wire.getTarget());
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

}
