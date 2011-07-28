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

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

public class JSONPServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeEndpoint endpoint;
    private ServletHost servletHost;
    private InterfaceContract contract;

    public JSONPServiceBindingProvider(RuntimeEndpoint endpoint, ServletHost servletHost) {
        this.endpoint = endpoint;
        this.servletHost = servletHost;
        
        try {
            contract = (InterfaceContract)endpoint.getComponentServiceInterfaceContract().clone();
        } catch (Exception ex){
            // we know this supports clone
        }
        contract.getInterface().resetDataBinding("JSON");
    }

    public void start() {
        ComponentService service = endpoint.getService();
        Interface serviceInterface = service.getInterfaceContract().getInterface();
        for (Operation op : serviceInterface.getOperations()) {
            JSONPServlet servlet = new JSONPServlet(endpoint, op);
            String path = endpoint.getBinding().getURI() + "/" + op.getName();
            String mappedURI = servletHost.addServletMapping(path, servlet);
            String endpointURI = mappedURI.substring(0, mappedURI.lastIndexOf("/" + op.getName()));
            endpoint.setDeployedURI(endpointURI);
            endpoint.getBinding().setURI(endpointURI);
        }
    }

    public void stop() {
        ComponentService service = endpoint.getService();
        Interface serviceInterface = service.getInterfaceContract().getInterface();
        for (Operation op : serviceInterface.getOperations()) {
            String path = endpoint.getBinding().getURI() + "/" + op.getName();
            servletHost.removeServletMapping(path);
        }
    }

    // TODO: Why are these two still on the ServiceBindingProvider interface?
    public InterfaceContract getBindingInterfaceContract() {
        return contract;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
