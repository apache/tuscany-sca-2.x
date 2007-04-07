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
package org.apache.tuscany.implementation.java.introspection.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspection.ImplementationProcessor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class IntrospectionRegistryTestCase extends TestCase {

    public void testRegister() throws Exception {
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl();
        ImplementationProcessor processor = EasyMock.createNiceMock(ImplementationProcessor.class);
        registry.registerProcessor(processor);
    }

    public void testUnegister() throws Exception {
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl();
        ImplementationProcessor processor = EasyMock.createNiceMock(ImplementationProcessor.class);
        registry.registerProcessor(processor);
        registry.unregisterProcessor(processor);
    }

    @SuppressWarnings("unchecked")
    public void testWalk() throws Exception {
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl();
        ImplementationProcessor processor = EasyMock.createMock(ImplementationProcessor.class);
        processor.visitClass(EasyMock.eq(Bar.class), EasyMock.isA(JavaImplementationDefinition.class));
        processor.visitConstructor(EasyMock.isA(Constructor.class), EasyMock.isA(JavaImplementationDefinition.class));
        processor.visitMethod(EasyMock.isA(Method.class), EasyMock.isA(JavaImplementationDefinition.class));
        processor.visitField(EasyMock.isA(Field.class), EasyMock.isA(JavaImplementationDefinition.class));
        processor.visitSuperClass(EasyMock.isA(Class.class), EasyMock.isA(JavaImplementationDefinition.class));
        processor.visitEnd(EasyMock.isA(Class.class), EasyMock.isA(JavaImplementationDefinition.class));

        // mock.expects(once()).method("visitClass");
        // mock.expects(once()).method("visitMethod");
        // mock.expects(once()).method("visitField");
        // mock.expects(once()).method("visitConstructor");
        // mock.expects(once()).method("visitSuperClass");
        // mock.expects(once()).method("visitEnd");
        EasyMock.replay(processor);
        registry.registerProcessor(processor);
        registry.introspect(Bar.class, new JavaImplementationDefinition());
        EasyMock.verify(processor);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    private class Baz {

    }

    private class Bar extends Baz {

        protected String bar;

        public Bar() {
        }

        public void bar() {
        }

    }

}
