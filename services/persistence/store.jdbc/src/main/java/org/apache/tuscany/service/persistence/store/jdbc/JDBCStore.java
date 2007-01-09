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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Resource;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.event.AbstractEventPublisher;
import org.apache.tuscany.spi.services.store.RecoveryListener;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.spi.services.store.StoreReadException;
import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.apache.tuscany.api.annotation.Monitor;

/**
 * A store implementation that uses a relational database to persist records transactionally.
 * <p/>
 * Note this implementation does not yet support destruction callbacks for expired. In order to support this, expired
 * records must be rehydrated and deleted individually.
 *
 * @version $Rev$ $Date$
 */
@Service(Store.class)
@EagerInit
public class JDBCStore extends AbstractEventPublisher implements Store {
    private DataSource dataSource;
    private StoreMonitor monitor;
    private Converter converter;
    // TODO integrate with a core threading scheme
    @SuppressWarnings({"FieldCanBeLocal"})
    private ScheduledExecutorService scheduler;
    private long reaperInterval = 300000;
    private long defaultExpirationOffset = 600000; // 10 minutes

    //private
    public JDBCStore(@Resource(mappedName = "StoreDS")DataSource dataSource,
                     @Autowire Converter converter,
                     @Monitor StoreMonitor monitor) {
        this.dataSource = dataSource;
        this.converter = converter;
        this.monitor = monitor;
    }

    /**
     * Returns the maximum default expiration offset for records in the store
     *
     * @return the maximum default expiration offset for records in the store
     */
    @Property
    public long getExpirationOffset() {
        return defaultExpirationOffset;
    }

    /**
     * Sets the maximum default expiration offset for records in the store
     */
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

    /**
     * Returns the interval for expired entry scanning to be performed
     */
    public long getReaperInterval() {
        return reaperInterval;
    }

    @Init
    public void init() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, TimeUnit.MILLISECONDS);
        monitor.start("JDBC store started");
    }

    @Destroy
    public void destroy() {
        scheduler.shutdown();
        monitor.stop("JDBC store stopped");
    }

    public void insertRecord(SCAObject owner, String id, Object object, long expiration) throws StoreWriteException {
        if (!(object instanceof Serializable)) {
            throw new NonSerializableTypeException("Type must implement serializable", owner.getCanonicalName(), id);
        }
        Serializable serializable = (Serializable) object;
        String canonicalName = owner.getCanonicalName();
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        long now = System.currentTimeMillis();
        try {
            conn = dataSource.getConnection();
            if (now < expiration || expiration == NEVER) {
                PreparedStatement stmt = conn.prepareStatement(converter.getSelectUpdateSql());
                if (converter.findAndLock(stmt, canonicalName, id)) {
                    updateStmt = conn.prepareStatement(converter.getUpdateSql());
                    converter.update(updateStmt, canonicalName, id, serializable);
                } else {
                    insertStmt = conn.prepareStatement(converter.getInsertSql());
                    converter.insert(insertStmt, canonicalName, id, expiration, serializable);
                }
            }
            try {
                if (insertStmt != null) {
                    insertStmt.executeUpdate();
                }
                if (updateStmt != null) {
                    updateStmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    monitor.error(e2);
                }
                throw new StoreWriteException(owner.getCanonicalName(), id, e);
            }
        } catch (SQLException e) {
            throw new StoreWriteException(owner.getCanonicalName(), id, e);
        } finally {
            close(insertStmt);
            close(updateStmt);
            close(conn);
        }
    }

    public void updateRecord(SCAObject owner, String id, Object object, long expiration) throws StoreWriteException {
        if (!(object instanceof Serializable)) {
            throw new NonSerializableTypeException("Type must implement serializable", owner.getCanonicalName(), id);
        }
        Serializable serializable = (Serializable) object;
        String canonicalName = owner.getCanonicalName();
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        try {
            conn = dataSource.getConnection();
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            updateStmt = conn.prepareStatement(converter.getUpdateSql());
            converter.update(updateStmt, canonicalName, id, serializable);
            try {
                if (updateStmt != null) {
                    updateStmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    monitor.error(e2);
                }
                throw new StoreWriteException(owner.getCanonicalName(), id, e);
            }
        } catch (SQLException e) {
            throw new StoreWriteException(owner.getCanonicalName(), id, e);
        } finally {
            close(insertStmt);
            close(updateStmt);
            close(conn);
        }

    }

    public Object readRecord(SCAObject owner, String id) throws StoreReadException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Object object = converter.read(conn, owner.getCanonicalName(), id);
            conn.commit();
            return object;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                monitor.error(e2);
            }
            throw new StoreReadException(owner.getName(), id, e);
        } finally {
            close(conn);
        }
    }

    public void removeRecord(SCAObject owner, String id) throws StoreWriteException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(converter.getDeleteRecordSql());
            converter.delete(stmt, owner.getCanonicalName(), id);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                monitor.error(e2);
            }
            throw new StoreWriteException(owner.getCanonicalName(), id, e);
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
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } finally {
            close(stmt, conn);
        }
    }

    public void recover(RecoveryListener listener) {
        throw new UnsupportedOperationException();
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
     * Inner class responsible for clearing out expired entries
     */
    private class Reaper implements Runnable {
        public void run() {
            long now = System.currentTimeMillis();
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(converter.getDeleteExpiredSql());
                stmt.setLong(1, now);
                stmt.executeUpdate();
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
