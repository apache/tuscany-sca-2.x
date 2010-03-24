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

package helloworld;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for helloworld web service client 
 */
public class HelloWorldClientTestCase {

    private HelloWorldService helloWorldService;
    private static Node node;

    private TestCaseRunner server;

    @Before
    public void startClient() throws Exception {
        try {

            NodeFactory factory = NodeFactory.newInstance();
            String contribution = ContributionLocationHelper.getContributionLocation(HelloWorldClient.class);
            node =
                factory.createNode("helloworldwsclient.composite", new Contribution("helloworld", contribution))
                    .start();

            helloWorldService = node.getService(HelloWorldService.class, "HelloWorldServiceComponent");

            server = new TestCaseRunner(HelloWorldTestServer.class);
            server.before();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWSClient() throws Exception {
        Name name = HelloworldFactory.INSTANCE.createName();
        name.setFirst("John");
        name.setLast("Smith");
        String msg = helloWorldService.getGreetings(name);
        Assert.assertEquals("Hello John Smith", msg);
    }

    @After
    public void stopClient() throws Exception {
        server.after();
        node.stop();
    }

    public static void main(String[] args) throws Exception {
        HelloWorldClientTestCase test = new HelloWorldClientTestCase();
        test.startClient();
        test.testWSClient();

        System.in.read();
    }

}
