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

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingImpl;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceProvider;
import org.apache.tuscany.sca.binding.ws.axis2.Java2WSDLHelper;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.management.ServiceDiscovery;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The service binding provider for the remote sca binding implementation. Relies on the 
 * binding-ws-axis implementation for providing a remote message endpoint for this service
 * 
 * @version $Rev: 563772 $ $Date: 2007-08-08 07:50:49 +0100 (Wed, 08 Aug 2007) $
 */
public class Axis2SCAServiceBindingProvider implements ServiceBindingProvider2 {

    private SCABinding binding;
    private Axis2ServiceProvider axisProvider;
    private WebServiceBinding wsBinding;
    
    private boolean started = false;


    public Axis2SCAServiceBindingProvider(RuntimeComponent component,
                                          RuntimeComponentService service,
                                          DistributedSCABinding binding,
                                          ServletHost servletHost,
                                          MessageFactory messageFactory) {
        this.binding = binding.getSCABinding();
        wsBinding = (new DefaultWebServiceBindingFactory()).createWebServiceBinding();
        
        // Turn the java interface contract into a wsdl interface contract
        InterfaceContract contract = service.getInterfaceContract();
        if ((contract instanceof JavaInterfaceContract)) {
            contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract);
        }
        
        // Set to use the Axiom data binding
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());
        
        wsBinding.setBindingInterfaceContract(contract);
        wsBinding.setName(this.binding.getName()); 
        wsBinding.setURI(this.binding.getURI());
        
        axisProvider = new Axis2SCAServiceProvider(component, 
                                                   service, 
                                                   this.binding,
                                                   wsBinding,
                                                   servletHost,
                                                   messageFactory);
        
        this.binding.setURI(wsBinding.getURI());
        
        // get the url out of the binding and send it to the registry if
        // a distributed domain is configured
        DistributedSCADomain distributedDomain = ((SCABindingImpl)this.binding).getDistributedDomain();
        
        ServiceDiscovery serviceDiscovery = distributedDomain.getServiceDiscovery();
        
        // register endpoint twice to take account the formats 
        //  ComponentName
        //  ComponentName/ServiceName
        // TODO - Can't we get this from somewhere? What happens with nested components. 
        serviceDiscovery.registerServiceEndpoint(distributedDomain.getDomainName(), 
                                                 distributedDomain.getNodeName(), 
                                                 component.getName(), 
                                                 SCABinding.class.getName(), 
                                                 this.binding.getURI());
        serviceDiscovery.registerServiceEndpoint(distributedDomain.getDomainName(), 
                                                 distributedDomain.getNodeName(), 
                                                 component.getName() + "/" + this.binding.getName(), 
                                                 SCABinding.class.getName(), 
                                                 this.binding.getURI());
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
