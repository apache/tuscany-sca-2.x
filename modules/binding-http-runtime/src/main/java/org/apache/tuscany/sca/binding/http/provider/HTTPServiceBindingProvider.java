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

package org.apache.tuscany.sca.binding.http.provider;

import java.util.List;

import javax.servlet.Servlet;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.SecurityContext;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.authentication.AuthenticationConfigurationPolicy;
import org.apache.tuscany.sca.policy.confidentiality.ConfidentialityPolicy;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProviderRRB;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of an HTTP binding provider.
 *
 * @version $Rev$ $Date$
 */
public class HTTPServiceBindingProvider implements ServiceBindingProviderRRB {
    private static final QName AUTEHTICATION_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0","authentication");
    private static final QName CONFIDENTIALITY_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0","confidentiality");
    
    private ExtensionPointRegistry extensionPoints;
    
    private RuntimeComponent component;
    private RuntimeComponentService service;  
    private InterfaceContract serviceContract;
    private HTTPBinding binding;
    private MessageFactory messageFactory;
    
    private OperationSelectorProvider osProvider;
    private WireFormatProvider wfProvider;
    
    private ServletHost servletHost;
    private String servletMapping;
    private HTTPBindingListenerServlet bindingListenerServlet;
   
    public HTTPServiceBindingProvider(RuntimeComponent component,
                                      RuntimeComponentService service,
                                      HTTPBinding binding,
                                      ExtensionPointRegistry extensionPoints,
                                      MessageFactory messageFactory,
                                      ServletHost servletHost) {
        this.component = component;
        this.service = service;
        
        this.binding = binding;
        this.extensionPoints = extensionPoints;
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;
        
        // retrieve operation selector and wire format service providers
        
        ProviderFactoryExtensionPoint  providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        
        if (binding.getOperationSelector() != null) {
            // Configure the interceptors for operation selection
            OperationSelectorProviderFactory osProviderFactory = (OperationSelectorProviderFactory) providerFactories.getProviderFactory(binding.getOperationSelector().getClass());
            if (osProviderFactory != null) {
                this.osProvider = osProviderFactory.createServiceOperationSelectorProvider(component, service, binding);
            }            
        }
        
        if (binding.getRequestWireFormat() != null && binding.getResponseWireFormat() != null) {
            // Configure the interceptors for wire format
            WireFormatProviderFactory wfProviderFactory = (WireFormatProviderFactory) providerFactories.getProviderFactory(binding.getRequestWireFormat().getClass());
            if (wfProviderFactory != null) {
                this.wfProvider = wfProviderFactory.createServiceWireFormatProvider(component, service, binding);
            }            
        }

        
        
        //clone the service contract to avoid databinding issues
        try {
            this.serviceContract = (InterfaceContract) service.getInterfaceContract().clone();
            
            // configure data binding
            if (this.wfProvider != null) {
                wfProvider.configureWireFormatInterfaceContract(service.getInterfaceContract());
            }
        } catch(CloneNotSupportedException e) {
            this.serviceContract = service.getInterfaceContract();
        }
        
    }

    public void start() {
        // Get the invokers for the supported operations
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        Servlet servlet = null;
        bindingListenerServlet = new HTTPBindingListenerServlet(binding, messageFactory );
        for (InvocationChain invocationChain : wire.getInvocationChains()) {
            Operation operation = invocationChain.getTargetOperation();
            String operationName = operation.getName();
            if (operationName.equals("get")) { 
                Invoker getInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setGetInvoker(getInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalGet")) {
                Invoker conditionalGetInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setConditionalGetInvoker(conditionalGetInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("delete")) {
                Invoker deleteInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setDeleteInvoker(deleteInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalDelete")) {
                Invoker conditionalDeleteInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setConditionalDeleteInvoker(conditionalDeleteInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("put")) {
                Invoker putInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setPutInvoker(putInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalPut")) {
                Invoker conditionalPutInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setConditionalPutInvoker(conditionalPutInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("post")) {
                Invoker postInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setPostInvoker(postInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalPost")) {
                Invoker conditionalPostInvoker = invocationChain.getHeadInvoker();
               	bindingListenerServlet.setConditionalPostInvoker(conditionalPostInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("service")) {
                Invoker serviceInvoker = invocationChain.getHeadInvoker();
                servlet = new HTTPServiceListenerServlet(binding, serviceInvoker, messageFactory);
                break;
            } else if (binding.getOperationSelector() != null || binding.getRequestWireFormat() != null) {
                Invoker bindingInvoker = wire.getBindingInvocationChain().getHeadInvoker();
                servlet = new HTTPRRBListenerServlet(binding, bindingInvoker, messageFactory);
                break;
            }
        }
        if (servlet == null) {
            throw new IllegalStateException("No get or service method found on the service");
        }
                
        // Create our HTTP service listener Servlet and register it with the
        // Servlet host
        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
        

        SecurityContext securityContext = new SecurityContext();
        boolean isConfidentialityRequired = false;
        boolean isAuthenticationRequired = false;
        
        
        // find out which policies are active
        if (binding instanceof PolicySetAttachPoint) {
            List<Intent> intents = ((PolicySetAttachPoint)binding).getRequiredIntents();
            for(Intent intent : intents) {
                if (intent.getName().equals(AUTEHTICATION_INTENT)) {
                    isAuthenticationRequired = true;
                } else if (intent.getName().equals(CONFIDENTIALITY_INTENT)) {
                    isConfidentialityRequired = true;
                }
            }
            
            List<PolicySet> policySets = ((PolicySetAttachPoint)binding).getApplicablePolicySets();
            for (PolicySet ps : policySets) {
                for (Object p : ps.getPolicies()) {
                    if (ConfidentialityPolicy.class.isInstance(p) && isConfidentialityRequired) {
                        //Handle enabling and configuring SSL
                        ConfidentialityPolicy confidentialityPolicy = (ConfidentialityPolicy)p;                        
                        
                        securityContext.setSSLEnabled(true);
                        securityContext.setSSLProperties(confidentialityPolicy.toProperties());
                    } else if(AuthenticationConfigurationPolicy.class.isInstance(p) && isAuthenticationRequired) {
                        // Handle authentication and user configuration
                        AuthenticationConfigurationPolicy authenticationConfiguration = (AuthenticationConfigurationPolicy)p;
                        
                        securityContext.setAuthenticationEnabled(true);
                        securityContext.getUsers().clear();
                        securityContext.getUsers().addAll(authenticationConfiguration.getUsers());
                    }
                }
            }
        }        
        
        
        servletHost.addServletMapping(servletMapping, servlet, securityContext);
    }

    public void stop() {        
        // Unregister the Servlet from the Servlet host
        servletHost.removeServletMapping(servletMapping);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }
    
    /**
     * Add specific http interceptor to invocation chain
     * @param runtimeWire
     */
    public void configureBindingChain(RuntimeWire runtimeWire) {

        InvocationChain bindingChain = runtimeWire.getBindingInvocationChain();

        if(osProvider != null) {
            bindingChain.addInterceptor(Phase.SERVICE_BINDING_OPERATION_SELECTOR, osProvider.createInterceptor());    
        }

        if (wfProvider != null) {
            bindingChain.addInterceptor(Phase.SERVICE_BINDING_WIREFORMAT, wfProvider.createInterceptor());
        }

    }

}
