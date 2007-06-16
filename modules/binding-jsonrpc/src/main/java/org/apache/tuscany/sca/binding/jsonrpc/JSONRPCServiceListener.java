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

import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.ServiceListener;

/**
 * Implementation of the JSONRPC binding provider.
 * 
 * There are multiple servlets used to support the JSON-RPC binidng.
 * One servlet to handle requests for the scaDomain script and seperate
 * servlets for each SCA <service> which uses <binding.jsonrpc>. 
 */
public class JSONRPCServiceListener implements ServiceListener {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private JSONRPCBinding binding;
    private ServletHost servletHost;

    public static final String SERVICE_PREFIX = "/SCADomain/";

    // path to the scaDomain.js script 
    // Note: this is the same as the Ajax binding to keep the client code
    //       the same for clients using either the ajax or jsonrpc binding
    public static final String SCA_DOMAIN_SCRIPT = SERVICE_PREFIX + "scaDomain.js";


    public JSONRPCServiceListener(RuntimeComponent component,
                                         RuntimeComponentService service,
                                         JSONRPCBinding binding,
                                         ServletHost servletHost) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
    }

    public void start() {
        
        // Create and register a servlet for this service
        Class<?> serviceInterface = getTargetJavaClass(service.getInterfaceContract().getInterface());
        Object instance = component.createSelfReference(serviceInterface).getService();
        JSONRPCServiceServlet serviceServlet = new JSONRPCServiceServlet(binding.getName(), serviceInterface, instance);
        servletHost.addServletMapping(SERVICE_PREFIX + binding.getName(), serviceServlet);

        // get the ScaDomainScriptServlet, if it doesn't yet exist create one
        // this uses removeServletMapping / addServletMapping as theres no getServletMapping facility
        ScaDomainScriptServlet scaDomainServlet = (ScaDomainScriptServlet) servletHost.removeServletMapping(SCA_DOMAIN_SCRIPT);
        if (scaDomainServlet == null) {
            scaDomainServlet = new ScaDomainScriptServlet();
        }
        servletHost.addServletMapping(SCA_DOMAIN_SCRIPT, scaDomainServlet);
        
        // Add this service to the scadomain script servlet
        scaDomainServlet.addService(binding.getName());

    }

    public void stop() {

        // Unregister from the service servlet mapping
        servletHost.removeServletMapping(SERVICE_PREFIX + binding.getName());

        // Unregister the service from the scaDomain script servlet
        // don't unregister the scaDomain script servlet if it still has other service names
        ScaDomainScriptServlet scaDomainServlet = (ScaDomainScriptServlet) servletHost.removeServletMapping(SCA_DOMAIN_SCRIPT);
        if (scaDomainServlet != null) {
            scaDomainServlet.removeService(binding.getName());
            // put it back if there are still other services registered with the servlet
            if (scaDomainServlet.getServiceNames().size() > 0) {
                servletHost.addServletMapping(SCA_DOMAIN_SCRIPT, scaDomainServlet);
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
