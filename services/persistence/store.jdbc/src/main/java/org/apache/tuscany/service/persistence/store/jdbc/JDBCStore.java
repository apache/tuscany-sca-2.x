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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Resource;

import org.apache.tuscany.spi.annotation.Autowire;

import org.apache.tuscany.api.annotation.Monitor;
import org.apache.tuscany.service.persistence.store.RecoveryListener;
import org.apache.tuscany.service.persistence.store.Store;
import org.apache.tuscany.service.persistence.store.StoreMonitor;
import org.apache.tuscany.service.persistence.store.StoreReadException;
import org.apache.tuscany.service.persistence.store.StoreWriteException;

/**
 * A store implementation that uses a relational database to persist records. Write-through and write-behind strategies
 * are both supported. The write-behind operation is performed when the default write size or value specified by
 * <code>getBatchSize</code> is reached.
 *
 * @version $Rev$ $Date$
 */
public class JDBCStore implements Store {

    private DataSource dataSource;
    private StoreMonitor monitor;
    private Converter converter;
    private boolean writeBehind;
    private int batchSize = 10;
    // TODO integrate with a core threading scheme
    private ScheduledExecutorService scheduler;
    private long reaperInterval = 300000;
    private final Map<UUID, Record> cache;

    public JDBCStore(@Resource(mappedName = "StoreDS") DataSource dataSource,
                     @Autowire Converter converter,
                     @Monitor StoreMonitor monitor) {
        this.dataSource = dataSource;
        this.converter = converter;
        this.monitor = monitor;
        cache = new LinkedHashMap<UUID, Record>();
    }

    /**
     * Returns true if write-through is enabled
     *
     * @return true if write-through is enabled
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

    /**
     * Sets the number of records to insert during a batch operation
     */
    @Property
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Returns the number of records to insert during a batch operation
     */
    public int getBatchSize() {
        return batchSize;
    }

    @Init(eager = true)
    public void init() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, TimeUnit.MILLISECONDS);
        monitor.start("JDBC store started");
    }

    @Destroy
    public void destroy() {
        monitor.stop("JDBC store stopped");

    }

    public void appendRecord(UUID id, Object object, long expiration) throws StoreWriteException {
        if (!(object instanceof Serializable)) {
            StoreWriteException e = new StoreWriteException("Type must implement serializable");
            e.setIdentifier(object.getClass().getName());
            throw e;
        }
        Serializable serializable = (Serializable) object;
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        long now = System.currentTimeMillis();
        try {
            Record[] records;
            synchronized (cache) {
                if (cache.size() < batchSize) {
                    // todo optimize multiple writes
                    if (now < expiration || expiration == NEVER) {
                        cache.put(id, new Record(id, serializable, expiration, Record.INSERT));
                    }
                    return;
                }
                // sort the records, inserts before deletes so they can be batched
                records = sort();
                cache.clear();
            }
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            int lastOperaton = -1;
            for (Record record : records) {
                if (now < record.getExpiration()) {
                    if (record.getOperation() == Record.INSERT) {
                        if (lastOperaton == -1) {
                            insertStmt = conn.prepareStatement(converter.getInsertSql());
                            lastOperaton = Record.INSERT;
                        }
                        addInsertBatch(insertStmt, record.getId(), record.getObject(), record.getExpiration());
                    } else if (record.getOperation() == Record.UPDATE) {
                        if (lastOperaton == -1 || lastOperaton == Record.INSERT) {
                            updateStmt = conn.prepareStatement(converter.getUpdateSql());
                            lastOperaton = Record.UPDATE;
                        }
                        addUpdateBatch(updateStmt, record.getId(), record.getObject());
                    }
                }
            }
            // add the last record
            if (now < expiration || expiration == NEVER) {
                if (insertStmt == null) {
                    insertStmt = conn.prepareStatement(converter.getInsertSql());
                }
                addInsertBatch(insertStmt, id, serializable, expiration);
            }
            try {
                if (insertStmt != null) {
                    insertStmt.execute();
                }
                if (updateStmt != null) {
                    updateStmt.execute();
                }
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    monitor.error(e2);
                }
                throw new StoreWriteException(e);
            }
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } finally {
            close(insertStmt);
            close(updateStmt);
            close(conn);
        }
    }

    public void forcedAppendRecord(UUID id, Object object, long expiration) throws StoreWriteException {
        long now = System.currentTimeMillis();
        if (now >= expiration && expiration != NEVER) {
            return;
        }
        if (!(object instanceof Serializable)) {
            StoreWriteException e = new StoreWriteException("Type must implement serializable");
            e.setIdentifier(object.getClass().getName());
            throw e;
        }
        Serializable serializable = (Serializable) object;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(converter.getInsertSql());
            addInsertBatch(stmt, id, serializable, expiration);
            try {
                stmt.execute();
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    monitor.error(e2);
                }
                throw new StoreWriteException(e);
            }
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } finally {
            close(stmt, conn);
        }
    }

    public void updateRecord(UUID id, Object object) throws StoreWriteException {
        if (!(object instanceof Serializable)) {
            StoreWriteException e = new StoreWriteException("Type must implement serializable");
            e.setIdentifier(object.getClass().getName());
            throw e;
        }
        Serializable serializable = (Serializable) object;
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        long now = System.currentTimeMillis();
        try {
            Record[] records;
            synchronized (cache) {
                if (cache.size() < batchSize) {
                    Record record = cache.get(id);
                    if (record == null) {
                        throw new RecordNotFoundException(id.toString());
                    }
                    // note that we do not change the operation to an update if it is an insert; both become optimized
                    record.setObject(serializable);
                    return;
                }
                // sort the records, inserts before deletes so they can be batched
                records = sort();
                cache.clear();
            }
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            int lastOperaton = -1;
            for (Record record : records) {
                if (now < record.getExpiration()) {
                    if (record.getOperation() == Record.INSERT) {
                        if (lastOperaton == -1) {
                            insertStmt = conn.prepareStatement(converter.getInsertSql());
                            lastOperaton = Record.INSERT;
                        }
                        addInsertBatch(insertStmt, record.getId(), record.getObject(), record.getExpiration());
                    } else if (record.getOperation() == Record.UPDATE) {
                        if (lastOperaton == -1 || lastOperaton == Record.INSERT) {
                            updateStmt = conn.prepareStatement(converter.getUpdateSql());
                            lastOperaton = Record.UPDATE;
                        }
                        addUpdateBatch(updateStmt, record.getId(), record.getObject());
                    }
                }
            }
            // update the last record
            if (updateStmt == null) {
                updateStmt = conn.prepareStatement(converter.getUpdateSql());
            }
            addUpdateBatch(updateStmt, id, serializable);

            try {
                if (insertStmt != null) {
                    insertStmt.execute();
                }
                if (updateStmt != null) {
                    updateStmt.execute();
                }
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    monitor.error(e2);
                }
                throw new StoreWriteException(e);
            }
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } finally {
            close(insertStmt);
            close(updateStmt);
            close(conn);
        }

    }

    public void forcedUpdateRecord(UUID id, Object object) throws StoreWriteException {
        throw new UnsupportedOperationException();
    }

    public Object readRecord(UUID id) throws StoreReadException {
        Record record;
        synchronized (cache) {
            record = cache.get(id);
        }
        if (record != null) {
            return record.getObject();
        }
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return converter.read(id, conn);
        } catch (SQLException e) {
            throw new StoreReadException(e);
        } finally {
            close(conn);
        }
    }

    public void removeRecords() throws StoreWriteException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(converter.getDeleteSql());
            stmt.execute();
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } finally {
            close(stmt, conn);
        }
    }

    public void recover(RecoveryListener listener) {
        throw new UnsupportedOperationException();
    }

    private void addInsertBatch(PreparedStatement stmt, UUID id, Serializable object, long expiration)
        throws StoreWriteException, SQLException {
        converter.insert(stmt, id, expiration, object);
        stmt.addBatch();
    }

    private void addUpdateBatch(PreparedStatement stmt, UUID id, Serializable object)
        throws StoreWriteException, SQLException {
        converter.update(stmt, id, object);
        stmt.addBatch();
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                monitor.error(e);
            }
        }
    }

    private void close(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                monitor.error(e);
            }
        }
    }

    private void close(PreparedStatement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                monitor.error(e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                monitor.error(e);
            }
        }
    }

    /**
     * Sorts pending operations, placing inserts before updates so writes may be batched
     */
    private Record[] sort() {
        synchronized (cache) {
            Record[] records = new Record[cache.size()];
            Arrays.sort(cache.values().toArray(records));
            return records;
        }
    }

    private class Reaper implements Runnable {
        public void run() {
            long now = System.currentTimeMillis();
            synchronized (cache) {
                for (Map.Entry<UUID, Record> entry : cache.entrySet()) {
                    final long expiration = entry.getValue().getExpiration();
                    if (expiration != NEVER && now >= expiration) {
                        cache.remove(entry.getKey());
                    }
                }
            }
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(converter.getDeleteExpiredSql());
                stmt.setLong(1, now);
                stmt.execute();
                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        // ignore
                    }
                }
                monitor.error(e);
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        // ingnore
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        // ignore
                    }
                }
            }
        }
    }


}
