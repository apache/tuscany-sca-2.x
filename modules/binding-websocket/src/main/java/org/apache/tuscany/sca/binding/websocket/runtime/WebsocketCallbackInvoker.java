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

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.core.invocation.impl.MessageImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * The callback invoker is used to send asynchronous responses back to the
 * browser client.
 */
public class WebsocketCallbackInvoker implements Invoker {

    protected Operation operation;
    protected EndpointReference endpoint;

    public WebsocketCallbackInvoker(Operation operation, EndpointReference endpoint) {
        this.operation = operation;
        this.endpoint = endpoint;
    }

    public Message invoke(Message msg) {
        String channelId = (String) msg.getHeaders().get(Constants.RELATES_TO);
        TuscanyWebsocket websocket = WebsocketConnectionManager.getConnection(channelId);
        Message response = new MessageImpl();
        if (websocket == null) {
            response.setBody(WebsocketStatus.CLOSED);
        } else {
            Object[] body = msg.getBody();
            String payload = JSONUtil.encodePayload(body[0]);
            String operation = msg.getTo().getURI();
            WebsocketBindingMessage message = new WebsocketBindingMessage(operation, payload);
            websocket.send(message);
            response.setBody(WebsocketStatus.OPEN);
        }
        return response;
    }
}
