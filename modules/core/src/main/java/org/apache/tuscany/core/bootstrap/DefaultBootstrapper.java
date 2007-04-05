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
package org.apache.tuscany.core.bootstrap;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.core.binding.local.LocalBindingBuilder;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.component.scope.AbstractScopeContainer;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.component.scope.RequestScopeContainer;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.implementation.composite.CompositeBuilder;
import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.core.resolver.DefaultAutowireResolver;
import org.apache.tuscany.core.wire.IDLMappingService;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.Deployer;

/**
 * A default implementation of a Bootstrapper. Please see the documentation on
 * the individual methods for how the primordial components are created.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultBootstrapper implements Bootstrapper {
    private final MonitorFactory monitorFactory;
    private final XMLInputFactory xmlFactory;
    private final ComponentManager componentManager;
    private final AutowireResolver resolver;
    private final Connector connector;
    private final ScopeRegistry scopeRegistry;
    private final ExtensionRegistry extensionRegistry;

    /**
     * Create a default bootstrapper.
     * 
     * @param monitorFactory the MonitorFactory to be used to create monitors
     *            for the primordial components
     * @param xmlFactory the XMLInputFactory to be used by the components to
     *            load XML artifacts
     * @param componentManager the component manager for the runtime instance
     * @param resolver the autowire resolver for the runtime instance
     * @param connector the connector for the runtime instance
     */
    public DefaultBootstrapper(MonitorFactory monitorFactory,
                               XMLInputFactory xmlFactory,
                               ComponentManager componentManager,
                               AutowireResolver resolver,
                               Connector connector) {
        this.monitorFactory = monitorFactory;
        this.xmlFactory = xmlFactory;
        this.componentManager = componentManager;
        this.resolver = resolver;
        this.connector = connector;
        this.scopeRegistry = createScopeRegistry();
        this.extensionRegistry = new ExtensionRegistryImpl();
    }
    
    public DefaultBootstrapper(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
        this.xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
        this.resolver = new DefaultAutowireResolver(new IDLMappingService());
        this.componentManager = new ComponentManagerImpl(null, this.resolver);
        this.connector = new ConnectorImpl(componentManager);
        this.scopeRegistry = createScopeRegistry();
        this.extensionRegistry = new ExtensionRegistryImpl();
    }    

    /**
     * Returns the MonitorFactory being used by this bootstrapper.
     * 
     * @return the MonitorFactory being used by this bootstrapper
     */
    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    /**
     * Create primordial deployer that can be used to load the system
     * definition.
     * 
     * @return the primordial deployer
     */
    public Deployer createDeployer() {
        ScopeRegistry scopeRegistry = getScopeRegistry();
        BuilderRegistry builder = createBuilder(scopeRegistry);
        DeployerImpl deployer = new DeployerImpl(xmlFactory, builder, componentManager, resolver, connector);
        deployer.setMonitor(getMonitorFactory().getMonitor(ScopeContainerMonitor.class));
        deployer.setScopeRegistry(getScopeRegistry());
        extensionRegistry.addExtension(ScopeRegistry.class, scopeRegistry);
        extensionRegistry.addExtension(BuilderRegistry.class, builder);
        // extensionRegistry.addExtension(LoaderRegistry.class, loader);
        extensionRegistry.addExtension(Deployer.class, deployer);
        return deployer;
    }

    /**
     * Create a basic ScopeRegistry containing the ScopeContainers that are
     * available to components in the system definition. The implementation
     * returned only support COMPOSITE scope.
     * 
     * @return a new ScopeRegistry
     */
    private ScopeRegistry createScopeRegistry() {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        ScopeContainerMonitor monitor = monitorFactory.getMonitor(ScopeContainerMonitor.class);
        AbstractScopeContainer[] containers = new AbstractScopeContainer[] {new CompositeScopeContainer(monitor),
                                                                            new StatelessScopeContainer(monitor),
                                                                            new RequestScopeContainer(monitor),
        // new ConversationalScopeContainer(monitor),
        // new HttpSessionScopeContainer(monitor)
        };
        for (AbstractScopeContainer c : containers) {
            c.start();
            scopeRegistry.register(c);
        }

        return scopeRegistry;
    }

    /**
     * Create a new Connector that can be used to wire primordial components
     * together.
     * 
     * @return a new Connector
     */
    public Connector getConnector() {
        return connector;
    }

    public AutowireResolver getAutowireResolver() {
        return resolver;
    }


    /**
     * Create a Builder that can be used to build the components in the system
     * definition. The default implementation only supports implementations from
     * the system programming model.
     * 
     * @param scopeRegistry the ScopeRegistry defining the component scopes that
     *            will be supported
     * @return a new Builder
     */
    private BuilderRegistry createBuilder(ScopeRegistry scopeRegistry) {
        BuilderRegistryImpl builderRegistry = new BuilderRegistryImpl(scopeRegistry);
        CompositeBuilder compositeBuilder = new CompositeBuilder();
        compositeBuilder.setBuilderRegistry(builderRegistry);
        compositeBuilder.setScopeRegistry(scopeRegistry);
        compositeBuilder.init();
        // builderRegistry.register(CompositeImplementation.class,
        // compositeBuilder);
        builderRegistry.register(LocalBindingDefinition.class, new LocalBindingBuilder());
        return builderRegistry;
    }

    /**
     * @return the componentManager
     */
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    /**
     * @return the scopeRegistry
     */
    public ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    /**
     * @return the extensionRegistry
     */
    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
    }

}
