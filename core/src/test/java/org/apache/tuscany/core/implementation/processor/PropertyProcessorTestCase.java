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

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.implementation.java.JavaMappedService;

import junit.framework.TestCase;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;

import org.apache.tuscany.spi.implementation.java.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class PropertyProcessorTestCase extends TestCase {

    PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type;
    PropertyProcessor processor;

    public void testMethodAnnotation() throws Exception {
        processor.visitMethod(null, Foo.class.getMethod("setFoo", String.class), type, null);
        assertNotNull(type.getProperties().get("foo"));
    }

    public void testMethodRequired() throws Exception {
        processor.visitMethod(null, Foo.class.getMethod("setFooRequired", String.class), type, null);
        JavaMappedProperty prop = type.getProperties().get("fooRequired");
        assertNotNull(prop);
        assertTrue(prop.isRequired());
    }

    public void testMethodName() throws Exception {
        processor.visitMethod(null, Foo.class.getMethod("setBarMethod", String.class), type, null);
        assertNotNull(type.getProperties().get("bar"));
    }

    public void testFieldAnnotation() throws Exception {
        processor.visitField(null, Foo.class.getDeclaredField("baz"), type, null);
        assertNotNull(type.getProperties().get("baz"));
    }

    public void testFieldRequired() throws Exception {
        processor.visitField(null, Foo.class.getDeclaredField("bazRequired"), type, null);
        JavaMappedProperty prop = type.getProperties().get("bazRequired");
        assertNotNull(prop);
        assertTrue(prop.isRequired());
    }

    public void testFieldName() throws Exception {
        processor.visitField(null, Foo.class.getDeclaredField("bazField"), type, null);
        assertNotNull(type.getProperties().get("theBaz"));
    }

    public void testDuplicateFields() throws Exception {
        processor.visitField(null, Bar.class.getDeclaredField("dup"), type, null);
        try {
            processor.visitField(null, Bar.class.getDeclaredField("baz"), type, null);
            fail();
        } catch (DuplicatePropertyException e) {
            //expected
        }
    }

    public void testDuplicateMethods() throws Exception {
        processor.visitMethod(null, Bar.class.getMethod("dupMethod", String.class), type, null);
        try {
            processor.visitMethod(null, Bar.class.getMethod("dupSomeMethod", String.class), type, null);
            fail();
        } catch (DuplicatePropertyException e) {
            //expected
        }
    }

    public void testInvalidProperty() throws Exception {
        try {
            processor.visitMethod(null, Bar.class.getMethod("badMethod"), type, null);
            fail();
        } catch (IllegalPropertyException e) {
            //expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor = new PropertyProcessor();
    }

    private class Foo {

        @Property
        protected String baz;
        @Property(required = true)
        protected String bazRequired;
        @Property(name = "theBaz")
        protected String bazField;


        @Property
        public void setFoo(String string) {
        }

        @Property(required = true)
        public void setFooRequired(String string) {
        }

        @Property(name = "bar")
        public void setBarMethod(String string) {
        }

    }


    private class Bar {

        @Property
        protected String dup;

        @Property(name = "dup")
        protected String baz;

        @Property
        public void dupMethod(String s) {
        }

        @Property(name = "dupMethod")
        public void dupSomeMethod(String s) {
        }

        @Property
        public void badMethod() {
        }


    }
}
