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
import org.apache.tuscany.sca.interfacedef.DataType;
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

    public boolean isTransformationRequired(DataType source, DataType target) {
        if (source == null || target == null) { // void return type
            return false;
        }
        if (source == target) {
            return false;
        }

        // Output type can be null
        if (source == null && target == null) {
            return false;
        } else if (source == null || target == null) {
            return true;
        }
        String sourceDataBinding = source.getDataBinding();
        String targetDataBinding = target.getDataBinding();
        if (sourceDataBinding == targetDataBinding) {
            return false;
        }
        if (sourceDataBinding == null || targetDataBinding == null) {
            // TODO: If any of the databinding is null, then no transformation
            return false;
        }
        return !sourceDataBinding.equals(targetDataBinding);
    }

    public boolean isTransformationRequired(Operation source, Operation target) {
        if (source == target) {
            return false;
        }

        if (source.isWrapperStyle() != target.isWrapperStyle()) {
            return true;
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
        if (size != targetInputType.size()) {
            // TUSCANY-1682: The wrapper style may have different arguments
            return true;
        }
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

    /**
     * FIXME: TUSCANY-2586, temporary work around till the JIRA is fixed to prevent
     *  the PassByValueInterceptor being used for services when the binding protocol
     *  doesn't need the copies done.
     */
    protected boolean isOnMessage(Operation op) {
        return "onMessage".equals(op.getName());
    }

    /**
     * Pass-by-value copies are required if the interfaces are remotable unless the
     * implementation uses the @AllowsPassByReference annotation.
     */
    protected boolean isRemotable(InvocationChain chain, Operation sourceOperation, Operation targetOperation) {
        if (!sourceOperation.getInterface().isRemotable()) {
            return false;
        }
        if (!targetOperation.getInterface().isRemotable()) {
            return false;
        }
        return true;
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
                interceptor =
                    new DataTransformationInterceptor(endpoint, sourceOperation, targetOperation, mediator);
            } else {
                // assume pass-by-values copies are required if interfaces are remotable and there is no data binding
                // transformation, i.e. a transformation will result in a copy so another pass-by-value copy is unnecessary
                if (!isOnMessage(targetOperation) && isRemotable(chain, sourceOperation, targetOperation)) {
                    interceptor =
                        new PassByValueInterceptor(mediator, chain, targetOperation);
                }
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

        if (!sourceContract.getInterface().isRemotable()) {
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
                interceptor =
                    new DataTransformationInterceptor(endpointReference, sourceOperation, targetOperation, mediator);
            } else {
                // assume pass-by-values copies are required if interfaces are remotable and there is no data binding
                // transformation, i.e. a transformation will result in a copy so another pass-by-value copy is unnecessary
                if (!isOnMessage(targetOperation) && isRemotable(chain, sourceOperation, targetOperation)) {
                    interceptor =
                        new PassByValueInterceptor(mediator, chain, targetOperation);
                }
            }
            if (interceptor != null) {
                String phase = Phase.REFERENCE_INTERFACE;
                chain.addInterceptor(phase, interceptor);
            }
        }
        
    }

}
