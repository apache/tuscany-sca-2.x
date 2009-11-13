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

package org.apache.tuscany.sca.binding.jms.provider;

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.host.JMSServiceListener;
import org.apache.tuscany.sca.binding.jms.host.JMSServiceListenerDetails;
import org.apache.tuscany.sca.binding.jms.host.JMSServiceListenerFactory;
import org.apache.tuscany.sca.binding.jms.transport.TransportServiceInterceptor;
import org.apache.tuscany.sca.binding.jms.wire.CallbackDestinationInterceptor;
import org.apache.tuscany.sca.binding.jms.wire.OperationPropertiesInterceptor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Implementation of the JMS service binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingServiceBindingProvider implements EndpointProvider, JMSServiceListenerDetails {
    private static final Logger logger = Logger.getLogger(JMSBindingServiceBindingProvider.class.getName());

    private ExtensionPointRegistry registry;
    private RuntimeEndpoint endpoint;
    private RuntimeComponentService service;
    private Binding targetBinding;
    private JMSBinding jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private JMSServiceListenerFactory serviceListenerFactory;
    private JMSServiceListener serviceListener;

    private RuntimeComponent component;
    private InterfaceContract interfaceContract;
    
    private ProviderFactoryExtensionPoint providerFactories;
    private FactoryExtensionPoint modelFactories;
    
    private MessageFactory messageFactory;
    
    private OperationSelectorProviderFactory operationSelectorProviderFactory;
    private OperationSelectorProvider operationSelectorProvider;
    
    private WireFormatProviderFactory requestWireFormatProviderFactory;
    private WireFormatProvider requestWireFormatProvider;
    
    private WireFormatProviderFactory responseWireFormatProviderFactory;
    private WireFormatProvider responseWireFormatProvider;

    public JMSBindingServiceBindingProvider(ExtensionPointRegistry registry, RuntimeEndpoint endpoint, JMSServiceListenerFactory serviceListenerFactory, ExtensionPointRegistry extensionPoints, JMSResourceFactory jmsResourceFactory) {
        this.endpoint = endpoint;
        this.component = (RuntimeComponent) endpoint.getComponent();
        this.service = (RuntimeComponentService) endpoint.getService();
        this.jmsBinding = (JMSBinding) endpoint.getBinding();
        this.serviceListenerFactory = serviceListenerFactory;
        this.targetBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.registry = registry;

        // Set the default destination when using a connection factory.
        // If an activation spec is being used, do not set the destination
        // because the activation spec provides the destination.
        if (jmsBinding.getDestinationName() == null &&
            (jmsBinding.getActivationSpecName() == null || jmsBinding.getActivationSpecName().equals(""))) {
//          if (!service.isCallback()) { // TODO: 2.x migration, is this check needed?
                // use the SCA service name as the default destination name
                jmsBinding.setDestinationName(service.getName());
//            }
        }
        
        // Get Message factory
        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        messageFactory = modelFactories.getFactory(MessageFactory.class);

        // Get the factories/providers for operation selection       
        this.providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        this.operationSelectorProviderFactory =
            (OperationSelectorProviderFactory)providerFactories.getProviderFactory(jmsBinding.getOperationSelector().getClass());
        if (this.operationSelectorProviderFactory != null){
            this.operationSelectorProvider = operationSelectorProviderFactory.createServiceOperationSelectorProvider(endpoint);
        }
        
        // Get the factories/providers for wire format        
        this.requestWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getRequestWireFormat().getClass());
        if (this.requestWireFormatProviderFactory != null){
            this.requestWireFormatProvider = requestWireFormatProviderFactory.createServiceWireFormatProvider(endpoint);
        }
        
        this.responseWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getResponseWireFormat().getClass());
        if (this.responseWireFormatProviderFactory != null){
            this.responseWireFormatProvider = responseWireFormatProviderFactory.createServiceWireFormatProvider(endpoint);
        }
        
        // create an interface contract that reflects both request and response
        // wire formats
        try {
            interfaceContract = (InterfaceContract)service.getInterfaceContract().clone();
            
            requestWireFormatProvider.configureWireFormatInterfaceContract(interfaceContract);
            responseWireFormatProvider.configureWireFormatInterfaceContract(interfaceContract);
        } catch (CloneNotSupportedException ex){
            interfaceContract = service.getInterfaceContract();
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return interfaceContract;
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public void start() {
        try {

            this.serviceListener = serviceListenerFactory.createJMSServiceListener(this);
            serviceListener.start();
            
        } catch (Exception e) {
            throw new JMSBindingException("Error starting JMSServiceBinding", e);
        }
    }

    public void stop() {
        try {
            serviceListener.stop();
        } catch (Exception e) {
            throw new JMSBindingException("Error stopping JMSServiceBinding", e);
        }
    }

    public String getDestinationName() {
        return serviceListener.getDestinationName();
    }
    
    /*
     * Adds JMS specific interceptors to the binding chain
     */
    public void configure() {
        
        InvocationChain bindingChain = endpoint.getBindingInvocationChain();
        
        // add transport interceptor
        bindingChain.addInterceptor(Phase.SERVICE_BINDING_TRANSPORT, 
                                    new TransportServiceInterceptor(registry, jmsBinding,
                                                                    jmsResourceFactory,
                                                                    endpoint) );

        // add operation selector interceptor
        bindingChain.addInterceptor(operationSelectorProvider.getPhase(), 
                                    operationSelectorProvider.createInterceptor());

        // add operationProperties interceptor after operation selector
        bindingChain.addInterceptor(Phase.SERVICE_BINDING_OPERATION_SELECTOR,
                                    new OperationPropertiesInterceptor(jmsBinding, endpoint));

        // add callback destination interceptor after operation selector
        bindingChain.addInterceptor(Phase.SERVICE_BINDING_WIREFORMAT,
                                    new CallbackDestinationInterceptor(endpoint));

        // add request wire format
        bindingChain.addInterceptor(requestWireFormatProvider.getPhase(), 
                                    requestWireFormatProvider.createInterceptor());
        
        // add response wire format, but only add it if it's different from the request
        if (!jmsBinding.getRequestWireFormat().equals(jmsBinding.getResponseWireFormat())){
            bindingChain.addInterceptor(responseWireFormatProvider.getPhase(), 
                                        responseWireFormatProvider.createInterceptor());
        }
    }

    public RuntimeComponent getComponent() {
        return component;
    }

    public RuntimeComponentService getService() {
        return service;
    }

    public Binding getTargetBinding() {
        return targetBinding;
    }

    public JMSBinding getJmsBinding() {
        return jmsBinding;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }
    
    public JMSResourceFactory getResourceFactory() {
        return jmsResourceFactory;
    }

    public RuntimeEndpoint getEndpoint() {
        return endpoint;
    }

}
