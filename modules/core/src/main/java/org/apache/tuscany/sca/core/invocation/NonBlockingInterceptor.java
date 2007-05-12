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
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Adds non-blocking behavior to an invocation chain
 *
 * @version $$Rev$$ $$Date$$
 */
public class NonBlockingInterceptor implements Interceptor {

    private static final Message RESPONSE = new ImmutableMessage();

    private WorkScheduler workScheduler;
    private Invoker next;

    public NonBlockingInterceptor(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    public NonBlockingInterceptor(WorkScheduler workScheduler, Interceptor next) {
        this.workScheduler = workScheduler;
        this.next = next;
    }

    public Message invoke(final Message msg) {
        // Retrieve conversation id to transfer to new thread
        // Notice that we cannot clear the conversation id from the current thread
        final String conversationID = ThreadMessageContext.getMessageContext().getConversationID();
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workScheduler.scheduleWork(new Runnable() {
                public void run() {
                    msg.setCorrelationID(null);
                    // if we got a conversation id, transfer it to new thread
                    if (conversationID != null) {
                        msg.setConversationID(conversationID);
                    }
                    Message context = ThreadMessageContext.setMessageContext(msg);
                    try {
                        next.invoke(msg);
                    } finally {
                        ThreadMessageContext.setMessageContext(context);
                    }
                }
            });
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return RESPONSE;
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    /**
     * A dummy message passed back on an invocation
     */
    private static class ImmutableMessage implements Message {

        public String getConversationID() {
            return null;
        }

        public RuntimeWire getWire() {
            return null;
        }

        public void setConversationID(String conversationId) {
            throw new UnsupportedOperationException();
        }

        public void setWire(RuntimeWire wire) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        public Object getBody() {
            return null;
        }

        public void setBody(Object body) {
            if (body != null) {
                throw new UnsupportedOperationException();
            }
        }

        public void setCallbackWires(LinkedList<RuntimeWire> wires) {

        }

        public Object getMessageID() {
            return null;
        }

        public void setMessageID(Object messageId) {
            throw new UnsupportedOperationException();
        }

        public Object getCorrelationID() {
            return null;
        }

        public void setCorrelationID(Object correlationId) {
            throw new UnsupportedOperationException();
        }

        public boolean isFault() {
            return false;
        }

        public void setFaultBody(Object fault) {
            throw new UnsupportedOperationException();
        }

        public void setConversationSequence(ConversationSequence sequence) {
            throw new UnsupportedOperationException();
        }
        
        public ConversationSequence getConversationSequence() {
            return null;
        }

        public String getFrom() {
            return null;
        }

        public String getTo() {
            return null;
        }

        public void setFrom(String from) {
            throw new UnsupportedOperationException();
        }

        public void setTo(String to) {
            throw new UnsupportedOperationException();
        }

    }

}
