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

package org.apache.tuscany.sca.binding.websocket.runtime;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.binding.websocket.WebsocketBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The service binding provider initiates the necessary infrastructure to expose
 * services via websockets.
 */
public class WebsocketServiceBindingProvider implements ServiceBindingProvider {

    private static final int DEFAULT_PORT = 9000;
    private static final String JAVASCRIPT_RESOURCE_PATH = "/org.apache.tuscany.sca.WebsocketComponentContext.js";
    private static Map<Integer, WebsocketServer> servers = new HashMap<Integer, WebsocketServer>();
    private ExtensionPointRegistry extensionPoints;
    private RuntimeEndpoint endpoint;
    private ServletHost servletHost;

    public WebsocketServiceBindingProvider(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint, ServletHost servletHost) {
        this.extensionPoints = extensionPoints;
        this.endpoint = endpoint;
        this.servletHost = servletHost;
    }

    public void start() {
        WebsocketBinding binding = (WebsocketBinding) endpoint.getBinding();
        int port = DEFAULT_PORT;
        if (binding.getPort() != null) {
            port = Integer.parseInt(binding.getPort());
        }
        try {
            WebsocketServer server = initServerForURI(port);
            String component = endpoint.getComponent().getName();
            String service = endpoint.getService().getName();
            for (Operation op : getBindingInterfaceContract().getInterface().getOperations()) {
                String operation = op.getName();
                server.getDispatcher().addOperation(component + "." + service + "." + operation, extensionPoints, endpoint, op);
            }
            JavascriptGenerator.generateServiceProxy(component, service, getBindingInterfaceContract().getInterface()
                    .getOperations(), port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initJavascriptResource();
    }

    private WebsocketServer initServerForURI(int port) throws Exception {
        WebsocketServer server = servers.get(port);
        if (server == null) {
            server = new WebsocketServer(port);
            server.start();
            servers.put(port, server);
        }
        return server;
    }

    private void initJavascriptResource() {
        if (servletHost.getServletMapping(JAVASCRIPT_RESOURCE_PATH) == null) {
            servletHost.addServletMapping(JAVASCRIPT_RESOURCE_PATH, new JavascriptResourceServlet());
        }
    }

    public void stop() {
        for (WebsocketServer server : servers.values()) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        servers.clear();
        servletHost.removeServletMapping(JAVASCRIPT_RESOURCE_PATH);
        JavascriptGenerator.clear();
        WebsocketConnectionManager.clear();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return endpoint.getService().getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
