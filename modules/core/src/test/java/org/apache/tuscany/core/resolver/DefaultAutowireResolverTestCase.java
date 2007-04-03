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
package org.apache.tuscany.core.resolver;

import java.net.URI;

import org.apache.tuscany.spi.model.AtomicImplementation;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class DefaultAutowireResolverTestCase extends TestCase {
    private static final URI REFERENCE_URI = URI.create("source#ref");
    private static final URI TARGET_URI = URI.create("target#service");
    private DefaultAutowireResolver resolver;

    public void testAutowireAtomicToAtomic() throws Exception {
        ComponentDefinition<CompositeImplementation> composite = createComposite("composite");
        CompositeComponentType<?, ?, ?> type = composite.getImplementation().getComponentType();
        ComponentDefinition<MockAtomicImpl> source = createSourceAtomic(Foo.class);
        type.add(source);
        ComponentDefinition<MockAtomicImpl> target = createTargetAtomic(Foo.class);
        type.add(target);
        resolver.resolve(null, composite);
        ReferenceTarget refTarget = source.getReferenceTargets().get(REFERENCE_URI.getFragment());
        assertEquals(TARGET_URI, refTarget.getTargets().get(0));
    }

    public void testAutowireAtomicToAtomicRequiresSuperInterface() throws Exception {
        ComponentDefinition<CompositeImplementation> composite = createComposite("composite");
        CompositeComponentType<?, ?, ?> type = composite.getImplementation().getComponentType();
        ComponentDefinition<MockAtomicImpl> source = createSourceAtomic(SuperFoo.class);
        type.add(source);
        ComponentDefinition<MockAtomicImpl> target = createTargetAtomic(Foo.class);
        type.add(target);
        resolver.resolve(null, composite);
        ReferenceTarget refTarget = source.getReferenceTargets().get(REFERENCE_URI.getFragment());
        assertEquals(TARGET_URI, refTarget.getTargets().get(0));
    }

    public void testAutowireAtomicToAtomicRequiresSubInterface() throws Exception {
        ComponentDefinition<CompositeImplementation> composite = createComposite("composite");
        CompositeComponentType<?, ?, ?> type = composite.getImplementation().getComponentType();
        ComponentDefinition<MockAtomicImpl> source = createSourceAtomic(Foo.class);
        type.add(source);
        ComponentDefinition<MockAtomicImpl> target = createTargetAtomic(SuperFoo.class);
        type.add(target);
        try {
            resolver.resolve(null, composite);
            fail();
        } catch (AutowireTargetNotFoundException e) {
            // expected
        }
    }

    public void testAutowireAtomicToAtomicIncompatibleInterfaces() throws Exception {
        ComponentDefinition<CompositeImplementation> composite = createComposite("composite");
        CompositeComponentType<?, ?, ?> type = composite.getImplementation().getComponentType();
        ComponentDefinition<MockAtomicImpl> source = createSourceAtomic(Foo.class);
        type.add(source);
        ComponentDefinition<MockAtomicImpl> target = createTargetAtomic(String.class);
        type.add(target);
        try {
            resolver.resolve(null, composite);
            fail();
        } catch (AutowireTargetNotFoundException e) {
            // expected
        }
    }

    public void testNestedAutowireAtomicToAtomic() throws Exception {
        ComponentDefinition<CompositeImplementation> composite = createComposite("composite");
        CompositeComponentType<?, ?, ?> type = composite.getImplementation().getComponentType();
        ComponentDefinition<MockAtomicImpl> source = createSourceAtomic(Foo.class);
        type.add(source);
        ComponentDefinition<MockAtomicImpl> target = createTargetAtomic(Foo.class);
        type.add(target);
        ComponentDefinition<CompositeImplementation> parent = createComposite("parent");
        parent.getImplementation().getComponentType().add(composite);
        resolver.resolve(null, parent);
        ReferenceTarget refTarget = source.getReferenceTargets().get(REFERENCE_URI.getFragment());
        assertEquals(TARGET_URI, refTarget.getTargets().get(0));
    }


    protected void setUp() throws Exception {
        super.setUp();
        resolver = new DefaultAutowireResolver();
    }

    private ComponentDefinition<CompositeImplementation> createComposite(String uri) {
        URI parentUri = URI.create(uri);
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        CompositeImplementation impl = new CompositeImplementation();
        impl.setComponentType(type);
        return new ComponentDefinition<CompositeImplementation>(parentUri, impl);
    }

    private ComponentDefinition<MockAtomicImpl> createSourceAtomic(Class<?> requiredInterface) {
        URI uri = URI.create("source");
        ServiceContract contract = new ServiceContract() {
        };
        contract.setInterfaceClass(requiredInterface);
        ReferenceDefinition reference = new ReferenceDefinition(URI.create("#ref"), contract);
        reference.setRequired(true);
        MockComponentType type = new MockComponentType();
        type.add(reference);
        MockAtomicImpl impl = new MockAtomicImpl();
        impl.setComponentType(type);
        ComponentDefinition<MockAtomicImpl> definition = new ComponentDefinition<MockAtomicImpl>(uri, impl);
        ReferenceTarget target = new ReferenceTarget();
        target.setReferenceName(REFERENCE_URI);
        target.setAutowire(true);
        definition.add(target);
        return definition;
    }

    private ComponentDefinition<MockAtomicImpl> createTargetAtomic(Class<?> serviceInterface) {
        URI uri = URI.create("target");
        ServiceDefinition service = new ServiceDefinition();
        service.setUri(URI.create("#service"));
        ServiceContract contract = new ServiceContract() {
        };
        contract.setInterfaceClass(serviceInterface);
        service.setServiceContract(contract);
        MockComponentType type = new MockComponentType();
        type.add(service);
        MockAtomicImpl impl = new MockAtomicImpl();
        impl.setComponentType(type);
        return new ComponentDefinition<MockAtomicImpl>(uri, impl);
    }

    private class MockAtomicImpl extends AtomicImplementation<MockComponentType> {

    }

    private class MockComponentType extends ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> {

    }

    private interface SuperFoo {

    }

    private interface Foo extends SuperFoo {

    }

}
