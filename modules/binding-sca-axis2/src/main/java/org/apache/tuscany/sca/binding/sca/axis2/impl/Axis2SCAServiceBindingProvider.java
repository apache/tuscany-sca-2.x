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

package org.apache.tuscany.sca.binding.sca.axis2.impl;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.axis2.Axis2ServiceClient;
import org.apache.tuscany.sca.binding.axis2.Axis2ServiceProvider;
import org.apache.tuscany.sca.binding.axis2.Java2WSDLHelper;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.impl.WebServiceBindingFactoryImpl;
import org.apache.tuscany.sca.binding.ws.impl.WebServiceBindingImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * @version $Rev: 563772 $ $Date: 2007-08-08 07:50:49 +0100 (Wed, 08 Aug 2007) $
 */
public class Axis2SCAServiceBindingProvider implements ServiceBindingProvider2 {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private SCABinding binding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
   
    private Axis2ServiceProvider axisProvider;
    private WebServiceBinding wsBinding;
    
    private boolean started = false;


    public Axis2SCAServiceBindingProvider(RuntimeComponent component,
                                          RuntimeComponentService service,
                                          DistributedSCABinding binding,
                                          ServletHost servletHost,
                                          MessageFactory messageFactory) {
        this.component = component;
        this.service = service;
        this.binding = binding.getSCABinding();
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        
        wsBinding = (new DefaultWebServiceBindingFactory()).createWebServiceBinding();
        
        // fix up the minimal things required to get the ws binding going. 
        
        // Turn the java interface contract into a wsdl interface contract
        InterfaceContract contract = service.getInterfaceContract();
        if ((contract instanceof JavaInterfaceContract)) {
            contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract);
        }
        
        // Set to use the Axiom data binding
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());
        
        wsBinding.setBindingInterfaceContract(contract);
        wsBinding.setName(this.binding.getName()); 
        
        // only pass on the uri if it absolute, i.e. come from the uri attribute
        // of the sca binding. The axis binding will sort it out for itself if 
        // it's relative
        URI bindingURI = URI.create(this.binding.getURI());
        if (bindingURI.isAbsolute()){
            wsBinding.setURI(this.binding.getURI());
        }
        
        axisProvider = new Axis2SCAServiceProvider(component, 
                                                   service, 
                                                   this.binding,
                                                   wsBinding,
                                                   servletHost,
                                                   messageFactory);
        
        this.binding.setURI(wsBinding.getURI());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsAsyncOneWayInvocation() {
        return false;
    }

    public Invoker createCallbackInvoker(Operation operation) {
        throw new UnsupportedOperationException();
    }

    public void start() {
        if (started) {
            return;
        } else {
            started = true;
        }
        
        axisProvider.start();
    }

    public void stop() {
        axisProvider.stop();
    }

}
