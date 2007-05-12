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

import java.net.URI;
import java.util.LinkedList;

import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.ConversationSequence;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.spi.component.WorkContext;

/**
 * Base class for performing invocations on a wire. Subclasses are responsible for retrieving and supplying the
 * appropriate chain, target invoker, and invocation arguments.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractInvocationHandler {
    protected boolean conversational;
    private boolean conversationStarted;
    private MessageFactory messageFactory;

    protected AbstractInvocationHandler(MessageFactory messageFactory, boolean conversational) {
        this.conversational = conversational;
        this.messageFactory = messageFactory;
    }

    protected Object invoke(InvocationChain chain,
                            Object[] args,
                            Object correlationId,
                            LinkedList<URI> callbackUris, WorkContext workContext)
        throws Throwable {
        Invoker headInvoker = chain.getHeadInvoker();
        Message msg = messageFactory.createMessage();
        msg.setWorkContext(workContext);
        msg.setCorrelationID(workContext.getCorrelationId());
        Operation operation = chain.getTargetOperation();
        Interface contract = operation.getInterface();
        if (contract != null && contract.isConversational()) {
            Operation.ConversationSequence sequence = chain.getTargetOperation().getConversationSequence();
            if (sequence == Operation.ConversationSequence.CONVERSATION_END) {
                msg.setConversationSequence(ConversationSequence.CONVERSATION_END);
                conversationStarted = false;
            } else if (sequence == Operation.ConversationSequence.CONVERSATION_CONTINUE) {
                if (conversationStarted) {
                    msg.setConversationSequence(ConversationSequence.CONVERSATION_CONTINUE);
                } else {
                    conversationStarted = true;
                    msg.setConversationSequence(ConversationSequence.CONVERSATION_START);
                }
            }
        }
        msg.setBody(args);
        // dispatch the wire down the chain and get the response
        Message resp = headInvoker.invoke(msg);
        Object body = resp.getBody();
        if (resp.isFault()) {
            throw (Throwable) body;
        }
        return body;
    }

}
