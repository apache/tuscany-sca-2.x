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

package org.apache.tuscany.sca.test.opoverload.interceptor;

import java.util.Map;
import java.util.Set;

import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;

/**
 * This processor is responsible to add an interceptor to invocation chain if
 * the source and target operations have different databinding requirements
 * 
 * @version $Rev$ $Date$
 */
public class MessageInterceptorWirePostProcessor extends WirePostProcessorExtension {
   

    public MessageInterceptorWirePostProcessor() {
        super();

    }

    public void process(OutboundWire source, InboundWire target) {
        Map<Operation<?>, OutboundInvocationChain> chains = source.getInvocationChains();
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : chains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(target.getInvocationChains().keySet(), sourceOperation.getName());

            MessageInterceptor interceptor = new MessageInterceptor(source, sourceOperation, targetOperation);

            entry.getValue().addInterceptor(0, interceptor);

        }

        // Check if there's a callback
        Map callbackOperations = source.getServiceContract().getCallbackOperations();
        if (callbackOperations == null || callbackOperations.isEmpty()) {
            return;
        }
        Object targetAddress = source.getContainer().getName();
        Map<Operation<?>, OutboundInvocationChain> callbackChains =
            target.getSourceCallbackInvocationChains(targetAddress);
        if (callbackChains == null) {
            // callback chains could be null
            return;
        }
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : callbackChains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(source.getTargetCallbackInvocationChains().keySet(), sourceOperation.getName());
            MessageInterceptor interceptor = new MessageInterceptor(source, sourceOperation, targetOperation);
            entry.getValue().addInterceptor(0, interceptor);

        }
    }

    public void process(InboundWire source, OutboundWire target) {
        SCAObject container = source.getContainer();
        // Either Service or Reference
        boolean isReference = container instanceof ReferenceBinding;

        Map<Operation<?>, InboundInvocationChain> chains = source.getInvocationChains();
        for (Map.Entry<Operation<?>, InboundInvocationChain> entry : chains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(target.getInvocationChains().keySet(), sourceOperation.getName());

            // Add the interceptor to the source side
            MessageInterceptor interceptor = new MessageInterceptor(source, sourceOperation, targetOperation);

            if (isReference) {

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

    private Operation getTargetOperation(Set<Operation<?>> operations, String operationName) {
        for (Operation<?> op : operations) {
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        return null;
    }

}
