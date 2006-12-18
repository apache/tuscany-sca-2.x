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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
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

    private DataBindingRegistry dataBindingRegistry;
    
    public PassByValueWirePostProcessor() {
        super();

    }
    
    /**
     * @param dataBindingRegistry the dataBindingRegistry to set
     */
    @Autowire
    public void setDataBindingRegistry(DataBindingRegistry dataBindingRegistry) {
        this.dataBindingRegistry = dataBindingRegistry;
    }

    public void process(OutboundWire source, InboundWire target) {
        Interceptor tailInterceptor;
        PassByValueInterceptor passByValueInterceptor = null;
        Operation<?> targetOperation;
        Operation<?> sourceOperation;
        DataBinding[] argsDataBindings = null;
        DataBinding resultDataBinding = null;
        
        boolean allowsPassByReference = false;
        if (target.getContainer() instanceof AtomicComponentExtension) {
            allowsPassByReference =
                ((AtomicComponentExtension) target.getContainer()).isAllowsPassByReference();
        }
        if (target.getServiceContract().isRemotable()
            && !allowsPassByReference) {
            Map<Operation<?>, InboundInvocationChain> chains = target.getInvocationChains();
            for (Map.Entry<Operation<?>, InboundInvocationChain> entry : chains.entrySet()) {
                targetOperation = entry.getKey();
                sourceOperation =
                    getSourceOperation(source.getInvocationChains().keySet(), targetOperation.getName());

                argsDataBindings = resolveArgsDataBindings(targetOperation);
                resultDataBinding = resolveResultDataBinding(targetOperation);
                
                passByValueInterceptor = new PassByValueInterceptor();
                passByValueInterceptor.setDataBinding(getDataBinding(targetOperation));
                passByValueInterceptor.setArgsDataBindings(argsDataBindings);
                passByValueInterceptor.setResultDataBinding(resultDataBinding);
    
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
                targetOperation = entry.getKey();
                sourceOperation =
                    getSourceOperation(target.getSourceCallbackInvocationChains(targetAddress).keySet(),
                        targetOperation.getName());
                
                argsDataBindings = resolveArgsDataBindings(targetOperation);
                resultDataBinding = resolveResultDataBinding(targetOperation);
                
                passByValueInterceptor = new PassByValueInterceptor();
                passByValueInterceptor.setDataBinding(getDataBinding(targetOperation));
                passByValueInterceptor.setArgsDataBindings(argsDataBindings);
                passByValueInterceptor.setResultDataBinding(resultDataBinding);
                
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
    
    private DataBinding getDataBinding(Operation<?> operation) {
        String dataBinding = operation.getDataBinding();
        if (dataBinding == null) {
            ServiceContract<?> serviceContract = operation.getServiceContract();
            dataBinding = serviceContract.getDataBinding();
        }
        return dataBindingRegistry.getDataBinding(dataBinding);
        
    }
    
    @SuppressWarnings("unchecked")
    private DataBinding[] resolveArgsDataBindings(Operation operation) {
        List<DataType<?>> argumentTypes = (List<DataType<?>>)operation.getInputType().getLogical();
        DataBinding[] argDataBindings = new DataBinding[argumentTypes.size()];
        int count = 0; 
        for ( DataType argType : argumentTypes ) {
            argDataBindings[count] = null;
            if ( argType != null ) {
                if ( argType.getLogical() instanceof Class ) {
                    argDataBindings[count] = 
                        dataBindingRegistry.getDataBinding(((Class)argType.getLogical()).getName());
                }
            }
            ++count;
        }
        return argDataBindings;
    }
    
    private DataBinding resolveResultDataBinding(Operation operation) {
        DataType<?> resultType = (DataType<?>)operation.getOutputType();
        DataBinding resultBinding = null;
        if ( resultType != null ) {
            if ( resultType.getLogical() instanceof Class ) {
                resultBinding = 
                        dataBindingRegistry.getDataBinding(((Class)resultType.getLogical()).getName());
            }
        }
        return resultBinding;
    }
}
