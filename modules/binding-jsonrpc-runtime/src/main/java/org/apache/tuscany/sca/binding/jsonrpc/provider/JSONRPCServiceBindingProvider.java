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

package org.apache.tuscany.sca.binding.jsonrpc.provider;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;


/**
 * Implementation of the JSONRPC Binding Provider for Services
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCServiceBindingProvider implements ServiceBindingProvider {
    private MessageFactory messageFactory;
    
    private RuntimeEndpoint endpoint;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private InterfaceContract serviceContract;
    private JSONRPCBinding binding;
    private ServletHost servletHost;
    private List<String> servletMappings = new ArrayList<String>();

    public JSONRPCServiceBindingProvider(RuntimeEndpoint endpoint,
                                         MessageFactory messageFactory,
                                         ServletHost servletHost) {
        this.endpoint = endpoint;
        this.component = (RuntimeComponent)endpoint.getComponent();
        this.service = (RuntimeComponentService)endpoint.getService();
        this.binding = (JSONRPCBinding) endpoint.getBinding();
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;
        
        //clone the service contract to avoid databinding issues
        try {
            this.serviceContract = (InterfaceContract)service.getInterfaceContract().clone();
        } catch(CloneNotSupportedException e) {
            this.serviceContract = service.getInterfaceContract();
        }
        
        JSONRPCDatabindingHelper.setDataBinding(serviceContract.getInterface());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return serviceContract;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }
    
    public void start() {
        // Set default databinding to json
        // replaced by JSONRPCDatabindingHelper.setDataBinding(serviceContract.getInterface()); above
        //serviceContract.getInterface().resetDataBinding(JSONDataBinding.NAME);

        // Determine the service business interface
        Class<?> serviceInterface = getTargetJavaClass(serviceContract.getInterface());

        // Create a Java proxy to the target service
        Object proxy = component.getComponentContext().getServiceReference(serviceInterface, endpoint).getService();

        // Create and register a Servlet for this service
        JSONRPCServiceServlet serviceServlet =
            new JSONRPCServiceServlet(messageFactory, endpoint, serviceInterface, proxy);
        String mapping = registerServlet(serviceServlet);
        servletMappings.add(mapping);
    }
    
    public String registerServlet(Servlet servlet) {
        // Create our HTTP service listener Servlet and register it with the
        // Servlet host
        String servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }

        String mappedURI = servletHost.addServletMapping(servletMapping, servlet);
        String deployedURI = mappedURI;
        if (deployedURI.endsWith("*")) {
            deployedURI = deployedURI.substring(0, deployedURI.length() - 1);
        }
        binding.setURI(deployedURI);
        return mappedURI;
    }

    public void stop() {

        // Remove the Servlet mappings we've added
        for (String mapping: servletMappings) {
            servletHost.removeServletMapping(mapping);
        }

    }

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out how to generate Java
        // Interface in cases where the target is not a Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }
}
