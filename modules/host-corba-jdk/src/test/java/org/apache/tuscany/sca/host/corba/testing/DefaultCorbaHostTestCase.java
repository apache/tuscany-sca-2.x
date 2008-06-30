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

package org.apache.tuscany.sca.host.corba.testing;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.host.corba.CorbaHostException;
import org.apache.tuscany.sca.host.corba.jdk.DefaultCorbaHost;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.apache.tuscany.sca.host.corba.testing.general.TestInterface;
import org.apache.tuscany.sca.host.corba.testing.general.TestInterfaceHelper;
import org.apache.tuscany.sca.host.corba.testing.servants.TestInterfaceServant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.ORB;

/**
 * General tests
 */
public class DefaultCorbaHostTestCase {

    private static final String LOCALHOST = "localhost";
    private static final int DEFAULT_PORT = 11100; //1050;
    
    private static CorbaHost host;

    private static TransientNameServer server;

    @BeforeClass
    public static void start() {
        try {
            server = new TransientNameServer(LOCALHOST, DEFAULT_PORT, TransientNameService.DEFAULT_SERVICE_NAME);
            Thread t = server.start();
            if (t == null) {
                fail("The naming server cannot be started");
            }
            host = new DefaultCorbaHost();
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }

    /**
     * Tests registering, getting and unregistering CORBA object
     */
    @Test
    public void test_registerServant() {
        try {
            ORB orb = host.createORB(LOCALHOST, DEFAULT_PORT, false);
            TestInterface servant = new TestInterfaceServant();
            host.registerServant(orb, "Test", servant);

            TestInterface ref = TestInterfaceHelper.narrow(host.lookup(orb, "Test"));
            assertEquals(2, ref.getInt(2));

            host.unregisterServant(orb, "Test");
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
        try {
            ORB orb = host.createORB(LOCALHOST, DEFAULT_PORT, false);
            TestInterface servant = new TestInterfaceServant();
            host.registerServant(orb, "Test", servant);
            host.registerServant(orb, "Test", servant);
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
        try {
            ORB orb = host.createORB(LOCALHOST, DEFAULT_PORT, false);
            host.lookup(orb, "NonExistingReference");
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.NO_SUCH_OBJECT));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests unregistering non existing reference
     */
    @Test
    public void test_unregisterNonExistentObject() {
        try {
            ORB orb = host.createORB(LOCALHOST, DEFAULT_PORT, false);
            host.unregisterServant(orb, "NonExistingReference2");
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.NO_SUCH_OBJECT));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests registering under invalid host
     */
    @Test
    public void test_invalidHost() {
        try {
            ORB orb = host.createORB("not_" + LOCALHOST, DEFAULT_PORT, false);
            TestInterface servant = new TestInterfaceServant();
            host.registerServant(orb, "Test", servant);
            fail();
        } catch (CorbaHostException e) {
            // Expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Tests registering under invalid port
     */
    @Test
    public void test_invalidPort() {
        try {
            ORB orb = host.createORB(LOCALHOST, DEFAULT_PORT + 1, false);
            TestInterface servant = new TestInterfaceServant();
            host.registerServant(orb, "Test", servant);
            fail();
        } catch (CorbaHostException e) {
            // Expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Tests registering under invalid name
     */
    @Test
    @Ignore("SUN JDK 6 is happy with all kind of names")
    public void test_invalidBindingName() {
        try {
            ORB orb = host.createORB(LOCALHOST, DEFAULT_PORT, false);
            TestInterface servant = new TestInterfaceServant();
            host.registerServant(orb, "---", servant);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.WRONG_NAME));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
