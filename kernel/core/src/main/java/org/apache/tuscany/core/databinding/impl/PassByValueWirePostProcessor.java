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

import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;

/**
 * This processor is responsible for enforcing the pass-by-value semantics required of Remotable interfaces. This is
 * done by adding a pass-by-value interceptor to the inbound invocation chain of a target if the target interface is
 * Remotable.
 */
public class PassByValueWirePostProcessor extends WirePostProcessorExtension {


    public PassByValueWirePostProcessor() {
        super();

    }

    public void process(OutboundWire source, InboundWire target) {
        Interceptor tailInterceptor;
        PassByValueInterceptor passByValueInterceptor;
        Operation<?> targetOperation;
        Operation<?> sourceOperation;
        boolean allowsPassByReference = false;
        if (target.getContainer() instanceof AtomicComponentExtension) {
            allowsPassByReference =
                ((AtomicComponentExtension) target.getContainer()).isAllowsPassByReference();
        }
        if (target.getServiceContract().isRemotable()
            && !allowsPassByReference) {
            Map<Operation<?>, InboundInvocationChain> chains = target.getInvocationChains();
            for (Map.Entry<Operation<?>, InboundInvocationChain> entry : chains.entrySet()) {
                passByValueInterceptor = new PassByValueInterceptor();
                targetOperation = entry.getKey();
                sourceOperation =
                    getSourceOperation(source.getInvocationChains().keySet(), targetOperation.getName());

                entry.getValue().addInterceptor(0, passByValueInterceptor);
                tailInterceptor = source.getInvocationChains().get(sourceOperation).getTailInterceptor();
                if (tailInterceptor != null) {
                    tailInterceptor.setNext(passByValueInterceptor);
                }
            }
        }

        // Check if there's a callback
        Map callbackOperations = source.getServiceContract().getCallbackOperations();
        allowsPassByReference = false;
        if (source.getContainer() instanceof AtomicComponentExtension) {
            allowsPassByReference =
                ((AtomicComponentExtension) source.getContainer()).isAllowsPassByReference();
        }

        if (source.getServiceContract().isRemotable()
            && !allowsPassByReference
            && callbackOperations != null
            && !callbackOperations.isEmpty()) {
            Object targetAddress = source.getContainer().getName();
            Map<Operation<?>, InboundInvocationChain> callbackChains = source.getTargetCallbackInvocationChains();
            for (Map.Entry<Operation<?>, InboundInvocationChain> entry : callbackChains.entrySet()) {
                passByValueInterceptor = new PassByValueInterceptor();
                targetOperation = entry.getKey();
                sourceOperation =
                    getSourceOperation(target.getSourceCallbackInvocationChains(targetAddress).keySet(),
                        targetOperation.getName());

                entry.getValue().addInterceptor(0, passByValueInterceptor);
                tailInterceptor =
                    target.getSourceCallbackInvocationChains(targetAddress).get(sourceOperation)
                        .getTailInterceptor();
                if (tailInterceptor != null) {
                    tailInterceptor.setNext(passByValueInterceptor);
                }
            }
        }
    }

    public void process(InboundWire source, OutboundWire target) {
        //to be done if required.. 
    }

    private Operation getSourceOperation(Set<Operation<?>> operations, String operationName) {
        for (Operation<?> op : operations) {
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        return null;
    }
}
