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

import helloworld.HelloWorldService;
import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.CurrentCompositeContext;

/**
 * Tests all the combinations of wiring services, components, and references
 * which use either interface.java or interface.wsdl.
 * 
 * The tests use a service (1) wired to a components (2) wired to another 
 * component (3) wired to a reference (4). Each of those uses either 
 * interface.java (a) or interface.wsdl (b). This results in 16 different
 * combinations 1a2a3a4a thru 1b2b3b4b.
 */
public class WSDLTestCase extends TestCase {

    public void testClient1a2a3a4a()  {
        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3a4a");
        assertEquals("Hi petra", client.getGreetings("petra"));
        HelloWorldService client1 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3a4b");
        assertEquals("Hi petra", client1.getGreetings("petra"));
        HelloWorldService client2 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3b4a");
        assertEquals("Hi petra", client2.getGreetings("petra"));
        HelloWorldService client3 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3b4b");
        assertEquals("Hi petra", client3.getGreetings("petra"));
        HelloWorldService client4 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3a4a");
        assertEquals("Hi petra", client4.getGreetings("petra"));
        HelloWorldService client5 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3a4b");
        assertEquals("Hi petra", client5.getGreetings("petra"));
        HelloWorldService client6 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3b4a");
        assertEquals("Hi petra", client6.getGreetings("petra"));
        HelloWorldService client7 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3b4b");
        assertEquals("Hi petra", client7.getGreetings("petra"));
        HelloWorldService client8 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3a4a");
        assertEquals("Hi petra", client8.getGreetings("petra"));
        HelloWorldService client9 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3a4b");
        assertEquals("Hi petra", client9.getGreetings("petra"));
        HelloWorldService client10 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3b4a");
        assertEquals("Hi petra", client10.getGreetings("petra"));
        HelloWorldService client11 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3b4b");
        assertEquals("Hi petra", client11.getGreetings("petra"));
//TODO: TUSCANY-1124 DataBindingInterceptor incorrect when binding.ws wired to java component using interface.wsdl 
//     HelloWorldService client12 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3a4a");
//      assertEquals("Hi petra", client12.getGreetings("petra"));
//      HelloWorldService client13 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3a4b");
//      assertEquals("Hi petra", client13.getGreetings("petra"));
//      HelloWorldService client14 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3b4a");
//      assertEquals("Hi petra", client14.getGreetings("petra"));
//      HelloWorldService client15 = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3b4b");
//      assertEquals("Hi petra", client15.getGreetings("petra"));
    }

    // TODO: TUSCANY-1125, Testcases fail with out of heap space if too many individual test run in one testcase 
//    public void testClient1a2a3a4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3a4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2a3a4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3a4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2a3b4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3b4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2a3b4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2a3b4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2b3a4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3a4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2b3a4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3a4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2b3b4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3b4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1a2b3b4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1a2b3b4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2a3a4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3a4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2a3a4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3a4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2a3b4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3b4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2a3b4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2a3b4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2b3a4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3a4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2b3a4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3a4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2b3b4a()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3b4a");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }
//    public void testClient1b2b3b4b()  {
//        HelloWorldService client = CurrentCompositeContext.getContext().locateService(HelloWorldService.class, "Client1b2b3b4b");
//        assertEquals("Hi petra", client.getGreetings("petra"));
//    }

    protected void setUp() throws Exception {
    	SCARuntime.start("WSDLTest.composite");
    }

    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }

}
