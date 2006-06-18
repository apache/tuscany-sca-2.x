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
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;

import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.composite.CompositeLoader;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.loader.AssemblyConstants;
import org.apache.tuscany.core.loader.ComponentLoader;
import org.apache.tuscany.core.loader.ComponentTypeElementLoader;
import org.apache.tuscany.core.loader.InterfaceJavaLoader;
import org.apache.tuscany.core.loader.LoaderRegistryImpl;
import org.apache.tuscany.core.loader.PropertyLoader;
import org.apache.tuscany.core.loader.ReferenceLoader;
import org.apache.tuscany.core.loader.ServiceLoader;
import org.apache.tuscany.core.loader.StringParserPropertyFactory;
import org.apache.tuscany.core.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.system.builder.SystemCompositeBuilder;
import org.apache.tuscany.core.system.loader.SystemBindingLoader;
import org.apache.tuscany.core.system.loader.SystemComponentTypeLoader;
import org.apache.tuscany.core.system.loader.SystemCompositeComponentTypeLoader;
import org.apache.tuscany.core.system.loader.SystemImplementationLoader;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.system.model.SystemImplementation;

/**
 * A Tuscany runtime bootstrapper responsible for instantiating the runtime with the default primordial configuration
 *
 * @version $Rev$ $Date$
 */
public class DefaultBootstrapper {
    private final BuilderRegistry builderRegistry;
    private final LoaderRegistry loaderRegistry;
    private final Connector connector;

    public DefaultBootstrapper(LoaderRegistry loaderRegistry,
                               BuilderRegistry builderRegistry,
                               Connector connector) {
        this.builderRegistry = builderRegistry;
        this.loaderRegistry = loaderRegistry;
        this.connector = connector;
    }

    public DefaultBootstrapper(MonitorFactory monitorFactory) {
        this.builderRegistry = getDefaultBuilderRegistry();
        this.loaderRegistry = getDefaultLoaderRegistry(monitorFactory, new StringParserPropertyFactory());
        this.connector = getDefaultConnector();
    }

    public Deployer createDeployer() {
        DeployerImpl deployer = new DeployerImpl();
        deployer.setBuilder(builderRegistry);
        deployer.setLoader(loaderRegistry);
        deployer.setConnector(connector);
        return deployer;
    }

    private BuilderRegistry getDefaultBuilderRegistry() {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(new WorkContextImpl());
        BuilderRegistry builderRegistry = new BuilderRegistryImpl(scopeRegistry);
        builderRegistry.register(SystemCompositeImplementation.class,
            new SystemCompositeBuilder(builderRegistry));
        builderRegistry.register(SystemImplementation.class, new SystemComponentBuilder());
        builderRegistry.register(SystemBinding.class, new SystemBindingBuilder());
        return builderRegistry;
    }

    private LoaderRegistry getDefaultLoaderRegistry(MonitorFactory monitorFactory,
                                                    StAXPropertyFactory propertyFactory) {
        LoaderRegistryImpl loaderRegistry = new LoaderRegistryImpl();
        loaderRegistry.setMonitor(monitorFactory.getMonitor(LoaderRegistryImpl.Monitor.class));

        // register component type loaders
        loaderRegistry.registerLoader(SystemImplementation.class, new SystemComponentTypeLoader());
        loaderRegistry.registerLoader(SystemCompositeImplementation.class,
            new SystemCompositeComponentTypeLoader(loaderRegistry));

        // register element loaders
        loaderRegistry.registerLoader(AssemblyConstants.COMPONENT, new ComponentLoader(loaderRegistry,
            propertyFactory));
        loaderRegistry.registerLoader(AssemblyConstants.COMPONENT_TYPE,
            new ComponentTypeElementLoader(loaderRegistry));
        loaderRegistry.registerLoader(AssemblyConstants.COMPOSITE, new CompositeLoader(loaderRegistry));
        loaderRegistry.registerLoader(AssemblyConstants.INTERFACE_JAVA,
            new InterfaceJavaLoader(loaderRegistry));
        loaderRegistry.registerLoader(AssemblyConstants.PROPERTY, new PropertyLoader(loaderRegistry));
        loaderRegistry.registerLoader(AssemblyConstants.REFERENCE, new ReferenceLoader(loaderRegistry));
        loaderRegistry.registerLoader(AssemblyConstants.SERVICE, new ServiceLoader(loaderRegistry));

        loaderRegistry.registerLoader(SystemImplementationLoader.SYSTEM_IMPLEMENTATION,
            new SystemImplementationLoader(loaderRegistry));
        loaderRegistry.registerLoader(SystemBindingLoader.SYSTEM_BINDING,
            new SystemBindingLoader(loaderRegistry));
        return loaderRegistry;
    }

    private Connector getDefaultConnector() {
        return new ConnectorImpl();
    }
}
