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

import junit.framework.TestCase;

import org.apache.tuscany.spi.implementation.java.DuplicatePropertyException;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorPropertyTestCase extends AbstractConstructorProcessorTest {

    public void testProperty() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        visitConstructor(ctor, type, null);
        JavaMappedProperty<?> property = type.getProperties().get("myProp");
        assertTrue(property.isRequired());
        assertEquals("myProp", property.getName());
    }

    public void testTwoPropertiesSameType() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class, String.class);
        visitConstructor(ctor, type, null);
        assertNotNull(type.getProperties().get("myProp1"));
        assertNotNull(type.getProperties().get("myProp2"));
    }

    public void testDuplicateProperty() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(String.class, String.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    public void testNoName() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(String.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (InvalidPropertyException e) {
            // expected
        }
    }

    public void testNamesOnConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(Integer.class);
        visitConstructor(ctor, type, null);
        assertNotNull(type.getProperties().get("myProp"));
    }

    public void testInvalidNumberOfNames() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(Integer.class, Integer.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    public void testNoMatchingNames() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(List.class, List.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

//    public void testMultiplicityRequired() throws Exception {
    // TODO multiplicity
//    }

    private static class Foo {

        @org.osoa.sca.annotations.Constructor()
        public Foo(@Property(name = "myProp", required = true)String prop) {

        }

        @org.osoa.sca.annotations.Constructor("myProp")
        public Foo(@Property Integer prop) {

        }

        @org.osoa.sca.annotations.Constructor()
        public Foo(@Property(name = "myProp1")String prop1, @Property(name = "myProp2")String prop2) {

        }

        @org.osoa.sca.annotations.Constructor()
        public Foo(@Property List prop) {

        }
    }

    private static class BadFoo {

        @org.osoa.sca.annotations.Constructor()
        public BadFoo(@Property(name = "myProp")String prop1, @Property(name = "myProp")String prop2) {

        }

        @org.osoa.sca.annotations.Constructor()
        public BadFoo(@Property String prop) {

        }

        @org.osoa.sca.annotations.Constructor("myProp")
        public BadFoo(@Property Integer prop, @Property Integer prop2) {

        }

        @org.osoa.sca.annotations.Constructor({"myRef", "myRef2"})
        public BadFoo(@Property List ref, @Property(name = "myOtherRef")List ref2) {

        }

    }

}
