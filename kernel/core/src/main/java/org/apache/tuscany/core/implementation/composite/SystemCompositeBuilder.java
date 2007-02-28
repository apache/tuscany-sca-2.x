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

import java.net.URI;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;

import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;

/**
 * Produces system composite components by evaluating an assembly.
 *
 * @version $Rev$ $Date$
 */
public class SystemCompositeBuilder extends AbstractCompositeBuilder<SystemCompositeImplementation> {

    @Constructor
    public SystemCompositeBuilder(@Reference BuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    public Component build(ComponentDefinition<SystemCompositeImplementation> componentDefinition,
                           DeploymentContext context) throws BuilderException {
        SystemCompositeImplementation impl = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = impl.getComponentType();
        URI name = componentDefinition.getUri();
        Component component = new CompositeComponentImpl(name);
        build(component, componentType, context);
        return component;
    }

    protected Class<SystemCompositeImplementation> getImplementationType() {
        return SystemCompositeImplementation.class;
    }

}
