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

import java.util.LinkedList;

import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.invocation.ConversationSequence;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.spi.component.WorkContext;

/**
 * The default implementation of a message flowed through a wire during an invocation
 *
 * @version $Rev $Date
 */
public class MessageImpl implements Message {
    private Object body;
    private LinkedList<RuntimeWire> callbackWires;
    private Object messageID;
    private Object correlationID;
    private boolean isFault;
    private ConversationSequence conversationSequence;
    private WorkContext workContext;

    protected MessageImpl() {
    }

    @SuppressWarnings("unchecked")
    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.isFault = false;
        this.body = body;
    }

    public WorkContext getWorkContext() {
        return workContext;
    }

    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }
    
    public ConversationSequence getConversationSequence() {
        return conversationSequence;
    }

    public void setConversationSequence(ConversationSequence conversationSequence) {
        this.conversationSequence = conversationSequence;
    }

    public void pushCallbackWire(RuntimeWire wire) {
        if (callbackWires == null) {
            callbackWires = new LinkedList<RuntimeWire>();
        }
        callbackWires.add(wire);
    }

    public LinkedList<RuntimeWire> getCallbackWires() {
        return callbackWires;
    }

    public void setCallbackWires(LinkedList<RuntimeWire> wires) {
        this.callbackWires = wires;
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

}
