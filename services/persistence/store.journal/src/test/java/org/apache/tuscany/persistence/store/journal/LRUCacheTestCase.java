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
package org.apache.tuscany.persistence.store.journal;

import java.util.UUID;

import junit.framework.TestCase;
import static org.apache.tuscany.spi.services.store.Store.NEVER;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class LRUCacheTestCase extends TestCase {

    public void testEviction() {
        LRUCache cache = new LRUCache(2);
        RecordKey key1 = new RecordKey(UUID.randomUUID(), "foo");
        RecordKey key2 = new RecordKey(UUID.randomUUID(), "bar");
        RecordKey key3 = new RecordKey(UUID.randomUUID(), "baz");
        RecordEntry entry1 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry2 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry3 = new RecordEntry("", Header.INSERT, null, NEVER);
        cache.put(key1, entry1);
        cache.put(key2, entry2);
        cache.get(key1);
        cache.put(key3, entry3);
        assertNotNull(cache.get(key3));
        assertNotNull(cache.get(key1));
        assertNull(cache.get(key2));
    }

    public void testPut() {
        LRUCache cache = new LRUCache(3);
        RecordKey key1 = new RecordKey(UUID.randomUUID(), "foo");
        RecordKey key2 = new RecordKey(UUID.randomUUID(), "bar");
        RecordKey key3 = new RecordKey(UUID.randomUUID(), "baz");
        RecordEntry entry1 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry2 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry3 = new RecordEntry("", Header.INSERT, null, NEVER);
        cache.put(key1, entry1);
        cache.put(key2, entry2);
        cache.get(key1);
        cache.put(key3, entry3);
        assertNotNull(cache.get(key3));
        assertNotNull(cache.get(key1));
        assertNotNull(cache.get(key3));
    }

    public void testNotifyListenerOnEviction() {
        LRUCache cache = new LRUCache(2);
        RecordKey key1 = new RecordKey(UUID.randomUUID(), "foo");
        RecordKey key2 = new RecordKey(UUID.randomUUID(), "bar");
        RecordKey key3 = new RecordKey(UUID.randomUUID(), "baz");
        RecordEntry entry1 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry2 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry3 = new RecordEntry("", Header.INSERT, null, NEVER);
        CacheEventListener listener = EasyMock.createMock(CacheEventListener.class);
        listener.onEviction(EasyMock.eq(key2), EasyMock.eq(entry2));
        EasyMock.replay(listener);
        cache.addListener(listener);
        cache.put(key1, entry1);
        cache.put(key2, entry2);
        cache.get(key1);
        cache.put(key3, entry3);
        EasyMock.verify(listener);
    }

    public void testRemoveListener() {
        LRUCache cache = new LRUCache(2);
        RecordKey key1 = new RecordKey(UUID.randomUUID(), "foo");
        RecordKey key2 = new RecordKey(UUID.randomUUID(), "bar");
        RecordKey key3 = new RecordKey(UUID.randomUUID(), "baz");
        RecordEntry entry1 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry2 = new RecordEntry("", Header.INSERT, null, NEVER);
        RecordEntry entry3 = new RecordEntry("", Header.INSERT, null, NEVER);
        CacheEventListener listener = EasyMock.createMock(CacheEventListener.class);
        EasyMock.replay(listener);
        cache.addListener(listener);
        cache.put(key1, entry1);
        cache.put(key2, entry2);
        cache.get(key1);
        cache.removeListener(listener);
        cache.put(key3, entry3);
        EasyMock.verify(listener);
    }

}
