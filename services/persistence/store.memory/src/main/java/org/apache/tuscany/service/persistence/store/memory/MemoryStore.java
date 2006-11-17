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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.component.SCAObject;

import org.apache.tuscany.api.annotation.Monitor;
import org.apache.tuscany.service.persistence.store.RecoveryListener;
import org.apache.tuscany.service.persistence.store.Store;
import org.apache.tuscany.service.persistence.store.StoreMonitor;
import org.apache.tuscany.service.persistence.store.StoreWriteException;

/**
 * Implements a non-durable, non-transactional store using a simple in-memory map
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class MemoryStore implements Store {
    private Map<SCAObject, Map<UUID, Record>> store;
    // TODO integrate with a core threading scheme
    private ScheduledExecutorService scheduler;
    private long reaperInterval = 300000;
    private StoreMonitor monitor;

    public MemoryStore(@Monitor StoreMonitor monitor) {
        this.monitor = monitor;
        this.store = new ConcurrentHashMap<SCAObject, Map<UUID, Record>>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
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

    @Init(eager = true)
    public void init() {
        scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, TimeUnit.MILLISECONDS);
        monitor.start("In-memory store started");
    }

    @Destroy
    public void destroy() {
        scheduler.shutdown();
        monitor.stop("In-memory store stopped");
    }

    public void appendRecord(SCAObject owner, UUID id, Object object, long expiration) throws StoreWriteException {
        Map<UUID, Record> map = store.get(owner);
        if (map == null) {
            map = new ConcurrentHashMap<UUID, Record>();
            store.put(owner, map);
        }
        map.put(id, new Record(object, expiration));
    }

    public void updateRecord(SCAObject owner, UUID id, Object object) throws StoreWriteException {
        Map<UUID, Record> map = store.get(owner);
        if (map == null) {
            StoreWriteException e = new StoreWriteException("Record not found");
            e.setIdentifier(id.toString());
            throw e;
        }
        Record record = map.get(id);
        if (record == null) {
            StoreWriteException e = new StoreWriteException("Record not found");
            e.setIdentifier(id.toString());
            throw e;
        }
        record.data = object;
    }

    public Object readRecord(SCAObject owner, UUID id) {
        Map<UUID, Record> map = store.get(owner);
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

    public void recover(RecoveryListener listener) {
        throw new UnsupportedOperationException();
//        monitor.beginRecover();
//        listener.onBegin();
//        for (UUID id : store.keySet()) {
//            monitor.recover(id);
//            listener.onRecord(id);
//        }
//        listener.onEnd();
//        monitor.endRecover();
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
            for (Map<UUID, Record> map : store.values()) {
                for (Map.Entry<UUID, Record> entry : map.entrySet()) {
                    final long expiration = entry.getValue().expiration;
                    if (expiration != NEVER && now >= expiration) {
                        map.remove(entry.getKey());
                    }
                }
            }
        }
    }

}
