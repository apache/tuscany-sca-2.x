/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.builder.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.core.addressing.AddressingFactory;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyInitializationException;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.types.InterfaceType;
import org.apache.tuscany.model.types.OperationType;

/**
 */

//FIXME this is a temporary builder for proxy factories, the builder walks the whole graph right
// now, but basically the it'll have to be integrated into a pluggable builder

public class PortRuntimeConfigurationBuilderImpl implements AssemblyModelVisitor {

    private MessageHandler configurationPipeline;
    private AddressingFactory addressingFactory;
    private MessageFactory messageFactory;
    private Map<Integer,ScopeContext> scopeContainers;

    /**
     * Constructor
     */
    public PortRuntimeConfigurationBuilderImpl(MessageHandler creationPipeline, AddressingFactory addressingFactory, MessageFactory messageFactory, Map<Integer,ScopeContext> scopeContainers) {
        super();
        this.configurationPipeline = creationPipeline;
        this.addressingFactory = addressingFactory;
        this.messageFactory = messageFactory;
        this.scopeContainers = scopeContainers;
    }

    /**
     * Build all proxies on all the ports in the module.
     *
     * @param module
     */
    public void build(Module module) {
        module.accept(this);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelVisitor#visit(org.apache.tuscany.model.assembly.AssemblyModelObject)
     */
    public boolean visit(AssemblyModelObject modelObject) {
        if (modelObject instanceof Component) {
            Component component = (Component) modelObject;

            for (Iterator<ConfiguredService> i = component.getConfiguredServices().iterator(); i.hasNext();) {
                ConfiguredService configuredService = i.next();
                ProxyFactory proxyFactory = buildProxyFactory(configuredService);
                configuredService.setProxyFactory(proxyFactory);
            }

            for (Iterator<ConfiguredReference> i = component.getConfiguredReferences().iterator(); i.hasNext();) {
                ConfiguredReference configuredReference = i.next();
                InterfaceType interfaceType = configuredReference.getReference().getInterfaceContract().getInterfaceType();
                Class businessInterface = interfaceType.getInstanceClass();
                ProxyFactory proxyFactory = buildProxyFactory(configuredReference);
                configuredReference.setProxyFactory(proxyFactory);
            }

        }
        return true;
    }

    private ProxyFactory buildProxyFactory(ConfiguredService configuredService) {
        InterfaceType interfaceType = configuredService.getService().getInterfaceContract().getInterfaceType();

        // Create Proxy configuration
        Map<OperationType, InvocationConfiguration> invocationConfigurations = new HashMap<OperationType, InvocationConfiguration>();
        Class javaInterface=interfaceType.getInstanceClass();
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration(invocationConfigurations, javaInterface.getClassLoader(), scopeContainers, messageFactory);
        
        // Create invocation configurations for all the operations on the business interface
        for (OperationType operationType : interfaceType.getOperationTypes()) {
            invocationConfigurations.put(operationType, new InvocationConfiguration(operationType));
        }
 
        // Create a message
        Message message = messageFactory.createMessage();
        EndpointReference endpointReference = addressingFactory.createEndpointReference();
        endpointReference.setConfiguredPort(configuredService);
        message.setEndpointReference(endpointReference);
        message.setBody(proxyConfiguration);

        configurationPipeline.processMessage(message);

        // Create a proxy factory
        ProxyFactory proxyFactory = new JDKProxyFactory();
        try {
            proxyFactory.initialize(javaInterface, proxyConfiguration);
        } catch (ProxyInitializationException e) {
            throw new ServiceRuntimeException(e);
        }
        return proxyFactory;
    }

    private ProxyFactory buildProxyFactory(ConfiguredReference configuredReference) {
        InterfaceType interfaceType = configuredReference.getReference().getInterfaceContract().getInterfaceType();
        
        ConfiguredService configuredService=configuredReference.getConfiguredServices().get(0);

        // Create Proxy configuration
        Map<OperationType, InvocationConfiguration> invocationConfigurations = new HashMap<OperationType, InvocationConfiguration>();
        Class javaInterface=interfaceType.getInstanceClass();
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration(invocationConfigurations, javaInterface.getClassLoader(), scopeContainers, messageFactory);
        
        // Create invocation configurations for all the operations on the business interface
        for (OperationType operationType : interfaceType.getOperationTypes()) {
            invocationConfigurations.put(operationType, new InvocationConfiguration(operationType));
        }
 
        // Create a message
        Message message = messageFactory.createMessage();
        EndpointReference endpointReference = addressingFactory.createEndpointReference();
        endpointReference.setConfiguredPort(configuredService);
        message.setEndpointReference(endpointReference);
        EndpointReference fromEndpointReference = addressingFactory.createEndpointReference();
        fromEndpointReference.setConfiguredPort(configuredReference);
        message.setFrom(fromEndpointReference);
        message.setBody(proxyConfiguration);

        // Send the message to the configuration pipeline
        configurationPipeline.processMessage(message);
        
        // Build the proxy configurations
        for (InvocationConfiguration invocationConfiguration : invocationConfigurations.values()) {
            invocationConfiguration.build();
        }

        // Create a proxy factory
        ProxyFactory proxyFactory = new JDKProxyFactory();
        try {
            proxyFactory.initialize(javaInterface, proxyConfiguration);
        } catch (ProxyInitializationException e) {
            throw new ServiceRuntimeException(e);
        }
        return proxyFactory;
    }
}
