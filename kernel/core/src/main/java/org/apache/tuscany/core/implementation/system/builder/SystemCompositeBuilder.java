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
package org.apache.tuscany.core.implementation.system.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;

/**
 * Produces system composite components by evaluating an assembly.
 *
 * @version $Rev$ $Date$
 */
public class SystemCompositeBuilder extends ComponentBuilderExtension<SystemCompositeImplementation> {
    public SystemCompositeBuilder() {
    }

    public SystemCompositeBuilder(BuilderRegistry builderRegistry, Connector connector) {
        this.builderRegistry = builderRegistry;
        this.connector = connector;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent,
                           ComponentDefinition<SystemCompositeImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderConfigException {
        SystemCompositeImplementation impl = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = impl.getComponentType();
        // create lists of all components and services in this composite
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

        // create the composite component
        String name = componentDefinition.getName();
        CompositeComponent component = new CompositeComponentImpl(name, parent, connector, true);
        for (ComponentDefinition<? extends Implementation> childComponentDefinition : allComponents) {
            Component child;
            try {
                child = builderRegistry.build(component, childComponentDefinition, deploymentContext);
            } catch (BuilderException e) {
                e.addContextName(component.getName());
                e.addContextName(name);
                e.addContextName(parent.getName());
                throw e;
            }
            component.register(child);
        }

        for (BoundServiceDefinition<? extends Binding> serviceDefinition : allBoundServices) {
            SCAObject object;
            try {
                object = builderRegistry.build(component, serviceDefinition, deploymentContext);
            } catch (BuilderException e) {
                e.addContextName(serviceDefinition.getName());
                e.addContextName(name);
                e.addContextName(parent.getName());
                throw e;
            }
            component.register(object);
        }
        return component;
    }

    protected Class<SystemCompositeImplementation> getImplementationType() {
        return SystemCompositeImplementation.class;
    }

}
