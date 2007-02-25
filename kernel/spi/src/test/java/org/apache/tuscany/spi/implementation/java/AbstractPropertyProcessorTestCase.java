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
package org.apache.tuscany.spi.implementation.java;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class AbstractPropertyProcessorTestCase extends TestCase {

    private ImplementationProcessor processor;


    public void testVisitMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(method, type, null);
        JavaMappedProperty<?> prop = type.getProperties().get("test");
        assertNotNull(prop.getDefaultValueFactory());
    }

    public void testVisitNoParamsMethod() throws Exception {
        Method method = Foo.class.getMethod("setNoParamsBar");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalPropertyException e) {
            //expected
        }
    }

    public void testVisitNonVoidMethod() throws Exception {
        Method method = Foo.class.getMethod("setBadBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalPropertyException e) {
            //expected
        }
    }

    public void testDuplicateMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(method, type, null);
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (DuplicatePropertyException e) {
            //expected
        }
    }

    public void testVisitField() throws Exception {
        Field field = Foo.class.getDeclaredField("d");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitField(field, type, null);
        JavaMappedProperty<?> prop = type.getProperties().get("test");
        assertNotNull(prop.getDefaultValueFactory());
    }

    public void testVisitConstructor() throws Exception {
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitConstructor(ctor, type, null);
        ConstructorDefinition def = type.getConstructorDefinition();
        assertEquals("test", def.getInjectionNames().get(0));
        assertNotNull(type.getProperties().get("test"));
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        ImplementationProcessorService service = EasyMock.createMock(ImplementationProcessorService.class);
        service.addName(EasyMock.isA(List.class), EasyMock.eq(0), EasyMock.eq("test"));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                ((List<Object>) EasyMock.getCurrentArguments()[0]).add("test");
                return null;
            }
        });
        EasyMock.replay(service);
        processor = new TestProcessor(service);
    }

    @Retention(RUNTIME)
    private @interface Bar {

    }

    private class TestProcessor extends AbstractPropertyProcessor<Bar> {

        public TestProcessor(ImplementationProcessorService service) {
            super(Bar.class, service);
        }

        @SuppressWarnings("unchecked")
        protected <T> void initProperty(JavaMappedProperty<T> property,
                                        Bar annotation,
                                        DeploymentContext context) {
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

        public Foo(String a, @Bar String b) {
        }
        
        public Foo(@Bar String d) {
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
