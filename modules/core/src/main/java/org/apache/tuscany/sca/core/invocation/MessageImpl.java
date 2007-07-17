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

import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;

/**
 * The default implementation of a message flowed through a wire during an invocation
 *
 * @version $Rev $Date
 */
public class MessageImpl implements Message {
    private Object body;
    private Object messageID;
    private Object correlationID;
    private boolean isFault;
    private ConversationSequence conversationSequence;
    private String conversationId;
    private Operation op;
    
    private EndpointReference from;
    private EndpointReference to;

    public MessageImpl(String conversationId, ConversationSequence conversationSequence, Object body) {
        this.conversationId = conversationId;
        this.conversationSequence = conversationSequence;
        this.body = body;
    }

    public MessageImpl() {
    }

    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        return (T) body;
    }

    public <T> void setBody(T body) {
        this.isFault = false;
        this.body = body;
    }

    public String getConversationID() {
        return conversationId;
    }

    public void setConversationID(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public ConversationSequence getConversationSequence() {
        return conversationSequence;
    }

    public void setConversationSequence(ConversationSequence conversationSequence) {
        this.conversationSequence = conversationSequence;
    }

    public Object getMessageID() {
        return messageID;
    }

    public void setMessageID(Object messageId) {
        this.messageID = messageId;
    }

    public Object getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(Object correlationId) {
        this.correlationID = correlationId;
    }

    public boolean isFault() {
        return isFault;
    }

    public void setFaultBody(Object fault) {
        this.isFault = true;
        this.body = fault;
    }

    public EndpointReference getFrom() {
        return from;
    }

    public void setFrom(EndpointReference from) {
        this.from = from;
    }

    public EndpointReference getTo() {
        return to;
    }

    public void setTo(EndpointReference to) {
        this.to = to;
    }

    public Operation getOperation() {
        return op;
    }

    public void setOperation(Operation op) {
        this.op = op;
    }
}
