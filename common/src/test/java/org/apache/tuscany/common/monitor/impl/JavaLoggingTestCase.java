/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common.monitor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.LogLevel;

/**
 * Test case for the JavaLoggingMonitorFactory.
 * 
 * @version $Rev$ $Date$
 */
public class JavaLoggingTestCase extends TestCase {
    private static final Logger logger = Logger.getLogger(Monitor.class.getName());
    private static final MockHandler handler = new MockHandler();

    private MonitorFactory factory;

    /**
     * Smoke test to ensure the logger is working.
     */
    public void testLogger() {
        logger.info("test");
        assertEquals(1, handler.logs.size());
    }

    /**
     * Test that no record is logged.
     */
    public void testUnloggedEvent() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventNotToLog();
        assertEquals(0, handler.logs.size());
    }

    /**
     * Test the correct record is written for an event with no arguments.
     */
    public void testEventWithNoArgs() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithNoArgs();
        assertEquals(1, handler.logs.size());
        LogRecord record = handler.logs.get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals(logger.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithNoArgs", record.getMessage());
    }

    /**
     * Test the correct record is written for an event defined by annotation.
     */
    public void testEventWithAnnotation() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithAnnotation();
        assertEquals(1, handler.logs.size());
        LogRecord record = handler.logs.get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals(logger.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithAnnotation", record.getMessage());
    }

    /**
     * Test a Throwable is logged when passed to an event.
     */
    public void testEventWithThrowable() {
        Exception e = new Exception();
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithThrowable(e);
        assertEquals(1, handler.logs.size());
        LogRecord record = handler.logs.get(0);
        assertEquals(Level.WARNING, record.getLevel());
        assertEquals(logger.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithThrowable", record.getMessage());
        assertSame(e, record.getThrown());
    }

    /**
     * Test the argument is logged.
     */
    public void testEventWithOneArg() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithOneArg("ARG");
        assertEquals(1, handler.logs.size());
        LogRecord record = handler.logs.get(0);
        assertEquals(Monitor.class.getName() + "#eventWithOneArg", record.getMessage());
        assertEquals(Monitor.class.getName(), record.getResourceBundleName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        handler.flush();

        String sourceClass = Monitor.class.getName();
        Properties levels = new Properties();
        levels.setProperty(sourceClass + "#eventWithNoArgs", "INFO");
        levels.setProperty(sourceClass + "#eventWithOneArg", "INFO");
        levels.setProperty(sourceClass + "#eventWithThrowable", "WARNING");
        factory = new JavaLoggingMonitorFactory(levels, Level.FINE, sourceClass);
    }

    protected void tearDown() throws Exception {
        logger.removeHandler(handler);
        handler.flush();
        super.tearDown();
    }

    /**
     * Mock log handler to capture records.
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

        void eventWithNoArgs();

        void eventWithOneArg(String msg);

        void eventWithThrowable(Exception e);

        @LogLevel("INFO")
        void eventWithAnnotation();
    }
}
