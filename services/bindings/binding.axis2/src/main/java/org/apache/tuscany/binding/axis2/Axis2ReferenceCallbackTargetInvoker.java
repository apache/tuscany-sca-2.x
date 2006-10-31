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
import java.util.Stack;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

public class Axis2ReferenceCallbackTargetInvoker implements TargetInvoker {
    
    private Operation operation;
    private InboundWire inboundWire;
    private Stack<Object> callbackRoutingChain;
    private boolean cacheable;
    Axis2CallbackInvocationHandler invocationHandler;
    
    public Axis2ReferenceCallbackTargetInvoker(Operation operation,
            InboundWire inboundWire,
            Axis2CallbackInvocationHandler invocationHandler) {

        this.operation = operation;
        this.inboundWire = inboundWire;
        this.invocationHandler = invocationHandler;
    }

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        Object[] args;
        if (payload != null && !payload.getClass().isArray()) {
            args = new Object[]{payload};
        } else {
            args = (Object[]) payload;
        }
        try {
            return invocationHandler.invoke(operation, args, callbackRoutingChain);
        } catch(Throwable t) {
            t.printStackTrace();
            throw new InvocationTargetException(t);
        }
    }
    
    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
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
    
    public void setCallbackRoutingChain(Stack<Object> callbackRoutingChain) {
        this.callbackRoutingChain = callbackRoutingChain;
    }
}
