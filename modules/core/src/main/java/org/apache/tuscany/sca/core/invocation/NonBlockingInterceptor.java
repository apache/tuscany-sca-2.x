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
import java.util.Map;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Adds non-blocking behavior to an invocation chain
 *
 * @version $Rev$ $Date$
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

    /**
     * Sets desired workScheduler to NonBlockingInterceptor. This is a useful function for the extension framework
     * to set desired workmanager on the InvocationChain, other than default workmanager which is set per Tuscany runtime.
     * Using this function, extension framework can set desired workmanager on InvocationChain during post wire processing.
     * @param workScheduler workScheduler which contains workmanager
     */
    public void setWorkScheduler(WorkScheduler workScheduler){
        this.workScheduler = workScheduler;
    }

    public Message invoke(final Message msg) {
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workScheduler.scheduleWork(new Runnable() {
                public void run() {
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

        public boolean isFault() {
            return false;
        }

        public void setFaultBody(Object fault) {
            throw new UnsupportedOperationException();
        }

        public EndpointReference getFrom() {
            return null;
        }

        public EndpointReference getTo() {
            return null;
        }

        public void setFrom(EndpointReference from) {
            throw new UnsupportedOperationException();
        }

        public void setTo(EndpointReference to) {
            throw new UnsupportedOperationException();
        }

        public Operation getOperation() {
            return null;
        }

        public void setOperation(Operation op) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.apache.tuscany.sca.invocation.Message#getReplyTo()
         */
        public EndpointReference getReplyTo() {
            return null;
        }

        public Map<String, Object> getQoSContext() {
            return null;
        }
    }

}
