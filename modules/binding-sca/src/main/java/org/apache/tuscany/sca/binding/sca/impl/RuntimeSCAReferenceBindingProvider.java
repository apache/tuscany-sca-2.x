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

package org.apache.tuscany.sca.binding.sca.impl;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.management.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
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

    private ExtensionPointRegistry extensionPoints;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private boolean started = false;
    
    private ReferenceBindingProvider2 distributedProvider = null;

    public RuntimeSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                              RuntimeComponent component,
                                              RuntimeComponentReference reference,
                                              SCABinding binding) 
      throws BindingNotDistributedException {
        this.reference = reference;
        this.binding = binding;

        // look to see if a distributed SCA binding implementation has
        // been included on the classpath. This will be needed by the 
        // provider itself to do it's thing
        ProviderFactoryExtensionPoint factoryExtensionPoint = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        BindingProviderFactory<DistributedSCABinding> distributedProviderFactory = (BindingProviderFactory<DistributedSCABinding>)
            factoryExtensionPoint.getProviderFactory(DistributedSCABinding.class);

        // if there is a wire to this service that crosses the node boundary 
        if (((WireableBinding)binding).getIsRemote() == true) {
            // Make sure that we have a distributed sca binding and 
            // that the interface is remoteable
            
            if ((distributedProviderFactory != null) && 
                (((SCABindingImpl)binding).getDistributedDomain() != null) &&
                (reference.getInterfaceContract().getInterface().isRemotable())){                  
                DistributedSCABinding distributedBinding = new DistributedSCABindingImpl();
                distributedBinding.setSCABinging(binding);
                
                distributedProvider = (ReferenceBindingProvider2)
                    distributedProviderFactory.createReferenceBindingProvider(component, reference, distributedBinding);
               
            } else {
                throw new BindingNotDistributedException();
            }
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (distributedProvider != null){
            return distributedProvider.getBindingInterfaceContract();
        } else {
            return reference.getInterfaceContract();
        }        
    }

    public boolean supportsAsyncOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(Operation operation) {
        if (distributedProvider != null){
            return distributedProvider.createInvoker(operation);
        } else {
            return new RuntimeSCABindingInvoker(); 
        }
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
                        if (!dynamicService) {
                            sourceWire.getTarget().setInterfaceContract(targetWire.getTarget().getInterfaceContract());
                        }
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
                    }
                }
            }
        }
        
        if (distributedProvider != null){
            distributedProvider.start();
        }
    }

    public void stop() {
        if (distributedProvider != null){
            distributedProvider.stop();
        }
    }

}
