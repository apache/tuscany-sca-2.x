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
import java.util.Map;

import org.apache.tuscany.spi.annotation.Monitor;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.injection.MonitorObjectFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class MonitorProcessorTestCase extends MockObjectTestCase {

    private AutowireComponent parent;

    public void testSetter() throws Exception {
        MonitorProcessor processor = new MonitorProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = Foo.class.getMethod("setMonitor", Foo.class);
        processor.visitMethod(parent, method, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        assertTrue(properties.get("monitor").getDefaultValueFactory() instanceof MonitorObjectFactory);
    }


    public void testBadSetter() throws Exception {
        MonitorProcessor processor = new MonitorProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = BadMonitor.class.getMethod("setMonitor");
        try {
            processor.visitMethod(parent, method, type, null);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    public void testField() throws Exception {
        MonitorProcessor processor = new MonitorProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Field field = Foo.class.getDeclaredField("bar");
        processor.visitField(parent, field, type, null);
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        assertTrue(properties.get("bar").getDefaultValueFactory() instanceof MonitorObjectFactory);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Mock mock = mock(AutowireComponent.class);
        parent = (AutowireComponent) mock.proxy();
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
}
