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
package org.apache.tuscany.core.implementation.composite;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * 
 */
public class OperationOutboundInvocationHandler extends AbstractOperationOutboundInvocationHandler {

    /*
     * an association of an operation to chain holder. The holder contains an invocation chain
     * and a local clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the wire chains will be used.
     */
    private Map<Operation, ChainHolder> chains;
    private Object fromAddress;
    private boolean contractHasCallback;

    public OperationOutboundInvocationHandler(OutboundWire wire) {
        Map<Operation<?>, OutboundInvocationChain> invocationChains = wire.getInvocationChains();
        this.chains = new HashMap<Operation, ChainHolder>(invocationChains.size());
        this.fromAddress = (wire.getContainer() == null) ? null : wire.getContainer().getName();
        // TODO optimize this
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : invocationChains.entrySet()) {
            Operation operation = entry.getKey();
            this.chains.put(operation, new ChainHolder(entry.getValue()));
        }
        this.contractHasCallback = wire.getServiceContract().getCallbackClass() != null;
    }

    public Message invoke(Operation operation, Message msg) throws Throwable {
        ChainHolder holder = chains.get(operation);
        if (holder == null) {
            TargetException e = new TargetException("Operation not configured");
            e.setIdentifier(operation.getName());
            throw e;
        }
        OutboundInvocationChain chain = holder.chain;
        TargetInvoker invoker;

        if (holder.cachedInvoker == null) {
            assert chain != null;
            if (chain.getTargetInvoker() == null) {
                TargetException e = new TargetException("No target invoker configured for operation");
                e.setIdentifier(chain.getOperation().getName());
                throw e;
            }
            if (chain.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.cachedInvoker = (TargetInvoker) chain.getTargetInvoker().clone();
                invoker = holder.cachedInvoker;
            } else {
                invoker = chain.getTargetInvoker();
            }
        } else {
            assert chain != null;
            invoker = chain.getTargetInvoker();
        }

        // Pushing the from address only needs to happen in the outbound (forward) direction for callbacks
        if (contractHasCallback) {
            msg.pushFromAddress(fromAddress);
        }

        return invoke(chain, invoker, msg);
    }

    /**
     * A holder used to associate an wire chain with a local copy of a target invoker that was previously cloned from
     * the chain master
     */
    private class ChainHolder {

        OutboundInvocationChain chain;
        TargetInvoker cachedInvoker;

        public ChainHolder(OutboundInvocationChain config) {
            this.chain = config;
        }

    }
}
