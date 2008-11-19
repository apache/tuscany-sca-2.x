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

package org.apache.tuscany.sca.host.corba.jee.testing;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import javax.naming.Context;

import org.apache.tuscany.sca.host.corba.CorbaHostException;
import org.apache.tuscany.sca.host.corba.jee.JEECorbaHost;
import org.apache.tuscany.sca.host.corba.jee.testing.general.TestInterface;
import org.apache.tuscany.sca.host.corba.jee.testing.general.TestInterfaceHelper;
import org.apache.tuscany.sca.host.corba.jee.testing.servants.TestInterfaceServant;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * General tests for JEECorbaHost. Uses host-corba-jdk as mock for JEE
 * environment ORB.
 */
public class JEECorbaHostTestCase {

    public static final String LOCALHOST = "localhost";
    public static final int DEFAULT_PORT = 11100;
    private static JEECorbaHost host;
    private static TransientNameServer server;
    private static String factoryClassName;

    private String createCorbanameURI(String name) {
        return "corbaname:#" + name;
    }

    @BeforeClass
    public static void start() {
        try {
            server = new TransientNameServer(LOCALHOST, DEFAULT_PORT, TransientNameService.DEFAULT_SERVICE_NAME);
            Thread t = server.start();
            if (t == null) {
                fail("The naming server cannot be started");
            }
            factoryClassName = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());
            host = new JEECorbaHost();
            host.start();
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void stop() {
        server.stop();
        if (factoryClassName != null) {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryClassName);
        } else {
            System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        }
    }

    /**
     * Tests registering and lookup CORBA services
     */
    @Test
    public void test_registerServant() {
        try {
            String uri = createCorbanameURI("Nested/Test");
            TestInterface servant = new TestInterfaceServant();
            host.registerServant(uri, servant);
            TestInterface ref = TestInterfaceHelper.narrow(host.lookup(uri));
            assertEquals(2, ref.getInt(2));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests unregistering servants
     */
    @Test
    public void test_unregisterServant() {
        try {
            String uri = createCorbanameURI("Unregistering/Test");
            TestInterface servant = new TestInterfaceServant();

            // creating and releasing using corbaname URI
            host.registerServant(uri, servant);
            host.unregisterServant(uri);
            host.registerServant(uri, servant);
            host.unregisterServant(uri);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests situation when name is already registered
     */
    @Test
    public void test_nameAlreadyRegistered() {
        // test using URI
        try {
            TestInterface servant = new TestInterfaceServant();
            String uri = createCorbanameURI("AlreadyRegisteredTest2");
            host.registerServant(uri, servant);
            host.registerServant(uri, servant);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.BINDING_IN_USE));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests getting non existing reference
     */
    @Test
    public void test_getNonExistingObject() {
        // try to fetch object with corbaname URI
        try {
            host.lookup(createCorbanameURI("NonExistingOne"));
            fail();
        } catch (CorbaHostException e) {
            // The message is JDK-specific
            // assertTrue(e.getMessage().equals(CorbaHostException.NO_SUCH_OBJECT));
        } catch (Exception e) {
            // e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests unregistering non existing reference
     */
    @Test
    public void test_unregisterNonExistentObject() {
        // test using URI
        try {
            String uri = createCorbanameURI("NonExistingReference1");
            host.unregisterServant(uri);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.NO_SUCH_OBJECT));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests unregistering servants on host stop
     */
    @Test
    public void test_unregisterOnStop() {
        try {
            String uri1 = createCorbanameURI("TempService1");
            String uri2 = createCorbanameURI("TempService2");
            JEECorbaHost innerHost = new JEECorbaHost();
            innerHost.start();
            TestInterfaceServant servant = new TestInterfaceServant();
            innerHost.registerServant(uri1, servant);
            innerHost.registerServant(uri2, servant);
            innerHost.stop();
            try {
                innerHost.lookup(uri1);
                fail();
            } catch (CorbaHostException e) {
                assertEquals(CorbaHostException.NO_SUCH_OBJECT, e.getMessage());
            }
            try {
                innerHost.lookup(uri2);
                fail();
            } catch (CorbaHostException e) {
                assertEquals(CorbaHostException.NO_SUCH_OBJECT, e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
