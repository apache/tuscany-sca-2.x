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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceProvider;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The service binding provider for the remote sca binding implementation. Relies on the 
 * binding-ws-axis implementation for providing a remote message endpoint for this service
 * 
 * @version $Rev: 563772 $ $Date: 2007-08-08 07:50:49 +0100 (Wed, 08 Aug 2007) $
 */
public class Axis2SCAServiceBindingProvider implements ServiceBindingProvider {
    
    private static final Logger logger = Logger.getLogger(Axis2SCAServiceBindingProvider.class.getName());

    private SCABinding binding;
    private Axis2ServiceProvider axisProvider;
    private WebServiceBinding wsBinding;
    
    private boolean started = false;


    public Axis2SCAServiceBindingProvider(RuntimeComponent component,
                                          RuntimeComponentService service,
                                          DistributedSCABinding binding,
                                          ExtensionPointRegistry extensionPoints,
                                          Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames) {

        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        ServletHost servletHost = servletHosts.getServletHosts().get(0);
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        MessageFactory messageFactory = modelFactories.getFactory(MessageFactory.class); 
        DataBindingExtensionPoint dataBindings = extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class);

        this.binding = binding.getSCABinding();
        wsBinding = modelFactories.getFactory(WebServiceBindingFactory.class).createWebServiceBinding();
        wsBinding.setName(this.binding.getName());         
        wsBinding.setURI(this.binding.getURI());
       
        // Turn the java interface contract into a WSDL interface contract
        BindingWSDLGenerator.generateWSDL(component, service, wsBinding, extensionPoints, null);

        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(OMElement.class.getName());
        
        axisProvider = new Axis2SCAServiceProvider(component, 
                                                   service, 
                                                   this.binding,
                                                   wsBinding,
                                                   servletHost,
                                                   messageFactory,
                                                   policyHandlerClassnames);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
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
        if (!started) {
            return;
        } else {
            started = false;
        }
        
        axisProvider.stop();
    }

}
