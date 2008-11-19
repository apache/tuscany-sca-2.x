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
import java.util.List;
import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ReferenceBindingProvider;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * The reference binding provider for the remote sca binding implementation. Relies on the 
 * binding-ws-axis implementation for sending messages to remote services to this provider
 * just uses the ws-axis provider. 
 * 
 * @version $Rev: 563772 $ $Date: 2007-08-08 07:50:49 +0100 (Wed, 08 Aug 2007) $
 */
public class Axis2SCAReferenceBindingProvider implements ReferenceBindingProvider {

    private static final Logger logger = Logger.getLogger(Axis2SCAReferenceBindingProvider.class.getName());
    
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private Axis2ReferenceBindingProvider axisReferenceBindingProvider;
    private WebServiceBinding wsBinding;
    
    private EndpointReference serviceEPR = null;
    private EndpointReference callbackEPR = null;

    public Axis2SCAReferenceBindingProvider(RuntimeComponent component,
                                            RuntimeComponentReference reference,
                                            DistributedSCABinding binding,
                                            ExtensionPointRegistry extensionPoints,
                                            List<PolicyHandlerTuple> policyHandlerClassnames) {

        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        ServletHost servletHost = servletHosts.getServletHosts().get(0);
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        DataBindingExtensionPoint dataBindings = extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class);

        this.component = component;
        this.reference = reference;
        this.binding = binding.getSCABinding();
        wsBinding = modelFactories.getFactory(WebServiceBindingFactory.class).createWebServiceBinding();
        wsBinding.setName(this.binding.getName());         
       
        // Turn the java interface contract into a WSDL interface contract
        BindingWSDLGenerator.generateWSDL(component, reference, wsBinding, extensionPoints, null);
        
        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(OMElement.class.getName());
        
        axisReferenceBindingProvider = new Axis2ReferenceBindingProvider(component,
                                                                         reference,
                                                                         wsBinding,
                                                                         modelFactories,
                                                                         policyHandlerClassnames,
                                                                         dataBindings);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(Operation operation) {
        return new Axis2SCABindingInvoker(this, axisReferenceBindingProvider.createInvoker(operation));
    }

    /**
     * Uses the distributed domain service discovery feature to locate remote
     * service endpoints
     * 
     * @return An EPR for the target service that this reference refers to 
     */
    public EndpointReference getServiceEndpoint(){
      
        if (serviceEPR == null){
            String endpointURL = null;
            
            if (binding.getURI() != null) {
                // check if the binding URI is already resolved if it is use is if not 
                try {
                    URI uri = new URI(binding.getURI());
                     if (uri.isAbsolute()) {
                         endpointURL = binding.getURI();
                     } 
                } catch(Exception ex) {
                    // do nothing
                } 
            }
            
            serviceEPR = new EndpointReferenceImpl(endpointURL);
        }
        
        return serviceEPR;
    }
    
    
    /**
     * Retrieves the URI of the callback service (that this reference has created)
     * returns null if there is no callback service for the sca binding
     * 
     * @return the callback endpoint
     */
    public EndpointReference getCallbackEndpoint(){
        if (callbackEPR == null) {
            if (reference.getCallbackService() != null) {
                for (Binding callbackBinding : reference.getCallbackService().getBindings()) {
                    if (callbackBinding instanceof SCABinding) {
                        callbackEPR = new EndpointReferenceImpl(reference.getName() + "/" + callbackBinding.getName());
                        continue;
                    }
                }
            }    
        }
        return callbackEPR;
    }
    
    
    public SCABinding getSCABinding () {
        return binding;
    }
    
    public RuntimeComponent getComponent () {
        return component;
    }
    
    public RuntimeComponentReference getComponentReference () {
        return reference;
    }    

    public void start() {
        axisReferenceBindingProvider.start();
    }

    public void stop() {
        axisReferenceBindingProvider.stop();
    }

}
