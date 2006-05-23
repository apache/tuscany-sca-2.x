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
import org.apache.tuscany.core.system.builder.SystemCompositeBuilder;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.JavaServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class DefaultBootstrapper {
    private final BuilderRegistry builderRegistry;

    public DefaultBootstrapper(BuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    public DefaultBootstrapper() {
        this.builderRegistry = getDefaultBuilderRegistry();
    }

    public static BuilderRegistry getDefaultBuilderRegistry() {
        BuilderRegistry builderRegistry = new BuilderRegistryImpl();
        builderRegistry.register(SystemCompositeImplementation.class, new SystemCompositeBuilder());
        return builderRegistry;
    }

    public Context<Deployer> createDeployer(String name, CompositeContext<?> parent) {
        CompositeComponentType composite = new CompositeComponentType();
        SystemCompositeImplementation impl = new SystemCompositeImplementation(composite);
        Component<SystemCompositeImplementation> deployerComposite = new Component<SystemCompositeImplementation>(name, impl);
//        composite.add(new BoundService<SystemBinding>("deployer", new JavaServiceContract(Deployer.class), URI.create("deployerImpl")));
        return builderRegistry.build(parent, deployerComposite);
    }
}
