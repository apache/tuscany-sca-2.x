/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class ServiceCallbackTestCase extends TestCase {

    ServiceProcessor processor = new ServiceProcessor();

    public void testMethodCallbackInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, FooImpl.class, type, null);
        JavaMappedService service = type.getServices().get("ServiceCallbackTestCase$Foo");
        assertNotNull(service);
        Method method = FooImpl.class.getMethod("setCallback", FooCallback.class);
        processor.visitMethod(null, method, type, null);
        assertEquals(method, service.getCallbackMember());
    }

    public void testFieldCallbackInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, FooImpl.class, type, null);
        JavaMappedService service = type.getServices().get("ServiceCallbackTestCase$Foo");
        assertNotNull(service);
        Field field = FooImpl.class.getDeclaredField("callback");
        processor.visitField(null, field, type, null);
        assertEquals(field, service.getCallbackMember());
    }

    public void testMethodDoesNotMatchCallback() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, BadBarImpl.class, type, null);
        Method method = BadBarImpl.class.getMethod("setWrongInterfaceCallback", String.class);
        try {
            processor.visitMethod(null, method, type, null);
            fail();
        } catch (IllegalCallbackException e) {
            // expected
        }
    }

    public void testNoParamCallback() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, BadBarImpl.class, type, null);
        Method method = BadBarImpl.class.getMethod("setNoParamCallback");
        try {
            processor.visitMethod(null, method, type, null);
            fail();
        } catch (IllegalCallbackException e) {
            // expected
        }
    }

    public void testFieldDoesNotMatchCallback() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, BadBarImpl.class, type, null);
        Field field = BadBarImpl.class.getDeclaredField("wrongInterfaceCallback");
        try {
            processor.visitField(null, field, type, null);
            fail();
        } catch (IllegalCallbackException e) {
            // expected
        }
    }

    public void testBadCallbackInterfaceAnnotation() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitClass(null, BadFooImpl.class, type, null);
            fail();
        } catch (IllegalCallbackException e) {
            // expected
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
