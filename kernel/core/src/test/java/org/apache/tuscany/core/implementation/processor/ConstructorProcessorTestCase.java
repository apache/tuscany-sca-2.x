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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.Multiplicity;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorProcessorTestCase extends TestCase {
    private ConstructorProcessor processor =
        new ConstructorProcessor(new ImplementationProcessorServiceImpl(new JavaInterfaceProcessorRegistryImpl()));

    public void testDuplicateConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitClass(null, BadFoo.class, type, null);
            fail();
        } catch (DuplicateConstructorException e) {
            // expected
        }
    }

    public void testConstructorAnnotation() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor1 = Foo.class.getConstructor(String.class);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }

    public void testNoAnnotation() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<NoAnnotation> ctor1 = NoAnnotation.class.getConstructor();
        processor.visitConstructor(null, ctor1, type, null);
        assertNull(type.getConstructorDefinition());
    }

    public void testBadAnnotation() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<BadAnnotation> ctor1 = BadAnnotation.class.getConstructor(String.class, Foo.class);
        try {
            processor.visitConstructor(null, ctor1, type, null);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    public void testMixedParameters() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Mixed> ctor1 = Mixed.class.getConstructor(String.class, String.class, String.class);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("java.lang.String0", type.getConstructorDefinition().getInjectionNames().get(0));
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(1));
        assertEquals("bar", type.getConstructorDefinition().getInjectionNames().get(2));
    }

    public void testAllAutowireWithNoNames() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<AllAutowireNoName> ctor1 =
            AllAutowireNoName.class.getConstructor(String.class, String.class, String.class);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("java.lang.String0", type.getConstructorDefinition().getInjectionNames().get(0));
        assertEquals("java.lang.String1", type.getConstructorDefinition().getInjectionNames().get(1));
        assertEquals("java.lang.String2", type.getConstructorDefinition().getInjectionNames().get(2));
    }

    private static class BadFoo {

        @org.osoa.sca.annotations.Constructor("foo")
        public BadFoo(String foo) {

        }

        @org.osoa.sca.annotations.Constructor({"foo", "bar"})
        public BadFoo(String foo, String bar) {

        }
    }

    private static class Foo {
        @org.osoa.sca.annotations.Constructor("foo")
        public Foo(String foo) {

        }
    }

    private static class NoAnnotation {
        public NoAnnotation() {
        }
    }

    private static class BadAnnotation {
        @org.osoa.sca.annotations.Constructor("foo")
        public BadAnnotation(String foo, Foo ref) {
        }
    }


    public static final class Mixed {
        @org.osoa.sca.annotations.Constructor
        public Mixed(@Autowire String param1,
                     @Property(name = "foo")String param2,
                     @Reference(name = "bar")String param3) {
        }
    }

    public static final class AllAutowireNoName {
        @org.osoa.sca.annotations.Constructor
        public AllAutowireNoName(@Autowire String param1, @Autowire String param2, @Autowire String param3) {
        }
    }

    public static final class Multiple {
        @org.osoa.sca.annotations.Constructor
        public Multiple(@Autowire Collection<String> param1,
                        @Property(name = "foo")String[] param2,
                        @Reference(name = "bar", required = true)List<String> param3,
                        @Property(name = "abc")Set<String> param4,
                        @Reference(name = "xyz")String[] param5) {
        }
    }

    public void testMultiplicity() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Multiple> ctor1 =
            Multiple.class.getConstructor(Collection.class, String[].class, List.class, Set.class, String[].class);
        processor.visitConstructor(null, ctor1, type, null);
        JavaMappedReference ref0 = type.getReferences().get("java.util.Collection0");
        assertNotNull(ref0);
        assertEquals(Multiplicity.ONE_N, ref0.getMultiplicity());
        JavaMappedReference ref1 = type.getReferences().get("bar");
        assertNotNull(ref1);
        assertEquals(Multiplicity.ONE_N, ref1.getMultiplicity());
        JavaMappedReference ref2 = type.getReferences().get("xyz");
        assertNotNull(ref2);
        assertEquals(Multiplicity.ZERO_N, ref2.getMultiplicity());
        JavaMappedProperty prop1 = type.getProperties().get("foo");
        assertNotNull(prop1);
        assertTrue(prop1.isMany());
        JavaMappedProperty prop2 = type.getProperties().get("abc");
        assertNotNull(prop2);
        assertTrue(prop2.isMany());
    }

}
