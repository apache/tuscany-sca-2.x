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
package org.apache.tuscany.service.persistence.store.memory;

import java.util.UUID;

import org.apache.tuscany.service.persistence.store.StoreMonitor;

import junit.framework.TestCase;
import org.apache.tuscany.service.persistence.store.Store;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class MemoryStoreTestCase extends TestCase {

    public void testEviction() throws Exception {
        MemoryStore store = new MemoryStore(EasyMock.createNiceMock(StoreMonitor.class));
        store.setReaperInterval(10);
        store.init();
        UUID id = UUID.randomUUID();
        Object value = new Object();
        store.appendRecord(id, value, 1);
        Thread.sleep(100);
        assertNull(store.readRecord(id));
        store.destroy();
    }

    public void testNoEviction() throws Exception {
        MemoryStore store = new MemoryStore(EasyMock.createNiceMock(StoreMonitor.class));
        store.setReaperInterval(10);
        store.init();
        UUID id = UUID.randomUUID();
        Object value = new Object();
        store.appendRecord(id, value, Store.NEVER);
        Thread.sleep(100);
        assertNotNull(store.readRecord(id));
        store.destroy();
    }


}
