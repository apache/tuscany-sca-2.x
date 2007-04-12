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

package org.apache.tuscany.core.databinding.wire;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

/**
 * This processor is responsible to add an interceptor to invocation chain if
 * the source and target operations have different databinding requirements
 * 
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessor extends WirePostProcessorExtension {
    private Mediator mediator;
    private ComponentManager componentManager;

    @Constructor( {"componentManager", "mediator"})
    public DataBindingWirePostProcessor(@Reference
    ComponentManager componentManager, @Reference
    Mediator mediator) {
        super();
        this.componentManager = componentManager;
        this.mediator = mediator;
    }

    public boolean isTransformationRequired(DataType source, DataType target) {
        if (source == target) {
            return false;
        }
        return !source.getDataBinding().equals(target.getDataBinding());
    }

    public boolean isTransformationRequired(Operation source, Operation target) {
        if (source == target) {
            return false;
        }

        // Check output type
        DataType sourceOutputType = source.getOutputType();
        DataType targetOutputType = target.getOutputType();

        // Note the target output type is now the source for checking
        // compatibility
        if (isTransformationRequired(targetOutputType, sourceOutputType)) {
            return true;
        }

        List<DataType> sourceInputType = source.getInputType().getLogical();
        List<DataType> targetInputType = target.getInputType().getLogical();

        int size = sourceInputType.size();
        for (int i = 0; i < size; i++) {
            if (isTransformationRequired(sourceInputType.get(i), targetInputType.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean isTransformationRequired(InterfaceContract sourceContract,
                                             Operation sourceOperation,
                                             InterfaceContract targetContract,
                                             Operation targetOperation) {
        if (targetContract == null) {
            targetContract = sourceContract;
        }
        if (sourceContract == targetContract) {
            return false;
        }
        return true;
    }

    public void process(Wire wire) {
        InterfaceContract sourceContract = wire.getSourceContract();
        InterfaceContract targetContract = wire.getTargetContract();
        if (targetContract == null) {
            targetContract = sourceContract;
        }
        if (sourceContract == targetContract) {
            return;
        }
        Map<Operation, InvocationChain> chains = wire.getInvocationChains();
        for (Map.Entry<Operation, InvocationChain> entry : chains.entrySet()) {
            String opName = entry.getKey().getName();
            Operation sourceOperation = sourceContract.getInterface().getOperation(opName);
            Operation targetOperation = targetContract.getInterface().getOperation(opName);

            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {
                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataBindingInteceptor interceptor = new DataBindingInteceptor(componentManager, wire, sourceOperation,
                                                                              targetOperation);
                interceptor.setMediator(mediator);
                entry.getValue().addInterceptor(0, interceptor);
            }
        }

        // Object targetAddress = UriHelper.getBaseName(source.getUri());
        Map<Operation, InvocationChain> callbackChains = wire.getCallbackInvocationChains();
        if (callbackChains == null) {
            // callback chains could be null
            return;
        }

        for (Map.Entry<Operation, InvocationChain> entry : callbackChains.entrySet()) {
            String opName = entry.getKey().getName();
            Operation sourceOperation = sourceContract.getCallbackInterface().getOperations().get(opName);
            Operation targetOperation = targetContract.getCallbackInterface().getOperations().get(opName);
            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {

                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataBindingInteceptor interceptor = new DataBindingInteceptor(componentManager, wire, sourceOperation,
                                                                              targetOperation);
                interceptor.setMediator(mediator);
                entry.getValue().addInterceptor(0, interceptor);
            }
        }
    }

}
