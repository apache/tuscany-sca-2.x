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
package org.apache.tuscany.core.services.store.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.event.AbstractEventPublisher;
import org.apache.tuscany.spi.services.store.DuplicateRecordException;
import org.apache.tuscany.spi.services.store.RecoveryListener;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreExpirationEvent;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.apache.tuscany.api.annotation.Monitor;

/**
 * Implements a non-durable, non-transactional store using a simple in-memory map
 *
 * @version $Rev$ $Date$
 */
@Service(Store.class)
@EagerInit
public class MemoryStore extends AbstractEventPublisher implements Store {
    private Map<SCAObject, Map<String, Record>> store;
    // TODO integrate with a core threading scheme
    private ScheduledExecutorService scheduler;
    private long reaperInterval = 300000;
    private StoreMonitor monitor;
    private long defaultExpirationOffset = 600000; // 10 minutes

    public MemoryStore(@Monitor StoreMonitor monitor) {
        this.monitor = monitor;
        this.store = new ConcurrentHashMap<SCAObject, Map<String, Record>>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Returns the maximum default expiration offset for records in the store
     *
     * @return the maximum default expiration offset for records in the store
     */
    public long getDefaultExpirationOffset() {
        return defaultExpirationOffset;
    }

    /**
     * Sets the maximum default expiration offset for records in the store
     */
    @Property
    public void setDefaultExpirationOffset(long defaultExpirationOffset) {
        this.defaultExpirationOffset = defaultExpirationOffset;
    }

    /**
     * Sets the interval for expired entry scanning to be performed
     */
    @Property
    public void setReaperInterval(long reaperInterval) {
        this.reaperInterval = reaperInterval;
    }

    public long getReaperInterval() {
        return reaperInterval;
    }

    @Init
    public void init() {
        scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, TimeUnit.MILLISECONDS);
        monitor.start("In-memory store started");
    }

    @Destroy
    public void destroy() {
        scheduler.shutdown();
        monitor.stop("In-memory store stopped");
    }

    public void insertRecord(SCAObject owner, String id, Object object, long expiration) throws StoreWriteException {
        Map<String, Record> map = store.get(owner);
        if (map == null) {
            map = new ConcurrentHashMap<String, Record>();
            store.put(owner, map);
        }
        if (map.containsKey(id)) {
            throw new DuplicateRecordException(owner.getUri().toString(), id);
        }
        map.put(id, new Record(object, expiration));
    }

    public void updateRecord(SCAObject owner, String id, Object object, long expiration) throws StoreWriteException {
        Map<String, Record> map = store.get(owner);
        if (map == null) {
            throw new StoreWriteException("Record not found", owner.getUri().toString(), id);
        }
        Record record = map.get(id);
        if (record == null) {
            throw new StoreWriteException("Record not found", owner.getUri().toString(), id);
        }
        record.data = object;
    }

    public Object readRecord(SCAObject owner, String id) {
        Map<String, Record> map = store.get(owner);
        if (map == null) {
            return null;
        }
        Record record = map.get(id);
        if (record != null) {
            return record.data;
        }
        return null;
    }

    public void removeRecords() {
        store.clear();
    }

    public void removeRecord(SCAObject owner, String id) throws StoreWriteException {
        Map<String, Record> map = store.get(owner);
        if (map == null) {
            throw new StoreWriteException("Owner not found", owner.getUri().toString(), id);
        }
        if (map.remove(id) == null) {
            throw new StoreWriteException("Owner not found", owner.getUri().toString(), id);
        }
    }

    public void recover(RecoveryListener listener) {
        throw new UnsupportedOperationException();
    }

    private class Record {
        private Object data;
        private long expiration = NEVER;

        public Record(Object data, long expiration) {
            this.data = data;
            this.expiration = expiration;
        }

        public Object getData() {
            return data;
        }

        public long getExpiration() {
            return expiration;
        }
    }

    private class Reaper implements Runnable {

        public void run() {
            long now = System.currentTimeMillis();
            for (Map.Entry<SCAObject, Map<String, Record>> entries : store.entrySet()) {
                for (Map.Entry<String, Record> entry : entries.getValue().entrySet()) {
                    final long expiration = entry.getValue().expiration;
                    if (expiration != NEVER && now >= expiration) {
                        SCAObject owner = entries.getKey();
                        Object instance = entry.getValue().getData();
                        // notify listeners of the expiration 
                        StoreExpirationEvent event = new StoreExpirationEvent(this, owner, instance);
                        publish(event);
                        entries.getValue().remove(entry.getKey());
                    }
                }
            }
        }
    }

}
