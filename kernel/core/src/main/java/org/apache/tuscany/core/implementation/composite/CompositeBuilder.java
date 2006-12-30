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
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.BindingDefinition;
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

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent,
                           ComponentDefinition<CompositeImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderException {
        CompositeImplementation implementation = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = implementation.getComponentType();
        String name = componentDefinition.getName();
        CompositeComponentImpl component = new CompositeComponentImpl(name, parent, connector, null);

        List<BoundReferenceDefinition<? extends BindingDefinition>> boundReferences =
            new ArrayList<BoundReferenceDefinition<? extends BindingDefinition>>();
        List<ReferenceDefinition> allTargetlessReferences = new ArrayList<ReferenceDefinition>();

        for (Object referenceTarget : componentType.getReferences().values()) {
            if (referenceTarget instanceof BoundReferenceDefinition<?>) {
                boundReferences.add((BoundReferenceDefinition<? extends BindingDefinition>) referenceTarget);
            } else if (referenceTarget instanceof ReferenceDefinition) {
                allTargetlessReferences.add((ReferenceDefinition) referenceTarget);
            }
        }

        for (ComponentDefinition<? extends Implementation<?>> definition : componentType.getComponents().values()) {
            try {
                Component child = builderRegistry.build(component, definition, deploymentContext);
                component.register(child);
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering component", e);
            }
        }
        for (ServiceDefinition definition : componentType.getServices().values()) {
            try {
                if (definition instanceof BoundServiceDefinition) {
                    BoundServiceDefinition bsd = (BoundServiceDefinition) definition;
                    Service service = builderRegistry.build(component, bsd, deploymentContext);
                    component.register(service);
                } else {
                    throw new UnsupportedOperationException();
                }
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        for (BoundReferenceDefinition<? extends BindingDefinition> definition : boundReferences) {
            try {
                SCAObject child = builderRegistry.build(component, definition, deploymentContext);
                component.register(child);
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering reference", e);
            }
        }
        // TODO JFM remove need for targetless references
        for (ReferenceDefinition definition : allTargetlessReferences) {
            try {
                SCAObject child = builderRegistry.build(component, definition, deploymentContext);
                component.register(child);
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
