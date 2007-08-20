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

package org.apache.tuscany.sca.binding.feed.provider;

import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.tuscany.sca.binding.feed.AtomBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Implementation of the Atom binding provider.
 */
class AtomReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private AtomBinding binding;
    private String authorizationHeader;
    private HttpClient httpClient;

    AtomReferenceBindingProvider(RuntimeComponent component,
                                        RuntimeComponentReference reference,
                                        AtomBinding binding) {
        this.reference = reference;
        this.binding = binding;

        // Prepare authorization header
        String authorization = "admin" + ":" + "admin";
        authorizationHeader = "Basic " + new String(Base64.encodeBase64(authorization.getBytes()));
        ;

        // Create an HTTP client
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        } else {
            String operationName = operation.getName();
            if (operationName.equals("get")) {
                return new AtomBindingInvoker.GetInvoker(operation, binding.getURI(), httpClient, authorizationHeader);
            } else if (operationName.equals("post")) {
                return new AtomBindingInvoker.PostInvoker(operation, binding.getURI(), httpClient, authorizationHeader);
            } else if (operationName.equals("put")) {
                return new AtomBindingInvoker.PutInvoker(operation, binding.getURI(), httpClient, authorizationHeader);
            } else if (operationName.equals("delete")) {
                return new AtomBindingInvoker.DeleteInvoker(operation, binding.getURI(), httpClient,
                                                            authorizationHeader);
            } else if (operationName.equals("getFeed")) {
                return new AtomBindingInvoker.GetCollectionInvoker(operation, binding.getURI(), httpClient,
                                                                   authorizationHeader);
            } else if (operationName.equals("postMedia")) {
                return new AtomBindingInvoker.PostMediaInvoker(operation, binding.getURI(), httpClient,
                                                               authorizationHeader);
            } else if (operationName.equals("putMedia")) {
                return new AtomBindingInvoker.PutMediaInvoker(operation, binding.getURI(), httpClient,
                                                              authorizationHeader);
            }

            return new AtomBindingInvoker(operation, binding.getURI(), httpClient, authorizationHeader);
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {

        // Configure the HTTP client credentials
        Credentials credentials = new UsernamePasswordCredentials("admin", "admin");
        httpClient.getParams().setAuthenticationPreemptive(true);
        URI uri = URI.create(binding.getURI());
        httpClient.getState().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), credentials);
    }

    public void stop() {
        httpClient = null;
    }

}
