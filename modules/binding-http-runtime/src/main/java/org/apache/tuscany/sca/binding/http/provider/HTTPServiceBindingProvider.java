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

package org.apache.tuscany.sca.binding.http.provider;

import javax.servlet.Servlet;

import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.binding.http.operationselector.HTTPDefaultOperationSelector;
import org.apache.tuscany.sca.binding.http.wireformat.HTTPDefaultWireFormat;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Implementation of an HTTP binding provider.
 *
 * @version $Rev$ $Date$
 */
public class HTTPServiceBindingProvider implements EndpointProvider {
    private ExtensionPointRegistry extensionPoints;
    
    private RuntimeEndpoint endpoint;
    private HTTPBinding binding;
    private MessageFactory messageFactory;
    private OperationSelectorProvider osProvider;
    private WireFormatProvider wfProvider;
    private ServletHost servletHost;
    private String servletMapping;
    private InterfaceContract interfaceContract;
   
    public HTTPServiceBindingProvider(RuntimeEndpoint endpoint,
                                      ExtensionPointRegistry extensionPoints,
                                      MessageFactory messageFactory,
                                      ServletHost servletHost) {
    	
    	this.endpoint = endpoint;
        this.binding = (HTTPBinding)endpoint.getBinding();
        
        this.extensionPoints = extensionPoints;
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;
        
        if (binding.getOperationSelector() == null) {
            binding.setOperationSelector(new HTTPDefaultOperationSelector());
        }
        if (binding.getRequestWireFormat() == null) {
            binding.setRequestWireFormat(new HTTPDefaultWireFormat());
        }
        if (binding.getResponseWireFormat() == null) {
            binding.setResponseWireFormat(new HTTPDefaultWireFormat());
        }
        
        ProviderFactoryExtensionPoint  providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        
        if (binding.getOperationSelector() != null) {
            // Configure the interceptors for operation selection
            OperationSelectorProviderFactory osProviderFactory = (OperationSelectorProviderFactory) providerFactories.getProviderFactory(binding.getOperationSelector().getClass());
            if (osProviderFactory != null) {
                this.osProvider = osProviderFactory.createServiceOperationSelectorProvider(endpoint);
            }            
        }
        
        if (binding.getRequestWireFormat() != null && binding.getResponseWireFormat() != null) {
            // Configure the interceptors for wire format
            WireFormatProviderFactory wfProviderFactory = (WireFormatProviderFactory) providerFactories.getProviderFactory(binding.getRequestWireFormat().getClass());
            if (wfProviderFactory != null) {
                this.wfProvider = wfProviderFactory.createServiceWireFormatProvider(endpoint);
            }            
        }

        //clone the service contract to avoid databinding issues
        try {
            interfaceContract = (InterfaceContract)endpoint.getComponentServiceInterfaceContract().clone();
            
            // configure data binding
            if (this.wfProvider != null) {
                wfProvider.configureWireFormatInterfaceContract(interfaceContract);
            }
        } catch(CloneNotSupportedException e) {
            // shouldn't happen
        }
        
        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
    }

    public void start() {

        /**
         * Consider three scenarios here :
         *  - Default servlet using service level operation
         *  - Default servlet using GET, PUT, POST, DELETE operations mapped to interface names with same name
         *  - RPC over HTTP like : http://localhost:8080/HelloworldComponent/Helloworld/sayHello?name=Petra
         */
        if (binding.getOperationSelector() == null || binding.getRequestWireFormat() == null || binding.getResponseWireFormat() == null) {
            throw new IllegalStateException("Binding operation selector and/or wire formats not properly setup.");
        }
        
        Servlet servlet = new HTTPBindingServiceServlet(endpoint, messageFactory);
        
        // Create our HTTP service listener Servlet and register it with the
        // Servlet host
        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
        
        String deployedURI = servletHost.addServletMapping(servletMapping, servlet);
        endpoint.setDeployedURI(deployedURI);
    }
    
    public void stop() {        
        servletHost.removeServletMapping(servletMapping);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return interfaceContract;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }
    
    /**
     * Add specific http interceptor to invocation chain
     */
    public void configure() {
        InvocationChain bindingChain = endpoint.getBindingInvocationChain();

        if(osProvider != null) {
            bindingChain.addInterceptor(Phase.SERVICE_BINDING_OPERATION_SELECTOR, osProvider.createInterceptor());    
        }

        if (wfProvider != null) {
            bindingChain.addInterceptor(Phase.SERVICE_BINDING_WIREFORMAT, wfProvider.createInterceptor());
        }

    }

}
