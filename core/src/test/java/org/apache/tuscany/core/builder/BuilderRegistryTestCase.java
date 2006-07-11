/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.builder;

import junit.framework.TestCase;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private BuilderRegistryImpl registry;

    public void testRegistrationWithoutGenerics() {
        RawBuilder builder = new RawBuilder();
        registry.register(CompositeImplementation.class, builder);
        ComponentDefinition<CompositeImplementation> componentDefinition
            = new ComponentDefinition(new CompositeImplementation());
        componentDefinition.getImplementation().setComponentType(new CompositeComponentType());
        registry.build(null, componentDefinition, deploymentContext);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new BuilderRegistryImpl();
        deploymentContext = new RootDeploymentContext(null, null, null);
    }

    public static class GenerifiedBuilder implements ComponentBuilder<CompositeImplementation> {
        public Component<?> build(CompositeComponent<?> parent,
                                  ComponentDefinition<CompositeImplementation> componentDefinition,
                                  DeploymentContext deploymentContext) {
            return null;
        }
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    public static class RawBuilder implements ComponentBuilder {
        public Component build(CompositeComponent parent,
                               ComponentDefinition componentDefinition,
                               DeploymentContext deploymentContext) throws BuilderConfigException {
            return null;
        }
    }
}
