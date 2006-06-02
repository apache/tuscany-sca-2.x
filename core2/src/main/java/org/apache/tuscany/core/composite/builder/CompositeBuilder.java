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
package org.apache.tuscany.core.composite.builder;

import org.apache.tuscany.core.context.CompositeContextImpl;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.BoundReference;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Service;

/**
 * @version $Rev$ $Date$
 */
public class CompositeBuilder extends ComponentBuilderExtension<CompositeImplementation> {

    public ComponentContext<?> build(CompositeContext<?> parent,
                                  Component<CompositeImplementation> component,
                                  DeploymentContext deploymentContext) throws BuilderConfigException {
        CompositeImplementation implementation = component.getImplementation();
        CompositeComponentType<?,?,?> componentType = implementation.getComponentType();
        CompositeContextImpl<?> context = new CompositeContextImpl(component.getName(), parent, null, wireService);
        for (ReferenceTarget target : component.getReferenceTargets().values()) {
            Reference reference = target.getReference();
            if (reference instanceof BoundReference) {
                Context<?> refereceContext = builderRegistry.build(context, (BoundReference) reference, deploymentContext);
                context.registerContext(refereceContext);
            }
        }
        for (Component<? extends Implementation<?>> child : componentType.getComponents().values()) {
            Context<?> childContext = builderRegistry.build(context, child, deploymentContext);
            context.registerContext(childContext);
        }
        for (Service service : componentType.getServices().values()) {
            if (service instanceof BoundService) {
                Context<?> serviceContext = builderRegistry.build(context, (BoundService) service, deploymentContext);
                context.registerContext(serviceContext);
            }
        }
        return context;
    }

    protected Class<CompositeImplementation> getImplementationType() {
        return CompositeImplementation.class;
    }
}
