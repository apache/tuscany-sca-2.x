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
package org.apache.tuscany.core.implementation.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Include;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Instantiates a composite component from an assembly definition
 *
 * @version $Rev$ $Date$
 */
public class CompositeBuilder extends ComponentBuilderExtension<CompositeImplementation> {

    public Component<?> build(CompositeComponent<?> parent,
                              ComponentDefinition<CompositeImplementation> componentDefinition,
                              DeploymentContext deploymentContext) throws BuilderConfigException {
        CompositeImplementation implementation = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = implementation.getComponentType();

        // create lists of all components, services and references in this composite
        List<ComponentDefinition<? extends Implementation<?>>> allComponents =
            new ArrayList<ComponentDefinition<? extends Implementation<?>>>();
        allComponents.addAll(componentType.getComponents().values());

        List<BoundServiceDefinition<? extends Binding>> allBoundServices =
            new ArrayList<BoundServiceDefinition<? extends Binding>>();
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            if (serviceDefinition instanceof BoundServiceDefinition) {
                BoundServiceDefinition<? extends Binding> boundService =
                    (BoundServiceDefinition<? extends Binding>) serviceDefinition;
                allBoundServices.add(boundService);
            }
        }

        // FIXME is this right?
        List<BoundReferenceDefinition<? extends Binding>> allBoundReferences =
            new ArrayList<BoundReferenceDefinition<? extends Binding>>();
        
        for (Object referenceTarget : componentType.getReferences().values()) {
            if (referenceTarget instanceof BoundReferenceDefinition<?>) {
                allBoundReferences.add((BoundReferenceDefinition<? extends Binding>) referenceTarget);
            }
        }

        // add in components and services from included composites
        for (Include include : componentType.getIncludes().values()) {
            CompositeComponentType<?, ?, ?> included = include.getIncluded();
            allComponents.addAll(included.getComponents().values());
            for (ServiceDefinition serviceDefinition : included.getServices().values()) {
                if (serviceDefinition instanceof BoundServiceDefinition) {
                    BoundServiceDefinition<? extends Binding> boundService =
                        (BoundServiceDefinition<? extends Binding>) serviceDefinition;
                    allBoundServices.add(boundService);
                }
            }
            // TODO how to include references
        }

        String name = componentDefinition.getName();
        CompositeComponentImpl<?> context = new CompositeComponentImpl(name, parent, null, null);
        for (BoundReferenceDefinition<? extends Binding> referenceDefinition : allBoundReferences) {
            context.register(builderRegistry.build(context, referenceDefinition, deploymentContext));
        }
        for (ComponentDefinition<? extends Implementation<?>> child : allComponents) {
            context.register(builderRegistry.build(context, child, deploymentContext));
        }
        for (BoundServiceDefinition<? extends Binding> serviceDefinition : allBoundServices) {
            context.register(builderRegistry.build(context, serviceDefinition, deploymentContext));
        }
        return context;
    }

    protected Class<CompositeImplementation> getImplementationType() {
        return CompositeImplementation.class;
    }
}
