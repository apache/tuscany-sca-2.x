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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.event.AbstractEventPublisher;
import org.apache.tuscany.spi.services.store.RecoveryListener;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreExpirationEvent;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.spi.services.store.StoreReadException;
import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.apache.tuscany.api.annotation.Monitor;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.partition;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serialize;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serializeHeader;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serializeRecordId;
import org.objectweb.howl.log.Configuration;
import org.objectweb.howl.log.InvalidFileSetException;
import org.objectweb.howl.log.InvalidLogBufferException;
import org.objectweb.howl.log.InvalidLogKeyException;
import org.objectweb.howl.log.LogClosedException;
import org.objectweb.howl.log.LogConfigurationException;
import org.objectweb.howl.log.LogEventListener;
import org.objectweb.howl.log.LogException;
import org.objectweb.howl.log.LogFileOverflowException;
import org.objectweb.howl.log.LogRecord;
import org.objectweb.howl.log.LogRecordSizeException;

/**
 * A journal-based store service that uses HOWL for reliable persistence and recovery of object instances. Insert,
 * update, and delete operations, as well as object instances, are written to a binary log. Delete operations are
 * written as a single header block as defined by {@link SerializationHelper#serializeHeader(short,int,String,String,
 *long)}. Insert and update operations are written using multiple blocks consisting of at least one header and 1..n
 * additional blocks containing the object byte array. If the byte array size is greater than the log block size (the
 * HOWL default is 4K), it must be partitioned into smaller units using {@link SerializationHelper#partition(byte[],
 *int)} and written across multiple blocks. The header contains the number of ensuing blocks a record occupies. Since
 * block writes to the log may be interleaved, blocks for a given record may not be consecutive. In order to identify
 * the record a block belongs to, the first byte array of written data for the block contains the serialized owner id
 * and UUID associated with the record.
 * <p/>
 * A cache of all active persisted instances is maintained in memory and is used for read operations. When an instance
 * is persisted, a log record is written and an entry is added to the cache containing the instance. When an instance is
 * deleted, a delete record is written to the log and its corresponding cache entry is removed. During recovery, the
 * instance cache is reconstituted by reading the log and deserializing active records.
 * <p/>
 * The journal store is designed for reliable, performant persistence and therefore avoids the overhead associated with
 * using a relational database. This does, however, impose two configuration requirements. The first is that a log file
 * must be larger than the size of all <bold>active</bold> instances in the runtime. As a log file begins to fill, the
 * store will copy active records (expired records will be ignored) to a new file and the old log will be recycled. A
 * second requirement is that all active instances must fit within the memory limits of the runtime JVM since the store
 * does not perform passivation and a table of all active instances is maintained for querying.
 *
 * @version $Rev$ $Date$
 */
@Service(Store.class)
@EagerInit
public class JournalStore extends AbstractEventPublisher implements Store {
    private static final int UNITIALIZED = -99;

    // the cache of active records
    private ConcurrentHashMap<RecordKey, RecordEntry> cache;
    // private ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Object shutdownLock = new Object();
    // TODO integrate with a core threading scheme
    private ScheduledExecutorService scheduler;
    private StoreMonitor monitor;
    private Journal journal;
    private long defaultExpirationOffset = 600000; // 10 minutes
    private long reaperInterval = 1000;
    private int blockSize = 4096;  // 4k standard for HOWL

    private Configuration config;
    // HOWL properties  - cf. <code>org.objectweb.howl.log.Configuration</code>
    private boolean checksumEnabled;
    private int bufferSize = UNITIALIZED;
    private String bufferClassName;
    private int maxBuffers = UNITIALIZED;
    private int minBuffers = UNITIALIZED;
    private int flushSleepTime = UNITIALIZED;
    private boolean flushPartialBuffers;
    private int threadsWaitingForceThreshold = UNITIALIZED;
    private int maxBlocksPerFile = UNITIALIZED;
    private int maxLogFiles = UNITIALIZED;
    private String logFileDir = "../stores";
    private String logFileExt = "store";
    private String logFileName = "tuscany";
    private String logFileMode;
    // end HOWL properties

    /**
     * Creates a new store instance
     *
     * @param monitor the monitor for recording store events
     */
    @Constructor
    public JournalStore(@Monitor StoreMonitor monitor) {
        this.monitor = monitor;
        config = new Configuration();
    }

    /**
     * Protected constructor for unit testing
     *
     * @param monitor the monitor for recording store events
     * @param journal the journal the store should write to, typically a mock
     */
    protected JournalStore(StoreMonitor monitor, Journal journal) {
        this.monitor = monitor;
        this.journal = journal;
        config = new Configuration();
    }

    public int getBlockSize() {
        return blockSize;
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

    @Property
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public boolean isChecksumEnabled() {
        return checksumEnabled;
    }

    @Property
    public void setChecksumEnabled(boolean val) {
        config.setChecksumEnabled(val);
        this.checksumEnabled = val;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    @Property
    public void setBufferSize(int bufferSize) throws JournalStorePropertyException {
        try {
            config.setBufferSize(bufferSize);
            this.bufferSize = bufferSize;
        } catch (LogConfigurationException e) {
            throw new JournalStorePropertyException(e);
        }
    }

    public String getBufferClassName() {
        return bufferClassName;
    }

    @Property
    public void setBufferClassName(String bufferClassName) {
        config.setBufferClassName(bufferClassName);
        this.bufferClassName = bufferClassName;
    }

    public int getMaxBuffers() {
        return maxBuffers;
    }

    @Property
    public void setMaxBuffers(int maxBuffers) throws JournalStorePropertyException {
        try {
            config.setMaxBuffers(maxBuffers);
            this.maxBuffers = maxBuffers;
        } catch (LogConfigurationException e) {
            throw new JournalStorePropertyException(e);
        }
    }

    public int getMinBuffers() {
        return minBuffers;
    }

    @Property
    public void setMinBuffers(int minBuffers) throws JournalStorePropertyException {
        try {
            config.setMinBuffers(minBuffers);
            this.minBuffers = minBuffers;
        } catch (LogConfigurationException e) {
            throw new JournalStorePropertyException(e);
        }
    }

    public int getFlushSleepTime() {
        return flushSleepTime;
    }

    @Property
    public void setFlushSleepTime(int time) {
        config.setFlushSleepTime(time);
        this.flushSleepTime = time;
    }

    public boolean isFlushPartialBuffers() {
        return flushPartialBuffers;
    }

    @Property
    public void setFlushPartialBuffers(boolean val) {
        config.setFlushPartialBuffers(val);
        this.flushPartialBuffers = val;
    }

    public int getThreadsWaitingForceThreshold() {
        return threadsWaitingForceThreshold;
    }

    @Property
    public void setThreadsWaitingForceThreshold(int threshold) {
        config.setThreadsWaitingForceThreshold(threshold);
        this.threadsWaitingForceThreshold = threshold;
    }

    public int getMaxBlocksPerFile() {
        return maxBlocksPerFile;
    }

    @Property
    public void setMaxBlocksPerFile(int maxBlocksPerFile) {
        config.setMaxBlocksPerFile(maxBlocksPerFile);
        this.maxBlocksPerFile = maxBlocksPerFile;
    }

    public int getMaxLogFiles() {
        return maxLogFiles;
    }

    @Property
    public void setMaxLogFiles(int maxLogFiles) {
        config.setMaxLogFiles(maxLogFiles);
        this.maxLogFiles = maxLogFiles;
    }

    public String getLogFileDir() {
        return logFileDir;
    }

    @Property
    public void setLogFileDir(String dir) {
        config.setLogFileDir(dir);
        this.logFileDir = dir;
    }

    public String getLogFileExt() {
        return logFileExt;
    }

    @Property
    public void setLogFileExt(String logFileExt) {
        config.setLogFileExt(logFileExt);
        this.logFileExt = logFileExt;
    }

    public String getLogFileName() {
        return logFileName;
    }

    @Property
    public void setLogFileName(String logFileName) {
        config.setLogFileName(logFileName);
        this.logFileName = logFileName;
    }

    public String getLogFileMode() {
        return logFileMode;
    }

    public void setLogFileMode(String logFileMode) throws JournalStorePropertyException {
        try {
            config.setLogFileMode(logFileMode);
            this.logFileMode = logFileMode;
        } catch (LogConfigurationException e) {
            throw new JournalStorePropertyException(e);
        }
    }

    /**
     * Initializes the store by opening the journal and starting the checkpoint daemon
     *
     * @throws JournalIinitializationException
     *
     */
    @Init
    public void init() throws JournalIinitializationException {
        try {
            cache = new ConcurrentHashMap<RecordKey, RecordEntry>();
            if (journal == null) {
                config.setLogFileName(logFileName);
                config.setLogFileExt(logFileExt);
                config.setLogFileDir(logFileDir);
                journal = new Journal(config);
            }
            journal.setLogEventListener(new JournalLogEventListener());
            journal.open();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, MILLISECONDS);
            monitor.start("Started journal store");
        } catch (IOException e) {
            throw new JournalIinitializationException(e);
        } catch (LogConfigurationException e) {
            throw new JournalIinitializationException(e);
        } catch (InvalidLogBufferException e) {
            throw new JournalIinitializationException(e);
        } catch (InterruptedException e) {
            throw new JournalIinitializationException(e);
        } catch (InvalidFileSetException e) {
            throw new JournalIinitializationException(e);
        }
    }

    /**
     * Performs an orderly close of the journal and checkpoint daemon
     *
     * @throws JournalShutdownException
     */
    @Destroy
    public void destroy() throws JournalShutdownException {
        try {
            // avoid potential deadlock by acquiring write lock since the reaper thread can block when calling a
            // synchronized journal method (e.g. journal.put) since journal.close() below holds a synchronization lock.
            // This deadlock will prevent the scheduler.shutdown from completing, hanging this store shutdown
            synchronized (shutdownLock) {
                scheduler.shutdown();
                journal.close();
                monitor.stop("Stopped journal store");
            }
        } catch (IOException e) {
            throw new JournalShutdownException(e);
        } catch (InterruptedException e) {
            throw new JournalShutdownException(e);
        }
    }

    public void insertRecord(SCAObject owner, String id, Object object, long expiration) throws StoreWriteException {
        write(owner, id, object, expiration, Header.INSERT);
    }

    public void updateRecord(SCAObject owner, String id, Object object, long expiration) throws StoreWriteException {
        write(owner, id, object, expiration, Header.UPDATE);
    }

    public Object readRecord(SCAObject owner, String id) throws StoreReadException {
        RecordEntry record;
        RecordKey key = new RecordKey(id, owner);
        record = cache.get(key);
        if (record == null) {
            return null;
        }
        if (record.getExpiration() != Store.NEVER && record.getExpiration() < System.currentTimeMillis()) {
            cache.remove(key);
        }
        return record.getObject();
    }

    public void removeRecord(SCAObject owner, String id) throws StoreWriteException {
        try {
            journal.writeHeader(serializeHeader(Header.DELETE, 0, owner.getUri().toString(), id, NEVER), true);
            RecordKey key = new RecordKey(id, owner);
            // remove from the cache
            cache.remove(key);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        }

    }

    public void removeRecords() throws StoreWriteException {

    }

    public void recover(RecoveryListener listener) throws StoreReadException {
    }

    /**
     * Writes a record to the log. If a record needs to be written in multiple blocks, only the last block write is
     * forced.
     *
     * @param owner
     * @param id
     * @param object
     * @param expiration
     * @param operation
     * @throws StoreWriteException
     */
    private void write(SCAObject owner, String id, Object object, long expiration, short operation)
        throws StoreWriteException {
        if (!(object instanceof Serializable)) {
            String name = object.getClass().getName();
            throw new StoreWriteException("Type must implement serializable", name, id);
        }
        Serializable serializable = (Serializable) object;
        String name = owner.getUri().toString();
        List<byte[]> bytes;
        try {
            bytes = partition(serialize(serializable), blockSize);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        }
        try {
            byte[] header = serializeHeader(operation, bytes.size(), name, id, expiration);
            long headerKey = journal.writeHeader(header, false);
            // write chunked records in non-forced mode except last one
            byte[] recordId = serializeRecordId(name, id);
            long[] keys = new long[bytes.size() + 1];
            keys[0] = headerKey;
            for (int i = 0; i < bytes.size() - 1; i++) {
                byte[] chunk = bytes.get(i);
                keys[i + 1] = journal.writeBlock(chunk, recordId, false);
            }
            // add last record using a forced write
            journal.writeBlock(bytes.get(bytes.size() - 1), recordId, true);
            RecordKey key = new RecordKey(id, owner);
            // add to the entry in the cache
            cache.put(key, new RecordEntry(serializable, operation, keys, expiration));
        } catch (IOException e) {
            throw new StoreWriteException(e);
        }
    }

    /**
     * Handles log overflow events
     */
    private class JournalLogEventListener implements LogEventListener {

        public void logOverflowNotification(long overflowFence) {
            assert overflowFence != 0;
            long newMark = Long.MAX_VALUE;
            long now = System.currentTimeMillis();
            try {
                for (Map.Entry<RecordKey, RecordEntry> entry : cache.entrySet()) {
                    RecordEntry record = entry.getValue();
                    long[] journalKey = record.getJournalKeys();
                    if (journalKey[0] > overflowFence) {
                        // do not copy to new log but check if this is the new mark
                        if (journalKey[0] < newMark) {
                            newMark = journalKey[0];
                        }
                        continue;
                    }
                    if (entry.getValue().getExpiration() > now) {
                        // copy the blocks associayed with the record to the new journal only if it is not expired
                        for (long key : journalKey) {
                            LogRecord lrecord = journal.get(null, key);
                            journal.put(lrecord.getFields(), false);

                        }
                    }
                }
                if (newMark == Long.MAX_VALUE) {
                    newMark = overflowFence;
                }
                // force write
                journal.mark(newMark, true);
            } catch (LogRecordSizeException e) {
                monitor.error(e);
            } catch (LogFileOverflowException e) {
                monitor.error(e);
            } catch (LogConfigurationException e) {
                monitor.error(e);
            } catch (IOException e) {
                monitor.error(e);
            } catch (InvalidLogBufferException e) {
                monitor.error(e);
            } catch (LogClosedException e) {
                monitor.error(e);
            } catch (LogException e) {
                monitor.error(e);
            } catch (InterruptedException e) {
                monitor.error(e);
            }
        }

        public boolean isLoggable(int level) {
            return false;
        }

        public void log(int level, String message) {

        }

        public void log(int level, String message, Throwable thrown) {

        }
    }

    /**
     * Periodically scans the instance cache, clearing out expired entries
     */
    private class Reaper implements Runnable {

        public void run() {
            try {
                synchronized (shutdownLock) {
                    boolean force = false;
                    long now = System.currentTimeMillis();
                    long oldest = Long.MAX_VALUE;
                    for (Map.Entry<RecordKey, RecordEntry> entry : cache.entrySet()) {
                        RecordKey key = entry.getKey();
                        RecordEntry record = entry.getValue();
                        if (record.getExpiration() <= now) {
                            try {
                                String ownerName = key.getOwner().getUri().toString();
                                String id = key.getId();
                                byte[] header =
                                    SerializationHelper.serializeHeader(Header.DELETE, 0, ownerName, id, Store.NEVER);
                                journal.writeHeader(header, false);
                                // notify listeners
                                SCAObject owner = key.getOwner();
                                Object instance = record.getObject();
                                // notify listeners of the expiration 
                                StoreExpirationEvent event = new StoreExpirationEvent(this, owner, instance);
                                publish(event);
                            } catch (IOException e) {
                                monitor.error(e);
                            } catch (StoreWriteException e) {
                                monitor.error(e);
                            }
                            force = true;
                            cache.remove(entry.getKey());  // semantics of ConcurrentHashMap allow this
                        } else {
                            if (record.getJournalKeys()[0] < oldest) {
                                oldest = record.getJournalKeys()[0];
                            }
                        }
                    }
                    if (force) {
                        // perform a forced write and update the journal mark
                        journal.mark(oldest, true);
                    }
                }
            } catch (IOException e) {
                monitor.error(e);
            } catch (LogClosedException e) {
                monitor.error(e);
            } catch (InvalidLogKeyException e) {
                monitor.error(e);
            } catch (InterruptedException e) {
                monitor.error(e);
            }
        }
    }


}
