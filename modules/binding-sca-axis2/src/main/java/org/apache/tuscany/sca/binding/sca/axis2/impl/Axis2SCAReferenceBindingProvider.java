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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ReferenceBindingProvider;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.domain.SCADomainEventService;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.java2wsdl.Java2WSDLHelper;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.node.NodeFactory;
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

    private final static Logger logger = Logger.getLogger(Axis2SCAReferenceBindingProvider.class.getName());
    
    private NodeFactory nodeFactory;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private Axis2ReferenceBindingProvider axisReferenceBindingProvider;
    private WebServiceBinding wsBinding;
    
    private EndpointReference serviceEPR = null;
    private EndpointReference callbackEPR = null;

    public Axis2SCAReferenceBindingProvider(NodeFactory nodeFactory,
    		                            RuntimeComponent component,
                                            RuntimeComponentReference reference,
                                            DistributedSCABinding binding,
                                            ServletHost servletHost,
                                            MessageFactory messageFactory,
                                            Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames) {
    	this.nodeFactory = nodeFactory;
        this.component = component;
        this.reference = reference;
        this.binding = binding.getSCABinding();
        wsBinding = (new DefaultWebServiceBindingFactory()).createWebServiceBinding();
       
        // Turn the java interface contract into a wsdl interface contract
        InterfaceContract contract = reference.getInterfaceContract();
        if ((contract instanceof JavaInterfaceContract)) {
            contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract);
        }
        
        // Set to use the Axiom data binding
        contract.getInterface().resetDataBinding(OMElement.class.getName());
        
        wsBinding.setBindingInterfaceContract(contract);
        wsBinding.setName(this.binding.getName());         
               
        axisReferenceBindingProvider = new Axis2ReferenceBindingProvider(component,
                                                                         reference,
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
            
            if ( endpointURL == null) {  
                SCADomainEventService domainProxy = (SCADomainEventService)nodeFactory.getNode().getDomain();
                
                try {
                    endpointURL = domainProxy.findServiceEndpoint(nodeFactory.getNode().getDomain().getURI(), 
                                                                  binding.getURI(), 
                                                                  binding.getClass().getName());
                } catch (Exception ex) {
                    logger.log(Level.WARNING, 
                               "Unable to  find service service: "  +
                               nodeFactory.getNode().getDomain().getURI() + " " +
                               nodeFactory.getNode().getURI() + " " +
                               binding.getURI() + " " +
                               binding.getClass().getName());                    
                }
                
                if (endpointURL.equals(domainProxy.SERVICE_NOT_REGISTERED)){
                    throw new IllegalStateException("Can't resolve reference as no SCA binding URI is available while trying to find target service. Component: "+
                                                    component.getName() +
                                                    " and reference: " + 
                                                    reference.getName() ); 
                }   
            }  
        }
        
        binding.setURI(endpointURL);

        serviceEPR = new EndpointReferenceImpl(binding.getURI());
            
        
/*        
        if ( serviceEPR == null && nodeFactory.getNode() != null ){
            // try to resolve the service endpoint with the registry 
            SCADomainEventService domainProxy = (SCADomainEventService)nodeFactory.getNode().getDomain();
            
            if (domainProxy != null){
            
	            // The binding URI might be null in the case where this reference is completely
	            // dynamic, for example, in the case of callbacks
                    String bindingURIString = binding.getURI();
	            if (bindingURIString != null) {
	                String serviceURL  = null;
	                URI bindingURI = null;
	                
	                // first time through here assume that the binding URI is correct
	                try {
	                    bindingURI = new URI(bindingURIString);
	                    if (bindingURI.isAbsolute()){
	                        serviceURL = bindingURIString;
	                    }
	                } catch(Exception ex) {
	                    
	                }
	                
	                if ( serviceURL == null) {   
        	                try {
        	                    serviceURL = domainProxy.findServiceEndpoint(nodeFactory.getNode().getDomain().getURI(), 
        	                                                                 bindingURI.getPath(), 
        	                                                                 binding.getClass().getName());
        	                } catch (Exception ex) {
        	                    logger.log(Level.WARNING, 
        	                               "Unable to  find service service: "  +
        	                               nodeFactory.getNode().getDomain().getURI() + " " +
        	                               nodeFactory.getNode().getURI() + " " +
        	                               binding.getURI() + " " +
        	                               binding.getClass().getName());	                 
        	                }
	                }
	                
	                if ( (serviceURL != null ) &&
	                     (!serviceURL.equals(""))){
	                    serviceEPR = new EndpointReferenceImpl(serviceURL);
	                }
	            }
            } else {
	            throw new IllegalStateException("No domain service available while trying to find component: "+
						                        component.getName() +
						                        " and service: " + 
						                        reference.getName());	 
            }
        }
*/        
        
        return serviceEPR;
    }
    
    /**
     * Go back to the distributed domain to go and get the service endpoint
     * 
     * @return An EPR for the target service that this reference refers to 
     */
    public EndpointReference refreshServiceEndpoint(){ 
        serviceEPR= null;
        return getServiceEndpoint();
    }
    
    /**
     * Retrieves the uri of the callback service (that this reference has created)
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
        // Try and resolve the service endpoint just in case it is available now
       // getServiceEndpoint();
        axisReferenceBindingProvider.start();
    }

    public void stop() {
        axisReferenceBindingProvider.stop();
    }

}
