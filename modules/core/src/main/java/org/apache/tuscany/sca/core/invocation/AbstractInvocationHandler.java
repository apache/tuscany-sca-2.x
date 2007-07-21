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
        Object  msgContextConversationId = msgContext.getConversationID();
        
        Message msg = messageFactory.createMessage();
               
        // make sure that the conversation id is set so it can be put in the 
        // outgoing messages. The id can come from one of three places
        // 1 - Generated here (if the source is stateless)
        // 2 - Specified by the application (through a service reference)
        // 3 - from the message context (if the source is stateful)
        //
        // TODO - number 3 seems a little shaky as we end up propogating
        //        a conversationId through the source component. If we don't
        //        do this though we can't correlate the callback call with the
        //        current target instance. Currently specifying an application
        //        conversationId in this case also means that the callback
        //        can't be correlated with the source component instance 
        if (conversational) {
            if (conversation == null){
                // this is a callback so create a conversation to 
                // hold onto the conversation state for the lifetime of the
                // stateful callback
                conversation = new ConversationImpl();
            }
            Object conversationId = conversation.getConversationID();
            
            // create a conversation id if one doesn't exist 
            // already. This could be because we are in the middle of a
            // conversation or the conversation hasn't started but the
            if ((conversationStarted == false) && (conversationId == null)) {
                
                // It the current component is already in a conversation
                // the use this just in case this message has a stateful 
                // callback. In which case the callback will come back
                // to the correct instance. 
                // TODO - need a better mechanism for identifyng the 
                //        stateful callback case
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
