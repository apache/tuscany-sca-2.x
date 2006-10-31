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
package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * The default implementation of a TargetInvoker
 */
public abstract class TargetInvokerExtension implements TargetInvoker {

    protected InboundWire wire;
    protected WorkContext workContext;
    protected ExecutionMonitor monitor;
    protected boolean cacheable;

    /**
     * Creates a new invoker
     *
     * @param wire the callback wire
     * @param workContext the work context to use for setting correlation information
     * @param monitor
     */
    public TargetInvokerExtension(InboundWire wire, WorkContext workContext, ExecutionMonitor monitor) {
        this.wire = wire;
        this.workContext = workContext;
        this.monitor = monitor;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object messageId = msg.getMessageId();
            if (messageId != null) {
                workContext.setCurrentMessageId(null);
                workContext.setCurrentCorrelationId(messageId);
            }
            LinkedList<Object> callbackRoutingChain = msg.getCallbackRoutingChain();
            if (callbackRoutingChain != null) {
                workContext.setCurrentCallbackRoutingChain(callbackRoutingChain);
            }
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            //monitor.executionError(e);
            msg.setBodyWithFault(e.getCause());
        }
        return msg;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable();
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // TargetInvoker extends Cloneable so this should not have been thrown
            throw new AssertionError(e);
        }
    }

}
