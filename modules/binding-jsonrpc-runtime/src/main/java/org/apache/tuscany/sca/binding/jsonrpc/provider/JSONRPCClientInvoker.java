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

import java.lang.reflect.Method;

import org.apache.commons.httpclient.HttpClient;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.jabsorb.client.Client;
import org.jabsorb.client.Session;
import org.jabsorb.client.TransportRegistry;

/**
 * Invoker for the JSONRPC Binding
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCClientInvoker implements Invoker {
    private EndpointReference endpointReference;
    private Operation operation;
    private Method method;
    private String uri;

    private HttpClient httpClient;

    public JSONRPCClientInvoker(EndpointReference endpointReference, Operation operation, HttpClient httpClient) {
        this.endpointReference = endpointReference;
        this.operation = operation;
        this.method = ((JavaOperation)operation).getJavaMethod();
        this.uri = ((JSONRPCBinding)endpointReference.getBinding()).getURI();

        this.httpClient = httpClient;
    }

    public Message invoke(Message msg) {
        Session session = TransportRegistry.i().createSession(uri);
        Client client = new Client(session);
        Object proxy = client.openProxy("", method.getDeclaringClass());

        try {
            Object result = client.invoke(proxy, method, (Object[])msg.getBody());
            msg.setBody(result);
        } catch (Exception e) {
            msg.setFaultBody(e);
        } finally {
            client.closeProxy(proxy);
        }
        return msg;
    }

}
