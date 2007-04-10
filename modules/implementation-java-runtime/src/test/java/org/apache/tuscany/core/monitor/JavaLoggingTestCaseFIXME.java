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
package org.apache.tuscany.core.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.apache.tuscany.api.annotation.LogLevel;
import org.apache.tuscany.host.MonitorFactory;

/**
 * Test case for the JavaLoggingMonitorFactory.
 *
 * @version $Rev$ $Date$
 */
public class JavaLoggingTestCaseFIXME extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(Monitor.class.getName());
    private static final MockHandler HANDLER = new MockHandler();

    private MonitorFactory factory;

    /**
     * Smoke test to ensure the LOGGER is working.
     */
    public void testLogger() {
        LOGGER.info("test");
        assertEquals(1, HANDLER.logs.size());
    }

    /**
     * Test that no record is logged.
     */
    public void testUnloggedEvent() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventNotToLog();
        assertEquals(0, HANDLER.logs.size());
    }

    /**
     * Test the correct record is written for an event with no arguments.
     */
    public void testEventWithNoArgs() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithNoArgs();
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals(LOGGER.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithNoArgs", record.getMessage());
    }

    /**
     * Test the correct record is written for an event defined by annotation.
     */
    public void testEventWithAnnotation() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithAnnotation();
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals(LOGGER.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithAnnotation", record.getMessage());
    }

    /**
     * Test the argument is logged.
     */
    public void testEventWithOneArg() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithOneArg("ARG");
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Monitor.class.getName() + "#eventWithOneArg", record.getMessage());
    }

    protected void setUp() throws Exception {
        super.setUp();
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(HANDLER);
        HANDLER.flush();

        String sourceClass = Monitor.class.getName();
        Properties levels = new Properties();
        levels.setProperty(sourceClass + "#eventWithNoArgs", "INFO");
        levels.setProperty(sourceClass + "#eventWithOneArg", "INFO");
        levels.setProperty(sourceClass + "#eventWithThrowable", "WARNING");
        factory = new JavaLoggingMonitorFactory(levels, Level.FINE, "TestMessages");
    }

    protected void tearDown() throws Exception {
        LOGGER.removeHandler(HANDLER);
        HANDLER.flush();
        super.tearDown();
    }

    /**
     * Mock log HANDLER to capture records.
     */
    public static class MockHandler extends Handler {
        List<LogRecord> logs = new ArrayList<LogRecord>();

        public void publish(LogRecord record) {
            logs.add(record);
        }

        public void flush() {
            logs.clear();
        }

        public void close() throws SecurityException {
        }
    }

    @SuppressWarnings({"JavaDoc"})
    public static interface Monitor {
        void eventNotToLog();

        @LogLevel("INFO")
        void eventWithNoArgs();

        @LogLevel("INFO")
        void eventWithOneArg(String msg);

        @LogLevel("WARNING")
        void eventWithThrowable(Exception e);

        @LogLevel("INFO")
        void eventWithAnnotation();
    }
}
