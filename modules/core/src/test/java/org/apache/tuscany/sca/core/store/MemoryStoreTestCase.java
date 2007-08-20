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
package org.apache.tuscany.sca.core.store;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.tuscany.sca.event.RuntimeEventListener;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.store.DuplicateRecordException;
import org.apache.tuscany.sca.store.Store;
import org.apache.tuscany.sca.store.StoreExpirationEvent;
import org.apache.tuscany.sca.store.StoreMonitor;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class MemoryStoreTestCase extends TestCase {
    private StoreMonitor monitor;

    public void testEviction() throws Exception {
        MemoryStore store = new MemoryStore(monitor);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();
        store.insertRecord(component, id, value, 1);
        Thread.sleep(200);
        assertNull(store.readRecord(component, id));
        store.destroy();
    }

    public void testNotifyOnEviction() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(EasyMock.isA(StoreExpirationEvent.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch.countDown();
                return null;
            }
        });
        EasyMock.replay(listener);
        MemoryStore store = new MemoryStore(monitor);
        store.addListener(listener);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();
        store.insertRecord(component, id, value, 1);
        if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
            // failed to notify listener
            fail();
        }
        EasyMock.verify(listener);
    }

    public void testNoEviction() throws Exception {
        MemoryStore store = new MemoryStore(monitor);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();
        store.insertRecord(component, id, value, Store.NEVER);
        Thread.sleep(100);
        assertNotNull(store.readRecord(component, id));
        store.destroy();
    }

    public void testInsertRecord() throws Exception {
        MemoryStore store = new MemoryStore(monitor);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();
        store.insertRecord(component, id, value, Store.NEVER);
        store.destroy();
    }

    public void testInsertAlreadyExists() throws Exception {
        MemoryStore store = new MemoryStore(monitor);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createMock(RuntimeComponent.class);
        EasyMock.expect(component.getURI()).andReturn("component");
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();
        store.insertRecord(component, id, value, Store.NEVER);
        try {
            store.insertRecord(component, id, value, Store.NEVER);
            fail();
        } catch (DuplicateRecordException e) {
            //expected
        }
        store.destroy();
    }

    public void testUpdateRecord() throws Exception {
        MemoryStore store = new MemoryStore(monitor);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();
        Object newValue = new Object();

        store.insertRecord(component, id, value, Store.NEVER);
        store.updateRecord(component, id, newValue, 1L);
        assertEquals(newValue, store.readRecord(component, id));
        store.destroy();
    }

    public void testDeleteRecord() throws Exception {
        MemoryStore store = new MemoryStore(monitor);
        store.setReaperInterval(10);
        store.init();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.replay(component);
        String id = UUID.randomUUID().toString();
        Object value = new Object();

        store.insertRecord(component, id, value, Store.NEVER);
        store.removeRecord(component, id);
        assertNull(store.readRecord(component, id));
        store.destroy();
    }

    @Override
    protected void setUp() throws Exception {
        monitor = EasyMock.createNiceMock(StoreMonitor.class);
        EasyMock.replay(monitor);
    }

}
