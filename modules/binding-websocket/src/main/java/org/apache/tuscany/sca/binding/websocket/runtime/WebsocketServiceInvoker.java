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

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.core.invocation.impl.MessageImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The service invoker is used to call an operation implementation in a
 * synchronous or asynchronous way.
 */
public class WebsocketServiceInvoker {

    protected AssemblyFactory assemblyFactory;
    protected Operation operation;
    protected RuntimeEndpoint endpoint;

    public WebsocketServiceInvoker(ExtensionPointRegistry extensionPoints, Operation operation, RuntimeEndpoint endpoint) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        
        this.operation = operation;
        this.endpoint = endpoint;
    }

    public WebsocketBindingMessage invokeSync(WebsocketBindingMessage request) {
        String jsonParams = request.getPayload();
        Object[] args = JSONUtil.decodePayloadForOperation(jsonParams, operation);
        try {
            Object operationResponse = endpoint.invoke(operation, args);
            String payload = JSONUtil.encodePayload(operationResponse);
            WebsocketBindingMessage response = new WebsocketBindingMessage(request.getOperation(), payload);
            return response;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void invokeAsync(WebsocketBindingMessage request, TuscanyWebsocket channel) {
        String jsonParams = request.getPayload();
        Object[] args = JSONUtil.decodePayloadForOperation(jsonParams, operation);
        Message msg = new MessageImpl();
        msg.getHeaders().put(Constants.MESSAGE_ID, channel.getId());
        msg.setBody(args);
        EndpointReference re = assemblyFactory.createEndpointReference(); //new RuntimeEndpointReferenceImpl();
        Endpoint callbackEndpoint = assemblyFactory.createEndpoint(); //new RuntimeEndpointImpl();
        callbackEndpoint.setURI(request.getOperation());
        re.setCallbackEndpoint(callbackEndpoint);
        msg.setFrom(re);
        endpoint.invoke(operation, msg);
    }

    public boolean isNonBlocking() {
        return operation.isNonBlocking();
    }

}
