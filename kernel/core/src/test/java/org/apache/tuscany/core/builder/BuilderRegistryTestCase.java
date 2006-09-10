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
package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private BuilderRegistryImpl registry;

    public void testRegistrationWithoutGenerics() {
        RawBuilder builder = new RawBuilder();
        registry.register(CompositeImplementation.class, builder);
        CompositeImplementation implementation = new CompositeImplementation();
        ComponentDefinition<CompositeImplementation> componentDefinition =
            new ComponentDefinition<CompositeImplementation>(implementation);
        componentDefinition.getImplementation().setComponentType(new CompositeComponentType());
        registry.build(null, componentDefinition, deploymentContext);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new BuilderRegistryImpl();
        WireService service = EasyMock.createNiceMock(WireService.class);
        registry.setWireService(service);
        deploymentContext = new RootDeploymentContext(null, null, null, null);
    }

    public static class GenerifiedBuilder implements ComponentBuilder<CompositeImplementation> {
        public Component build(CompositeComponent parent,
                               ComponentDefinition<CompositeImplementation> componentDefinition,
                               DeploymentContext deploymentContext) {
            return null;
        }
    }

    public static class RawBuilder implements ComponentBuilder<CompositeImplementation> {
        public Component build(CompositeComponent parent,
                               ComponentDefinition componentDefinition,
                               DeploymentContext deploymentContext) throws BuilderConfigException {
            return null;
        }
    }
}
