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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import org.apache.tuscany.api.annotation.Monitor;
import org.apache.tuscany.core.idl.java.InterfaceJavaIntrospectorImpl;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.host.MonitorFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class MonitorProcessorTestCase extends MockObjectTestCase {

    private MonitorProcessor processor;
    private Mock monitorFactory;

    public void testSetter() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = Foo.class.getMethod("setMonitor", Foo.class);
        monitorFactory.expects(once()).method("getMonitor").with(eq(Foo.class)).will(returnValue(null));
        processor.visitMethod(null, method, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        assertTrue(properties.get("monitor").getDefaultValueFactory() instanceof SingletonObjectFactory);
    }


    public void testBadSetter() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = BadMonitor.class.getMethod("setMonitor");
        try {
            processor.visitMethod(null, method, type, null);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    public void testField() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Field field = Foo.class.getDeclaredField("bar");
        monitorFactory.expects(once()).method("getMonitor").with(eq(Foo.class)).will(returnValue(null));
        processor.visitField(null, field, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        assertTrue(properties.get("bar").getDefaultValueFactory() instanceof SingletonObjectFactory);
    }

    public void testConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Bar> ctor = Bar.class.getConstructor(BazMonitor.class);
        monitorFactory.expects(once()).method("getMonitor").with(eq(BazMonitor.class)).will(returnValue(null));
        processor.visitConstructor(null, ctor, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        assertTrue(
            properties.get(BazMonitor.class.getName()).getDefaultValueFactory() instanceof SingletonObjectFactory);
    }

    /**
     * Verifies calling the monitor processor to evaluate a constructor can be done after a property parameter is
     * processed
     */
    public void testConstructorAfterProperty() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Bar> ctor = Bar.class.getConstructor(String.class, BazMonitor.class);
        monitorFactory.expects(once()).method("getMonitor").with(eq(BazMonitor.class)).will(returnValue(null));
        ConstructorDefinition<Bar> definition = new ConstructorDefinition<Bar>(ctor);
        JavaMappedProperty prop = new JavaMappedProperty();
        definition.getInjectionNames().add("prop");
        type.setConstructorDefinition(definition);
        type.getProperties().put("prop", prop);
        processor.visitConstructor(null, ctor, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        assertEquals(BazMonitor.class.getName(), definition.getInjectionNames().get(1));
        assertEquals(2, type.getProperties().size());
        assertTrue(
            properties.get(BazMonitor.class.getName()).getDefaultValueFactory() instanceof SingletonObjectFactory);
    }

    /**
     * Verifies calling the monitor processor to evaluate a constructor can be done before a property parameter is
     * processed
     */
    public void testConstructorBeforeProperty() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Bar> ctor = Bar.class.getConstructor(String.class, BazMonitor.class);
        monitorFactory.expects(once()).method("getMonitor").with(eq(BazMonitor.class)).will(returnValue(null));
        processor.visitConstructor(null, ctor, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        ConstructorDefinition definition = type.getConstructorDefinition();
        assertEquals(2, definition.getInjectionNames().size());
        assertEquals(BazMonitor.class.getName(), definition.getInjectionNames().get(1));
        assertTrue(
            properties.get(BazMonitor.class.getName()).getDefaultValueFactory() instanceof SingletonObjectFactory);
    }

    protected void setUp() throws Exception {
        super.setUp();
        monitorFactory = mock(MonitorFactory.class);
        processor = new MonitorProcessor((MonitorFactory) monitorFactory.proxy(),
            new ImplementationProcessorServiceImpl(new InterfaceJavaIntrospectorImpl()));
    }

    private class Foo {

        @Monitor
        protected Foo bar;

        @Monitor
        public void setMonitor(Foo foo) {
        }
    }


    private class BadMonitor {

        @Monitor
        public void setMonitor() {
        }
    }

    private interface BazMonitor {

    }

    private static class Bar {

        public Bar(@Monitor BazMonitor monitor) {
        }

        public Bar(String prop, @Monitor BazMonitor monitor) {
        }
    }
}
