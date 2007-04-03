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

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;

/**
 * Instantiates a composite component from an assembly definition
 *
 * @version $Rev$ $Date$
 */
public class CompositeBuilder extends AbstractCompositeBuilder<CompositeImplementation> {

    public Component build(ComponentDefinition<CompositeImplementation> componentDefinition,
        DeploymentContext deploymentContext) throws BuilderException {

        CompositeImplementation implementation = componentDefinition.getImplementation();
        CompositeComponentType<?, ?, ?> componentType = implementation.getComponentType();
        URI name = componentDefinition.getUri();
        CompositeComponentImpl component = new CompositeComponentImpl(name);

        return build(component, componentType, deploymentContext);
    }

    protected Class<CompositeImplementation> getImplementationType() {
        return CompositeImplementation.class;
    }
}
