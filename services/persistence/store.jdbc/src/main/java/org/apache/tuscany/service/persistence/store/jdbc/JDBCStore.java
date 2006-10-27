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
package org.apache.tuscany.service.persistence.store.jdbc;

import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.api.annotation.Monitor;
import org.apache.tuscany.service.persistence.store.RecoveryListener;
import org.apache.tuscany.service.persistence.store.Store;
import org.apache.tuscany.service.persistence.store.StoreMonitor;
import org.apache.tuscany.service.persistence.store.StoreWriteException;

/**
 * A store implementation that uses a relational database to persist records. Write-through and write-behind strategies
 * are both supported.
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JDBCStore implements Store {

    private StoreMonitor monitor;
    private boolean writeBehind;
    private long writeInterval;
    // TODO integrate with a core threading scheme
    private ScheduledExecutorService scheduler;
    private long reaperInterval = 300000;
    private LinkedHashMap<Object, Object> cache;

    public JDBCStore(@Monitor StoreMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Returns true of write-through is enabled
     *
     * @return true of write-through is enabled
     */
    public boolean isWriteBehind() {
        return writeBehind;
    }

    /**
     * Sets whether write-behind is enabled. The default is write-through.
     */
    @Property
    public void setWriteBehind(boolean writeBehind) {
        this.writeBehind = writeBehind;
    }

    /**
     * Returns the interval in milliseconds to perform write-behind operations if enabled
     */
    public long getWriteInterval() {
        return writeInterval;
    }

    /**
     * Sets the interval in milliseconds to perform write-behind operations if enabled
     */

    @Property
    public void setWriteInterval(long writeInterval) {
        this.writeInterval = writeInterval;
    }

    /**
     * Sets the interval for expired entry scanning to be performed
     */
    @Property
    public void setReaperInterval(long reaperInterval) {
        this.reaperInterval = reaperInterval;
    }

    /**
     * Returns the interval for expired entry scanning to be performed
     */
    public long getReaperInterval() {
        return reaperInterval;
    }

    @Init(eager = true)
    public void init() {
        if (writeBehind) {
            cache = new LinkedHashMap<Object, Object>();
        }
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, TimeUnit.MILLISECONDS);
        monitor.start("JDBC store started");
    }

    @Destroy
    public void destroy() {
        monitor.stop("JDBC store stopped");

    }

    public void writeRecord(Object id, Object record) throws StoreWriteException {

    }

    public void writeRecord(Object id, Object record, long expiration) throws StoreWriteException {

    }

    public void writeRecord(Object id, Object record, boolean force) throws StoreWriteException {

    }

    public void writeRecord(Object id, Object record, long expiration, boolean force) throws StoreWriteException {

    }

    public Object readRecord(Object id) {
        return null;
    }

    public void removeRecords() {

    }

    public void recover(RecoveryListener listener) {

    }

    private class Record {
        private Object data;
        private long expiration = NEVER;

        public Record(Object data, long expiration) {
            this.data = data;
            this.expiration = expiration;
        }
    }

    private class Reaper implements Runnable {

        public void run() {
            //long now = System.currentTimeMillis();
        }
    }

}
