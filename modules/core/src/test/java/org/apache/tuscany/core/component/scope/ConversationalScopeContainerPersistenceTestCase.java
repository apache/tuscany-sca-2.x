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

import junit.framework.TestCase;

import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public abstract class ConversationalScopeContainerPersistenceTestCase extends TestCase {
    private ConversationalScopeContainer container;
    private WorkContext context;

    public void testNotYetPersistedInMemory() throws Exception {
/*
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        EasyMock.replay(wrapper);

        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.expect(component.getMaxAge()).andReturn(600000L).atLeastOnce();
        EasyMock.replay(component);

        container.register(component, null);
        assertSame(wrapper, container.getWrapper(component));
        EasyMock.verify(component);
        EasyMock.verify(wrapper);
*/
    }

    public void testPersistNewInMemory() throws Exception {
/*
        String id = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.replay(component);
        container.register(component, null);
        InstanceWrapper fooWrapper = EasyMock.createMock(InstanceWrapper.class);
        InstanceWrapper fooWrapper2 = EasyMock.createMock(InstanceWrapper.class);
        container.persistNew(component, id, fooWrapper, System.currentTimeMillis() + 100000);
        assertEquals(fooWrapper, container.getWrapper(component));
        container.persistNew(component, id2, fooWrapper2, System.currentTimeMillis() + 100000);
        context.setIdentifier(Scope.CONVERSATION, id2);
        assertEquals(fooWrapper2, container.getWrapper(component));
        EasyMock.verify(component);
*/
    }

    public void testPersistInMemory() throws Exception {
/*
        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.replay(component);
        container.register(component, null);
        InstanceWrapper fooWrapper = EasyMock.createMock(InstanceWrapper.class);
        container.persistNew(component, id, fooWrapper, System.currentTimeMillis() + 100000);
        assertEquals(fooWrapper, container.getWrapper(component));
        container.persist(component, id, fooWrapper, System.currentTimeMillis() + 100000);
        assertEquals(fooWrapper, container.getWrapper(component));
        EasyMock.verify(component);
*/
    }

    public void testRemoveInMemory() throws Exception {
/*
        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.expect(component.getUri()).andReturn(URI.create("foo")).atLeastOnce();
        EasyMock.replay(component);
        container.register(component, null);
        InstanceWrapper fooWrapper = EasyMock.createMock(InstanceWrapper.class);
        container.persistNew(component, id, fooWrapper, System.currentTimeMillis() + 100000);
        assertEquals(fooWrapper, container.getWrapper(component));
        container.remove(component);
        try {
            container.getAssociatedWrapper(component);
            fail();
        } catch (TargetNotFoundException e) {
            //expected
        }
        EasyMock.verify(component);
*/
    }

    public void testRecreateAfterRemoveInMemory() throws Exception {
/*
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        EasyMock.replay(wrapper);

        String id = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.eq(container));
        EasyMock.expect(component.getMaxAge()).andReturn(600000L).atLeastOnce();
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component);

        container.register(component, null);
        InstanceWrapper fooWrapper = EasyMock.createMock(InstanceWrapper.class);
        container.persistNew(component, id, fooWrapper, System.currentTimeMillis() + 100000);
        assertEquals(fooWrapper, container.getWrapper(component));
        container.remove(component);
        assertSame(wrapper, container.getWrapper(component));
        EasyMock.verify(component);
*/
    }

    public void testGetPersistedInstance() throws Exception {
/*
        String id = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        context.setIdentifier(Scope.CONVERSATION, id);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("foo")).atLeastOnce();
        EasyMock.expect(component.getMaxIdleTime()).andReturn(-1L).atLeastOnce();
        component.addListener(EasyMock.eq(container));
        EasyMock.replay(component);
        container.register(component, null);

        InstanceWrapper fooWrapper = EasyMock.createMock(InstanceWrapper.class);
        container.persistNew(component, id, fooWrapper, System.currentTimeMillis() + 100000);
        assertEquals(fooWrapper, container.getAssociatedWrapper(component));
        assertEquals(fooWrapper, container.getAssociatedWrapper(component));
        context.setIdentifier(Scope.CONVERSATION, id2);
        try {
            container.getAssociatedWrapper(component);
            fail();
        } catch (TargetNotFoundException e) {
            //expected
        }
        EasyMock.verify(component);
*/
    }

    protected void setUp() throws Exception {
        super.setUp();
        context = new SimpleWorkContext();
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
}
