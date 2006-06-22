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

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
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
        CompositeComponentImpl<?> context = new CompositeComponentImpl(componentDefinition.getName(),
            parent,
            null,
            wireService);
        for (ReferenceTarget target : componentDefinition.getReferenceTargets().values()) {
            ReferenceDefinition referenceDefinition = target.getReference();
            if (referenceDefinition instanceof BoundReferenceDefinition) {
                SCAObject<?> refereceSCAObject = builderRegistry.build(context,
                    (BoundReferenceDefinition) referenceDefinition,
                    deploymentContext);
                context.register(refereceSCAObject);
            }
        }
        for (ComponentDefinition<? extends Implementation<?>> child : componentType.getComponents().values()) {
            SCAObject<?> childSCAObject = builderRegistry.build(context, child, deploymentContext);
            context.register(childSCAObject);
        }
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            if (serviceDefinition instanceof BoundServiceDefinition) {
                SCAObject<?> serviceSCAObject = builderRegistry.build(context,
                    (BoundServiceDefinition) serviceDefinition,
                    deploymentContext);
                context.register(serviceSCAObject);
            }
        }
        return context;
    }

    protected Class<CompositeImplementation> getImplementationType() {
        return CompositeImplementation.class;
    }
}
