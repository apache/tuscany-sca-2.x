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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.DuplicatePropertyException;
import org.apache.tuscany.spi.implementation.java.IllegalPropertyException;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessor;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AbstractPropertyProcessorTestCase extends TestCase {

    private ImplementationProcessor processor;

    public void testVisitMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(method, type, null);
        JavaMappedProperty<?> prop = type.getProperties().get("test");
        assertNotNull(prop.getDefaultValueFactory());
    }

    public void testVisitNoParamsMethod() throws Exception {
        Method method = Foo.class.getMethod("setNoParamsBar");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    public void testVisitNonVoidMethod() throws Exception {
        Method method = Foo.class.getMethod("setBadBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    public void testDuplicateMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(method, type, null);
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    public void testVisitField() throws Exception {
        Field field = Foo.class.getDeclaredField("d");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitField(field, type, null);
        JavaMappedProperty<?> prop = type.getProperties().get("test");
        assertNotNull(prop.getDefaultValueFactory());
    }

    public void testVisitConstructor() throws Exception {
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        ConstructorDefinition def = new ConstructorDefinition<Foo>(ctor);
        Parameter parameter = def.getParameters()[0];
        processor.visitConstructorParameter(parameter, type, null);
        assertEquals("test", def.getParameters()[0].getName());
        assertNotNull(type.getProperties().get("test"));
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        processor = new TestProcessor();
    }

    @Retention(RUNTIME)
    private @interface Bar {

    }

    private class TestProcessor extends AbstractPropertyProcessor<Bar> {

        public TestProcessor() {
            super(Bar.class);
        }

        @SuppressWarnings("unchecked")
        protected <T> void initProperty(JavaMappedProperty<T> property, Bar annotation, DeploymentContext context) {
            property.setDefaultValueFactory(EasyMock.createMock(ObjectFactory.class));
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
