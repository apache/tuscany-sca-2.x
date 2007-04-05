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
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.wire.IDLMappingService;
import org.apache.tuscany.idl.java.JavaInterface;
import org.apache.tuscany.idl.java.impl.JavaInterfaceImpl;

/**
 * @version $Rev$ $Date$
 */
public class DefaultAutowireResolverTestCase extends TestCase {
    private static final URI REFERENCE_URI = URI.create("source#ref");
    private static final URI TARGET_URI = URI.create("target#service");
    private AssemblyFactory factory;
    private DefaultAutowireResolver resolver;

    private <T extends Reference> T getReference(List<T> refs, String name) {
        for (T ref : refs) {
            if (ref.getName().equals(name)) {
                return ref;
            }
        }
        return null;
    }

    public void testAutowireAtomicToAtomic() throws Exception {
        Component composite = createComposite("composite");
        Composite type = (Composite)composite.getImplementation();
        Component source = createSourceAtomic(Foo.class);
        type.getComponents().add(source);
        Component target = createTargetAtomic(Foo.class);
        type.getComponents().add(target);
        resolver.resolve(null, composite);
        Reference refTarget = getReference(source.getReferences(), "ref");
        assertEquals("service", refTarget.getTargets().get(0).getName());
    }

    public void testAutowireAtomicToAtomicRequiresSuperInterface() throws Exception {
        Component composite = createComposite("composite");
        Composite type = (Composite)composite.getImplementation();
        Component source = createSourceAtomic(SuperFoo.class);
        type.getComponents().add(source);
        Component target = createTargetAtomic(Foo.class);
        type.getComponents().add(target);
        resolver.resolve(null, composite);
        Reference refTarget = getReference(source.getReferences(), "ref");
        assertEquals("service", refTarget.getTargets().get(0).getName());
    }

    public void testAutowireAtomicToAtomicRequiresSubInterface() throws Exception {
        Component composite = createComposite("composite");
        Composite type = (Composite)composite.getImplementation();
        Component source = createSourceAtomic(Foo.class);
        type.getComponents().add(source);
        Component target = createTargetAtomic(SuperFoo.class);
        type.getComponents().add(target);
        try {
            resolver.resolve(null, composite);
            fail();
        } catch (AutowireTargetNotFoundException e) {
            // expected
        }
    }

    public void testAutowireAtomicToAtomicIncompatibleInterfaces() throws Exception {
        Component composite = createComposite("composite");
        Composite type = (Composite)composite.getImplementation();
        Component source = createSourceAtomic(Foo.class);
        type.getComponents().add(source);
        Component target = createTargetAtomic(String.class);
        type.getComponents().add(target);
        try {
            resolver.resolve(null, composite);
            fail();
        } catch (AutowireTargetNotFoundException e) {
            // expected
        }
    }

    public void testNestedAutowireAtomicToAtomic() throws Exception {
        Component composite = createComposite("composite");
        Composite type = (Composite)composite.getImplementation();
        Component source = createSourceAtomic(Foo.class);
        type.getComponents().add(source);
        Component target = createTargetAtomic(Foo.class);
        type.getComponents().add(target);
        Component parent = createComposite("parent");
        ((Composite)parent.getImplementation()).getComponents().add(composite);
        resolver.resolve(null, parent);
        Reference refTarget = getReference(source.getReferences(), "ref");
        assertEquals("service", refTarget.getTargets().get(0).getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        resolver = new DefaultAutowireResolver(new IDLMappingService());
        factory = new DefaultAssemblyFactory();
    }

    private Component createComposite(String uri) {
        Composite type = factory.createComposite();
        type.setAutowire(true);
        Component component = factory.createComponent();
        component.setImplementation(type);
        component.setName(uri);
        return component;
    }

    private Component createSourceAtomic(Class<?> requiredInterface) {
        ComponentService service = factory.createComponentService();
        service.setName("service");

        JavaInterface javaInterface = new JavaInterfaceImpl();
        javaInterface.setJavaClass(requiredInterface);
        service.setInterface(javaInterface);

        Reference reference = factory.createReference();
        reference.setName("ref");
        reference.setAutowire(true);
        reference.setInterface(javaInterface);
        reference.setMultiplicity(Multiplicity.ONE_ONE);

        MockAtomicImpl impl = new MockAtomicImpl();
        Component definition = factory.createComponent();
        definition.setImplementation(impl);
        definition.setName("source");
        // definition.getServices().add(service);

        impl.getServices().add(service);
        impl.getReferences().add(reference);

        return definition;
    }

    private Component createTargetAtomic(Class<?> serviceInterface) {
        ComponentService service = factory.createComponentService();
        service.setName("service");

        JavaInterface javaInterface = new JavaInterfaceImpl();
        javaInterface.setJavaClass(serviceInterface);
        service.setInterface(javaInterface);

        MockAtomicImpl impl = new MockAtomicImpl();
        impl.getServices().add(service);
        Component component = factory.createComponent();
        component.setName("target");
        component.setImplementation(impl);
        return component;
    }

    private class MockAtomicImpl extends ComponentTypeImpl implements Implementation {

    }

    private interface SuperFoo {

    }

    private interface Foo extends SuperFoo {

    }

}
