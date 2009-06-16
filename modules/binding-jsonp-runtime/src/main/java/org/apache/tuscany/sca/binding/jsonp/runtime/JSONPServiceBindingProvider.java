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

package org.apache.tuscany.sca.binding.jsonp.runtime;

import org.apache.tuscany.sca.binding.jsonp.JSONPBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

public class JSONPServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponentService service;
    private JSONPBinding binding;
    private ServletHost servletHost;

    public JSONPServiceBindingProvider(RuntimeComponent component,
                                       RuntimeComponentService service,
                                       JSONPBinding binding,
                                       ServletHost servletHost) {
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
    }

    public void start() {
        RuntimeWire wire = service.getRuntimeWire(binding);
        Interface serviceInterface = service.getInterfaceContract().getInterface();
        for (Operation op : serviceInterface.getOperations()) {
            JSONPServlet servlet = new JSONPServlet(wire, op);
            String path = service.getName() + "/" + op.getName();
            servletHost.addServletMapping(path, servlet);
        }
    }

    public void stop() {
        Interface serviceInterface = service.getInterfaceContract().getInterface();
        for (Operation op : serviceInterface.getOperations()) {
            String path = service.getName() + "/" + op.getName();
            servletHost.removeServletMapping(path);
        }
    }

    // TODO: Why are these two still on the ServiceBindingProvider interface?
    public InterfaceContract getBindingInterfaceContract() {
        return null;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
