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

package org.apache.tuscany.sca.core.databinding.wire;

import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.util.OperationDataBindingHelper;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;

/**
 * This processor is responsible to add an interceptor to invocation chain if
 * the source and target operations have different databinding requirements
 *
 * @version $Rev$ $Date$
 */
public class DataBindingRuntimeWireProcessor implements RuntimeWireProcessor {
    private Mediator mediator;
    
    public DataBindingRuntimeWireProcessor(ExtensionPointRegistry registry) {
        super();
        this.mediator = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(Mediator.class);
    }

    public void process(RuntimeEndpoint endpoint) {
        InterfaceContract sourceContract = endpoint.getBindingInterfaceContract();
        InterfaceContract targetContract = endpoint.getComponentTypeServiceInterfaceContract();
        if (targetContract == null) {
            targetContract = sourceContract;
        }

        if (!sourceContract.getInterface().isRemotable()) {
            return;
        }
        List<InvocationChain> chains = endpoint.getInvocationChains();
        for (InvocationChain chain : chains) {
            Operation sourceOperation = chain.getSourceOperation();
            Operation targetOperation = chain.getTargetOperation();

            Interceptor interceptor = null;
            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {
                // Add the interceptor to the source side because multiple
                // references can be wired to the same service
                interceptor = new DataTransformationInterceptor(endpoint, sourceOperation, targetOperation, mediator);
            }
            if (interceptor != null) {
                String phase = Phase.SERVICE_INTERFACE;
                chain.addInterceptor(phase, interceptor);
            }
        }

    }

    public void process(RuntimeEndpointReference endpointReference) {
        InterfaceContract sourceContract = endpointReference.getComponentTypeReferenceInterfaceContract();
        InterfaceContract targetContract = endpointReference.getBindingInterfaceContract();
        if (targetContract == null) {
            targetContract = sourceContract;
        }

        if (sourceContract == null || !sourceContract.getInterface().isRemotable()) {
            return;
        }
        List<InvocationChain> chains = endpointReference.getInvocationChains();
        for (InvocationChain chain : chains) {
            Operation sourceOperation = chain.getSourceOperation();
            Operation targetOperation = chain.getTargetOperation();

            Interceptor interceptor = null;
            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {
                // Add the interceptor to the source side because multiple
                // references can be wired to the same service
                interceptor = new DataTransformationInterceptor(endpointReference, sourceOperation, targetOperation, mediator);
            }
            if (interceptor != null) {
                String phase = Phase.REFERENCE_INTERFACE;
                chain.addInterceptor(phase, interceptor);
            }
        }
        
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
        return OperationDataBindingHelper.isTransformationRequired(sourceOperation, targetOperation);
    }

}
