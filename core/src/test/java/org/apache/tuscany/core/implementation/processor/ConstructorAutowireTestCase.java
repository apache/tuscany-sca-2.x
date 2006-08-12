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
import java.util.List;

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.annotation.Autowire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorAutowireTestCase extends TestCase {

    ConstructorProcessor processor = new ConstructorProcessor();

    public void testAutowire() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = Foo.class.getConstructor(Bar.class);
        processor.visitConstructor(null, ctor, type, null);
        assertNotNull(type.getReferences().get("myRef"));
    }

    public void testNamesOnConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = Foo.class.getConstructor(Bar.class, Bar.class);
        processor.visitConstructor(null, ctor, type, null);
        assertEquals(2, type.getConstructorDefinition().getInjectionNames().size());
        assertNotNull(type.getReferences().get("myRef1"));
        assertNotNull(type.getReferences().get("myRef2"));
    }

    public void testNoName() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = Foo2.class.getConstructor(Bar.class);
        processor.visitConstructor(null, ctor, type, null);
        assertNotNull(type.getReferences().get(Bar.class.getName()));
    }

    public void testInvalidNumberOfNames() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = BadFoo.class.getConstructor(Bar.class, Bar.class);
        try {
            processor.visitConstructor(null, ctor, type, null);
            fail();
        } catch (InvalidAutowireException e) {
            // expected
        }
    }

    public void testNoMatchingNames() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = BadFoo.class.getConstructor(List.class, List.class);
        try {
            processor.visitConstructor(null, ctor, type, null);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    /**
     * Verifies processing executes with additional extension annotations
     */
    public void testRandomAnnotation() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = Foo3.class.getConstructor(String.class, String.class);
        processor.visitConstructor(null, ctor, type, null);
        assertEquals(1, type.getProperties().size());
        assertNotNull(type.getProperties().get("prop1"));
    }

    private static interface Bar {

    }

    private static class Foo {

        @org.osoa.sca.annotations.Constructor()
        public Foo(@Autowire(name = "myRef") Bar ref) {

        }

        @org.osoa.sca.annotations.Constructor({"myRef1", "myRef2"})
        public Foo(@Autowire Bar ref1, @Autowire Bar ref2) {

        }

    }

    private static class Foo2 {
        @org.osoa.sca.annotations.Constructor()
        public Foo2(@Autowire Bar ref) {

        }
    }

    private static class BadFoo {

        @org.osoa.sca.annotations.Constructor({"ref1"})
        public BadFoo(@Autowire Bar ref1, @Autowire Bar ref2) {

        }

        @org.osoa.sca.annotations.Constructor({"myRef", "myRef2"})
        public BadFoo(@Autowire List ref, @Autowire(name = "myOtherRef") List ref2) {

        }

    }

    public static class Foo3 {

        @org.osoa.sca.annotations.Constructor
        public Foo3(@Property(name = "prop1") String prop, @Baz String baz) {
        }
    }

    public @interface Baz {

    }
}
