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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspect.impl.ContextProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.IllegalContextException;
import org.apache.tuscany.implementation.java.introspect.impl.UnknownContextTypeException;
import org.easymock.EasyMock;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.Context;

/**
 * @version $Rev$ $Date$
 */
public class ContextProcessorTestCase extends TestCase {
    private ContextProcessor processor;
    private Component composite;

    // FIXME: resurrect to test ComponentContext injection
/*
    public void testCompositeContextMethod() throws Exception {
        Method method = Foo.class.getMethod("setContext", ComponentContext.class);
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitMethod(composite, method, type);
        assertNotNull(type.getResources().get("context"));
    }
*/

    // FIXME: resurrect to test ComponentContext injection
/*
    public void testCompositeContextField() throws Exception {
        Field field = Foo.class.getDeclaredField("context");
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitField(composite, field, type);
        assertNotNull(type.getResources().get("context"));
    }
*/

    public void testRequestContextMethod() throws Exception {
        Method method = Foo.class.getMethod("setRequestContext", RequestContext.class);
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitMethod(method, type);
        assertNotNull(type.getResources().get("requestContext"));
    }

    public void testRequestContextField() throws Exception {
        Field field = Foo.class.getDeclaredField("requestContext");
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitField(field, type);
        assertNotNull(type.getResources().get("requestContext"));
    }

    public void testInvalidParamType() throws Exception {
        Method method = Foo.class.getMethod("setContext", String.class);
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (UnknownContextTypeException e) {
            // expected
        }
    }

    public void testInvalidParamTypeField() throws Exception {
        Field field = Foo.class.getDeclaredField("badContext");
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        try {
            processor.visitField(field, type);
            fail();
        } catch (UnknownContextTypeException e) {
            // expected
        }
    }


    public void testInvalidParamNum() throws Exception {
        Method method = Foo.class.getMethod("setContext", ComponentContext.class, String.class);
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (IllegalContextException e) {
            // expected
        }
    }

    public void testInvalidNoParams() throws Exception {
        Method method = Foo.class.getMethod("setContext");
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (IllegalContextException e) {
            // expected
        }
    }

    public void testNoContext() throws Exception {
        Method method = Foo.class.getMethod("noContext", ComponentContext.class);
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitMethod(method, type);
        assertEquals(0, type.getResources().size());
    }

    public void testNoContextField() throws Exception {
        Field field = Foo.class.getDeclaredField("noContext");
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitField(field, type);
        assertEquals(0, type.getResources().size());
    }

    protected void setUp() throws Exception {
        super.setUp();
        processor = new ContextProcessor();
        // processor.setWorkContext(EasyMock.createNiceMock(WorkContext.class));
        composite = EasyMock.createNiceMock(Component.class);
    }

    private class Foo {
        @Context
        protected ComponentContext context;

        @Context
        protected Object badContext;

        protected ComponentContext noContext;

        @Context
        protected RequestContext requestContext;

        @Context
        public void setContext(ComponentContext context) {

        }

        @Context
        public void setContext(String context) {

        }

        @Context
        public void setContext(ComponentContext context, String string) {

        }

        @Context
        public void setContext() {

        }

        public void noContext(ComponentContext context) {

        }

        @Context
        public void setRequestContext(RequestContext requestContext) {
            this.requestContext = requestContext;
        }
    }
}
