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
package org.apache.tuscany.core.wire;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Bridges interceptors in a non-blocking fashion between an {@link org.apache.tuscany.spi.wire.InboundInvocationChain}
 * and an {@link org.apache.tuscany.spi.wire.OutboundInvocationChain} by using a {@link
 * org.apache.tuscany.spi.component.WorkContext}.
 *
 * @version $$Rev$$ $$Date$$
 */
public class NonBlockingBridgingInterceptor implements BridgingInterceptor {

    private static final ContextBinder BINDER = new ContextBinder();
    private static final Message RESPONSE = new ImmutableMessage();

    private WorkScheduler workScheduler;
    private WorkContext workContext;
    private Interceptor next;

    public NonBlockingBridgingInterceptor(WorkScheduler workScheduler, WorkContext workContext) {
        this.workScheduler = workScheduler;
        this.workContext = workContext;
    }

    public NonBlockingBridgingInterceptor(WorkScheduler workScheduler, WorkContext workContext, Interceptor next) {
        this.workScheduler = workScheduler;
        this.workContext = workContext;
        this.next = next;
    }

    public Message invoke(final Message msg) {
        final CompositeContext currentContext = CurrentCompositeContext.getContext();
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workScheduler.scheduleWork(new Runnable() {
                public void run() {
                    workContext.setCurrentMessageId(null);
                    workContext.setCurrentCorrelationId(null);
                    CompositeContext oldContext = CurrentCompositeContext.getContext();
                    try {
                        BINDER.setContext(currentContext);
                        next.invoke(msg);
                    } catch (Exception e) {
                        // REVIEW uncomment when it is available
                        // monitor.executionError(e);
                        e.printStackTrace();
                    } finally {
                        BINDER.setContext(oldContext);
                    }
                }
            });
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return RESPONSE;
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public boolean isOptimizable() {
        return true;
    }

    private static class ContextBinder extends SCA {
        public void setContext(CompositeContext context) {
            setCompositeContext(context);
        }

        public void start() {
            throw new AssertionError();
        }

        public void stop() {
            throw new AssertionError();
        }
    }

    /**
     * A dummy message passed back on an invocation
     */
    private static class ImmutableMessage implements Message {

        public Object getBody() {
            return null;
        }

        public void setBody(Object body) {
            throw new UnsupportedOperationException();
        }

        public void setTargetInvoker(TargetInvoker invoker) {
            throw new UnsupportedOperationException();
        }

        public TargetInvoker getTargetInvoker() {
            return null;
        }

        public Message getRelatedCallbackMessage() {
            return null;
        }

        public Object getFromAddress() {
            return null;
        }

        public void setFromAddress(Object fromAddress) {
            throw new UnsupportedOperationException();
        }

        public Object getMessageId() {
            return null;
        }

        public void setMessageId(Object messageId) {
            throw new UnsupportedOperationException();
        }

        public Object getCorrelationId() {
            return null;
        }

        public void setCorrelationId(Object correlationId) {
            throw new UnsupportedOperationException();
        }

        public boolean isFault() {
            return false;
        }

        public void setBodyWithFault(Object fault) {
            throw new UnsupportedOperationException();
        }

    }

}
