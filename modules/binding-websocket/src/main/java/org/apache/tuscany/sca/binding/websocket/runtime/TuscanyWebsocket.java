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

import java.io.IOException;
import java.util.UUID;

import org.eclipse.jetty.websocket.WebSocket;

/**
 * A websocket connection handling communication between one browser client and
 * *all* websocket services hosted by a server. The connection is persisted as
 * long as the client is connected and communication with all services is
 * multiplexed via a single websocket connection.
 */
public class TuscanyWebsocket implements WebSocket, WebSocket.OnTextMessage {

    private String id;
    private Connection connection;
    private WebsocketBindingDispatcher dispatcher;

    public TuscanyWebsocket(WebsocketBindingDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void onOpen(Connection connection) {
        this.connection = connection;
        this.id = UUID.randomUUID().toString();
        WebsocketConnectionManager.addConnection(this);
    }

    @Override
    public void onMessage(String jsonRequest) {
        WebsocketBindingMessage request = JSONUtil.decodeMessage(jsonRequest);
        WebsocketServiceInvoker invoker = dispatcher.dispatch(request.getOperation());
        if (invoker == null) {
            throw new RuntimeException("No operation found for " + request.getOperation());
        } else {
            if (!invoker.isNonBlocking()) {
                WebsocketBindingMessage response = invoker.invokeSync(request);
                send(response);
            } else {
                invoker.invokeAsync(request, this);
            }
        }
    }

    @Override
    public void onClose(int closeCode, String message) {
        WebsocketConnectionManager.removeConnection(this);
    }

    public void send(WebsocketBindingMessage message) {
        try {
            if (connection.isOpen()) {
                connection.sendMessage(JSONUtil.encodeMessage(message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return id;
    }

}
