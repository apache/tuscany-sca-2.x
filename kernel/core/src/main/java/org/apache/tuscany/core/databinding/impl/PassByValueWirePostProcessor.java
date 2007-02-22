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

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;

/**
 * This processor is responsible for enforcing the pass-by-value semantics required of Remotable interfaces. This is
 * done by adding a pass-by-value interceptor to the inbound invocation chain of a target if the target interface is
 * Remotable.
 *
 * @version $Rev$ $Date$
 */
public class PassByValueWirePostProcessor extends WirePostProcessorExtension {
    //private DataBindingRegistry dataBindingRegistry;

    public PassByValueWirePostProcessor() {
        super();

    }

    /**
     * @param dataBindingRegistry the dataBindingRegistry to set
     */
    @Autowire
    public void setDataBindingRegistry(DataBindingRegistry dataBindingRegistry) {
        //  this.dataBindingRegistry = dataBindingRegistry;
    }

    public void process(Wire wire) {

    }

//    public void process(SCAObject source, OutboundWire sourceWire, SCAObject target, InboundWire targetWire) {
//        Interceptor tailInterceptor;
//        PassByValueInterceptor passByValueInterceptor;
//        Operation<?> targetOperation;
//        Operation<?> sourceOperation;
//        DataBinding[] argsDataBindings;
//        DataBinding resultDataBinding;
//
//        boolean allowsPassByReference = false;
//        // JFM this needs to be fixed
//        if (target instanceof AtomicComponentExtension) {
//            allowsPassByReference =
//                ((AtomicComponentExtension) target).isAllowsPassByReference();
//        }
//        if (targetWire.getSourceContract().isRemotable()
//            && !allowsPassByReference) {
//            Map<Operation<?>, InboundInvocationChain> chains = targetWire.getInboundInvocationChains();
//            for (Map.Entry<Operation<?>, InboundInvocationChain> entry : chains.entrySet()) {
//                targetOperation = entry.getKey();
//                sourceOperation =
//                    getSourceOperation(sourceWire.getOutboundInvocationChains().keySet(), targetOperation.getName());
//
//
//                if (null != sourceOperation) {
//                    argsDataBindings = resolveArgsDataBindings(targetOperation);
//                    resultDataBinding = resolveResultDataBinding(targetOperation);
//                    passByValueInterceptor = new PassByValueInterceptor();
//                    passByValueInterceptor.setDataBinding(getDataBinding(targetOperation));
//                    passByValueInterceptor.setArgsDataBindings(argsDataBindings);
//                    passByValueInterceptor.setResultDataBinding(resultDataBinding);
//                    entry.getValue().addInterceptor(0, passByValueInterceptor);
//                    tailInterceptor =
//                        sourceWire.getOutboundInvocationChains().get(sourceOperation).getTailInterceptor();
//                    if (tailInterceptor != null) {
//                        tailInterceptor.setNext(passByValueInterceptor);
//                    }
//                }
//            }
//        }
//
//        // Check if there's a callback
//        Map callbackOperations = sourceWire.getSourceContract().getCallbackOperations();
//        allowsPassByReference = false;
//        if (source instanceof AtomicComponentExtension) {
//            allowsPassByReference =
//                ((AtomicComponentExtension) source).isAllowsPassByReference();
//        }
//
//        if (sourceWire.getSourceContract().isRemotable()
//            && !allowsPassByReference
//            && callbackOperations != null
//            && !callbackOperations.isEmpty()) {
//            //URI targetAddress = UriHelper.getBaseName(source.getUri());
//            Map<Operation<?>, InboundInvocationChain> callbackChains = sourceWire.getTargetCallbackInvocationChains();
//            for (Map.Entry<Operation<?>, InboundInvocationChain> entry : callbackChains.entrySet()) {
//                targetOperation = entry.getKey();
//                sourceOperation =
//                    getSourceOperation(targetWire.getSourceCallbackInvocationChains(
// sourceWire.getSourceUri()).keySet(),
//                        targetOperation.getName());
//
//                argsDataBindings = resolveArgsDataBindings(targetOperation);
//                resultDataBinding = resolveResultDataBinding(targetOperation);
//
//                passByValueInterceptor = new PassByValueInterceptor();
//                passByValueInterceptor.setDataBinding(getDataBinding(targetOperation));
//                passByValueInterceptor.setArgsDataBindings(argsDataBindings);
//                passByValueInterceptor.setResultDataBinding(resultDataBinding);
//
//                entry.getValue().addInterceptor(0, passByValueInterceptor);
//                tailInterceptor =
//                    targetWire.getSourceCallbackInvocationChains(sourceWire.getSourceUri()).get(sourceOperation)
//                        .getTailInterceptor();
//                if (tailInterceptor != null) {
//                    tailInterceptor.setNext(passByValueInterceptor);
//                }
//            }
//        }
//    }

//    private Operation getSourceOperation(Set<Operation<?>> operations, String operationName) {
//        for (Operation<?> op : operations) {
//            if (op.getName().equals(operationName)) {
//                return op;
//            }
//        }
//        return null;
//    }
//
//    private DataBinding getDataBinding(Operation<?> operation) {
//        String dataBinding = operation.getDataBinding();
//        if (dataBinding == null) {
//            ServiceContract<?> serviceContract = operation.getServiceContract();
//            dataBinding = serviceContract.getDataBinding();
//        }
//        return dataBindingRegistry.getDataBinding(dataBinding);
//
//    }

//    @SuppressWarnings("unchecked")
//    private DataBinding[] resolveArgsDataBindings(Operation operation) {
//        List<DataType<?>> argumentTypes = (List<DataType<?>>) operation.getInputType().getLogical();
//        DataBinding[] argDataBindings = new DataBinding[argumentTypes.size()];
//        int count = 0;
//        for (DataType argType : argumentTypes) {
//            argDataBindings[count] = null;
//            if (argType != null) {
//                if (argType.getLogical() instanceof Class) {
//                    argDataBindings[count] =
//                        dataBindingRegistry.getDataBinding(((Class) argType.getLogical()).getName());
//                }
//            }
//            ++count;
//        }
//        return argDataBindings;
//    }
//
//    private DataBinding resolveResultDataBinding(Operation operation) {
//        DataType<?> resultType = (DataType<?>) operation.getOutputType();
//        DataBinding resultBinding = null;
//        if (resultType != null && resultType.getLogical() instanceof Class) {
//            Class<?> logical = (Class<?>) resultType.getLogical();
//            resultBinding = dataBindingRegistry.getDataBinding(logical.getName());
//        }
//        return resultBinding;
//    }
//
}
