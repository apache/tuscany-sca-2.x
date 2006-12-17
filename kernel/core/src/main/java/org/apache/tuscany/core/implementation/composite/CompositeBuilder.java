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
package org.apache.tuscany.core.implementation.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Instantiates a composite component from an assembly definition
 *
 * @version $Rev$ $Date$
 */
public class CompositeBuilder extends ComponentBuilderExtension<CompositeImplementation> {

    public Component build(CompositeComponent parent,
                           ComponentDefinition<CompositeImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderException {
        CompositeImplementation implementation = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = implementation.getComponentType();

        // create lists of all components, services and references in this composite
        List<ComponentDefinition<? extends Implementation<?>>> allComponents =
            new ArrayList<ComponentDefinition<? extends Implementation<?>>>();
        allComponents.addAll(componentType.getComponents().values());

        List<BoundServiceDefinition<? extends Binding>> allBoundServices =
            new ArrayList<BoundServiceDefinition<? extends Binding>>();
        List<BindlessServiceDefinition> allBindlessServices = new ArrayList<BindlessServiceDefinition>();
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            if (serviceDefinition instanceof BoundServiceDefinition) {
                BoundServiceDefinition<? extends Binding> boundService =
                    (BoundServiceDefinition<? extends Binding>) serviceDefinition;
                allBoundServices.add(boundService);
            } else if (serviceDefinition instanceof BindlessServiceDefinition) {
                allBindlessServices.add((BindlessServiceDefinition) serviceDefinition);
            }
        }

        // FIXME is this right?
        List<BoundReferenceDefinition<? extends Binding>> allBoundReferences =
            new ArrayList<BoundReferenceDefinition<? extends Binding>>();
        List<ReferenceDefinition> allTargetlessReferences = new ArrayList<ReferenceDefinition>();

        for (Object referenceTarget : componentType.getReferences().values()) {
            if (referenceTarget instanceof BoundReferenceDefinition<?>) {
                allBoundReferences.add((BoundReferenceDefinition<? extends Binding>) referenceTarget);
            } else if (referenceTarget instanceof ReferenceDefinition) {
                allTargetlessReferences.add((ReferenceDefinition) referenceTarget);
            }
        }

        String name = componentDefinition.getName();
        CompositeComponentImpl component = new CompositeComponentImpl(name, parent, connector, null);
        for (BoundReferenceDefinition<? extends Binding> referenceDefinition : allBoundReferences) {
            try {
                component.register(builderRegistry.build(component, referenceDefinition, deploymentContext));
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering reference", e);
            }
        }
        for (BindlessServiceDefinition bindlessServiceDef : allBindlessServices) {
            try {
                component.register(builderRegistry.build(component, bindlessServiceDef, deploymentContext));
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        for (ComponentDefinition<? extends Implementation<?>> child : allComponents) {
            try {
                component.register(builderRegistry.build(component, child, deploymentContext));
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering component", e);
            }
        }
        for (BoundServiceDefinition<? extends Binding> serviceDefinition : allBoundServices) {
            try {
                component.register(builderRegistry.build(component, serviceDefinition, deploymentContext));
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        for (ReferenceDefinition targetlessReferenceDef : allTargetlessReferences) {
            try {
                component.register(builderRegistry.build(component, targetlessReferenceDef, deploymentContext));
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering reference", e);
            }
        }
        component.getExtensions().putAll(componentType.getExtensions());
        return component;
    }

    protected Class<CompositeImplementation> getImplementationType() {
        return CompositeImplementation.class;
    }
}
