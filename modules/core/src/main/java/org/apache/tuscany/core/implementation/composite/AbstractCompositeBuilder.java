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

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Abstract builder for composites
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeBuilder<T extends Implementation<CompositeComponentType>>
    extends ComponentBuilderExtension<T> {

    public Component build(
        Component component,
        CompositeComponentType<?, ?, ?> componentType,
        DeploymentContext deploymentContext) throws BuilderException {
        for (ComponentDefinition<? extends Implementation<?>> definition : componentType.getComponents().values()) {
            builderRegistry.build(definition, deploymentContext);
        }
        for (ServiceDefinition definition : componentType.getServices().values()) {
            try {
                Service service = builderRegistry.build(definition, deploymentContext);
                component.register(service);
            } catch (RegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        for (ReferenceDefinition definition : componentType.getReferences().values()) {
            try {
                Reference reference = builderRegistry.build(definition, deploymentContext);
                component.register(reference);
            } catch (RegistrationException e) {
                throw new BuilderInstantiationException("Error registering reference", e);
            }
        }
        return component;
    }

}
