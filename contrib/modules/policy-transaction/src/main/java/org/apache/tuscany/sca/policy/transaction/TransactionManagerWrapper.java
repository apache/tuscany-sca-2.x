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

package org.apache.tuscany.sca.policy.transaction;

import java.io.File;

import javax.transaction.TransactionManager;

import org.apache.geronimo.transaction.log.HOWLLog;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.geronimo.transaction.manager.XidFactory;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;

/**
 * @version $Rev$ $Date$
 */
public class TransactionManagerWrapper {
    private TransactionManager transactionManager;
    private HOWLLog howlLog;

    private String logFileDir = "target/logs";
    private String bufferClassName = "org.objectweb.howl.log.BlockLogBuffer";
    private int bufferSizeKBytes = 32;
    private boolean checksumEnabled = true;
    private boolean adler32Checksum = true;
    private int flushSleepTimeMilliseconds = 50;
    private String logFileExt = "log";
    private String logFileName = "transaction";
    private int maxBlocksPerFile = -1;
    private int maxLogFiles = 2;
    private int maxBuffers = 0;
    private int minBuffers = 4;
    private int threadsWaitingForceThreshold = -1;
    private File serverBaseDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));

    public TransactionManagerWrapper() {
        super();
    }

    public void start() {
        try {
            XidFactory xidFactory = new XidFactoryImpl();
            howlLog =
                new HOWLLog(bufferClassName, bufferSizeKBytes, checksumEnabled, adler32Checksum,
                            flushSleepTimeMilliseconds, logFileDir, logFileExt, logFileName, maxBlocksPerFile,
                            maxBuffers, maxLogFiles, minBuffers, threadsWaitingForceThreshold, xidFactory,
                            serverBaseDir);

            howlLog.doStart();
            transactionManager = new GeronimoTransactionManager(1200, xidFactory, howlLog);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.ModuleActivator#stop(org.apache.tuscany.sca.core.ExtensionPointRegistry)
     */
    public void stop() {
        try {
            if (howlLog != null) {
                howlLog.doStop();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

}
