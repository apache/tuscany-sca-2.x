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

import java.io.File;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.apache.geronimo.transaction.manager.XidFactory;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.objectweb.howl.log.Configuration;

/**
 * A system service that wraps the Geronimo transaction log
 *
 * @version $Rev$ $Date$
 */
public class GeronimoTransactionLogService {
    private XidFactory xidFactory;
    private StandaloneRuntimeInfo info;
    private HOWLLog log;
    private String logFileDir = ".";
    private int maxBlocksPerFile = -1;
    private int maxLogFiles = 2;
    private int maxBuffers;
    private int minBuffers = 4;
    private int bufferSizeKBytes = 32;
    private int flushSleepTimeMilliseconds = 50;
    private String logFileExt = "log";
    private String logFileName = "transaction";
    private int threadsWaitingForceThreshold = -1;
    private boolean checksumEnabled = true;
    private String bufferClassName = "org.objectweb.howl.log.BlockLogBuffer";

    public GeronimoTransactionLogService(@Reference StandaloneRuntimeInfo info, @Reference XidFactory xidFactory) {
        this.info = info;
        this.xidFactory = xidFactory;
    }

    public String getLogFileDir() {
        return logFileDir;
    }

    @Property
    public void setLogFileDir(String logFileDir) {
        this.logFileDir = logFileDir;
    }

    public int getMaxBlocksPerFile() {
        return maxBlocksPerFile;
    }

    @Property
    public void setMaxBlocksPerFile(int maxBlocksPerFile) {
        this.maxBlocksPerFile = maxBlocksPerFile;
    }

    public int getMaxLogFiles() {
        return maxLogFiles;
    }

    @Property
    public void setMaxLogFiles(int maxLogFiles) {
        this.maxLogFiles = maxLogFiles;
    }

    public int getMaxBuffers() {
        return maxBuffers;
    }

    @Property
    public void setMaxBuffers(int maxBuffers) {
        this.maxBuffers = maxBuffers;
    }

    public int getMinBuffers() {
        return minBuffers;
    }

    @Property
    public void setMinBuffers(int minBuffers) {
        this.minBuffers = minBuffers;
    }

    public int getBufferSizeKBytes() {
        return bufferSizeKBytes;
    }

    @Property
    public void setBufferSizeKBytes(int bufferSizeKBytes) {
        this.bufferSizeKBytes = bufferSizeKBytes;
    }

    public int getFlushSleepTimeMilliseconds() {
        return flushSleepTimeMilliseconds;
    }

    @Property
    public void setFlushSleepTimeMilliseconds(int flushSleepTimeMilliseconds) {
        this.flushSleepTimeMilliseconds = flushSleepTimeMilliseconds;
    }

    public String getLogFileExt() {
        return logFileExt;
    }

    @Property
    public void setLogFileExt(String logFileExt) {
        this.logFileExt = logFileExt;
    }

    public String getLogFileName() {
        return logFileName;
    }

    @Property
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public int getThreadsWaitingForceThreshold() {
        return threadsWaitingForceThreshold;
    }

    @Property
    public void setThreadsWaitingForceThreshold(int threadsWaitingForceThreshold) {
        this.threadsWaitingForceThreshold = threadsWaitingForceThreshold;
    }

    public boolean isChecksumEnabled() {
        return checksumEnabled;
    }

    @Property
    public void setChecksumEnabled(boolean checksumEnabled) {
        this.checksumEnabled = checksumEnabled;
    }

    public String getBufferClassName() {
        return bufferClassName;
    }

    @Property
    public void setBufferClassName(String bufferClassName) {
        this.bufferClassName = bufferClassName;
    }

    @Init
    public void init() throws Exception {
        Configuration config = new Configuration();
        config.setBufferClassName(bufferClassName);
        config.setBufferSize(bufferSizeKBytes);
        config.setChecksumEnabled(checksumEnabled);
        config.setFlushSleepTime(flushSleepTimeMilliseconds);
        File logDir = new File(logFileDir);
        if (!logDir.isAbsolute()) {
            logDir = new File(info.getInstallDirectory(), logFileDir);
        }
        config.setLogFileDir(logDir.getAbsolutePath());
        config.setLogFileExt(logFileExt);
        config.setLogFileName(logFileName);
        config.setMaxBlocksPerFile(maxBlocksPerFile == -1 ? Integer.MAX_VALUE : maxBlocksPerFile);
        config.setMaxBuffers(maxBuffers);
        config.setMaxLogFiles(maxLogFiles);
        config.setMinBuffers(minBuffers);
        config.setThreadsWaitingForceThreshold(
            threadsWaitingForceThreshold == -1 ? Integer.MAX_VALUE : threadsWaitingForceThreshold);
        log = new HOWLLog(config, xidFactory);
        log.doStart();
    }

    @Destroy
    public void destroy() throws Exception {
        log.doStop();
    }

    public HOWLLog getLog() {
        return log;
    }

}
