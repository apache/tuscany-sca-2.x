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

import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.AbstractOutboundInvocationHandler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;

public class Axis2CallbackInvocationHandler extends AbstractOutboundInvocationHandler {

    private InboundWire inboundWire;
    private Object messageId;
    private Object correlationId;

    public Axis2CallbackInvocationHandler(InboundWire inboundWire) {
        this.inboundWire = inboundWire;
    }

    public Object invoke(Operation operation, Object[] args) throws Throwable {
        Object targetAddress = inboundWire.retrieveMapping(correlationId);
        if (targetAddress == null) {
            throw new AssertionError("No from address associated with message id [" + correlationId + "]");
        }
        //TODO optimize as this is slow in local invocations
        Map<Operation<?>, OutboundInvocationChain> sourceCallbackInvocationChains =
            inboundWire.getSourceCallbackInvocationChains(targetAddress);
        OutboundInvocationChain chain = sourceCallbackInvocationChains.get(operation);
        TargetInvoker invoker = chain.getTargetInvoker();
        return invoke(chain, invoker, args);
    }

    // This must be called before invoke
    public void setMessageId(Object messageId) {
        this.messageId = messageId;
    }
    
    // This must be called before invoke
    public void setCorrelationId(Object correlationId) {
        this.correlationId = correlationId;
    }

    protected Object getFromAddress() {
        return (inboundWire.getContainer() == null) ? null : inboundWire.getContainer().getName();
    }
    
    protected Object getMessageId() {
        return messageId;
    }
    
    protected Object getCorrelationId() {
        return correlationId;
    }

}
