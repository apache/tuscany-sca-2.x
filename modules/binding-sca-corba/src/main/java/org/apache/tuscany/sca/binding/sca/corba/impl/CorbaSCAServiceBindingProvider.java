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

package org.apache.tuscany.sca.binding.sca.corba.impl;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.corba.impl.service.DynaCorbaServant;
import org.apache.tuscany.sca.binding.corba.impl.service.InvocationProxy;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Service binding provider for SCA default binding over CORBA binding
 */
public class CorbaSCAServiceBindingProvider implements ServiceBindingProvider {

    private SCABinding binding;
    private CorbaHost host;
    private RuntimeComponentService service;
    private DynaCorbaServant servant;
    private MessageFactory messageFactory;
    private InterfaceContract wsdlInterfaceContract;

    public CorbaSCAServiceBindingProvider(SCABinding binding,
                                          CorbaHost host,
                                          RuntimeComponent component,
                                          RuntimeComponentService service,
                                          ExtensionPointRegistry extensions) {
        this.binding = binding;
        this.host = host;
        this.service = service;
        
        messageFactory = extensions.getExtensionPoint(MessageFactory.class);

        WebServiceBindingFactory wsFactory = extensions.getExtensionPoint(WebServiceBindingFactory.class);
        WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
        BindingWSDLGenerator.generateWSDL(component, service, wsBinding, extensions, null);
        wsdlInterfaceContract = wsBinding.getBindingInterfaceContract();
        wsdlInterfaceContract.getInterface().resetDataBinding(OMElement.class.getName());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsdlInterfaceContract;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        try {
            InvocationProxy proxy =
                new CorbaSCAInvocationProxy(service.getRuntimeWire(binding), service.getInterfaceContract()
                    .getInterface(), messageFactory);
            servant = new DynaCorbaServant(proxy, "IDL:org/apache/tuscany/sca/binding/sca/corba/Service:1.0");
            host.registerServant(binding.getURI(), servant);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
        try {
            host.unregisterServant(binding.getURI());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

    }

}
