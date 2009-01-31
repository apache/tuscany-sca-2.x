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

package org.apache.tuscany.sca.itest;

import static junit.framework.Assert.assertEquals;
import helloworld.HelloWorldService;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests all the combinations of wiring services, components, and references
 * which use either interface.java or interface.wsdl. The tests use a service
 * (1) wired to a components (2) wired to another component (3) wired to a
 * reference (4). Each of those uses either interface.java (a) or interface.wsdl
 * (b). This results in 16 different combinations 1a2a3a4a thru 1b2b3b4b.
 */
public class WSDLTestCase {

    private static SCADomain domain;

    @Test
    public void testClient1a2a3a4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2a3a4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2a3a4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2a3a4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2a3b4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2a3b4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2a3b4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2a3b4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2b3a4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2b3a4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2b3a4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2b3a4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2b3b4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2b3b4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1a2b3b4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1a2b3b4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2a3a4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2a3a4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2a3a4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2a3a4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2a3b4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2a3b4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2a3b4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2a3b4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2b3a4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2b3a4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2b3a4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2b3a4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2b3b4a() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2b3b4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @Test
    public void testClient1b2b3b4b() {
        HelloWorldService client = domain.getService(HelloWorldService.class, "Client1b2b3b4b");
        assertEquals("Hi petra", client.getGreetings("petra"));
    }

    @BeforeClass
    public static void init() throws Throwable {
        domain = SCADomain.newInstance("WSDLTest.composite");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
