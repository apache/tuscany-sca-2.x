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

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;

/**
 * This processor is responsible to add an interceptor to invocation chain if
 * the source and target operations have different databinding requirements
 * 
 * @version $Rev$ $Date$
 */
public class DataBindingRuntimeWireProcessor implements RuntimeWireProcessor {
    private Mediator mediator;

    public DataBindingRuntimeWireProcessor(Mediator mediator) {
        super();
        this.mediator = mediator;
    }

    public boolean isTransformationRequired(DataType source, DataType target) {
        if (source == target) {
            return false;
        }
        String sourceDataBinding = source.getDataBinding();
        String targetDataBinding = target.getDataBinding();
        if (sourceDataBinding == targetDataBinding) {
            return false;
        }
        if (sourceDataBinding == null || targetDataBinding == null) {
            return true;
        }
        return !sourceDataBinding.equals(targetDataBinding);
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
        return isTransformationRequired(sourceOperation, targetOperation);
    }

    public void process(RuntimeWire wire) {
        InterfaceContract sourceContract = wire.getSource().getInterfaceContract();
        InterfaceContract targetContract = wire.getTarget().getInterfaceContract();
        if (targetContract == null) {
            targetContract = sourceContract;
        }
        if (sourceContract == targetContract) {
            return;
        }
        List<InvocationChain> chains = wire.getInvocationChains();
        for (InvocationChain chain : chains) {
            Operation sourceOperation = chain.getSourceOperation();
            Operation targetOperation = chain.getTargetOperation();

            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {
                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataTransformationInteceptor interceptor = new DataTransformationInteceptor(wire, sourceOperation,
                                                                              targetOperation);
                interceptor.setMediator(mediator);
                chain.addInterceptor(0, interceptor);
            }
        }

        // Object targetAddress = UriHelper.getBaseName(source.getUri());
        List<InvocationChain> callbackChains = wire.getCallbackInvocationChains();
        if (callbackChains == null) {
            // callback chains could be null
            return;
        }

        for (InvocationChain chain : callbackChains) {
            Operation sourceOperation = chain.getSourceOperation();
            Operation targetOperation = chain.getTargetOperation();
            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {

                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataTransformationInteceptor interceptor = new DataTransformationInteceptor(wire, sourceOperation,
                                                                              targetOperation);
                interceptor.setMediator(mediator);
                chain.addInterceptor(0, interceptor);
            }
        }
    }

}
