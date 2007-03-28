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

import org.apache.tuscany.api.annotation.Resource;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorResourceTestCase extends AbstractProcessorTest {

    public void testResource() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        visitConstructor(ctor, type, null);
        org.apache.tuscany.spi.implementation.java.Resource resource = type.getResources().get("myResource");
        assertFalse(resource.isOptional());
    }

    public void testTwoResourcesSameType() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class, String.class);
        visitConstructor(ctor, type, null);
        assertNotNull(type.getResources().get("myResource1"));
        assertNotNull(type.getResources().get("myResource2"));
    }

    public void testDuplicateResource() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(String.class, String.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (DuplicateResourceException e) {
            // expected
        }
    }

    public void testNoName() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<ConstructorResourceTestCase.BadFoo> ctor =
            ConstructorResourceTestCase.BadFoo.class.getConstructor(String.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (InvalidResourceException e) {
            // expected
        }
    }

    public void testNamesOnConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(Integer.class);
        visitConstructor(ctor, type, null);
        assertNotNull(type.getResources().get("myResource"));
    }

    public void testInvalidNumberOfNames() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<ConstructorResourceTestCase.BadFoo> ctor =
            ConstructorResourceTestCase.BadFoo.class.getConstructor(Integer.class, Integer.class);
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
        Constructor<ConstructorResourceTestCase.BadFoo> ctor =
            ConstructorResourceTestCase.BadFoo.class.getConstructor(List.class, List.class);
        try {
            visitConstructor(ctor, type, null);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    private static class Foo {

        @org.osoa.sca.annotations.Constructor
        public Foo(@Resource(name = "myResource") String resource) {

        }

        @org.osoa.sca.annotations.Constructor("myResource")
        public Foo(@Resource Integer resource) {

        }

        @org.osoa.sca.annotations.Constructor
        public Foo(@Resource(name = "myResource1") String res1, @Resource(name = "myResource2") String res2) {

        }

        @org.osoa.sca.annotations.Constructor
        public Foo(@Resource List res) {

        }
    }

    private static class BadFoo {

        @org.osoa.sca.annotations.Constructor
        public BadFoo(@Resource(name = "myResource") String res1, @Resource(name = "myResource") String res2) {

        }

        @org.osoa.sca.annotations.Constructor
        public BadFoo(@Resource String res) {

        }

        @org.osoa.sca.annotations.Constructor("myProp")
        public BadFoo(@Resource Integer res, @Resource Integer res2) {

        }

        @org.osoa.sca.annotations.Constructor({"myRes", "myRes2"})
        public BadFoo(@Resource List res, @Resource(name = "myOtherRes") List res2) {

        }

    }

}
