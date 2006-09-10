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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;

/**
 * Implementations build <code>SCAObject</code> types from model objects.
 *
 * @version $Rev$ $Date$
 */
public interface Builder {
    /**
     * Builds a <code>Component</code> context from a <code>ComponentDefinition</code>
     *
     * @param parent              the composite that will be the parent of the newly built component
     * @param componentDefinition the component definition as parsed from an SCA assembly
     * @param deploymentContext   the current deployment context
     * @return a newly created component
     */
    <I extends Implementation<?>> Component build(CompositeComponent parent,
                                                     ComponentDefinition<I> componentDefinition,
                                                     DeploymentContext deploymentContext);

    /**
     * TODO: JavaDoc this when we know if we will still register Services as contexts
     */
    <B extends Binding> SCAObject build(CompositeComponent parent,
                                        BoundServiceDefinition<B> boundServiceDefinition,
                                        DeploymentContext deploymentContext);

    /**
     * TODO: JavaDoc this when we know if we will still register References as contexts
     */
    <B extends Binding> SCAObject build(CompositeComponent parent,
                                        BoundReferenceDefinition<B> boundReferenceDefinition,
                                        DeploymentContext deploymentContext);

    /**
     * TODO: Make sure that this method belongs here
     * Allow a builder registry to provide building of bindless services via
     * appropriate registered builder
     */
    SCAObject build(CompositeComponent parent,
                                       BindlessServiceDefinition serviceDefinition,
                                       DeploymentContext deploymentContext);

    /**
     * TODO: Make sure that this method belongs here
     * Allow a builder registry to provide building of targetless references via
     * appropriate registered builder
     */
    SCAObject build(CompositeComponent parent,
                                       ReferenceDefinition referenceDefinition,
                                       DeploymentContext deploymentContext);
}
