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
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The sca service binding provider mediates between the twin requirements of 
 * local sca bindings and remote sca bindings. In the local case is does 
 * very little. When the sca binding model is set as being remote this binding will 
 * try and create a remote service endpoint for remote references to connect to 
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeSCAServiceBindingProvider implements ServiceBindingProvider2 {

    private ExtensionPointRegistry extensionPoints;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private SCABinding binding;
    
    private BindingProviderFactory<DistributedSCABinding> distributedProviderFactory;
    private ServiceBindingProvider2 distributedProvider;
    private DistributedSCABinding distributedBinding;
    
    private boolean started = false;

    public RuntimeSCAServiceBindingProvider(ExtensionPointRegistry extensionPoints,
                                            RuntimeComponent component,
                                            RuntimeComponentService service,
                                            SCABinding binding) {
        this.extensionPoints = extensionPoints;
        this.component = component;
        this.service = service;
        this.binding = binding;
        
        // if there is potentially a wire to this service that crosses the node boundary 
        if (((WireableBinding)binding).isRemote()) {        
            // look to see if a distributed SCA binding implementation has
            // been included on the classpath. This will be needed by the 
            // provider itself to do it's thing
            ProviderFactoryExtensionPoint factoryExtensionPoint =
                extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
            distributedProviderFactory =
                (BindingProviderFactory<DistributedSCABinding>)factoryExtensionPoint
                    .getProviderFactory(DistributedSCABinding.class);      
            
            // Check the things that will generally be required to set up a 
            // distributed sca domain reference provider. I.e. make sure that we have a
            // - distributed implementation of the sca binding available
            // - distributed domain in which to look for remote endpoints 
            // - remotable interface on the service
            if (distributedProviderFactory == null) {
                throw new IllegalStateException("No distributed SCA binding available for component: "+
                                                component.getName() +
                                                " and service: " + 
                                                service.getName());
            }
            
            if (((SCABindingImpl)binding).getDistributedDomain() == null) {
                throw new IllegalStateException("No distributed domain available for component: "+
                                                component.getName() +
                                                " and service: " + 
                                                service.getName());
            }
            
            if (!service.getInterfaceContract().getInterface().isRemotable()) {
                throw new IllegalStateException("Reference interface not remoteable for component: "+
                                                component.getName() +
                                                " and service: " + 
                                                service.getName());
            }           
            
            //  create a nested provider to handle the remote case
            distributedBinding = new DistributedSCABindingImpl();
            distributedBinding.setSCABinging(binding);
            
            distributedProvider = (ServiceBindingProvider2)
                                  distributedProviderFactory.createServiceBindingProvider(component, service, distributedBinding);
            
            // get the url out of the binding and send it to the registry if
            // a distributed domain is configured
            DistributedSCADomain distributedDomain = ((SCABindingImpl)binding).getDistributedDomain();
            
            ServiceDiscovery serviceDiscovery = distributedDomain.getServiceDiscovery();
            
            // register endpoint twice to take account the formats 
            //  ComponentName
            //  ComponentName/ServiceName
            // TODO - Can't we get this from somewhere? What happens with nested components. 
            serviceDiscovery.registerServiceEndpoint(distributedDomain.getDomainName(), 
                                                     distributedDomain.getNodeName(), 
                                                     component.getName(), 
                                                     SCABinding.class.getName(), 
                                                     binding.getURI());
            serviceDiscovery.registerServiceEndpoint(distributedDomain.getDomainName(), 
                                                     distributedDomain.getNodeName(), 
                                                     component.getName() + "/" + service.getName(), 
                                                     SCABinding.class.getName(), 
                                                     binding.getURI());
        } 
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (distributedProvider != null){
            return distributedProvider.getBindingInterfaceContract();
        } else {
            return service.getInterfaceContract();
        }
    }

    public boolean supportsAsyncOneWayInvocation() {
        return false;
    }

    public Invoker createCallbackInvoker(Operation operation) {
        throw new UnsupportedOperationException();
    }

    public void start() {

        if (distributedProvider != null) {
            distributedProvider.start();
        }
    }

    public void stop() {
        if (distributedProvider != null) {
            distributedProvider.stop();
        }
    }

}
