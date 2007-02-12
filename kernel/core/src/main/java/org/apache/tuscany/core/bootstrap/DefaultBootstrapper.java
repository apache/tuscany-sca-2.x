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

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.Introspector;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;

import org.apache.tuscany.core.binding.local.LocalBindingBuilder;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.binding.local.LocalBindingLoader;
import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.CompositeScopeObjectFactory;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.idl.java.InterfaceJavaLoader;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.composite.CompositeLoader;
import org.apache.tuscany.core.implementation.composite.SystemCompositeBuilder;
import org.apache.tuscany.core.implementation.processor.ConstructorProcessor;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.EagerInitProcessor;
import org.apache.tuscany.core.implementation.processor.HeuristicPojoProcessor;
import org.apache.tuscany.core.implementation.processor.ImplementationProcessorServiceImpl;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.MonitorProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ResourceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.implementation.processor.ServiceProcessor;
import org.apache.tuscany.core.implementation.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.implementation.system.loader.SystemComponentTypeLoader;
import org.apache.tuscany.core.implementation.system.loader.SystemCompositeComponentTypeLoader;
import org.apache.tuscany.core.implementation.system.loader.SystemImplementationLoader;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.loader.ComponentLoader;
import org.apache.tuscany.core.loader.ComponentTypeElementLoader;
import org.apache.tuscany.core.loader.IncludeLoader;
import org.apache.tuscany.core.loader.LoaderRegistryImpl;
import org.apache.tuscany.core.loader.PropertyLoader;
import org.apache.tuscany.core.loader.ReferenceLoader;
import org.apache.tuscany.core.loader.ServiceLoader;
import org.apache.tuscany.core.property.PropertyObjectFactoryImpl;
import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.apache.tuscany.host.MonitorFactory;

/**
 * A default implementation of a Bootstrapper. Please see the documentation on the individual methods for how the
 * primordial components are created.
 *
 * @version $Rev$ $Date$
 */
public class DefaultBootstrapper implements Bootstrapper {
    private final MonitorFactory monitorFactory;
    private final XMLInputFactory xmlFactory;
    private final ComponentManager componentManager;
    private final AutowireResolver resolver;
    private final Connector connector;

    /**
     * Create a default bootstrapper.
     *
     * @param monitorFactory   the MonitorFactory to be used to create monitors for the primordial components
     * @param xmlFactory       the XMLInputFactory to be used by the components to load XML artifacts
     * @param componentManager the component manager for the runtime instance
     * @param resolver         the autowire resolver for the runtime instance
     * @param connector        the connector for the runtime instance
     */
    public DefaultBootstrapper(MonitorFactory monitorFactory,
                               XMLInputFactory xmlFactory,
                               ComponentManager componentManager,
                               AutowireResolver resolver,
                               Connector connector
    ) {
        this.monitorFactory = monitorFactory;
        this.xmlFactory = xmlFactory;
        this.componentManager = componentManager;
        this.resolver = resolver;
        this.connector = connector;
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
     * Create the RuntimeComponent that will form the root of the component tree. Returns an new instance of a {@link
     * DefaultRuntime} with the system and application root components initialized with default composite components.
     *
     * @return a newly created root for the component tree
     */
    public RuntimeComponent createRuntime() {
        DefaultRuntime runtime = new DefaultRuntime();
        CompositeComponent systemComponent =
            new CompositeComponentImpl(ComponentNames.TUSCANY_SYSTEM_ROOT, null);
        runtime.setSystemComponent(systemComponent);
        CompositeComponent rootComponent =
            new CompositeComponentImpl(ComponentNames.TUSCANY_APPLICATION_ROOT, null);
        runtime.setRootComponent(rootComponent);
        return runtime;
    }

    /**
     * Create primordial deployer that can be used to load the system definition.
     *
     * @return the primordial deployer
     */
    public Deployer createDeployer() {
        ScopeRegistry scopeRegistry = createScopeRegistry(new WorkContextImpl());
        Builder builder = createBuilder(scopeRegistry);
        JavaInterfaceProcessorRegistry interfaceIntrospector = new JavaInterfaceProcessorRegistryImpl();
        Introspector introspector = createIntrospector(interfaceIntrospector);
        LoaderRegistry loader = createLoader(new PropertyObjectFactoryImpl(), introspector);
        DeployerImpl deployer = new DeployerImpl(xmlFactory, loader, builder, resolver, componentManager, connector);
        deployer.setMonitor(getMonitorFactory().getMonitor(ScopeContainerMonitor.class));
        return deployer;
    }

    /**
     * Create a basic ScopeRegistry containing the ScopeContainers that are available to components in the system
     * definition. The implementation returned only support COMPOSITE scope.
     *
     * @param workContext the WorkContext the scopes should use
     * @return a new ScopeRegistry
     */
    public ScopeRegistry createScopeRegistry(WorkContext workContext) {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        new CompositeScopeObjectFactory(scopeRegistry, monitorFactory.getMonitor(ScopeContainerMonitor.class));
        return scopeRegistry;
    }

    /**
     * Create a Loader that can be used to parse an XML file containing the SCDL for the system definition. The
     * following Implementation types are supported: <ul> <li>SystemImplementation</li>
     * <li>SystemCompositeImplementation</li> </ul> and the following SCDL elements are supported: <ul>
     * <li>composite</li> <li>component</li> <li>componentType</li> <li>interface.java</li> <li>property</li>
     * <li>reference</li> <li>service</li> <li>implementation.system</li> </ul> Note the Java component type and the
     * WSDL interface type are not supported.
     *
     * @param propertyFactory the StAXPropertyFactory to be used for parsing Property values
     * @param introspector    the Introspector to be used to inspect component implementations
     * @return a new StAX XML loader
     */
    public LoaderRegistry createLoader(PropertyObjectFactory propertyFactory, Introspector introspector) {
        LoaderRegistryImpl loaderRegistry =
            new LoaderRegistryImpl(monitorFactory.getMonitor(LoaderRegistryImpl.Monitor.class));

        // register component type loaders
        loaderRegistry.registerLoader(SystemImplementation.class, new SystemComponentTypeLoader(introspector));
        loaderRegistry.registerLoader(SystemCompositeImplementation.class,
            new SystemCompositeComponentTypeLoader(loaderRegistry));

        // register element loaders
        registerLoader(loaderRegistry, new ComponentLoader(loaderRegistry, propertyFactory));
        registerLoader(loaderRegistry, new ComponentTypeElementLoader(loaderRegistry));
        registerLoader(loaderRegistry, new CompositeLoader(loaderRegistry, null));
        registerLoader(loaderRegistry, new IncludeLoader(loaderRegistry));
        JavaInterfaceProcessorRegistryImpl processorRegistry = new JavaInterfaceProcessorRegistryImpl();
        registerLoader(loaderRegistry, new InterfaceJavaLoader(loaderRegistry, processorRegistry));
        registerLoader(loaderRegistry, new PropertyLoader(loaderRegistry));
        registerLoader(loaderRegistry, new ReferenceLoader(loaderRegistry));
        registerLoader(loaderRegistry, new ServiceLoader(loaderRegistry));
        registerLoader(loaderRegistry, new SystemImplementationLoader(loaderRegistry));
        registerLoader(loaderRegistry, new LocalBindingLoader(loaderRegistry));
        return loaderRegistry;
    }

    /**
     * Create new Introspector for extracting a ComponentType definition from a Java class.
     *
     * @return a new Introspector
     */
    public Introspector createIntrospector(JavaInterfaceProcessorRegistry registry) {
        ImplementationProcessorService service = new ImplementationProcessorServiceImpl(registry);
        IntrospectionRegistryImpl.Monitor monitor = monitorFactory.getMonitor(IntrospectionRegistryImpl.Monitor.class);
        IntrospectionRegistryImpl introspectionRegistry = new IntrospectionRegistryImpl(monitor);
        introspectionRegistry.registerProcessor(new ConstructorProcessor(service));
        introspectionRegistry.registerProcessor(new DestroyProcessor());
        introspectionRegistry.registerProcessor(new InitProcessor());
        introspectionRegistry.registerProcessor(new EagerInitProcessor());
        introspectionRegistry.registerProcessor(new ScopeProcessor());
        introspectionRegistry.registerProcessor(new PropertyProcessor(service));
        introspectionRegistry.registerProcessor(new ReferenceProcessor(registry));
        introspectionRegistry.registerProcessor(new ResourceProcessor());
        introspectionRegistry.registerProcessor(new ServiceProcessor(service));
        introspectionRegistry.registerProcessor(new HeuristicPojoProcessor(service));
        introspectionRegistry.registerProcessor(new MonitorProcessor(monitorFactory, service));
        return introspectionRegistry;
    }

    /**
     * Create a new Connector that can be used to wire primordial components together.
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
     * Helper method for registering a loader with the registry. The Loader is registered once for the QName returned by
     * its {@link LoaderExtension#getXMLType()} method.
     *
     * @param registry the LoaderRegistry to register with
     * @param loader   the Loader to register
     */
    protected void registerLoader(LoaderRegistry registry, LoaderExtension<?> loader) {
        registry.registerLoader(loader.getXMLType(), loader);
    }

    /**
     * Create a Builder that can be used to build the components in the system definition. The default implementation
     * only supports implementations from the system programming model.
     *
     * @param scopeRegistry the ScopeRegistry defining the component scopes that will be supported
     * @return a new Builder
     */
    private Builder createBuilder(ScopeRegistry scopeRegistry) {
        BuilderRegistryImpl builderRegistry =
            new BuilderRegistryImpl(scopeRegistry, new JDKWireService(), componentManager);
        SystemCompositeBuilder builder = new SystemCompositeBuilder(builderRegistry);
        builderRegistry.register(SystemCompositeImplementation.class, builder);
        builderRegistry.register(SystemImplementation.class, new SystemComponentBuilder());
        builderRegistry.register(LocalBindingDefinition.class, new LocalBindingBuilder());
        return builderRegistry;
    }

}
