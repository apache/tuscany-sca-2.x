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
package org.apache.tuscany.implementation.java.introspect.impl;

import static org.apache.tuscany.implementation.java.introspect.impl.ModelHelper.getProperty;

import java.lang.reflect.Constructor;

import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.implementation.java.introspect.impl.AmbiguousConstructorException;
import org.apache.tuscany.implementation.java.introspect.impl.HeuristicPojoProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.NoConstructorException;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;

/**
 * @version $Rev$ $Date$
 */
public class HeuristicConstructorTestCase extends AbstractProcessorTest {

    private HeuristicPojoProcessor processor;

    public HeuristicConstructorTestCase() {
        DefaultJavaInterfaceIntrospector introspector = new DefaultJavaInterfaceIntrospector();
        processor = new HeuristicPojoProcessor(introspector);
    }

    private <T> void visitEnd(Class<T> clazz, JavaImplementationDefinition type) throws IntrospectionException {
        for (Constructor<T> constructor : clazz.getConstructors()) {
            visitConstructor(constructor, type);
        }
        processor.visitEnd(clazz, type);
    }

    /**
     * Verifies a single constructor is chosen with a parameter as the type
     */
    public void testSingleConstructorWithParam() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        org.apache.tuscany.assembly.Property prop = factory.createProperty();
        prop.setName("foo");
        type.getProperties().add(prop);
        // Hack to add a property member
        JavaElement element = new JavaElement("foo", String.class, null);
        type.getPropertyMembers().put("foo", element);
        visitEnd(Foo1.class, type);
        assertNotNull(type.getConstructorDefinition().getConstructor());
        assertEquals("foo", type.getConstructorDefinition().getParameters()[0].getName());
    }

    /**
     * Verifies a single constructor is chosen with a reference as the type
     */
    public void testSingleConstructorWithRef() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        org.apache.tuscany.assembly.Reference ref = factory.createReference();
        ref.setName("foo");
        type.getReferences().add(ref);
        type.getReferenceMembers().put("foo", new JavaElement("foo", String.class, null));
        visitEnd(Foo1.class, type);
        assertNotNull(type.getConstructorDefinition().getConstructor());
        assertEquals("foo", type.getConstructorDefinition().getParameters()[0].getName());
    }

    /**
     * Verifies a single constructor is chosen with a property and a reference
     * as the type
     */
    public void testSingleConstructorWithPropRef() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();

        org.apache.tuscany.assembly.Property prop = factory.createProperty();
        prop.setName("foo");
        type.getProperties().add(prop);
        // Hack to add a property member
        JavaElement element = new JavaElement("foo", String.class, null);
        type.getPropertyMembers().put("foo", element);

        org.apache.tuscany.assembly.Reference ref = ModelHelper.createReference("ref", Foo1.class);
        type.getReferences().add(ref);
        type.getReferenceMembers().put("ref", new JavaElement("ref", Foo1.class, null));
        visitEnd(Foo2.class, type);
        assertNotNull(type.getConstructorDefinition().getConstructor());
        assertEquals(2, type.getConstructorDefinition().getParameters().length);
    }

    public void testSingleConstructorResolvableParam() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo5.class, type);
        assertEquals(String.class, type.getPropertyMembers().get("string").getType());
    }

    public void testSingleConstructorResolvableRef() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo6.class, type);
        assertTrue(ModelHelper.matches(ModelHelper.getReference(type, "ref"), Ref.class));
    }

    public void testSingleConstructorAmbiguousRef() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        org.apache.tuscany.assembly.Reference ref = ModelHelper.createReference("ref", Foo1.class);
        type.getReferences().add(ref);
        type.getReferenceMembers().put("ref", new JavaElement("ref", Foo1.class, null));
        org.apache.tuscany.assembly.Reference ref2 = ModelHelper.createReference("ref2", Foo1.class);
        type.getReferences().add(ref2);
        type.getReferenceMembers().put("ref2", new JavaElement("ref2", Foo1.class, null));
        try {
            visitEnd(Foo4.class, type);
            fail();
        } catch (AmbiguousConstructorException e) {
            // expected
        }
    }

    public void testConstructorPropertyAnnotatedParamsOnly() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo7.class, type);
        assertNotNull(getProperty(type, "myProp"));
    }

    public void testConstructorReferenceAnnotatedParamsOnly() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo8.class, type);
        assertNotNull(ModelHelper.getReference(type, "myRef"));
    }

    @SuppressWarnings("unchecked")
    public void testDefaultConstructor() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo3.class, type);
        assertNotNull(type.getConstructorDefinition().getConstructor());
    }

    public void testSameTypesButAnnotated() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo12.class, type);
        assertEquals(2, type.getProperties().size());
        assertNotNull(getProperty(type, "prop1"));
        assertNotNull(getProperty(type, "prop2"));
    }

    /**
     * Verifies processing executes with additional extension annotations
     */
    public void testRandomAnnotation() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo11.class, type);
        assertEquals(1, type.getProperties().size());
        assertNotNull(getProperty(type, "prop1"));
    }

    public void testPrivateConstructor() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        try {
            visitEnd(Foo14.class, type);
            fail();
        } catch (NoConstructorException e) {
            // expected
        }
    }

    public void testMultipleConstructors() throws Exception {
        // throw new UnsupportedOperationException("Finish heuristic multiple
        // constructors - Foo10");
    }

    public static class Foo1 {
        public Foo1(String val) {
        }
    }

    public static class Foo2 {
        public Foo2(String val, Foo1 ref) {
        }
    }

    public static class Foo3 {
    }

    public static class Foo4 {
        public Foo4(Foo1 ref) {
        }
    }

    public static class Prop {

    }

    @Remotable
    public static interface Ref {

    }

    public static class Foo5 {
        public Foo5(String val) {
        }
    }

    public static class Foo6 {
        public Foo6(Ref ref) {
        }
    }

    public static class Foo7 {
        public Foo7(@Property(name = "myProp")
        String prop) {
        }
    }

    public static class Foo8 {
        public Foo8(@Reference(name = "myRef")
        String ref) {
        }
    }

    public static class Foo9 {
        public Foo9(@Reference(name = "myRef")
        String ref) {
        }
    }

    public static class Foo10 {

        public Foo10() {
        }

        public Foo10(String prop) {
        }

        public Foo10(@Property(name = "prop1")
        String prop1, @Property(name = "prop2")
        String prop2) {

        }
    }

    public static class Foo11 {

        public Foo11(@Property(name = "prop1")
        String prop, @Baz
        String baz) {
        }
    }

    public static class Foo12 {

        public Foo12(@Property(name = "prop1")
        String prop, @Property(name = "prop2")
        String baz) {
        }
    }

    public @interface Baz {

    }

    public static class Foo13 {
        public Foo13(@Reference
        String foo) {
        }
    }

    public static final class Foo14 {
        private Foo14() {
        }
    }

    public static final class Foo15 {
        public Foo15(@Reference
        String param1, @Reference
        String param2) {
        }
    }

    public static final class Foo16 {
        public Foo16(@Reference
        String param1, @Property(name = "foo")
        String param2, @Reference(name = "bar")
        String param3) {
        }
    }

}
