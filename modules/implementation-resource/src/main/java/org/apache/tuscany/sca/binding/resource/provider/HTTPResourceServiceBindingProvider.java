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

package org.apache.tuscany.sca.binding.resource.provider;

import java.net.URL;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.binding.resource.HTTPResourceBinding;
import org.apache.tuscany.sca.http.DefaultResourceServlet;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the Echo binding provider.
 */
public class HTTPResourceServiceBindingProvider implements ServiceBindingProvider {
    
    private RuntimeComponent component;
    private RuntimeComponentService service;  
    private HTTPResourceBinding binding;
    private ServletHost servletHost;
    private String uri; 
    
    public HTTPResourceServiceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentService service,
                                              HTTPResourceBinding binding,
                                              ServletHost servletHost) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;

        if (binding.getURI() != null) {
            uri = binding.getURI();
            if (!uri.endsWith("/")) {
                uri += "/";
            }
            if (!uri.endsWith("*")) {
                uri += "*";
            }
        } else {
            uri = "http://localhost:8080/" + this.component.getName() + "/*";
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {

        // Get the target component implementation, for now we are assuming
        // that it's an implementation.resource
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        Implementation implementation = wire.getTarget().getComponent().getImplementation();
        ResourceImplementation resourceImplementation = (ResourceImplementation)implementation;
        
        // Get the resource location URL
        URL locationURL = resourceImplementation.getLocationURL();
        
        // Register the default resource servlet with the servlet host
        DefaultResourceServlet resourceServlet = new DefaultResourceServlet(locationURL.toString());
        servletHost.addServletMapping(uri, resourceServlet);
        
    }

    public void stop() {
        
        // Unregister from the hosting server
        servletHost.removeServletMapping(uri);
    }

}
