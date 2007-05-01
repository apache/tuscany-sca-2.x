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

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.tuscany.implementation.java.introspect.impl.ModelHelper.getProperty;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.implementation.java.introspect.DuplicatePropertyException;
import org.apache.tuscany.implementation.java.introspect.IllegalPropertyException;
import org.apache.tuscany.implementation.java.introspect.JavaClassIntrospectorExtension;


/**
 * @version $Rev$ $Date$
 */
public class AbstractPropertyProcessorTestCase extends TestCase {

    private JavaClassIntrospectorExtension extension;
    private JavaImplementationFactory javaImplementationFactory;

    public void testVisitMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", String.class);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        extension.visitMethod(method, type);
        Property prop = getProperty(type, "test");
        assertNotNull(prop);
    }

    public void testVisitNoParamsMethod() throws Exception {
        Method method = Foo.class.getMethod("setNoParamsBar");
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        try {
            extension.visitMethod(method, type);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    public void testVisitNonVoidMethod() throws Exception {
        Method method = Foo.class.getMethod("setBadBar", String.class);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        try {
            extension.visitMethod(method, type);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    public void testDuplicateMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", String.class);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        extension.visitMethod(method, type);
        try {
            extension.visitMethod(method, type);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    public void testVisitField() throws Exception {
        Field field = Foo.class.getDeclaredField("d");
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        extension.visitField(field, type);
        Property prop = getProperty(type, "test");
        assertNotNull(prop);
    }

    public void testVisitConstructor() throws Exception {
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        JavaConstructorImpl<Foo> def = new JavaConstructorImpl<Foo>(ctor);
        JavaParameterImpl parameter = def.getParameters()[0];
        extension.visitConstructorParameter(parameter, type);
        assertEquals("test", def.getParameters()[0].getName());
        assertNotNull(getProperty(type, "test"));
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        extension = new TestProcessor();
        javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultAssemblyFactory());
    }

    @Retention(RUNTIME)
    private @interface Bar {

    }

    private class TestProcessor extends AbstractPropertyProcessor<Bar> {

        public TestProcessor() {
            super(new DefaultAssemblyFactory(), Bar.class);
        }

        @SuppressWarnings("unchecked")
        protected void initProperty(Property property, Bar annotation) {
            // property.setDefaultValueFactory(EasyMock.createMock(ObjectFactory.class));
            property.setName("test");
        }

        protected String getName(Bar annotation) {
            return "test";
        }
    }

    private static class Foo {

        @Bar
        protected String d;

        public Foo(String a, @Bar
        String b) {
        }

        public Foo(@Bar
        String d) {
            this.d = d;
        }

        @Bar
        public void setBar(String d) {
            this.d = d;
        }

        @Bar
        public void setNoParamsBar() {
        }

        @Bar
        public String setBadBar(String d) {
            return null;
        }
    }
}
