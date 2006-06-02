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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.BoundReference;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Service interface that builds runtime contexts from model objects.
 *
 * @version $Rev$ $Date$
 */
public interface Builder {
    /**
     * Build a component context from a component definition.
     *
     * @param parent the context that will be the parent of the newly build context
     * @param component the definition of the component
     * @param deploymentContext the current deployment context
     * @return a newly created component context
     */
    <I extends Implementation<?>> ComponentContext<?> build(CompositeContext<?> parent, Component<I> component, DeploymentContext deploymentContext);

    /**
     * TODO: JavaDoc this when we know if we will still register Services as contexts 
     */
    <B extends Binding> Context build(CompositeContext parent, BoundService<B> boundService, DeploymentContext deploymentContext);

    /**
     * TODO: JavaDoc this when we know if we will still register References as contexts
     */
    <B extends Binding> Context build(CompositeContext parent, BoundReference<B> boundReference, DeploymentContext deploymentContext);
}
