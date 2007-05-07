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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.InvocationRuntimeException;
import org.apache.tuscany.invocation.Message;
import org.apache.tuscany.invocation.TargetInvoker;
import org.apache.tuscany.invocation.Wire;
import org.apache.tuscany.spi.component.WorkContext;

public class Axis2ReferenceCallbackTargetInvoker implements TargetInvoker {

    private Operation operation;
    private Wire inboundWire;
    private LinkedList<URI> callbackRoutingChain;
    private boolean cacheable;
    Axis2CallbackInvocationHandler invocationHandler;
    private CountDownLatch signal;
    private Object returnPayload;

    public Axis2ReferenceCallbackTargetInvoker(Operation operation,
                                               Wire inboundWire,
                                               Axis2CallbackInvocationHandler invocationHandler) {

        this.operation = operation;
        this.inboundWire = inboundWire;
        this.invocationHandler = invocationHandler;
    }

    public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext) throws InvocationTargetException {
        Object[] args;
        if (payload != null && !payload.getClass().isArray()) {
            args = new Object[]{payload};
            returnPayload = payload;
        } else {
            args = (Object[]) payload;
            returnPayload = args[0];
        }
        // FIXME synchronize with forward thread to return value
        signal.countDown();
        try {
//            return invocationHandler.invoke(operation, args, callbackRoutingChain);
            return null; // TODO ???
        } catch (Throwable t) {
            t.printStackTrace();
            throw new InvocationTargetException(t);
        }
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody(), NONE, null);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        } catch (Throwable e) {
            msg.setFaultBody(e);
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
        return isCacheable(); // we only need to check if the scopes are correct
    }

    public Axis2ReferenceCallbackTargetInvoker clone() throws CloneNotSupportedException {
        Axis2ReferenceCallbackTargetInvoker invoker = (Axis2ReferenceCallbackTargetInvoker) super.clone();
        invoker.operation = this.operation;
        invoker.inboundWire = this.inboundWire;
        invoker.callbackRoutingChain = this.callbackRoutingChain;
        invoker.cacheable = this.cacheable;
        invoker.invocationHandler = this.invocationHandler;
        return invoker;
    }

    public void setCallbackRoutingChain(LinkedList<URI> callbackRoutingChain) {
        this.callbackRoutingChain = callbackRoutingChain;
    }
    
    public void setSignal(CountDownLatch signal) {
        this.signal = signal;
    }
    
    public Object getReturnPayload() {
        return returnPayload;
    }

}
