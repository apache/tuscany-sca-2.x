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
package org.apache.tuscany.sca.binding.gdata.provider;


import org.apache.tuscany.sca.binding.gdata.GDataBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.commons.httpclient.HttpClient;
import java.net.URI;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * Implementation of the Atom binding provider.
 *
 * @version $Rev$ $Date$
 */
class GDataReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private GDataBinding binding;
    private HttpClient httpClient;
    private String authorizationHeader;

    /**
     * Constructs a new AtomReferenceBindingProvider
     * @param component
     * @param reference
     * @param binding
     * @param mediator
     */
    GDataReferenceBindingProvider(RuntimeComponent component,
            RuntimeComponentReference reference,
            GDataBinding binding) {
        this.reference = reference;
        this.binding = binding;

        // Prepare authorization header
        //String authorization = "admin" + ":" + "admin";
        String authorization = "gsocstudent2008" + ":" + "gsoc2008";
        authorizationHeader = "Basic " + new String(Base64.encodeBase64(authorization.getBytes()));
        
        // Create an HTTP client
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(10);
        connectionManager.getParams().setConnectionTimeout(60000);
        httpClient = new HttpClient(connectionManager);
    }



    public Invoker createInvoker(Operation operation) {

        String operationName = operation.getName();
        if (operationName.equals("get")) {
            return new GDataBindingInvoker.GetInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("post")) {
            return new GDataBindingInvoker.PostInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("put")) {
            return new GDataBindingInvoker.PutInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("delete")) {
            return new GDataBindingInvoker.DeleteInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("getFeed") || operationName.equals("getAll")) {
            return new GDataBindingInvoker.GetAllInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("postMedia")) {
            return new GDataBindingInvoker.PostMediaInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("putMedia")) {
            return new GDataBindingInvoker.PutMediaInvoker(operation, binding, httpClient, authorizationHeader);
        } else if (operationName.equals("query")) {
            return new GDataBindingInvoker.QueryInvoker(operation, binding, httpClient, authorizationHeader);
        }

        return new GDataBindingInvoker(operation, binding, httpClient, authorizationHeader);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {
        // Configure the HTTP client credentials
        //Credentials credentials = new UsernamePasswordCredentials("admin", "admin");
        Credentials credentials = new UsernamePasswordCredentials("gsocstudent2008", "gsoc2008");
        httpClient.getParams().setAuthenticationPreemptive(true);
        URI uri = URI.create(binding.getURI());
        httpClient.getState().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), credentials);

        // Find the get operation on the reference interface
        if (true) {
            return;
        }
    }

    public void stop() {
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }
}
