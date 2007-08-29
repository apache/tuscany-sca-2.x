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

package org.apache.tuscany.sca.binding.jsonrpc;

import java.net.URI;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.invocation.JDKProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.extension.helper.ComponentLifecycle;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the JSONRPC binding provider.
 * 
 * There are multiple servlets used to support the JSON-RPC binidng.
 * One servlet to handle requests for the scaDomain script and seperate
 * servlets for each SCA <service> which uses <binding.jsonrpc>. 
 */
public class JSONRPCService implements ComponentLifecycle {

    private RuntimeComponentService service;
    private ServletHost servletHost;
    private Binding binding;

    public static final String SERVICE_PREFIX = "/SCADomain/";

    // path to the scaDomain.js script 
    // Note: this is the same as the Ajax binding to keep the client code
    //       the same for clients using either the ajax or jsonrpc binding
    public static final String SCA_DOMAIN_SCRIPT = SERVICE_PREFIX + "scaDomain.js";


    public JSONRPCService(RuntimeComponent component,
                                         RuntimeComponentService service,
                                         Binding b,
                                         JSONRPCBinding binding,
                                         ServletHost servletHost) {
        this.service = service;
        this.binding = b;
        this.servletHost = servletHost;
    }

    public void start() {
        
        // Determine the service business interface
        Class<?> serviceInterface = getTargetJavaClass(service.getInterfaceContract().getInterface());

        // Create a Java proxy to the target service
        ProxyFactory proxyFactory = new JDKProxyFactory();
        Object proxy = proxyFactory.createProxy(serviceInterface, service.getRuntimeWire(binding));
        
        // Create and register a servlet for this service
        JSONRPCServiceServlet serviceServlet = new JSONRPCServiceServlet(binding.getName(), serviceInterface, proxy);
        int port;
        servletHost.addServletMapping(binding.getURI(), serviceServlet);
        URI uri = URI.create(binding.getURI());
        port = uri.getPort();
        if (port == -1)
            port = 8080;

        // get the ScaDomainScriptServlet, if it doesn't yet exist create one
        // this uses removeServletMapping / addServletMapping as theres no getServletMapping facility
        URI domainURI = URI.create("http://localhost:" + port + SCA_DOMAIN_SCRIPT);
        ScaDomainScriptServlet scaDomainServlet = (ScaDomainScriptServlet) servletHost.getServletMapping(domainURI.toString());
        if (scaDomainServlet == null) {
            scaDomainServlet = new ScaDomainScriptServlet();
            servletHost.addServletMapping(domainURI.toString(), scaDomainServlet);
        }
        
        // Add this service to the scadomain script servlet
        scaDomainServlet.addService(binding.getName());

    }

    public void stop() {

        // Unregister from the service servlet mapping
        int port;
        servletHost.removeServletMapping(binding.getURI());
        URI uri = URI.create(binding.getURI());
        port = uri.getPort();
        if (port == -1)
            port = 8080;

        // Unregister the service from the scaDomain script servlet
        URI domainURI = URI.create("http://localhost:" + port + SCA_DOMAIN_SCRIPT);
        ScaDomainScriptServlet scaDomainServlet = (ScaDomainScriptServlet) servletHost.getServletMapping(domainURI.toString());
        if (scaDomainServlet != null) {
            scaDomainServlet.removeService(binding.getName());

            // Remove the servlet if there's no more registered services
            if (scaDomainServlet.getServiceNames().isEmpty()) {
                servletHost.removeServletMapping(domainURI.toString());
            }
        }
    }

    protected Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out  how to generate Java
        // Interface in cases where the target is not a Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }

}
