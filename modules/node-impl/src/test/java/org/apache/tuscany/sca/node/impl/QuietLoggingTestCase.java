/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * \"License\"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.node.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for NodeImpl
 */
public class QuietLoggingTestCase {

    @Test
    public void testQuietLogging() throws Exception {

        final List<LogRecord> logRecords = new ArrayList<LogRecord>();
        LogManager logManager = LogManager.getLogManager();
        logManager.reset();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRecords.add(record);
            }
            @Override
            public void flush() {
            }
            @Override
            public void close() throws SecurityException {
            }
        };
        Logger.getLogger("").addHandler(handler);

        Properties props = new Properties();
        props.setProperty(RuntimeProperties.QUIET_LOGGING, "true");
        NodeFactory nf = NodeFactory.newInstance(props);
        Node node = nf.createNode();
        node.start();
        node.stop();

        Assert.assertEquals(0 , logRecords.size());
    }

}
