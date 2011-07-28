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

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

/**
 * The websocket server is an embedded Jetty instance which will be started on
 * the port specified in the component definition.
 */
public class WebsocketServer extends Server {

    private WebsocketBindingDispatcher dispatcher;

    public WebsocketServer(int port) throws URISyntaxException {
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        addConnector(connector);

        setHandler(new WebSocketHandler() {

            @Override
            public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
                return new TuscanyWebsocket(dispatcher);
            }
        });

        dispatcher = new WebsocketBindingDispatcher();
    }

    public WebsocketBindingDispatcher getDispatcher() {
        return dispatcher;
    }

}
