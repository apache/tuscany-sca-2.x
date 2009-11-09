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

package org.apache.tuscany.sca.binding.sca.provider;

import java.net.URI;
import java.util.Collection;

import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
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
public class RuntimeSCAServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private SCABinding binding;
    
    
    private BindingProviderFactory<DistributedSCABinding> distributedProviderFactory;
    private ServiceBindingProvider distributedProvider;
    private DistributedSCABinding distributedBinding;
    

    public RuntimeSCAServiceBindingProvider(ExtensionPointRegistry extensionPoints, Endpoint endpoint) {
        this.component = (RuntimeComponent)endpoint.getComponent();
        this.service = (RuntimeComponentService)endpoint.getService();
        this.binding = (SCABinding)endpoint.getBinding();
        
        // if there is potentially a wire to this service that crosses the node boundary
        // then we need to create a remote endpoint 
        if (service.getInterfaceContract().getInterface().isRemotable()) {

            // look to see if a distributed SCA binding implementation has
            // been included on the classpath. This will be needed by the
            // provider itself to do it's thing
            ProviderFactoryExtensionPoint factoryExtensionPoint =
                extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
            distributedProviderFactory =
                (BindingProviderFactory<DistributedSCABinding>)factoryExtensionPoint
                    .getProviderFactory(DistributedSCABinding.class);

            if (isDistributed(extensionPoints, endpoint)) {

                SCABindingFactory scaBindingFactory =
                    extensionPoints.getExtensionPoint(FactoryExtensionPoint.class).getFactory(SCABindingFactory.class);

                //  create a nested provider to handle the remote case
                distributedBinding = scaBindingFactory.createDistributedSCABinding();
                distributedBinding.setSCABinding(binding);
                
                // create a copy of the endpoint and change the binding
                Endpoint ep = null;
                try {
                    ep = (Endpoint)endpoint.clone();
                } catch (Exception ex) {
                    // we know we can clone endpoint 
                }
                ep.setBinding(distributedBinding);

                distributedProvider =
                    distributedProviderFactory.createServiceBindingProvider(ep);
            } 
        }
    }
    
    protected boolean isDistributed(ExtensionPointRegistry extensionPoints, Endpoint endpoint) {
        // find if the node config is for distributed endpoints
        // TODO: temp, need a much better way to do this
        if (distributedProviderFactory != null) {
            UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
            DomainRegistryFactory domainRegistryFactory = utilities.getUtility(DomainRegistryFactory.class);
            Collection<EndpointRegistry> eprs = domainRegistryFactory.getEndpointRegistries();
            if (eprs.size() > 0) {
                String eprName = eprs.iterator().next().getClass().getName();
                return !eprName.equals("org.apache.tuscany.sca.core.assembly.impl.EndpointRegistryImpl");
            }
        }
        return false;
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (distributedProvider != null) {
            return distributedProvider.getBindingInterfaceContract();
        } else {
            if (service.getService() != null) {
                return service.getService().getInterfaceContract();
            } else {
                return service.getInterfaceContract();
            }
        }
    }

    public boolean supportsOneWayInvocation() {
        return false;
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

        if (distributedBinding != null) {
            // reset the binding URI to null so that if the composite containing the component
            // with the service/binding is restarted the binding will have the correct URI set
            SCABinding scaBinding = distributedBinding.getSCABinding();
            try {
                URI tempURI = new URI(scaBinding.getURI());
                if (!tempURI.isAbsolute()){
                    scaBinding.setURI(null);
                }
            } catch (Exception ex){
                scaBinding.setURI(null);
            }
        }

    }

}
