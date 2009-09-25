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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.databinding.javabeans.SimpleJavaDataBinding;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the JSONRPC Binding Provider for Services
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCServiceBindingProvider implements ServiceBindingProvider {
    
    // Path to the scaDomain.js script 
    // Note: this is the same as the Ajax binding to keep the client code
    //       the same for clients using either the ajax or jsonrpc binding
    private static final String SCA_DOMAIN_SCRIPT = "/SCADomain/scaDomain.js";
    
    private MessageFactory messageFactory;
    
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private InterfaceContract serviceContract;
    private JSONRPCBinding binding;
    private ServletHost servletHost;
    private List<String> servletMappings = new ArrayList<String>();
    private String domainScriptMapping;

    public JSONRPCServiceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentService service,
                                         JSONRPCBinding binding,
                                         MessageFactory messageFactory,
                                         ServletHost servletHost) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;
        
        //clone the service contract to avoid databinding issues
        try {
            this.serviceContract = (InterfaceContract)service.getInterfaceContract().clone();
        } catch(CloneNotSupportedException e) {
            this.serviceContract = service.getInterfaceContract();
        }
        
        setDataBinding(serviceContract.getInterface());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }
    
    public void start() {
        // Set default databinding to json
        serviceContract.getInterface().resetDataBinding(JSONDataBinding.NAME);

        // Determine the service business interface
        Class<?> serviceInterface = getTargetJavaClass(serviceContract.getInterface());

        // Create a Java proxy to the target service
		Object proxy = component.getComponentContext().createSelfReference(serviceInterface, service).getService();

        // Create and register a Servlet for this service
        JSONRPCServiceServlet serviceServlet =
            new JSONRPCServiceServlet(messageFactory, binding, service, serviceContract, serviceInterface, proxy);
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
        
        // Register service to scaDomain.js
        int port;
        URI uri = URI.create(servletHost.getURLMapping(binding.getURI()).toString());
        port = uri.getPort();
        if (port == -1) {
            port = servletHost.getDefaultPort();
        }
        
        // get the ScaDomainScriptServlet, if it doesn't yet exist create one
        // this uses removeServletMapping / addServletMapping as there is no getServletMapping facility
        domainScriptMapping = URI.create("http://localhost:" + port + SCA_DOMAIN_SCRIPT).toString();
        ScaDomainScriptServlet scaDomainServlet =
            (ScaDomainScriptServlet)servletHost.getServletMapping(domainScriptMapping);
        if (scaDomainServlet == null) {
            scaDomainServlet = new ScaDomainScriptServlet();
            servletHost.addServletMapping(domainScriptMapping, scaDomainServlet);
        }

        // Add this service to the SCA Domain Script Servlet
        scaDomainServlet.addService(binding.getName());
    }

    public void stop() {

        // Remove the Servlet mappings we've added
        for (String mapping: servletMappings) {
            servletHost.removeServletMapping(mapping);
        }

        // Unregister the service from the SCA Domain Script Servlet
        ScaDomainScriptServlet scaDomainServlet = (ScaDomainScriptServlet) servletHost.getServletMapping(domainScriptMapping);
        if (scaDomainServlet != null) {
            scaDomainServlet.removeService(binding.getName());

            // Remove the Servlet if there's no more registered services
            if (scaDomainServlet.getServiceNames().isEmpty()) {
                servletHost.removeServletMapping(domainScriptMapping);
            }
        }

    }

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out how to generate Java
        // Interface in cases where the target is not a Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }
    
    private void setDataBinding(Interface interfaze) {
        List<Operation> operations = interfaze.getOperations();
        for (Operation operation : operations) {
            operation.setDataBinding(JSONDataBinding.NAME);
            DataType<List<DataType>> inputType = operation.getInputType();
            if (inputType != null) {
                List<DataType> logical = inputType.getLogical();
                for (DataType inArg : logical) {
                    if (!SimpleJavaDataBinding.NAME.equals(inArg.getDataBinding())) {
                        inArg.setDataBinding(JSONDataBinding.NAME);
                    } 
                }
            }
            DataType outputType = operation.getOutputType();
            if (outputType != null) {
                if (!SimpleJavaDataBinding.NAME.equals(outputType.getDataBinding())) {
                    outputType.setDataBinding(JSONDataBinding.NAME);
                }
            }
        }
    }


}
