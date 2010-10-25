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

package org.apache.tuscany.sca.binding.hazelcast;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

public class HazelcastBindingProviderFactory implements BindingProviderFactory<HazelcastBinding> {

    private ExtensionPointRegistry extensionsRegistry;

    public HazelcastBindingProviderFactory(ExtensionPointRegistry extensionsRegistry) {
        this.extensionsRegistry = extensionsRegistry;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeEndpointReference endpointReference) {
        InterfaceContract interfaceContract = createDOMInterfaceContract(endpointReference.getComponent(), endpointReference.getReference());       
        return new HazelcastReferenceBindingProvider(extensionsRegistry, endpointReference.getBinding().getURI(), interfaceContract);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeEndpoint endpoint) {
        InterfaceContract interfaceContract = createDOMInterfaceContract(endpoint.getComponent(), endpoint.getService());       
        return new HazelcastServiceBindingProvider(endpoint, interfaceContract);
    }

    private InterfaceContract createDOMInterfaceContract(Component component, Contract contract) {
        WebServiceBindingFactory wsFactory = extensionsRegistry.getExtensionPoint(WebServiceBindingFactory.class);
        WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
        BindingWSDLGenerator.generateWSDL(component, contract, wsBinding, extensionsRegistry, null);
        InterfaceContract interfaceContract = wsBinding.getBindingInterfaceContract();
        interfaceContract.getInterface().resetDataBinding(DOMDataBinding.NAME);
        return interfaceContract;
    }

    public Class<HazelcastBinding> getModelType() {
        return HazelcastBinding.class;
    }
}
