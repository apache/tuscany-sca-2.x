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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * A dispatcher stores all service invokers for a servers and is used to
 * determine which one of them should be used when a request comes in.
 */
public class WebsocketBindingDispatcher {

    private Map<String, WebsocketServiceInvoker> invokers = new HashMap<String, WebsocketServiceInvoker>();

    public void addOperation(String uri, ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint, Operation operation) {
        invokers.put(uri, new WebsocketServiceInvoker(extensionPoints, operation, endpoint));
    }

    public WebsocketServiceInvoker dispatch(String uri) {
        return invokers.get(uri);
    }
}