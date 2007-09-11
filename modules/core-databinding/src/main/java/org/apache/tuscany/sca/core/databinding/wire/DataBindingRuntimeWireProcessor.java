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

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
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
    private DataBindingExtensionPoint dataBindings;

    public DataBindingRuntimeWireProcessor(Mediator mediator, DataBindingExtensionPoint dataBindings) {
        super();
        this.mediator = mediator;
        this.dataBindings = dataBindings;
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

    public void process(RuntimeWire wire) {
        InterfaceContract sourceContract = wire.getSource().getInterfaceContract();
        InterfaceContract targetContract = wire.getTarget().getInterfaceContract();
        if (targetContract == null) {
            targetContract = sourceContract;
        }

        if (!sourceContract.getInterface().isRemotable()) {
            return;
        }
        List<InvocationChain> chains = wire.getInvocationChains();
        for (InvocationChain chain : chains) {
            Operation sourceOperation = chain.getSourceOperation();
            Operation targetOperation = chain.getTargetOperation();

            Interceptor interceptor = null;
            if (isTransformationRequired(sourceContract, sourceOperation, targetContract, targetOperation)) {
                // Add the interceptor to the source side because multiple
                // references can be wired to the same service
                interceptor = new DataTransformationInteceptor(wire, sourceOperation, targetOperation, mediator);
            } else {
                // assume pass-by-values copies are required if interfaces are remotable and there is no data binding
                // transformation, i.e. a transformation will result in a copy so another pass-by-value copy is unnecessary
                if (requiresCopy(wire, sourceOperation, targetOperation)) {
                    interceptor = new PassByValueInteceptor(dataBindings, targetOperation);
                }
            }
            if (interceptor != null) {
                chain.addInterceptor(0, interceptor);
            }
        }

    }

    /**
     * Pass-by-value copies are required if the interfaces are remotable unless the
     * implementation uses the @AllowsPassByReference annotation.
     */
    protected boolean requiresCopy(RuntimeWire wire, Operation sourceOperation, Operation targetOperation) {
        if (!sourceOperation.getInterface().isRemotable()) {
            return false;
        }
        if (!targetOperation.getInterface().isRemotable()) {
            return false;
        }

        if (allowsPassByReference(wire.getSource().getComponent(), sourceOperation)) {
            return false;
        }
        
        if (allowsPassByReference(wire.getTarget().getComponent(), sourceOperation)) {
            return false;
        }

        return true;
    }

    /**
     * Does the implementation use the @AllowsPassByReference annotation for the operation.
     * Uses reflection to avoid a dependency on JavaImplementation because the isAllowsPassByReference
     * and getAllowsPassByReference methods are not on the Implementation interface.
     * TODO: move isAllowsPassByReference/getAllowsPassByReference to Implementation interface 
     */
    protected boolean allowsPassByReference(RuntimeComponent component, Operation operation) {
        if (component == null || component.getImplementation() == null) {
            return true; // err on the side of no copies
        }
        Implementation impl = component.getImplementation();
        try {

            Method m = impl.getClass().getMethod("isAllowsPassByReference", new Class[] {});
            if ((Boolean)m.invoke(impl, new Object[]{})) {
                return true;
            }

            m = impl.getClass().getMethod("getAllowsPassByReferenceMethods", new Class[] {});
            List<Method> ms = (List<Method>)m.invoke(impl, new Object[]{});
            if (ms != null) {
                for (Method m2 : ms) {
                    // simple name matching is ok as its a remote operation so no overloading 
                    if (operation.getName().equals(m2.getName()))
                        return true;
                }
            }
            
        } catch (Exception e) {
            // ignore, assume the impl has no isAllowsPassByReference method
        }

        return false;
    }

}
