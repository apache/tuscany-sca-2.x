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

package org.apache.tuscany.sca.binding.rest.provider;

import org.apache.http.client.HttpClient;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.client.HttpClientFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * 
 */
public class RESTReferenceBindingProvider implements EndpointReferenceProvider {
    private ExtensionPointRegistry registry;
    private RuntimeEndpointReference endpointReference;
    
    private HttpClientFactory httpClientFactory;
    private HttpClient httpClient;

    public RESTReferenceBindingProvider(ExtensionPointRegistry registry, RuntimeEndpointReference endpointReference) {
        super();
        this.registry = registry;
        this.endpointReference = endpointReference;
        this.httpClientFactory = HttpClientFactory.getInstance(registry);
    }

    public void configure() {
    }

    public Invoker createInvoker(Operation operation) {
        return new RESTBindingInvoker(registry, endpointReference, (RESTBinding)endpointReference.getBinding(), operation, httpClient);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return endpointReference.getComponentReferenceInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        // Create an HTTP client
        httpClient = httpClientFactory.createHttpClient();
    }

    public void stop() {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

}
