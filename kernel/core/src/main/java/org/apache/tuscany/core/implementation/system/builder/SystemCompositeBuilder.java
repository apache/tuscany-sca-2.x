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

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.services.management.ManagementService;

import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;

/**
 * Produces system composite components by evaluating an assembly.
 * 
 * @version $Rev$ $Date$
 */
public class SystemCompositeBuilder extends ComponentBuilderExtension<SystemCompositeImplementation> {
    private ManagementService managementService;

    public SystemCompositeBuilder() {
    }

    public SystemCompositeBuilder(BuilderRegistry builderRegistry,
                                  Connector connector,
                                  ManagementService managementService) {
        this.builderRegistry = builderRegistry;
        this.connector = connector;
        this.managementService = managementService;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent,
                           ComponentDefinition<SystemCompositeImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderException {
        SystemCompositeImplementation impl = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = impl.getComponentType();
        // create lists of all components and serviceBindings in this composite
        List<ComponentDefinition<? extends Implementation<?>>> allComponents =
            new ArrayList<ComponentDefinition<? extends Implementation<?>>>();
        allComponents.addAll(componentType.getComponents().values());

        List<BoundServiceDefinition> allBoundServices = new ArrayList<BoundServiceDefinition>();
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            if (serviceDefinition instanceof BoundServiceDefinition) {
                BoundServiceDefinition boundService = (BoundServiceDefinition)serviceDefinition;
                allBoundServices.add(boundService);
            }
        }

        // create the composite component
        String name = componentDefinition.getName();
        CompositeComponent component = new CompositeComponentImpl(name, parent, connector, true);
        component.setManagementService(managementService);
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
            try {
                component.register(child);
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering component", e);
            }
        }

        for (BoundServiceDefinition serviceDefinition : allBoundServices) {
            SCAObject object;
            try {
                object = builderRegistry.build(component, serviceDefinition, deploymentContext);
            } catch (BuilderException e) {
                e.addContextName(serviceDefinition.getName());
                e.addContextName(name);
                e.addContextName(parent.getName());
                throw e;
            }
            try {
                component.register(object);
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        return component;
    }

    protected Class<SystemCompositeImplementation> getImplementationType() {
        return SystemCompositeImplementation.class;
    }

}
