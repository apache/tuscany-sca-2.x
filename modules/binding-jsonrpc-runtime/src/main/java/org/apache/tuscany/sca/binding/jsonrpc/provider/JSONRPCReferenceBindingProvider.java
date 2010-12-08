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

import org.apache.http.client.HttpClient;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.host.http.client.HttpClientFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Implementation of the JSONRPC Binding Provider for References
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCReferenceBindingProvider implements ReferenceBindingProvider {

    private EndpointReference endpointReference;
    private RuntimeComponentReference reference;
    private InterfaceContract referenceContract;

    private HttpClient httpClient;

    public JSONRPCReferenceBindingProvider(EndpointReference endpointReference) {

        this.endpointReference = endpointReference;
        this.reference = (RuntimeComponentReference)endpointReference.getReference();

        
        //clone the service contract to avoid databinding issues
        /*
        try {
            this.referenceContract = (InterfaceContract)reference.getInterfaceContract().clone();
        } catch(CloneNotSupportedException e) {
            this.referenceContract = reference.getInterfaceContract();
        }

        JSONRPCDatabindingHelper.setDataBinding(referenceContract.getInterface());
        */

        // Create an HTTP client
        // httpClient = createHttpClient();
    }

    public InterfaceContract getBindingInterfaceContract() {
        //return referenceContract;
        return reference.getInterfaceContract();
    }

    public Invoker createInvoker(Operation operation) {
        // final Interface intf = reference.getInterfaceContract().getInterface();
        return new JSONRPCBindingInvoker(endpointReference, operation, httpClient);
    }

    public void start() {
        // Create an HTTP client
        HttpClientFactory clientFactory = new HttpClientFactory();
        httpClient = clientFactory.createHttpClient();
    }

    public void stop() {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
