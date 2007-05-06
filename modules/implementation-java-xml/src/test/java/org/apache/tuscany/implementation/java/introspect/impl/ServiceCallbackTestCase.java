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

import static org.apache.tuscany.implementation.java.introspect.impl.ModelHelper.getService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.interfacedef.InvalidCallbackException;
import org.apache.tuscany.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
public class ServiceCallbackTestCase extends TestCase {
    private ServiceProcessor processor;
    private JavaImplementationFactory javaImplementationFactory;

    @Override
    protected void setUp() throws Exception {
        JavaInterfaceIntrospectorExtensionPoint visitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
        processor = new ServiceProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory(), new ExtensibleJavaInterfaceIntrospector(new DefaultJavaInterfaceFactory(), visitors));
        javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultAssemblyFactory());
    }

    public void testMethodCallbackInterface() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(FooImpl.class, type);
        org.apache.tuscany.assembly.Service service = getService(type, Foo.class.getSimpleName());
        assertNotNull(service);
        Method method = FooImpl.class.getMethod("setCallback", FooCallback.class);
        processor.visitMethod(method, type);
        assertEquals(method, type.getCallbackMembers().get(FooCallback.class.getName()).getAnchor());
    }

    public void testFieldCallbackInterface() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(FooImpl.class, type);
        org.apache.tuscany.assembly.Service service = getService(type, Foo.class.getSimpleName());
        assertNotNull(service);
        Field field = FooImpl.class.getDeclaredField("callback");
        processor.visitField(field, type);
        assertEquals(field, type.getCallbackMembers().get(FooCallback.class.getName()).getAnchor());
    }

    public void testMethodDoesNotMatchCallback() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
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
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
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
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
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
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        try {
            processor.visitClass(BadFooImpl.class, type);
            fail();
        } catch (IntrospectionException e) {
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
