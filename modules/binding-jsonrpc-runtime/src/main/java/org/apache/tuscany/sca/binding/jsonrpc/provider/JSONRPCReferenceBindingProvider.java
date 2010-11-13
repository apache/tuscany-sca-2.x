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

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Interface;
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
        httpClient = createHttpClient();
    }

    public HttpClient createHttpClient() {
        HttpParams defaultParameters = new BasicHttpParams();
        //defaultParameters.setIntParameter(HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS, 10);
        HttpProtocolParams.setContentCharset(defaultParameters, HTTP.UTF_8);
        HttpConnectionParams.setConnectionTimeout(defaultParameters, 60000);
        HttpConnectionParams.setSoTimeout(defaultParameters, 60000);

        SchemeRegistry supportedSchemes = new SchemeRegistry();
        supportedSchemes.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));

        ClientConnectionManager connectionManager =
            new ThreadSafeClientConnManager(defaultParameters, supportedSchemes);

        return new DefaultHttpClient(connectionManager, defaultParameters);
    }

    public InterfaceContract getBindingInterfaceContract() {
        //return referenceContract;
        return reference.getInterfaceContract();
    }

    public Invoker createInvoker(Operation operation) {
        final Interface intf = reference.getInterfaceContract().getInterface();
        if (intf.isDynamic()) {
            return new JSONRPCBindingInvoker(endpointReference, operation, httpClient);
        }
        return new JSONRPCClientInvoker(endpointReference, operation, httpClient);
    }

    public void start() {

    }

    public void stop() {

    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
