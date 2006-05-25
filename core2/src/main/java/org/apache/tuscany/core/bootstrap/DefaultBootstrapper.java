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

import java.net.URI;

import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.loader.LoaderRegistryImpl;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.system.builder.SystemCompositeBuilder;
import org.apache.tuscany.core.system.loader.SystemComponentTypeLoader;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryService;
import org.apache.tuscany.core.wire.system.WireServiceImpl;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.JavaServiceContract;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.wire.WireService;

/**
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

    public DefaultBootstrapper() {
        this.builderRegistry = getDefaultBuilderRegistry();
        this.loaderRegistry = getDefaultLoaderRegistry();
        this.connector = getDefaultConnector();
    }

    public static BuilderRegistry getDefaultBuilderRegistry() {
        WireService wireService = new WireServiceImpl(new JDKWireFactoryService());
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(new WorkContextImpl());
        BuilderRegistry builderRegistry = new BuilderRegistryImpl(wireService, scopeRegistry);
        builderRegistry.register(SystemCompositeImplementation.class, new SystemCompositeBuilder(builderRegistry));
        builderRegistry.register(SystemImplementation.class, new SystemComponentBuilder());
        builderRegistry.register(SystemBinding.class, new SystemBindingBuilder());
        return builderRegistry;
    }

    public static LoaderRegistry getDefaultLoaderRegistry() {
        LoaderRegistry loaderRegistry = new LoaderRegistryImpl();
        loaderRegistry.registerLoader(SystemImplementation.class, new SystemComponentTypeLoader());
        return loaderRegistry;
    }

    public static Connector getDefaultConnector() {
        return new ConnectorImpl();
    }

    public Context<Deployer> createDeployer(String name, CompositeContext<?> parent) {
        ScopeContext moduleScope = new ModuleScopeContext();
        DeploymentContext deploymentContext = new DeploymentContext(null, null, moduleScope);
        CompositeComponentType composite = new CompositeComponentType();

        // expose the deployer as a system service
        composite.add(new BoundService<SystemBinding>("deployer", new JavaServiceContract(Deployer.class), new SystemBinding(), URI.create("deployerImpl")));

        // create the deployer component
        composite.add(createdeployer());

        Component<SystemCompositeImplementation> deployerComposite = new Component<SystemCompositeImplementation>(name, new SystemCompositeImplementation(composite));
        Context<Deployer> context = builderRegistry.build(parent, deployerComposite, deploymentContext);
        connector.connect(context);
        return context;
    }

    protected Component<SystemImplementation> createdeployer() {
        PojoComponentType type = new PojoComponentType();
        type.add(new Service("default", new JavaServiceContract(Deployer.class)));
        return new Component<SystemImplementation>("deployerImpl", new SystemImplementation(type, DeployerImpl.class));
    }
}
