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
package org.apache.tuscany.core.implementation.system.component;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.easymock.EasyMock;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Verifies a system atomic component can be started and initialized
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private URI groupId;

    public void testDestroy() throws Exception {
        PojoObjectFactory<Foo> factory = new PojoObjectFactory<Foo>(Foo.class.getConstructor((Class[]) null));
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(factory);
        configuration.setDestroyInvoker(destroyInvoker);
        configuration.setName(new URI("foo"));
        configuration.setGroupId(groupId);
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl(configuration);
        Foo foo = (Foo) component.createInstance();
        component.destroy(foo);
        assertTrue(foo.destroyed);
    }

    public void testReferenceAndPropertyConstructor() throws Exception {
        PojoObjectFactory<Bar> factory = new PojoObjectFactory<Bar>(Bar.class.getConstructor(String.class, Foo.class));
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        Constructor<?> constructor = Bar.class.getConstructor(new Class[] {String.class, Foo.class});
        ConstructorDefinition<?> definition = new ConstructorDefinition(constructor);
        definition.getParameters()[0].setName("foo");
        definition.getParameters()[0].setClassifer(Property.class);
        definition.getParameters()[1].setName("ref");
        definition.getParameters()[1].setClassifer(Reference.class);
        configuration.setConstructor(definition);
        configuration.setName(new URI("foo"));
        configuration.setGroupId(groupId);
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl(configuration);
        component.addPropertyFactory("foo", new SingletonObjectFactory<String>("baz"));
        Foo target = new Foo();
        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getTargetInstance()).andReturn(target);
        URI uri = URI.create("#ref");
        EasyMock.expect(wire.getSourceUri()).andReturn(uri).anyTimes();
        EasyMock.replay(wire);
        component.attachWire(wire);
        Bar bar = (Bar) component.createInstance();
        assertEquals("baz", bar.foo);
        assertEquals(target, bar.ref);
        EasyMock.verify(wire);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initInvoker = new MethodEventInvoker<Object>(Foo.class.getMethod("init"));
        destroyInvoker = new MethodEventInvoker<Object>(Foo.class.getMethod("destroy"));
        groupId = URI.create("composite");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static class Foo {
        private boolean initialized;
        private boolean destroyed;

        public void init() {
            initialized = true;
        }

        public void destroy() {
            destroyed = true;
        }
    }

    public static class Bar {

        private String foo;
        private Foo ref;

        public Bar(String foo, Foo ref) {
            this.foo = foo;
            this.ref = ref;
        }
    }


}
