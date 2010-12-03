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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The sca service binding provider mediates between the twin requirements of
 * local sca bindings and remote sca bindings. In the local case is does
 * very little. When the sca binding model is set as being remote this binding will
 * try and create a remote service endpoint for remote references to connect to
 *
 * @version $Rev$ $Date$
 */
public class RuntimeSCAServiceBindingProvider implements EndpointAsyncProvider {
    private RuntimeEndpoint endpoint;
    private RuntimeComponentService service;

    private ServiceBindingProvider distributedProvider;
    private SCABindingMapper scaBindingMapper;

    public RuntimeSCAServiceBindingProvider(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
        this.endpoint = endpoint;
        this.service = (RuntimeComponentService)endpoint.getService();
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.scaBindingMapper = utilities.getUtility(SCABindingMapper.class);

        // if there is potentially a wire to this service that crosses the node boundary
        // then we need to create a remote endpoint 
        if (service.getInterfaceContract().getInterface().isRemotable()) {

            if (scaBindingMapper.isRemotable()) {
                distributedProvider = new DelegatingSCAServiceBindingProvider(endpoint, scaBindingMapper);
            }
        }
    }

    /*
    protected boolean isDistributed(ExtensionPointRegistry extensionPoints, Endpoint endpoint) {
        // find if the node config is for distributed endpoints
        // TODO: temp, need a much better way to do this
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionPoints);
        Collection<EndpointRegistry> eprs = domainRegistryFactory.getEndpointRegistries();
        if (eprs.size() > 0) {
            String eprName = eprs.iterator().next().getClass().getName();
            return !eprName.equals("org.apache.tuscany.sca.core.assembly.impl.EndpointRegistryImpl");
        }
        return false;
    }
    */

    public InterfaceContract getBindingInterfaceContract() {
        if (distributedProvider != null) {
            return distributedProvider.getBindingInterfaceContract();
        } else {
            return endpoint.getComponentTypeServiceInterfaceContract();
        }
    }

    public boolean supportsOneWayInvocation() {
        if (distributedProvider != null) {
            return distributedProvider.supportsOneWayInvocation();
        }
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
    }

    public void configure() {
        // TODO Auto-generated method stub   
    }
    
    public boolean supportsNativeAsync() {
        return true;
    }
    
    public Invoker createAsyncResponseInvoker(Operation operation) {
        return new SCABindingAsyncResponseInvoker(null, null);
    }
    
}
