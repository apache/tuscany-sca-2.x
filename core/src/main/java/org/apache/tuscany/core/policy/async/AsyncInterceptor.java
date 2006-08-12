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
package org.apache.tuscany.core.policy.async;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.osoa.sca.SCA;

import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Uses a <code>WorkManager</code> to schedule asynchronous execution of invocations
 *
 * @version $$Rev$$ $$Date$$
 */
public class AsyncInterceptor implements Interceptor {

    private static final ContextBinder BINDER = new ContextBinder();
    private static final Message RESPONSE = new ImmutableMessage();

    private WorkScheduler workScheduler;
    private Interceptor next;
    private AsyncMonitor monitor;

    public AsyncInterceptor(WorkScheduler workScheduler, AsyncMonitor monitor) {
        this.workScheduler = workScheduler;
        this.monitor = monitor;
    }

    public Message invoke(final Message message) {
        final CompositeContext currentContext = CurrentCompositeContext.getContext();
        // Schedule the invocation of the next interceptor in a new Work instance
        workScheduler.scheduleWork(new Runnable() {
            public void run() {
                CompositeContext oldContext = CurrentCompositeContext.getContext();
                try {
                    AsyncInterceptor.BINDER.setContext(currentContext);
                    next.invoke(message); // Invoke the next interceptor
                } catch (Exception e) {
                    monitor.executionError(e);
                } finally {
                    AsyncInterceptor.BINDER.setContext(oldContext);
                }
            }
        });
        return RESPONSE; // No return on a OneWay invocation.
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public Interceptor getNext() {
        return next;
    }

    public boolean isOptimizable() {
        return false;
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

        public MessageChannel getCallbackChannel() {
            return null;
        }

        public Message getRelatedCallbackMessage() {
            return null;
        }
    }

}
