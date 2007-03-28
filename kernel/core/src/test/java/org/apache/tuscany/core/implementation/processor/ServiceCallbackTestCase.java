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

import junit.framework.TestCase;

import org.apache.tuscany.core.idl.java.IllegalCallbackException;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
public class ServiceCallbackTestCase extends TestCase {
    private ServiceProcessor processor;

    @Override
    protected void setUp() throws Exception {
        processor = new ServiceProcessor();
        processor.setInterfaceProcessorRegistry(new JavaInterfaceProcessorRegistryImpl());
    }

    public void testMethodCallbackInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(FooImpl.class, type, null);
        JavaMappedService service = type.getServices().get(Foo.class.getSimpleName());
        assertNotNull(service);
        Method method = FooImpl.class.getMethod("setCallback", FooCallback.class);
        processor.visitMethod(method, type, null);
        assertEquals(method, service.getCallbackMember());
    }

    public void testFieldCallbackInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(FooImpl.class, type, null);
        JavaMappedService service = type.getServices().get(Foo.class.getSimpleName());
        assertNotNull(service);
        Field field = FooImpl.class.getDeclaredField("callback");
        processor.visitField(field, type, null);
        assertEquals(field, service.getCallbackMember());
    }

    public void testMethodDoesNotMatchCallback() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(BadBarImpl.class, type, null);
        Method method = BadBarImpl.class.getMethod("setWrongInterfaceCallback", String.class);
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalCallbackReferenceException e) {
            // expected
        }
    }

    public void testNoParamCallback() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(BadBarImpl.class, type, null);
        Method method = BadBarImpl.class.getMethod("setNoParamCallback");
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalCallbackReferenceException e) {
            // expected
        }
    }

    public void testFieldDoesNotMatchCallback() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(BadBarImpl.class, type, null);
        Field field = BadBarImpl.class.getDeclaredField("wrongInterfaceCallback");
        try {
            processor.visitField(field, type, null);
            fail();
        } catch (IllegalCallbackReferenceException e) {
            // expected
        }
    }

    public void testBadCallbackInterfaceAnnotation() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitClass(BadFooImpl.class, type, null);
            fail();
        } catch (ProcessingException e) {
            // expected
            assertTrue(e.getCause() instanceof IllegalCallbackException);
        }
    }

    @Callback(FooCallback.class)
    private interface Foo {

    }

    private interface FooCallback {

    }

    @Service(Foo.class)
    private static class FooImpl implements Foo {

        @Callback
        protected FooCallback callback;

        @Callback
        public void setCallback(FooCallback cb) {

        }
    }

    private static class BadBarImpl implements Foo {
        @Callback
        protected String wrongInterfaceCallback;

        @Callback
        public void setWrongInterfaceCallback(String cb) {

        }

        @Callback
        public void setNoParamCallback() {

        }

    }

    @Callback
    private interface BadFoo {

    }

    @Service(BadFoo.class)
    private static class BadFooImpl implements BadFoo {

    }

}
