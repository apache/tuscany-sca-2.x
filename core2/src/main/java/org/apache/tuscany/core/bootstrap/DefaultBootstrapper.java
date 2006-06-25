/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.bootstrap;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.component.scope.ModuleScopeObjectFactory;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.Introspector;
import org.apache.tuscany.core.implementation.composite.CompositeLoader;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.implementation.processor.ServiceProcessor;
import org.apache.tuscany.core.implementation.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.implementation.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.implementation.system.builder.SystemCompositeBuilder;
import org.apache.tuscany.core.implementation.system.loader.SystemBindingLoader;
import org.apache.tuscany.core.implementation.system.loader.SystemComponentTypeLoader;
import org.apache.tuscany.core.implementation.system.loader.SystemCompositeComponentTypeLoader;
import org.apache.tuscany.core.implementation.system.loader.SystemImplementationLoader;
import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.loader.ComponentLoader;
import org.apache.tuscany.core.loader.ComponentTypeElementLoader;
import org.apache.tuscany.core.loader.InterfaceJavaLoader;
import org.apache.tuscany.core.loader.LoaderRegistryImpl;
import org.apache.tuscany.core.loader.PropertyLoader;
import org.apache.tuscany.core.loader.ReferenceLoader;
import org.apache.tuscany.core.loader.ServiceLoader;
import org.apache.tuscany.core.loader.StringParserPropertyFactory;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;
import org.apache.tuscany.spi.model.Scope;

/**
 * A Tuscany runtime bootstrapper responsible for instantiating the runtime with the default primordial configuration
 *
 * @version $Rev$ $Date$
 */
public class DefaultBootstrapper implements Bootstrapper {
    private final MonitorFactory monitorFactory;
    private final XMLInputFactory xmlFactory;

    public DefaultBootstrapper(MonitorFactory monitorFactory, XMLInputFactory xmlFactory) {
        this.monitorFactory = monitorFactory;
        this.xmlFactory = xmlFactory;
    }

    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    public Deployer createDeployer() {
        ScopeRegistry scopeRegistry = createScopeRegistry(new WorkContextImpl());
        BuilderRegistry builder = createBuilder(scopeRegistry);
        Introspector introspector = createIntrospector();
        LoaderRegistry loader = createLoader(new StringParserPropertyFactory(), introspector);
        Connector connector = createConnector();
        return new DeployerImpl(xmlFactory, loader, builder, connector);
    }

    public ScopeRegistry createScopeRegistry(WorkContext workContext) {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        scopeRegistry.registerFactory(Scope.MODULE, new ModuleScopeObjectFactory());
        return scopeRegistry;
    }

    public BuilderRegistry createBuilder(ScopeRegistry scopeRegistry) {
        BuilderRegistry builderRegistry = new BuilderRegistryImpl(scopeRegistry);
        builderRegistry.register(SystemCompositeImplementation.class, new SystemCompositeBuilder(builderRegistry));
        builderRegistry.register(SystemImplementation.class, new SystemComponentBuilder());
        builderRegistry.register(SystemBinding.class, new SystemBindingBuilder());
        return builderRegistry;
    }

    public LoaderRegistry createLoader(StAXPropertyFactory propertyFactory, Introspector introspector) {
        LoaderRegistryImpl loaderRegistry =
                new LoaderRegistryImpl(monitorFactory.getMonitor(LoaderRegistryImpl.Monitor.class));

        // register component type loaders
        loaderRegistry.registerLoader(SystemImplementation.class,
            new SystemComponentTypeLoader(introspector));
        loaderRegistry.registerLoader(SystemCompositeImplementation.class,
            new SystemCompositeComponentTypeLoader(loaderRegistry));

        // register element loaders
        registerLoader(loaderRegistry, new ComponentLoader(loaderRegistry, propertyFactory));
        registerLoader(loaderRegistry, new ComponentTypeElementLoader(loaderRegistry));
        registerLoader(loaderRegistry, new CompositeLoader(loaderRegistry));
        registerLoader(loaderRegistry, new InterfaceJavaLoader(loaderRegistry));
        registerLoader(loaderRegistry, new PropertyLoader(loaderRegistry));
        registerLoader(loaderRegistry, new ReferenceLoader(loaderRegistry));
        registerLoader(loaderRegistry, new ServiceLoader(loaderRegistry));

        registerLoader(loaderRegistry, new SystemImplementationLoader(loaderRegistry));
        registerLoader(loaderRegistry, new SystemBindingLoader(loaderRegistry));
        return loaderRegistry;
    }

    protected void registerLoader(LoaderRegistry registry, LoaderExtension<?> loader) {
        registry.registerLoader(loader.getXMLType(), loader);
    }

    public Introspector createIntrospector() {
        IntrospectionRegistryImpl introspectionRegistry =
                new IntrospectionRegistryImpl(monitorFactory.getMonitor(IntrospectionRegistryImpl.Monitor.class));

        introspectionRegistry.registerProcessor(new DestroyProcessor());
        introspectionRegistry.registerProcessor(new InitProcessor());
        introspectionRegistry.registerProcessor(new ScopeProcessor());
        introspectionRegistry.registerProcessor(new PropertyProcessor());
        introspectionRegistry.registerProcessor(new ReferenceProcessor());
        introspectionRegistry.registerProcessor(new ServiceProcessor());
        return introspectionRegistry;
    }

    public Connector createConnector() {
        return new ConnectorImpl();
    }
}
