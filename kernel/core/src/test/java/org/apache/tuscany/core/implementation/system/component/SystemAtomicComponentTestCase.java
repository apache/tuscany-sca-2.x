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

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.easymock.EasyMock;

/**
 * Verifies a system atomic component can be started and initialized
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;

    public void testDefaultCreationAndInit() throws Exception {
        PojoObjectFactory<Foo> factory = new PojoObjectFactory<Foo>(Foo.class.getConstructor((Class[]) null));
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.addServiceInterface(Foo.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl("foo", configuration);
        Foo foo = (Foo) component.createInstance();
        component.init(foo);
        assertTrue(foo.initialized);
    }

    public void testReferenceAndPropertyConstructor() throws Exception {
        PojoObjectFactory<Bar> factory = new PojoObjectFactory<Bar>(Bar.class.getConstructor(String.class, Foo.class));
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.addServiceInterface(Foo.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.addConstructorParamName("foo");
        configuration.addConstructorParamName("ref");
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl("foo", configuration);
        component.addPropertyFactory("foo", new SingletonObjectFactory<String>("baz"));
        Foo target = new Foo();
        SystemOutboundWire wire = EasyMock.createMock(SystemOutboundWire.class);
        EasyMock.expect(wire.getTargetService()).andReturn(target);
        EasyMock.expect(wire.getReferenceName()).andReturn("ref").anyTimes();
        EasyMock.replay(wire);
        component.addOutboundWire(wire);
        Bar bar = (Bar) component.createInstance();
        assertEquals("baz", bar.foo);
        assertEquals(target, bar.ref);
        EasyMock.verify(wire);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initInvoker = new MethodEventInvoker<Object>(Foo.class.getMethod("init"));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static class Foo {

        private boolean initialized;

        public void init() {
            initialized = true;
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
