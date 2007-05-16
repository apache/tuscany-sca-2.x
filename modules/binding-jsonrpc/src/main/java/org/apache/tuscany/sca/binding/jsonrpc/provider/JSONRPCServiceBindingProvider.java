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

import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.binding.jsonrpc.server.JSONRPCEntryPointServlet;
import org.apache.tuscany.sca.binding.jsonrpc.server.JSONRPCScriptServlet;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the JSONRPC binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCServiceBindingProvider implements ServiceBindingProvider {

    private static int servletRegistrationCount = 0;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private JSONRPCBinding binding;
    private ServletHost servletHost;

    // path to the JSONRPC javascript servlet
    public static final String SCRIPT_GETTER_SERVICE_MAPPING = "/SCA/scripts";

    public static final String JSONRPC_SERVICE_MAPPING_PREFIX = "/";

    public JSONRPCServiceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentService service,
                                         JSONRPCBinding binding,
                                         ServletHost servletHost) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {
        JSONRPCEntryPointServlet servlet;

        Class<?> aClass = getTargetJavaClass(service.getInterfaceContract().getInterface());
        Object instance = component.createSelfReference(aClass).getService();

        servlet = new JSONRPCEntryPointServlet(binding.getName(), aClass, instance);

        // register the servlet based on the service name
        servletHost.addServletMapping(JSONRPC_SERVICE_MAPPING_PREFIX + binding.getName(), servlet);

        // if the script getter servlet is not already registered then register
        // it
        if (servletRegistrationCount == 0) {
            servletHost.addServletMapping(SCRIPT_GETTER_SERVICE_MAPPING, new JSONRPCScriptServlet());
        }

        // increase the registered servlet count
        servletRegistrationCount++;
    }

    public void stop() {

        // Unregister from the servlet mapping
        servletHost.removeServletMapping(JSONRPC_SERVICE_MAPPING_PREFIX + binding.getName());
        servletRegistrationCount--;
        // if we unregistered the last JSONRPC servlet, then unreister the
        // script servlet
        if (servletRegistrationCount == 0) {
            servletHost.removeServletMapping(SCRIPT_GETTER_SERVICE_MAPPING);
        }
    }

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out
        // how to generate Java Interface in cases where the target is not a
        // Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }

}
