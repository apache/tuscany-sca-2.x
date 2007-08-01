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
package org.apache.tuscany.sca.test.callback;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * A testcase that demonstrates resolving the client service and initiating the callback sequence
 */
public class CallbackMultiWireTestCase extends TestCase {

    private SCADomain scaDomain;
    private MyClient myClient1;
    private MyClient myClient2;

    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("CallbackMultiWireTest.composite");
        myClient1 = scaDomain.getService(MyClient.class, "MyClientComponent1");
        myClient2 = scaDomain.getService(MyClient.class, "MyClientComponent2");
    }
    
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void test() throws Exception {
        System.out.println("Main thread " + Thread.currentThread());
        myClient1.aClientMethod();
        myClient2.aClientMethod();
        System.out.println("Sleeping ...");
        Thread.sleep(300);
        assertEquals("-> someMethod1 -> receiveResult", MyClientImpl1.result);
        assertEquals("-> someMethod2 -> receiveResult", MyClientImpl2.result);
    }
}
