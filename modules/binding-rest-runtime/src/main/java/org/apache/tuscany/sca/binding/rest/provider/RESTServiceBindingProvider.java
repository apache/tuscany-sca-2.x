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

package org.apache.tuscany.sca.binding.rest.provider;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the REST Binding Provider for Services
 * 
 * @version $Rev: 683451 $ $Date: 2008-08-07 00:59:24 +0100 (Thu, 07 Aug 2008) $
 */
public class RESTServiceBindingProvider implements ServiceBindingProvider {
    
    
        
    
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private InterfaceContract serviceContract;
    private RESTBinding binding;
    private ServletHost servletHost;
    private List<String> servletMappings = new ArrayList<String>();

    public RESTServiceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentService service,
                                         RESTBinding binding,
                                         ServletHost servletHost) {
    	this.component = component;
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
        
        //clone the service contract to avoid databinding issues
        try {
            this.serviceContract = (InterfaceContract)service.getInterfaceContract().clone();
        } catch(CloneNotSupportedException e) {
            this.serviceContract = service.getInterfaceContract();
        }

    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }
    
    public void start() {

        // Determine the service business interface
        Class<?> serviceInterface = getTargetJavaClass(serviceContract.getInterface());

        // Create a Java proxy to the target service
        //ProxyFactory proxyFactory = new JDKProxyFactory();
        Object proxy = component.getComponentContext().createSelfReference(serviceInterface, service).getService();
        //Object proxy = proxyFactory.createProxy(serviceInterface, service.getRuntimeWire(binding));

        // Create and register a servlet for this service
        RESTServiceServlet serviceServlet =
            new RESTServiceServlet(binding, service, serviceContract, serviceInterface, proxy);
			
		//Prateek: to add 'rest/' before the name of the service so that it could be added to url-pattern for the filter
//		String tempURI = binding.getURI();
//		binding.setURI("rest" + tempURI);
		
        String mapping = binding.getURI();
        if (!mapping.endsWith("/")) {
            mapping += "/";
        }
        if (!mapping.endsWith("*")) {
        	mapping += "*";
        }

        servletHost.addServletMapping(mapping, serviceServlet);
        servletMappings.add(mapping);
        servletHost.addServletMapping(binding.getURI(), serviceServlet);
        servletMappings.add(binding.getURI());
        
        //Prateek: add /rest for reverse-compatibility
        servletHost.addServletMapping("/rest" + mapping, serviceServlet);
        servletMappings.add("/rest" + mapping);
        servletHost.addServletMapping("/rest" + binding.getURI(), serviceServlet);
        servletMappings.add("/rest" + binding.getURI());
        
        // Save the actual binding URI
        binding.setURI(servletHost.getURLMapping(binding.getURI()).toString());

        // Register service to scaDomain.js
        int port;
        URI uri = URI.create(binding.getURI());
        port = uri.getPort();
        if (port == -1) {
            port = servletHost.getDefaultPort();
        }
     }

    public void stop() {

        // Remove the servlet mappings we've added
        for (String mapping: servletMappings) {
            servletHost.removeServletMapping(mapping);
            
            //Prateek: for /rest
            servletHost.removeServletMapping("/rest" + mapping);
        }

    }

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out how to generate Java
        // Interface in cases where the target is not a Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }
    


}
