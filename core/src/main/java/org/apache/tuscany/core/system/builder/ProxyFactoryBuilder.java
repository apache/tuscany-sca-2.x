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
package org.apache.tuscany.core.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tuscany.core.addressing.AddressingFactory;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.BuilderInitException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.ScopeAwareContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyInitializationException;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.context.RuntimeContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.types.InterfaceType;
import org.apache.tuscany.model.types.OperationType;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

/**
 * A system component that configures proxy factories for references
 * <p>
 * FIXME integrate back with {@link org.apache.tuscany.core.builder.impl.PortRuntimeConfigurationBuilderImpl}
 */

@Scope("MODULE")
public class ProxyFactoryBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

    @ComponentName
    private String name;

    @Autowire
    RuntimeContext runtime;

    @Autowire
    private MessageHandler configurationPipeline;

    @Autowire
    private AddressingFactory addressingFactory;

    @Autowire
    private MessageFactory messageFactory;

    @Property(required = false)
    // the proxy factory, defaults to JDK proxies
    private String proxyFactoryClass;

    private Constructor proxyFactoryConstructor;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ProxyFactoryBuilder() {
    }

    public ProxyFactoryBuilder(MessageHandler configPipeline, AddressingFactory addressingFactory, MessageFactory messageFactory,
            Map<Integer, ScopeContext> scopeContainers) {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    @Init(eager = true)
    public void init() throws BuilderInitException {
        try {
            if (proxyFactoryClass == null) {
                proxyFactoryClass = JDKProxyFactory.class.getName();
                proxyFactoryConstructor = JavaIntrospectionHelper.getDefaultConstructor(JDKProxyFactory.class);
            } else {
                proxyFactoryConstructor = JavaIntrospectionHelper.getDefaultConstructor(JavaIntrospectionHelper
                        .loadClass(proxyFactoryClass));
            }
            runtime.addBuilder(this); // register the builder with the runtime
        } catch (NoSuchMethodException e) {
            BuilderInitException be = new BuilderInitException(e);
            be.setIdentifier(proxyFactoryClass);
            be.addContextName(name);
            throw be;
        } catch (ClassNotFoundException e) {
            BuilderInitException be = new BuilderInitException(e);
            be.addContextName(name);
            throw be;
        }
    }

    private AggregateContext parentContext;

    // scope containers of the parent context
    private Map<Integer, ScopeContext> scopeContexts;

    public void setParentContext(AggregateContext context) {
        assert (parentContext != null) : "Parent context was null";
        parentContext = context;
        if (!(parentContext instanceof ScopeAwareContext)) {
            BuilderInitException e = new BuilderInitException("Parent context is not scope aware");
            e.setIdentifier(parentContext.getName());
            e.addContextName(name);
            throw e;
        }
        scopeContexts = ((ScopeAwareContext) parentContext).getScopeContexts();
    }

    private AssemblyModelObject model;

    public void setModelObject(AssemblyModelObject model) {
        this.model = model;
    }

    public void build() throws BuilderException {
        if (model instanceof Component) {
            try {
                Component component = (Component) model;
                if (component.getComponentImplementation() instanceof SystemImplementation) {
                    return; // system implementations do not use proxies
                }
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
            } catch (ProxyException e) {
                throw new BuilderConfigException(e);
            }
        }
    }

    private ProxyFactory buildProxyFactory(ConfiguredService configuredService) throws ProxyException {
        InterfaceType interfaceType = configuredService.getService().getInterfaceContract().getInterfaceType();

        // Create Proxy configuration
        Map<OperationType, InvocationConfiguration> invocationConfigurations = new HashMap<OperationType, InvocationConfiguration>();
        Class javaInterface = interfaceType.getInstanceClass();
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration(invocationConfigurations, javaInterface.getClassLoader(),
                scopeContexts, messageFactory);

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
        return createProxyFactory(javaInterface, proxyConfiguration);
    }

    private ProxyFactory buildProxyFactory(ConfiguredReference configuredReference) throws ProxyInitializationException {
        InterfaceType interfaceType = configuredReference.getReference().getInterfaceContract().getInterfaceType();

        ConfiguredService configuredService = configuredReference.getConfiguredServices().get(0);

        // Create Proxy configuration
        Map<OperationType, InvocationConfiguration> invocationConfigurations = new HashMap<OperationType, InvocationConfiguration>();
        Class javaInterface = interfaceType.getInstanceClass();
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration(invocationConfigurations, javaInterface.getClassLoader(),
                scopeContexts, messageFactory);

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
        return createProxyFactory(javaInterface, proxyConfiguration);
    }

    /**
     * Creates the proxy factory for the given interface and proxy configuration
     * 
     * @throws ProxyInitializationException
     */
    private ProxyFactory createProxyFactory(Class javaInterface, ProxyConfiguration proxyConfiguration)
            throws ProxyInitializationException {
        try {
            // Create a proxy factory
            ProxyFactory proxyFactory = (ProxyFactory) proxyFactoryConstructor.newInstance((Object[]) null);
            proxyFactory.initialize(javaInterface, proxyConfiguration);
            return proxyFactory;
        } catch (ProxyInitializationException e) {
            e.addContextName(name);
            throw e;
        } catch (IllegalArgumentException e) {
            ProxyInitializationException pe = new ProxyInitializationException(e);
            pe.addContextName(name);
            throw pe;
        } catch (InstantiationException e) {
            ProxyInitializationException pe = new ProxyInitializationException(e);
            pe.addContextName(name);
            throw pe;
        } catch (IllegalAccessException e) {
            ProxyInitializationException pe = new ProxyInitializationException(e);
            pe.addContextName(name);
            throw pe;
        } catch (InvocationTargetException e) {
            ProxyInitializationException pe = new ProxyInitializationException(e);
            pe.addContextName(name);
            throw pe;
        }
    }

}
