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

package org.apache.tuscany.core.databinding.impl;

import java.util.Map;
import java.util.Set;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * This processor is responsible to add an interceptor to invocation chain if
 * the source and target operations have different databinding requirements
 */
public class DataBindingWirePostProcessor extends WirePostProcessorExtension {
    private Mediator mediator;

    @Constructor({"mediator"})
    public DataBindingWirePostProcessor(@Autowire Mediator mediator) {
        super();
        this.mediator = mediator;
    }

    public void process(OutboundWire source, InboundWire target) {
        Map<Operation<?>, OutboundInvocationChain> chains = source.getInvocationChains();
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : chains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(target.getInvocationChains().keySet(), sourceOperation.getName());
            String sourceDataBinding = getDataBinding(sourceOperation);
            String targetDataBinding = getDataBinding(targetOperation);
            if (sourceDataBinding == null || targetDataBinding == null
                || !sourceDataBinding.equals(targetDataBinding)) {
                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataBindingInteceptor interceptor =
                    new DataBindingInteceptor(source, sourceOperation, target, targetOperation);
                interceptor.setMediator(mediator);
                entry.getValue().addInterceptor(0, interceptor);
            }
        }

        // Check if there's a callback
        Map callbackOperations = source.getServiceContract().getCallbackOperations();
        if (callbackOperations == null || callbackOperations.isEmpty()) {
            return;
        }
        Object targetAddress = source.getContainer().getName();
        Map<Operation<?>, OutboundInvocationChain> callbackChains =
            target.getSourceCallbackInvocationChains(targetAddress);
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : callbackChains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(source.getTargetCallbackInvocationChains().keySet(), sourceOperation
                    .getName());
            String sourceDataBinding = getDataBinding(sourceOperation);
            String targetDataBinding = getDataBinding(targetOperation);
            if (sourceDataBinding == null || targetDataBinding == null
                || !sourceDataBinding.equals(targetDataBinding)) {
                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataBindingInteceptor interceptor =
                    new DataBindingInteceptor(source, sourceOperation, target, targetOperation);
                interceptor.setMediator(mediator);
                entry.getValue().addInterceptor(0, interceptor);
            }
        }
    }

    public void process(InboundWire source, OutboundWire target) {
        SCAObject container = source.getContainer();
        // Either Service or Reference
        boolean isReference = container instanceof Reference;

        Map<Operation<?>, InboundInvocationChain> chains = source.getInvocationChains();
        for (Map.Entry<Operation<?>, InboundInvocationChain> entry : chains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(target.getInvocationChains().keySet(), sourceOperation.getName());
            String sourceDataBinding = getDataBinding(sourceOperation);
            String targetDataBinding = getDataBinding(targetOperation);
            if (sourceDataBinding == null || targetDataBinding == null
                || !sourceDataBinding.equals(targetDataBinding)) {
                // Add the interceptor to the source side
                DataBindingInteceptor interceptor =
                    new DataBindingInteceptor(source, sourceOperation, target, targetOperation);
                interceptor.setMediator(mediator);
                if (isReference) {
                    // FIXME: We need a better way to position the interceptors
                    target.getInvocationChains().get(targetOperation).addInterceptor(0, interceptor);
                    Interceptor tail = entry.getValue().getTailInterceptor();
                    if (tail != null) {
                        // HACK to relink the bridging interceptor
                        tail.setNext(interceptor);
                    }
                } else {
                    entry.getValue().addInterceptor(0, interceptor);
                }

            }
        }
    }

    private Operation getTargetOperation(Set<Operation<?>> operations, String operationName) {
        for (Operation<?> op : operations) {
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        return null;
    }

    private String getDataBinding(Operation<?> operation) {
        String dataBinding = operation.getDataBinding();
        if (dataBinding == null) {
            ServiceContract<?> serviceContract = operation.getServiceContract();
            dataBinding = serviceContract.getDataBinding();
        }
        return dataBinding;
    }

}
