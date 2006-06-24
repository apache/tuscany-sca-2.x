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

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;

import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
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

/**
 * A Tuscany runtime bootstrapper responsible for instantiating the runtime with the default primordial configuration
 *
 * @version $Rev$ $Date$
 */
public class DefaultBootstrapper {
    private final BuilderRegistry builderRegistry;
    private final LoaderRegistry loaderRegistry;
    private final Connector connector;

    public DefaultBootstrapper(LoaderRegistry loaderRegistry, BuilderRegistry builderRegistry, Connector connector) {
        this.builderRegistry = builderRegistry;
        this.loaderRegistry = loaderRegistry;
        this.connector = connector;
    }

    public DefaultBootstrapper(MonitorFactory monitorFactory) {
        this(getDefaultLoaderRegistry(monitorFactory, new StringParserPropertyFactory()),
            getDefaultBuilderRegistry(),
            getDefaultConnector());
    }

    public Deployer createDeployer() {
        DeployerImpl deployer = new DeployerImpl();
        deployer.setBuilder(builderRegistry);
        deployer.setLoader(loaderRegistry);
        deployer.setConnector(connector);
        return deployer;
    }

    protected static BuilderRegistry getDefaultBuilderRegistry() {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(new WorkContextImpl());
        BuilderRegistry builderRegistry = new BuilderRegistryImpl(scopeRegistry);
        builderRegistry.register(SystemCompositeImplementation.class, new SystemCompositeBuilder(builderRegistry));
        builderRegistry.register(SystemImplementation.class, new SystemComponentBuilder());
        builderRegistry.register(SystemBinding.class, new SystemBindingBuilder());
        return builderRegistry;
    }

    protected static LoaderRegistry getDefaultLoaderRegistry(MonitorFactory monitorFactory,
                                                             StAXPropertyFactory propertyFactory) {
        LoaderRegistryImpl loaderRegistry = new LoaderRegistryImpl();
        loaderRegistry.setMonitor(monitorFactory.getMonitor(LoaderRegistryImpl.Monitor.class));

        // load default processors
        IntrospectionRegistryImpl introspectionRegistry = new IntrospectionRegistryImpl();
        introspectionRegistry
            .setMonitor(monitorFactory.getMonitor(IntrospectionRegistryImpl.IntrospectionMonitor.class));
        introspectionRegistry.registerProcessor(new DestroyProcessor());
        introspectionRegistry.registerProcessor(new InitProcessor());
        introspectionRegistry.registerProcessor(new ScopeProcessor());
        introspectionRegistry.registerProcessor(new PropertyProcessor());
        introspectionRegistry.registerProcessor(new ReferenceProcessor());
        introspectionRegistry.registerProcessor(new ServiceProcessor());

        // register component type loaders
        loaderRegistry.registerLoader(SystemImplementation.class,
            new SystemComponentTypeLoader(introspectionRegistry));
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

    protected static void registerLoader(LoaderRegistry registry, LoaderExtension<?> loader) {
        registry.registerLoader(loader.getXMLType(), loader);
    }

    protected static Connector getDefaultConnector() {
        return new ConnectorImpl();
    }
}
