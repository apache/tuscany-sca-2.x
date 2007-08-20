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
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
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
 * The sca reference binding provider mediates between the twin requirements of 
 * local sca bindings and remote sca bindings. In the local case is does 
 * very little. When the sca binding model is set as being remote (because a 
 * reference target can't be resolved in the current model) this binding will 
 * try and create a remote connection to it
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeSCAReferenceBindingProvider implements ReferenceBindingProvider2 {

    private RuntimeComponentReference reference;
    private SCABinding binding;
    private boolean started = false;

    private ReferenceBindingProvider2 distributedProvider = null;

    public RuntimeSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                              RuntimeComponent component,
                                              RuntimeComponentReference reference,
                                              SCABinding binding) {
        this.reference = reference;
        this.binding = binding;

        // if there is a wire from this reference that crosses the node boundary 
        // then we need to create a remote binding
        if (((WireableBinding)binding).isRemote() == true) {
            
            // look to see if a distributed SCA binding implementation has
            // been included on the classpath. This will be needed by the 
            // provider itself to do it's thing
            ProviderFactoryExtensionPoint factoryExtensionPoint =
                extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
            BindingProviderFactory<DistributedSCABinding> distributedProviderFactory =
                (BindingProviderFactory<DistributedSCABinding>)factoryExtensionPoint
                    .getProviderFactory(DistributedSCABinding.class);
            
            // Check the things that will generally be required to set up a 
            // distributed sca domain reference provider. I.e. make sure that we have a
            // - distributed implementation of the sca binding available
            // - distributed domain in which to look for remote endpoints 
            // - remotable interface on the target service
            if (distributedProviderFactory == null) {
                throw new IllegalStateException("No distributed SCA binding available for component: "+
                                                component.getName() +
                                                " and reference: " + 
                                                reference.getName());
            }
            
            if (((SCABindingImpl)binding).getDistributedDomain() == null) {
                throw new IllegalStateException("No distributed domain available for component: "+
                        component.getName() +
                        " and reference: " + 
                        reference.getName());
            }
            
            if (!reference.getInterfaceContract().getInterface().isRemotable()) {
                throw new IllegalStateException("Reference interface not remoteable for component: "+
                        component.getName() +
                        " and reference: " + 
                        reference.getName());
            }

            DistributedSCABinding distributedBinding = new DistributedSCABindingImpl();
            distributedBinding.setSCABinging(binding);

            distributedProvider =
                (ReferenceBindingProvider2)distributedProviderFactory
                    .createReferenceBindingProvider(component, reference, distributedBinding);

        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (distributedProvider != null) {
            return distributedProvider.getBindingInterfaceContract();
        } else {
            return reference.getInterfaceContract();
        }
    }

    public boolean supportsAsyncOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(Operation operation) {
        if (distributedProvider != null) {
            return distributedProvider.createInvoker(operation);
        } else {
            RuntimeWire wire = reference.getRuntimeWire(binding);
            Invoker invoker = getInvoker(wire, operation);
            if (invoker == null) {
                throw new IllegalStateException("No service invoker");
            }
            return new RuntimeSCABindingInvoker(invoker);
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

//        ComponentService service = ((WireableBinding)binding).getTargetComponentService();
//        if (service != null) {
//            RuntimeWire wire = reference.getRuntimeWire(binding);
//            InterfaceContract interfaceContract = service.getInterfaceContract();
//            boolean dynamicService = interfaceContract.getInterface().isDynamic();
//            if (!dynamicService) {
//                wire.getTarget().setInterfaceContract(interfaceContract);
//            }
//        }

        if (distributedProvider != null) {
            distributedProvider.start();
        }
    }

    /**
     * @param wire
     */
    private Invoker getInvoker(RuntimeWire wire, Operation operation) {
        EndpointReference target = wire.getTarget();
        if (target != null) {
            RuntimeComponentService service = (RuntimeComponentService)target.getContract();
            if (service != null) { // not a callback wire
                SCABinding scaBinding = service.getBinding(SCABinding.class);
                return service.getInvoker(scaBinding, wire.getSource().getInterfaceContract(), operation);
            }
        }
        return null;
    }

    public void stop() {
        if (distributedProvider != null) {
            distributedProvider.stop();
        }
    }

}
