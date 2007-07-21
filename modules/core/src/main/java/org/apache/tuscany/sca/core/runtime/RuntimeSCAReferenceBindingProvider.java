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

package org.apache.tuscany.sca.core.runtime;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider2;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeSCAReferenceBindingProvider implements ReferenceBindingProvider2 {

    private RuntimeComponentReference reference;
    private SCABinding binding;
    private boolean started = false;

    public RuntimeSCAReferenceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentReference reference,
                                              SCABinding binding) {
        this.reference = reference;
        this.binding = binding;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

    public Invoker createInvoker(Operation operation) {
        return new RuntimeSCABindingInvoker();
    }

    @Deprecated
    public Invoker createInvoker(Operation operation, boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        } else {
            return createInvoker(operation);
        }
    }

    public void start() {
        if (started) {
            return;
        } else {
            started = true;
        }
        for (RuntimeWire sourceWire : reference.getRuntimeWires()) {
            if (sourceWire.getSource().getBinding() == binding) {
                EndpointReference target = sourceWire.getTarget();
                if (target != null) {
                    RuntimeComponentService service = (RuntimeComponentService)target.getContract();
                    if (service != null) { // not a callback wire
                        SCABinding scaBinding = service.getBinding(SCABinding.class);
                        RuntimeWire targetWire = service.getRuntimeWire(scaBinding);
                        boolean dynamicService = service.getInterfaceContract().getInterface().isDynamic();
                        for (InvocationChain sourceChain : sourceWire.getInvocationChains()) {
                            InvocationChain targetChain =
                                service.getInvocationChain(scaBinding, sourceChain.getTargetOperation());
                            if (targetChain == null && dynamicService) {
                                targetChain = targetWire.getInvocationChains().get(0);
                            }
                            if (targetChain != null) {
                                ((Interceptor)sourceChain.getTailInvoker()).setNext(targetChain.getHeadInvoker());
                                if (!dynamicService) {
                                    // FIXME: [rfeng] Change the target operation will impact the interceptors
                                    sourceChain.setTargetOperation(targetChain.getTargetOperation());
                                }
                            } else {
                                throw new RuntimeException("Incompatible operations for source and target wires");
                            }
                        }
                        if (!dynamicService) {
                            sourceWire.getTarget().setInterfaceContract(targetWire.getTarget().getInterfaceContract());
                        }
                    }
                }
            }
        }
    }

    public void stop() {
    }

}
