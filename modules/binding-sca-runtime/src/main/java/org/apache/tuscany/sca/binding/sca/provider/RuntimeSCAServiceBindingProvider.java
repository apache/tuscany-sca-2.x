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

import org.apache.tuscany.sca.binding.local.LocalSCABindingInvocationInterceptor;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.OptimisingBindingProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The sca service binding provider mediates between the twin requirements of
 * local sca bindings and remote sca bindings. In the local case is does
 * very little. When the sca binding model is set as being remote this binding will
 * try and create a remote service endpoint for remote references to connect to
 *
 * @version $Rev$ $Date$
 */
public class RuntimeSCAServiceBindingProvider implements EndpointAsyncProvider, OptimisingBindingProvider {
    private RuntimeEndpoint endpoint;
    private SCABindingMapper mapper;

    private ServiceBindingProvider delegatingBindingProvider;

    public RuntimeSCAServiceBindingProvider(SCABindingMapper scaBindingMapper, RuntimeEndpoint endpoint) {
        this.endpoint = endpoint;
        this.mapper = scaBindingMapper;
        getDelegatingProvider();
    }

    // if there is potentially a wire to this service that crosses the node boundary
    // then we need to create a remote endpoint
    private ServiceBindingProvider getDelegatingProvider() {
    	if(delegatingBindingProvider == null) {
			delegatingBindingProvider = new DelegatingSCAServiceBindingProvider(endpoint, mapper);
    	}
        return delegatingBindingProvider;
    }

    /*
    protected boolean isDistributed(ExtensionPointRegistry extensionPoints, Endpoint endpoint) {
        // find if the node config is for distributed endpoints
        // TODO: temp, need a much better way to do this
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionPoints);
        Collection<DomainRegistry> eprs = domainRegistryFactory.getEndpointRegistries();
        if (eprs.size() > 0) {
            String eprName = eprs.iterator().next().getClass().getName();
            return !eprName.equals("org.apache.tuscany.sca.core.assembly.impl.DomainRegistryImpl");
        }
        return false;
    }
    */

    public InterfaceContract getBindingInterfaceContract() {
        return getDelegatingProvider().getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return getDelegatingProvider().supportsOneWayInvocation();
    }

    public void start() {
        getDelegatingProvider().start();
    }

    public void stop() {
        endpoint.getBinding().setURI(null);
        getDelegatingProvider().stop();
    }

    public void configure() {
        if (getDelegatingProvider() instanceof EndpointProvider) {
            ((EndpointProvider)getDelegatingProvider()).configure();
        }
    }
    
    public boolean supportsNativeAsync() {
        return true;
    }
    
    public InvokerAsyncResponse createAsyncResponseInvoker() {
        return ((EndpointAsyncProvider)getDelegatingProvider()).createAsyncResponseInvoker();
    }

    /**
     * Handles the optimization for the service side chain, which provides a mechanism for direct local
     * invocation of the service in cases where the component reference is in the same JVM as the 
     * component service.  Effectively, this means skipping any Remote Binding listener and its associated
     * binding chain and data binding processors.
     * 
     * This means inserting a SCABindingLocalInvocationInterceptor into the chains for the Endpoint,
     * which is placed immediately before the Policy processors (and after any Databinding processors)
     */
	public void optimiseBinding(RuntimeEndpoint ep) {
		// To optimise, place an SCA binding Local Invocation interceptor at the start of the POLICY phase
		// of the service chain...
		for (InvocationChain chain : ep.getInvocationChains()) {
			chain.addHeadInterceptor( Phase.SERVICE_POLICY, new LocalSCABindingInvocationInterceptor() );
		} // end for
			
	} // end method optimiseBinding
	
    public RuntimeEndpoint getDelegateEndpoint(){
        return ((DelegatingSCAServiceBindingProvider)delegatingBindingProvider).getDelegateEndpoint();
    }
    
} // end class RuntimeSCAServiceBinding
