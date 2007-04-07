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
package org.apache.tuscany.implementation.java.processor;

import static org.apache.tuscany.implementation.java.processor.ModelHelper.getService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspection.ProcessingException;
import org.apache.tuscany.implementation.java.processor.IllegalCallbackReferenceException;
import org.apache.tuscany.implementation.java.processor.ServiceProcessor;
import org.apache.tuscany.interfacedef.InvalidCallbackException;
import org.apache.tuscany.interfacedef.java.introspection.impl.JavaInterfaceProcessorRegistryImpl;
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
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        processor.visitClass(FooImpl.class, type);
        org.apache.tuscany.assembly.Service service = getService(type, Foo.class.getSimpleName());
        assertNotNull(service);
        Method method = FooImpl.class.getMethod("setCallback", FooCallback.class);
        processor.visitMethod(method, type);
        assertEquals(method, type.getCallbackMembers().get("callback").getAnchor());
    }

    public void testFieldCallbackInterface() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        processor.visitClass(FooImpl.class, type);
        org.apache.tuscany.assembly.Service service = getService(type, Foo.class.getSimpleName());
        assertNotNull(service);
        Field field = FooImpl.class.getDeclaredField("callback");
        processor.visitField(field, type);
        assertEquals(field, type.getCallbackMembers().get(field.getName()).getAnchor());
    }

    public void testMethodDoesNotMatchCallback() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        processor.visitClass(BadBarImpl.class, type);
        Method method = BadBarImpl.class.getMethod("setWrongInterfaceCallback", String.class);
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (IllegalCallbackReferenceException e) {
            // expected
        }
    }

    public void testNoParamCallback() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        processor.visitClass(BadBarImpl.class, type);
        Method method = BadBarImpl.class.getMethod("setNoParamCallback");
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (IllegalCallbackReferenceException e) {
            // expected
        }
    }

    public void testFieldDoesNotMatchCallback() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        processor.visitClass(BadBarImpl.class, type);
        Field field = BadBarImpl.class.getDeclaredField("wrongInterfaceCallback");
        try {
            processor.visitField(field, type);
            fail();
        } catch (IllegalCallbackReferenceException e) {
            // expected
        }
    }

    public void testBadCallbackInterfaceAnnotation() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        try {
            processor.visitClass(BadFooImpl.class, type);
            fail();
        } catch (ProcessingException e) {
            // expected
            assertTrue(e.getCause() instanceof InvalidCallbackException);
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
