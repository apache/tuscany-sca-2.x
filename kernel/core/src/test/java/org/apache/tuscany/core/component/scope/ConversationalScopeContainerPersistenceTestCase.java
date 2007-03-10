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
package org.apache.tuscany.core.component.scope;

import java.util.UUID;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreMonitor;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalScopeContainerPersistenceTestCase extends TestCase {
    private ScopeContainer container;
    private WorkContext context;

    /**
     * Verifies the scope container properly creates an instance
     */
    public void testNotYetPersistedInMemory() throws Exception {
        Foo comp = new Foo();
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        EasyMock.expect(wrapper.getInstance()).andReturn(comp);
        EasyMock.replay(wrapper);

        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.expect(component.getMaxAge()).andReturn(600000L).atLeastOnce();
        EasyMock.replay(component);

        container.register(component);
        assertSame(comp, container.getInstance(component));
        EasyMock.verify(component);
        EasyMock.verify(wrapper);
    }

    public void testPersistNewInMemory() throws Exception {
        String id = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.replay(component);
        container.register(component);
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        container.persistNew(component, id, foo, System.currentTimeMillis() + 100000);
        assertEquals(foo, container.getInstance(component));
        container.persistNew(component, id2, foo2, System.currentTimeMillis() + 100000);
        context.setIdentifier(Scope.CONVERSATION, id2);
        assertEquals(foo2, container.getInstance(component));
        EasyMock.verify(component);
    }

    public void testPersistInMemory() throws Exception {
        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.replay(component);
        container.register(component);
        Foo foo = new Foo();
        container.persistNew(component, id, foo, System.currentTimeMillis() + 100000);
        assertEquals(foo, container.getInstance(component));
        container.persist(component, id, foo, System.currentTimeMillis() + 100000);
        assertEquals(foo, container.getInstance(component));
        EasyMock.verify(component);
    }

    public void testRemoveInMemory() throws Exception {
        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.expect(component.getUri()).andReturn(URI.create("foo")).atLeastOnce();
        EasyMock.replay(component);
        container.register(component);
        Foo foo = new Foo();
        container.persistNew(component, id, foo, System.currentTimeMillis() + 100000);
        assertEquals(foo, container.getInstance(component));
        container.remove(component);
        try {
            container.getAssociatedInstance(component);
            fail();
        } catch (TargetNotFoundException e) {
            //expected
        }
        EasyMock.verify(component);
    }

    public void testRecreateAfterRemoveInMemory() throws Exception {
        Foo comp = new Foo();
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        EasyMock.expect(wrapper.getInstance()).andReturn(comp);
        EasyMock.replay(wrapper);

        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxAge()).andReturn(600000L).atLeastOnce();
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component);

        container.register(component);
        Foo foo = new Foo();
        container.persistNew(component, id, foo, System.currentTimeMillis() + 100000);
        assertEquals(foo, container.getInstance(component));
        container.remove(component);
        Foo foo2 = (Foo) container.getInstance(component);
        assertNotNull(foo2);
        assertNotSame(foo, foo2);
        EasyMock.verify(component);
    }

    public void testGetPersistedInstance() throws Exception {
        String id = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("foo")).atLeastOnce();
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        component.addListener(EasyMock.eq(container));
        EasyMock.replay(component);
        container.register(component);
        Foo foo = new Foo();
        container.persistNew(component, id, foo, System.currentTimeMillis() + 100000);
        assertEquals(foo, container.getAssociatedInstance(component));
        assertEquals(foo, container.getAssociatedInstance(component));
        context.setIdentifier(Scope.CONVERSATION, id2);
        try {
            container.getAssociatedInstance(component);
            fail();
        } catch (TargetNotFoundException e) {
            //expected
        }
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        context = new WorkContextImpl();
        StoreMonitor mock = EasyMock.createNiceMock(StoreMonitor.class);
        EasyMock.replay(mock);
        Store store = new MemoryStore(mock);
        container = new ConversationalScopeContainer(store, context, null);
        container.start();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        context.clearIdentifier(Scope.CONVERSATION);
        container.stop();
    }

    private class Foo {

    }
}
