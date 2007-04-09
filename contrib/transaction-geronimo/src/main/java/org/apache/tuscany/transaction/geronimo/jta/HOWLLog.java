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
package org.apache.tuscany.transaction.geronimo.jta;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.transaction.manager.LogException;
import org.apache.geronimo.transaction.manager.Recovery;
import org.apache.geronimo.transaction.manager.TransactionBranchInfo;
import org.apache.geronimo.transaction.manager.TransactionBranchInfoImpl;
import org.apache.geronimo.transaction.manager.TransactionLog;
import org.apache.geronimo.transaction.manager.XidFactory;
import org.objectweb.howl.log.Configuration;
import org.objectweb.howl.log.LogClosedException;
import org.objectweb.howl.log.LogFileOverflowException;
import org.objectweb.howl.log.LogRecord;
import org.objectweb.howl.log.LogRecordSizeException;
import org.objectweb.howl.log.LogRecordType;
import org.objectweb.howl.log.ReplayListener;
import org.objectweb.howl.log.xa.XACommittingTx;
import org.objectweb.howl.log.xa.XALogRecord;
import org.objectweb.howl.log.xa.XALogger;

/**
 * A copy of the Geronimo transaction log of the same class name. This was necessary to avoid a GBean dependency which
 * had the side-effect of requiring a large number of additional Geronimo modules (referenced through transitive
 * dependencies). At some point, this class should be eliminated if the Geronimo trasnaction manager can be made more
 * modular.
 *
 * @version $Rev$ $Date$
 */
public class HOWLLog implements TransactionLog {
    private static final Log LOG = LogFactory.getLog(HOWLLog.class);
    private static final byte COMMIT = 2;
    private static final byte ROLLBACK = 3;

    private final XidFactory xidFactory;
    private final XALogger logger;
    @SuppressWarnings({"FieldCanBeLocal"})
    private Map<Xid, Recovery.XidBranchesPair> recovered;

    public HOWLLog(Configuration configuration, XidFactory xidFactory) throws IOException {
        this.xidFactory = xidFactory;
        this.logger = new XALogger(configuration);
    }

    public void doStart() throws Exception {
        LOG.debug("Initiating transaction manager recovery");
        recovered = new HashMap<Xid, Recovery.XidBranchesPair>();

        logger.open(null);

        ReplayListener replayListener = new GeronimoReplayListener(xidFactory, recovered);
        logger.replayActiveTx(replayListener);

        LOG.debug("In doubt transactions recovered from log");
    }

    public void doStop() throws Exception {
        logger.close();
        recovered = null;
    }

    public void doFail() {
    }

    public void begin(Xid xid) throws LogException {
    }

    public Object prepare(Xid xid, List branches) throws LogException {
        int branchCount = branches.size();
        byte[][] data = new byte[3 + 2 * branchCount][];
        data[0] = intToBytes(xid.getFormatId());
        data[1] = xid.getGlobalTransactionId();
        data[2] = xid.getBranchQualifier();
        int i = 3;
        for (Object branche : branches) {
            TransactionBranchInfo transactionBranchInfo = (TransactionBranchInfo) branche;
            data[i++] = transactionBranchInfo.getBranchXid().getBranchQualifier();
            data[i++] = transactionBranchInfo.getResourceName().getBytes();
        }
        try {
            return logger.putCommit(data);
        } catch (LogClosedException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (LogRecordSizeException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (LogFileOverflowException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (InterruptedException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    public void commit(Xid xid, Object logMark) throws LogException {
        //the data is theoretically unnecessary but is included to help with debugging and because HOWL
        // currently requires it.
        byte[][] data = new byte[4][];
        data[0] = new byte[]{COMMIT};
        data[1] = intToBytes(xid.getFormatId());
        data[2] = xid.getGlobalTransactionId();
        data[3] = xid.getBranchQualifier();
        try {
            logger.putDone(data, (XACommittingTx) logMark);
        } catch (LogClosedException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (LogRecordSizeException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (LogFileOverflowException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (InterruptedException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    public void rollback(Xid xid, Object logMark) throws LogException {
        //the data is theoretically unnecessary but is included to help with debugging and because HOWL
        // currently requires it.
        byte[][] data = new byte[4][];
        data[0] = new byte[]{ROLLBACK};
        data[1] = intToBytes(xid.getFormatId());
        data[2] = xid.getGlobalTransactionId();
        data[3] = xid.getBranchQualifier();
        try {
            logger.putDone(data, (XACommittingTx) logMark);
        } catch (LogClosedException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (LogRecordSizeException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (LogFileOverflowException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (InterruptedException e) {
            throw (IllegalStateException) new IllegalStateException().initCause(e);
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    public Collection recover(XidFactory xidFactory) throws LogException {
        LOG.debug("Initiating transaction manager recovery");
        Map<Xid, Recovery.XidBranchesPair> recovered = new HashMap<Xid, Recovery.XidBranchesPair>();
        ReplayListener replayListener = new GeronimoReplayListener(xidFactory, recovered);
        logger.replayActiveTx(replayListener);
        LOG.debug("In doubt transactions recovered from log");
        return recovered.values();
    }

    public String getXMLStats() {
        return logger.getStats();
    }

    public int getAverageForceTime() {
        return 0;
    }

    public int getAverageBytesPerForce() {
        return 0;
    }

    private byte[] intToBytes(int formatId) {
        byte[] buffer = new byte[4];
        buffer[0] = (byte) (formatId >> 24);
        buffer[1] = (byte) (formatId >> 16);
        buffer[2] = (byte) (formatId >> 8);
        buffer[3] = (byte) (formatId >> 0);
        return buffer;
    }

    private int bytesToInt(byte[] buffer) {
        return ((int) buffer[0]) << 24 + ((int) buffer[1]) << 16 + ((int) buffer[2]) << 8 + ((int) buffer[3]) << 0;
    }

    private class GeronimoReplayListener implements ReplayListener {

        private final XidFactory xidFactory;
        private final Map<Xid, Recovery.XidBranchesPair> recoveredTx;

        public GeronimoReplayListener(XidFactory xidFactory, Map<Xid, Recovery.XidBranchesPair> recoveredTx) {
            this.xidFactory = xidFactory;
            this.recoveredTx = recoveredTx;
        }

        public void onRecord(LogRecord plainlr) {
            XALogRecord lr = (XALogRecord) plainlr;
            short recordType = lr.type;
            XACommittingTx tx = lr.getTx();
            if (recordType == LogRecordType.XACOMMIT) {

                byte[][] data = tx.getRecord();

                assert data[0].length == 4;
                int formatId = bytesToInt(data[1]);
                byte[] globalId = data[1];
                byte[] branchId = data[2];
                Xid masterXid = xidFactory.recover(formatId, globalId, branchId);

                Recovery.XidBranchesPair xidBranchesPair = new Recovery.XidBranchesPair(masterXid, tx);
                recoveredTx.put(masterXid, xidBranchesPair);
                LOG.debug("recovered prepare record for master xid: " + masterXid);
                for (int i = 3; i < data.length; i += 2) {
                    byte[] branchBranchId = data[i];
                    String name = new String(data[i + 1]);

                    Xid branchXid = xidFactory.recover(formatId, globalId, branchBranchId);
                    TransactionBranchInfoImpl branchInfo = new TransactionBranchInfoImpl(branchXid, name);
                    xidBranchesPair.addBranch(branchInfo);
                    LOG.debug("recovered branch for resource manager, branchId " + name + ", " + branchXid);
                }
            } else {
                if (recordType != LogRecordType.END_OF_LOG) { // This value crops up every time the server is started
                    LOG.warn("Received unexpected log record: " + lr + " (" + recordType + ")");
                }
            }
        }

        public void onError(org.objectweb.howl.log.LogException exception) {
            LOG.error("Error during recovery: ", exception);
        }

        public LogRecord getLogRecord() {
            //TODO justify this size estimate
            return new LogRecord(10 * 2 * Xid.MAXBQUALSIZE);
        }

    }


}
