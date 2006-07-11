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
package org.apache.tuscany.core.implementation.system.builder;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponent;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Produces system compisite components by evaluating an assembly
 *
 * @version $Rev$ $Date$
 */
public class SystemCompositeBuilder extends ComponentBuilderExtension<SystemCompositeImplementation> {
    public SystemCompositeBuilder() {
    }

    public SystemCompositeBuilder(BuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    protected Class<SystemCompositeImplementation> getImplementationType() {
        return SystemCompositeImplementation.class;
    }

    public Component<?> build(CompositeComponent<?> parent,
                              ComponentDefinition<SystemCompositeImplementation> componentDefinition,
                              DeploymentContext deploymentContext) throws BuilderConfigException {
        SystemCompositeImplementation impl = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = impl.getComponentType();
        SystemCompositeComponent<?> context =
            new SystemCompositeComponentImpl(componentDefinition.getName(), parent, getAutowireContext(parent));
        for (ComponentDefinition<? extends Implementation> childComponentDefinition
            : componentType.getComponents().values()) {
            context.register(builderRegistry.build(context, childComponentDefinition, deploymentContext));
        }
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            if (serviceDefinition instanceof BoundServiceDefinition) {
                context.register(builderRegistry.build(context,
                    (BoundServiceDefinition<? extends Binding>) serviceDefinition,
                    deploymentContext));
            }
        }
        return context;
    }

    /**
     * Return the autowire context for the supplied parent
     *
     * @param parent the parent for a new context
     * @return the autowire context for the parent or null if it does not support autowire
     */
    protected AutowireComponent getAutowireContext(CompositeComponent<?> parent) {
        if (parent instanceof AutowireComponent) {
            return (AutowireComponent) parent;
        } else {
            return null;
        }
    }
}
