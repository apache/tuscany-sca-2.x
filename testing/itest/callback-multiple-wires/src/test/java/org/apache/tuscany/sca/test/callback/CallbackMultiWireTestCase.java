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

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A TestCase that demonstrates resolving the client service and initiating the callback sequence
 */
public class CallbackMultiWireTestCase {

    private Node node;
    private MyClient myClient1;
    private MyClient myClient2;

    @Before
    public void setUp() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("CallbackMultiWireTest.composite");
        node = NodeFactory.newInstance().createNode("CallbackMultiWireTest.composite", new Contribution("c1", location));
        node.start();
        myClient1 = node.getService(MyClient.class, "MyClientComponent1");
        myClient2 = node.getService(MyClient.class, "MyClientComponent2");
    }

    @After
    public void tearDown() throws Exception {
        node.stop();
    }

    @Test
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
