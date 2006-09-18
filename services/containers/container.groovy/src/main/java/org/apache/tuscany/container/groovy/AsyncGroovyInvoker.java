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
package org.apache.tuscany.container.groovy;

import java.lang.reflect.InvocationTargetException;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Responsible for performing a non-blocking dispatch on a Groovy component implementation instance
 *
 * @version $Rev$ $Date$
 */
public class AsyncGroovyInvoker extends GroovyInvoker {

    private static final ContextBinder BINDER = new ContextBinder();
    private static final Message RESPONSE = new AsyncGroovyInvoker.ImmutableMessage();

    private InboundWire wire;
    private WorkScheduler workScheduler;
    private AsyncMonitor monitor;
    private WorkContext workContext;
    private Object target;
    private Object messageId;

    /**
     * Creates a new invoker
     *
     * @param operation     the operation the invoker is associated with
     * @param wire
     * @param component     the target component
     * @param workScheduler the work scheduler to run the invocation
     * @param monitor       the monitor to pass events to
     * @param workContext
     */
    public AsyncGroovyInvoker(String operation,
                              InboundWire wire,
                              GroovyAtomicComponent component,
                              WorkScheduler workScheduler,
                              AsyncMonitor monitor,
                              WorkContext workContext) {
        super(operation, component);
        this.wire = wire;
        this.workScheduler = workScheduler;
        this.monitor = monitor;
        this.workContext = workContext;
    }

    // Override invocation methods to defer invocation to work item
    // Both methods return null to indicate asynchrony; result will
    // be conveyed by callback
    @Override
    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        final CompositeContext currentContext = CurrentCompositeContext.getContext();
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workScheduler.scheduleWork(new Runnable() {
                private Object currentMessageId = messageId;

                public void run() {
                    workContext.setCurrentMessageId(null);
                    workContext.setCurrentCorrelationId(currentMessageId);
                    CompositeContext oldContext = CurrentCompositeContext.getContext();
                    try {
                        BINDER.setContext(currentContext);
                        // REVIEW response must be null for one-way and non-null for callback
                        AsyncGroovyInvoker.super.invokeTarget(payload);
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

    public Message invoke(Message msg) throws InvocationRuntimeException {
        // can't just call overriden invoke because it would bypass async
        try {
            messageId = msg.getMessageId();
            wire.addMapping(messageId, msg.getFromAddress());
            return (Message) invokeTarget(msg.getBody());
        } catch (InvocationTargetException e) {
            // FIXME need to log exceptions
            e.printStackTrace();
            return null;
        } catch (Throwable e) {
            // FIXME need to log exceptions
            e.printStackTrace();
            return null;
        }
    }

    public AsyncGroovyInvoker clone() throws CloneNotSupportedException {
        AsyncGroovyInvoker invoker = (AsyncGroovyInvoker) super.clone();
        invoker.workScheduler = this.workScheduler;
        invoker.monitor = this.monitor;
        return invoker;
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected Object getInstance() throws TargetException {
        if (!cacheable) {
            return component.getTargetInstance();
        } else {
            if (target == null) {
                target = component.getTargetInstance();
            }
            return target;
        }
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
