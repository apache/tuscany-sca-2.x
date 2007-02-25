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
package org.apache.tuscany.container.spring.impl;

import java.net.URI;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;

import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.springframework.core.io.Resource;

/**
 * Creates a {@link org.apache.tuscany.container.spring.impl.SpringCompositeComponent} from an assembly model
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilder extends ComponentBuilderExtension<SpringImplementation> {

    public Component build(ComponentDefinition<SpringImplementation> componentDefinition, DeploymentContext context)
        throws BuilderException {
        URI uri = componentDefinition.getUri();
        SpringImplementation implementation = componentDefinition.getImplementation();
        Resource resource = implementation.getApplicationResource();
        ClassLoader cl = implementation.getClassLoader();
        return new SpringCompositeComponent(uri, resource, proxyService, null, cl);
    }

    protected Class<SpringImplementation> getImplementationType() {
        return SpringImplementation.class;
    }
}
