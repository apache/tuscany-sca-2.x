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

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.net.Socket;
import java.net.SocketException;

import org.apache.tuscany.sca.binding.corba.impl.service.DynaCorbaServant;
import org.apache.tuscany.sca.host.corba.jse.tns.TnsDefaultCorbaHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 * General tests
 */
public class TnsDefaultCorbaHostTestCase {

    private static TnsDefaultCorbaHost host;

    @BeforeClass
    public static void start() {
        try {
            host = new TnsDefaultCorbaHost();
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void stop() {
        host.stop();
    }

    /**
     * Test for creating and releasing local name server
     */
    @Test
    public void test_localNameServer() {
        int testPort = 5070;
        try {
            String testUri1 = "corbaname::localhost:" + testPort + "#Test1";
            String testUri2 = "corbaname::localhost:" + testPort + "#Test2";
            String testUri3 = "corbaname::localhost:" + testPort + "#Test3";
            DynaCorbaServant servant = new DynaCorbaServant(null, "IDL:org/apache/tuscany:1.0");
            host.registerServant(testUri1, servant);
            host.registerServant(testUri2, servant);
            host.registerServant(testUri3, servant);
            Thread.sleep(1000);
            // make test connection to name server
            Socket socket = new Socket("localhost", testPort);
            socket.close();
            // and stop server
            host.unregisterServant(testUri1);
            host.unregisterServant(testUri2);
            // after releasing 2 clients 3rd should still hold the server
            socket = new Socket("localhost", testPort);
            socket.close();
            host.unregisterServant(testUri3);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        try {
            // previously made 3rd stop so there should be no name server under
            // this port
            new Socket("localhost", testPort);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof SocketException);
        }
    }
}

