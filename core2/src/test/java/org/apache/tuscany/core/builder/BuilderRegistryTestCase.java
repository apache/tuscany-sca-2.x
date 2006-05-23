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
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryTestCase extends TestCase {
    private BuilderRegistryImpl registry;

    public void testRegistrationWithGenerics() {
        GenerifiedBuilder builder = new GenerifiedBuilder();
        registry.register(builder);
        Component<CompositeImplementation> component = new Component(new CompositeImplementation());
        component.getImplementation().setComponentType(new CompositeComponentType());
        registry.build(null, component);
    }

    public void testRegistrationWithoutGenerics() {
        RawBuilder builder = new RawBuilder();
        registry.register(CompositeImplementation.class, builder);
        Component<CompositeImplementation> component = new Component(new CompositeImplementation());
        component.getImplementation().setComponentType(new CompositeComponentType());
        registry.build(null, component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new BuilderRegistryImpl();
    }

    public static class GenerifiedBuilder implements ComponentBuilder<CompositeImplementation> {
        public ComponentContext build(CompositeContext parent, Component<CompositeImplementation> component) {
            return null;
        }
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    public static class RawBuilder implements ComponentBuilder {
        public ComponentContext build(CompositeContext parent, Component component) {
            return null;
        }
    }
}
