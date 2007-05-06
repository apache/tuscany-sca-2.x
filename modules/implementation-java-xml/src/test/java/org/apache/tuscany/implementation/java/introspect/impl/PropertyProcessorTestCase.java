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

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.implementation.java.introspect.DuplicatePropertyException;
import org.apache.tuscany.implementation.java.introspect.IllegalPropertyException;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev$ $Date$
 */
public class PropertyProcessorTestCase extends TestCase {

    JavaImplementation type;
    PropertyProcessor processor;

    public void testMethodAnnotation() throws Exception {
        processor.visitMethod(Foo.class.getMethod("setFoo", String.class), type);
        assertNotNull(getProperty(type, "foo"));
    }

    public void testMethodRequired() throws Exception {
        processor.visitMethod(Foo.class.getMethod("setFooRequired", String.class), type);
        org.apache.tuscany.assembly.Property prop = getProperty(type, "fooRequired");
        assertNotNull(prop);
        assertTrue(prop.isMustSupply());
    }

    public void testMethodName() throws Exception {
        processor.visitMethod(Foo.class.getMethod("setBarMethod", String.class), type);
        assertNotNull(getProperty(type, "bar"));
    }

    public void testFieldAnnotation() throws Exception {
        processor.visitField(Foo.class.getDeclaredField("baz"), type);
        assertNotNull(getProperty(type, "baz"));
    }

    public void testFieldRequired() throws Exception {
        processor.visitField(Foo.class.getDeclaredField("bazRequired"), type);
        org.apache.tuscany.assembly.Property prop = getProperty(type, "bazRequired");
        assertNotNull(prop);
        assertTrue(prop.isMustSupply());
    }

    public void testFieldName() throws Exception {
        processor.visitField(Foo.class.getDeclaredField("bazField"), type);
        assertNotNull(getProperty(type, "theBaz"));
    }

    public void testDuplicateFields() throws Exception {
        processor.visitField(Bar.class.getDeclaredField("dup"), type);
        try {
            processor.visitField(Bar.class.getDeclaredField("baz"), type);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    public void testDuplicateMethods() throws Exception {
        processor.visitMethod(Bar.class.getMethod("dupMethod", String.class), type);
        try {
            processor.visitMethod(Bar.class.getMethod("dupSomeMethod", String.class), type);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    public void testInvalidProperty() throws Exception {
        try {
            processor.visitMethod(Bar.class.getMethod("badMethod"), type);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        type = javaImplementationFactory.createJavaImplementation();
        processor = new PropertyProcessor(new DefaultAssemblyFactory());
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

    private class Multiple {
        @Property
        protected List<String> refs1;

        @Property
        protected String[] refs2;

        @Property
        public void setRefs3(String[] refs) {
        }

        @Property
        public void setRefs4(Collection<String> refs) {
        }

    }

    private Class<?> getBaseType(JavaElementImpl element) {
        return JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
    }

    public void testMultiplicityCollection() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs1"), type);
        org.apache.tuscany.assembly.Property prop = getProperty(type, "refs1");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

    public void testMultiplicityArray() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs2"), type);
        org.apache.tuscany.assembly.Property prop = getProperty(type, "refs2");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

    public void testMultiplicityArrayMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs3", String[].class), type);
        org.apache.tuscany.assembly.Property prop = getProperty(type, "refs3");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

    public void testMultiplicityCollectionMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs4", Collection.class), type);
        org.apache.tuscany.assembly.Property prop = getProperty(type, "refs4");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

}
