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

import junit.framework.TestCase;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import org.easymock.EasyMock;

import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.transaction.geronimo.TestUtils;

/**
 * @version $Rev$ $Date$
 */
public class GeronimoTransactionLogServiceTestCase extends TestCase {
    private GeronimoTransactionLogService service;

    public void testInitialization() throws Exception {
        service.setBufferSizeKBytes(32);
        service.setChecksumEnabled(true);
        service.setFlushSleepTimeMilliseconds(3000);
        service.setLogFileDir(".");
        service.setLogFileExt("log");
        service.setLogFileName("transaction");
        service.setMaxBlocksPerFile(10000);
        service.setMaxBuffers(1000);
        service.setMaxLogFiles(2);
        service.setMinBuffers(20);
        service.setThreadsWaitingForceThreshold(1000);
        service.init();
        service.destroy();
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        StandaloneRuntimeInfo info = EasyMock.createMock(StandaloneRuntimeInfo.class);
        EasyMock.expect(info.getInstallDirectory()).andReturn(new File("."));
        EasyMock.replay(info);
        service = new GeronimoTransactionLogService(info, new XidFactoryImpl());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanupLog();
    }

}
