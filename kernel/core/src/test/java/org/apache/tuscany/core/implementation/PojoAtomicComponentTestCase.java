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
package org.apache.tuscany.core.implementation;

import java.net.URI;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class PojoAtomicComponentTestCase extends TestCase {
    private PojoObjectFactory<Foo> factory;

    @SuppressWarnings({"unchecked"})
    public void testDestroy() throws Exception {
        PojoConfiguration config = new PojoConfiguration();
        config.setName(URI.create("foo"));
        config.setInstanceFactory(factory);
        EventInvoker<Object> invoker = EasyMock.createMock(EventInvoker.class);
        invoker.invokeEvent(EasyMock.notNull());
        EasyMock.replay(invoker);
        config.setDestroyInvoker(invoker);
        AtomicComponent component = new TestAtomicComponent(config);
        assertTrue(component.isDestroyable());
        component.destroy(new Object());
        EasyMock.verify(invoker);
    }

    @SuppressWarnings({"unchecked"})
    public void testInit() throws Exception {
        PojoConfiguration config = new PojoConfiguration();
        config.setName(URI.create("foo"));
        config.setInstanceFactory(factory);
        EventInvoker<Object> invoker = EasyMock.createMock(EventInvoker.class);
        invoker.invokeEvent(EasyMock.notNull());
        EasyMock.replay(invoker);
        config.setInitInvoker(invoker);
        AtomicComponent component = new TestAtomicComponent(config);
        component.init(new Object());
        EasyMock.verify(invoker);
    }

    public void testOptimizable() throws Exception {
        PojoConfiguration config = new PojoConfiguration();
        config.setName(URI.create("foo"));
        config.setInstanceFactory(factory);
        TestAtomicComponent component = new TestAtomicComponent(config);
        assertTrue(component.isOptimizable());
    }

    @SuppressWarnings({"unchecked"})
    public void testDestroyableButOptimizable() throws Exception {
        PojoConfiguration config = new PojoConfiguration();
        config.setInstanceFactory(factory);
        config.setName(URI.create("foo"));
        EventInvoker<Object> invoker = EasyMock.createMock(EventInvoker.class);
        invoker.invokeEvent(EasyMock.notNull());
        EasyMock.replay(invoker);
        config.setDestroyInvoker(invoker);
        TestAtomicComponent component = new TestAtomicComponent(config);
        assertTrue(component.isOptimizable());
    }

    @SuppressWarnings({"unchecked"})
    public void testStatelessOptimizable() throws Exception {
        PojoConfiguration config = new PojoConfiguration();
        config.setName(URI.create("foo"));
        config.setInstanceFactory(factory);
        TestStatelessAtomicComponent component = new TestStatelessAtomicComponent(config);
        assertTrue(component.isOptimizable());
    }

    @SuppressWarnings({"unchecked"})
    public void testNotOptimizable() throws Exception {
        PojoConfiguration config = new PojoConfiguration();
        config.setInstanceFactory(factory);
        config.setName(URI.create("foo"));
        EventInvoker<Object> invoker = EasyMock.createMock(EventInvoker.class);
        invoker.invokeEvent(EasyMock.notNull());
        EasyMock.replay(invoker);
        config.setDestroyInvoker(invoker);
        TestStatelessAtomicComponent component = new TestStatelessAtomicComponent(config);
        assertFalse(component.isOptimizable());
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<Foo>(Foo.class.getConstructor());
    }

    private class TestAtomicComponent extends PojoAtomicComponent {

        public TestAtomicComponent(PojoConfiguration configuration) {
            super(configuration);
        }

        public Scope getScope() {
            return Scope.COMPOSITE;
        }

        protected ObjectFactory<?> createWireFactory(Class<?> interfaze, OutboundWire wire) {
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire)
            throws TargetInvokerCreationException {
            return null;
        }
    }

    private class TestStatelessAtomicComponent extends PojoAtomicComponent {

        public TestStatelessAtomicComponent(PojoConfiguration configuration) {
            super(configuration);
        }

        public Scope getScope() {
            return Scope.STATELESS;
        }

        protected ObjectFactory<?> createWireFactory(Class<?> interfaze, OutboundWire wire) {
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire)
            throws TargetInvokerCreationException {
            return null;
        }
    }

    private static class Foo {
        public Foo() {
        }
    }

}


