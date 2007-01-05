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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.annotations.Context;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ContextProcessorTestCase extends TestCase {
    private ContextProcessor processor;
    private CompositeComponent composite;

    public void testMethod() throws Exception {
        Method method = Foo.class.getMethod("setContext", CompositeContext.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(composite, method, type, null);
        assertNotNull(type.getResources().get("context"));
    }

    public void testField() throws Exception {
        Field field = Foo.class.getDeclaredField("context");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitField(composite, field, type, null);
        assertNotNull(type.getResources().get("context"));
    }

    public void testInvalidParamType() throws Exception {
        Method method = Foo.class.getMethod("setContext", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(composite, method, type, null);
            fail();
        } catch (UnknownContextTypeException e) {
            // expected
        }
    }

    public void testInvalidParamTypeField() throws Exception {
        Field field = Foo.class.getDeclaredField("badContext");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitField(composite, field, type, null);
            fail();
        } catch (UnknownContextTypeException e) {
            // expected
        }
    }


    public void testInvalidParamNum() throws Exception {
        Method method = Foo.class.getMethod("setContext", CompositeContext.class, String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(composite, method, type, null);
            fail();
        } catch (IllegalContextException e) {
            // expected
        }
    }

    public void testInvalidNoParams() throws Exception {
        Method method = Foo.class.getMethod("setContext");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(composite, method, type, null);
            fail();
        } catch (IllegalContextException e) {
            // expected
        }
    }

    public void testNoContext() throws Exception {
        Method method = Foo.class.getMethod("noContext", CompositeContext.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(composite, method, type, null);
        assertEquals(0, type.getResources().size());
    }

    public void testNoContextField() throws Exception {
        Field field = Foo.class.getDeclaredField("noContext");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitField(composite, field, type, null);
        assertEquals(0, type.getResources().size());
    }

    protected void setUp() throws Exception {
        super.setUp();
        processor = new ContextProcessor();
        processor.setWireService(EasyMock.createNiceMock(WireService.class));
        composite = EasyMock.createNiceMock(CompositeComponent.class);
    }

    private class Foo {
        @Context
        protected CompositeContext context;

        @Context
        protected Object badContext;

        protected CompositeContext noContext;

        @Context
        public void setContext(CompositeContext context) {

        }

        @Context
        public void setContext(String context) {

        }

        @Context
        public void setContext(CompositeContext context, String string) {

        }

        @Context
        public void setContext() {

        }

        public void noContext(CompositeContext context) {

        }

    }
}
