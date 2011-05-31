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

package org.apache.tuscany.sca.binding.comet.runtime;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.binding.comet.runtime.javascript.JavascriptGenerator;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometEndpointManager;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometOperationManager;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometSessionManager;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Provider for services having comet binding specified in the scdl.
 */
public class CometServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeEndpoint endpoint;
    private ServletHost servletHost;

    public CometServiceBindingProvider(final RuntimeEndpoint endpoint, final ServletHost servletHost) {
        this.endpoint = endpoint;
        this.servletHost = servletHost;
    }

    /**
     * Init the comet binding server-side infrastructure.
     */
    @Override
    public void start() {
        String deployedURI = ServletFactory.registerServlet(this.servletHost);
        endpoint.setDeployedURI(deployedURI);
        final ComponentService service = this.endpoint.getService();
        final Interface serviceInterface = service.getInterfaceContract().getInterface();
        JavascriptGenerator.generateServiceProxy(service);
        for (final Operation operation : serviceInterface.getOperations()) {
            final String url = "/" + endpoint.getService().getName() + "/" + operation.getName();
            CometEndpointManager.add(url, endpoint);
            CometOperationManager.add(url, operation);
            JavascriptGenerator.generateMethodProxy(service, operation);
        }
    }

    /**
     * Stop the comet binding server-side infrastructure.
     */
    @Override
    public void stop() {
        ServletFactory.unregisterServlet(this.servletHost);
        CometEndpointManager.clear();
        CometOperationManager.clear();
        CometSessionManager.clear();
    }

    @Override
    public InterfaceContract getBindingInterfaceContract() {
        return endpoint.getService().getInterfaceContract();
    }

    @Override
    public boolean supportsOneWayInvocation() {
        // set to false so the runtime will add a nonBlocking interceptor to
        // handle @OneWay calls
        return false;
    }

}
