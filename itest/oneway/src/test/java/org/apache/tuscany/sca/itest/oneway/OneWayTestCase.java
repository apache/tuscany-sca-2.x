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

package org.apache.tuscany.sca.itest.oneway;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.itest.oneway.impl.OneWayClientImpl;
import org.apache.tuscany.sca.itest.oneway.impl.OneWayServiceImpl;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case will test invoking @OneWay operations.
 *
 * @version $Date$ $Revision$
 */
public class OneWayTestCase {
    /**
     * Maximum period of time that we are prepared to wait for all the @OneWay
     * method calls to complete in milliseconds.
     */
    private static final int MAX_SLEEP_TIME = 10000;

    private SCANode node;
    
    /**
     * Initialise the SCADomain.
     *
     * @throws Exception Failed to initialise the SCADomain
     */
    @Before
    public void setUp() throws Exception {
        
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = nodeFactory.createSCANodeFromClassLoader("OneWayContribution/META-INF/sca-deployables/oneWay.composite", null);
        node.start();
        
    }

    /**
     * This method will ensure that the SCADomain is shutdown.
     *
     * @throws Exception Failed to shutdown the SCADomain
     */
    @After
    public void tearDown() throws Exception {
        node.stop();
    }

    /**
     * This test will test repeatedly calling a @OneWay operation and ensure that the correct
     * number of @OneWay operations  are run.
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testOneWay() throws Exception {
        OneWayClient client =
            ((SCAClient)node).getService(OneWayClient.class, "OneWayClientComponent");

        int count = 100;

        for (int i = 0; i < 10; i++) {
           // System.out.println("Test: doSomething " + count);
           // System.out.flush();
            client.doSomething(count);

            // TUSCANY-2192 - We need to sleep to allow the @OneWay method calls to complete.
            // Note: This can take different periods depending on the speed and load
            // on the computer where the test is being run.
            // This loop will wait for the required number of @OneWay method calls to
            // have taken place or MAX_SLEEP_TIME to have passed.
            long startSleep = System.currentTimeMillis();
            while (OneWayClientImpl.callCount != OneWayServiceImpl.CALL_COUNT.get() 
                    && System.currentTimeMillis() - startSleep < MAX_SLEEP_TIME) {
                Thread.sleep(100);
                // System.out.println("" + OneWayClientImpl.callCount + "," + OneWayServiceImpl.callCount);
            }

            System.out.println("Finished callCount = " + OneWayServiceImpl.CALL_COUNT);

            Assert.assertEquals(OneWayClientImpl.callCount, OneWayServiceImpl.CALL_COUNT.get());
        }
    }

    /**
     * This method will invoke a @OneWay method that throws an exception
     * when invoked over a SCA Binding which uses the NonBlockingInterceptor and
     * ensure that the Exception is logged.
     * See TUSCANY-2225
     */
    @Test
    public void testOneWayUsingNonBlockingInterceptorThrowsAnException() {
        OneWayClient client =
            ((SCAClient)node).getService(OneWayClient.class, "OneWayClientComponentSCABinding");
            
        // We need to modify the JDK Logger for the NonBlockingInterceptor so we
        // can check that it logs a message for the @OneWay invocation that throws
        // an Exception
        Logger nbiLogger = Logger.getLogger(NonBlockingInterceptor.class.getName());
        DummyJDKHandler handler = new DummyJDKHandler();
        nbiLogger.addHandler(handler);

        // Add a message on the console to explain the stack dump that is going to be
        // displayed and state that this is not a problem but expected behaviour
        System.out.println();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("IMPORTANT: The error message that appears on the console");
        System.out.println("below is an expected error if it is a NullPointerException");
        System.out.println(" with the message of:");
        System.out.println("  \"" + OneWayServiceImpl.EXCEPTION_MESSAGE + "\"");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println();
        System.out.flush();

        // Invoke the @OneWay method that throws an exception
        client.doSomethingWhichThrowsException();

        // The logging is done asynchronously so we will need to wait a bit before
        // the log message appears.
        long start = System.currentTimeMillis();
        boolean logged = false;
        while (System.currentTimeMillis() - start < MAX_SLEEP_TIME && !logged) {
            // Has the log message been logged?
            if (handler.exceptionLogged.get()) {
                logged = true;
            } else {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ex) {
                    Assert.fail("Unexpected exception " + ex);
                }
            }
        }

        // Add a message explaining that errors beyond this point should be reported 
        System.out.println();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("End of expected exceptions. Any errors beyond this point are errors!");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println();
        System.out.flush();

        // Remove the handler
        nbiLogger.removeHandler(handler);

        // Make sure that the exception was logged
        Assert.assertTrue(handler.exceptionLogged.get());
    }

    /**
     * A handler that is added to the JDK Logging system to examine the log messages
     * generated to ensure that a @OneWay method that throws an Exception will 
     * generate a log message.
     */
    private class DummyJDKHandler extends Handler {

        /**
         * Used to track whether the exception has been logged.
         */
        private AtomicBoolean exceptionLogged = new AtomicBoolean(false);

        /**
         * Constructor.
         */
        private DummyJDKHandler() {
            super.setLevel(Level.ALL);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws SecurityException {
            // Nothing to do
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void flush() {
            // Nothing to do
        }

        /**
         * Examines the LogRecord and checks whether it matches the one we are looking for.
         * If it matches, it will set exceptionLogged to True.
         * 
         * @param record The Log Record that is being published
         */
        @Override
        public void publish(LogRecord record) {
            // The log message we are looking for is Severe
            if (record.getLevel() == Level.SEVERE) {
                if (record.getThrown() != null
                        && record.getThrown().toString().indexOf(
                                OneWayServiceImpl.EXCEPTION_MESSAGE) != -1) {
                    // We have found our Exception.
                    exceptionLogged.set(true);
                }
            }
        }
    }
}
