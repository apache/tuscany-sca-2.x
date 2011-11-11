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

package org.apache.tuscany.sca.binding.http.wireformat.provider;

import org.apache.tuscany.sca.binding.http.wireformat.HTTPJSONWireFormat;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 *
 * @version $Rev$ $Date$
 */
public class HTTPJSONWireFormatProviderFactory implements WireFormatProviderFactory<HTTPJSONWireFormat> {

    public HTTPJSONWireFormatProviderFactory(ExtensionPointRegistry extensionPoints) {
    }

    @Override
    public Class<HTTPJSONWireFormat> getModelType() {
        return HTTPJSONWireFormat.class;
    }

    @Override
    public WireFormatProvider createReferenceWireFormatProvider(RuntimeEndpointReference endpointReference) {
        return null;
    }

    @Override
    public WireFormatProvider createServiceWireFormatProvider(final RuntimeEndpoint endpoint) {
        return new WireFormatProvider() {
            @Override
            public InterfaceContract configureWireFormatInterfaceContract(InterfaceContract interfaceContract) {
                // TODO: Ideally this wants to set the databinding on a per request basis from the 
                // http content type and accept headers and so support things like json or xml etc,
                // for now to get started just use json 
                interfaceContract.getInterface().resetDataBinding("JSON");
                return interfaceContract;
            }
            @Override
            public Interceptor createInterceptor() {
                return new HTTPJSONWireFormatServiceInterceptor(endpoint);
            }
            @Override
            public String getPhase() {
                return Phase.SERVICE_BINDING_WIREFORMAT;
            }};
    }

}
