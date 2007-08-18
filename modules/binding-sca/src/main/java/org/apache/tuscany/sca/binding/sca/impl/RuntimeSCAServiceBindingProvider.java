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
            // been included on the classpath and store it if it has
            ProviderFactoryExtensionPoint factoryExtensionPoint = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
            distributedProviderFactory = (BindingProviderFactory<DistributedSCABinding>)
                                         factoryExtensionPoint.getProviderFactory(DistributedSCABinding.class);
            
            // if a distributed sca binding is available then create a nested provider to handle the remote case
            if ((distributedProviderFactory != null) &&
                (((SCABindingImpl)binding).getDistributedDomain() != null)){
                distributedBinding = new DistributedSCABindingImpl();
                distributedBinding.setSCABinging(binding);
                
                distributedProvider = (ServiceBindingProvider2)
                                      distributedProviderFactory.createServiceBindingProvider(component, service, distributedBinding);
                
                // get the url out of the binding and send it to the registry if
                // a distributed domain is configured
                DistributedSCADomain distributedDomain = ((SCABindingImpl)binding).getDistributedDomain();
                
                if (distributedDomain != null){
                    ServiceDiscovery serviceDiscovery = distributedDomain.getServiceDiscovery();
                    
                    // register endpoint twice to take account the formats 
                    //  ComponentName
                    //  ComponentName/ServiceName
                    // TODO - Can;t we get this from somewhere? What happens with nested components. 
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
