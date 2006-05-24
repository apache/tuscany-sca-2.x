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
package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * @version $Rev$ $Date$
 */
public class SystemCompositeBuilder extends ComponentBuilderExtension<SystemCompositeImplementation> {
    private BuilderRegistry builderRegistry;

    public SystemCompositeBuilder() {
    }

    public SystemCompositeBuilder(BuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    protected Class<SystemCompositeImplementation> getImplementationType() {
        return SystemCompositeImplementation.class;
    }

    @Autowire
    public void setBuilderRegistry(BuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    public ComponentContext build(CompositeContext parent, Component<SystemCompositeImplementation> component, DeploymentContext deploymentContext) throws BuilderConfigException {
        SystemCompositeImplementation impl = component.getImplementation();
        CompositeComponentType componentType = impl.getComponentType();
        SystemCompositeContext<?> context = new SystemCompositeContextImpl(component.getName(), parent, getAutowireContext(parent));
        for (Service service : componentType.getServices().values()) {
            if (service instanceof BoundService) {
                context.registerContext(builderRegistry.build(parent, (BoundService<? extends Binding>) service, deploymentContext));
            }
        }
        for (Component<? extends Implementation> childComponent : componentType.getComponents().values()) {
            context.registerContext(builderRegistry.build(parent, childComponent, deploymentContext));
        }
        return context;
    }

    /**
     * Return the autowire context for the supplied parent
     *
     * @param parent the parent for a new context
     * @return the autowire context for the parent or null if it does not support autowire
     */
    protected AutowireContext getAutowireContext(CompositeContext<?> parent) {
        if (parent instanceof AutowireContext) {
            return (AutowireContext) parent;
        } else {
            return null;
        }
    }
}
