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

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;

/**
 * Responsible for performing a non-blocking dispatch on a Groovy component implementation instance
 *
 * @version $Rev$ $Date$
 */
public class AsyncGroovyInvoker extends GroovyInvoker {

    private InboundWire wire;
    private AsyncMonitor monitor;
    private WorkContext workContext;
    private Object target;

    /**
     * Creates a new invoker
     *
     * @param operation   the operation the invoker is associated with
     * @param wire
     * @param component   the target component
     * @param monitor     the monitor to pass events to
     * @param workContext
     */
    public AsyncGroovyInvoker(String operation,
                              InboundWire wire,
                              GroovyAtomicComponent component,
                              AsyncMonitor monitor,
                              WorkContext workContext) {
        super(operation, component);
        this.wire = wire;
        this.monitor = monitor;
        this.workContext = workContext;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object messageId = msg.getMessageId();
            wire.addMapping(messageId, msg.getFromAddress());
            workContext.setCurrentMessageId(null);
            workContext.setCurrentCorrelationId(messageId);
            invokeTarget(msg.getBody());
            // async so no return value
            return null;
        } catch (Throwable e) {
            // FIXME need to log exceptions
            monitor.executionError(e);
            return null;
        }
    }

    public AsyncGroovyInvoker clone() throws CloneNotSupportedException {
        return (AsyncGroovyInvoker) super.clone();
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
}
