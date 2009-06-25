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

package itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import itest.nodes.Helloworld;

import java.io.File;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.client.SCAClient;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * This shows how to test the Calculator service component.
 */
public class ClientNode {

    private static Node clientNode;
    private static TestCaseRunner runner;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        runner = new TestCaseRunner(ServiceNode.class);
        runner.beforeClass();
        System.setProperty("org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint.enabled",
                           "false");
        NodeFactory factory = NodeFactory.newInstance();
        NodeConfiguration conf =
            factory.createNodeConfiguration().setURI("clientNode").
                addBinding(SCABinding.TYPE, "http://localhost:8085/sca https://localhost:9085/sca")
                .addBinding(WebServiceBinding.TYPE, "http://localhost:8086/ws")
                .addContribution("client", new File("../helloworld-client/target/classes").toURI().toString());
        clientNode = factory.createNode(conf).start();
        Thread.sleep(1000);
    }

    @Test
    public void testCalculator() throws Exception {

        Helloworld client = clientNode.getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }

    @Test
    @Ignore("SCAClient needs to leverage the EndpointRegistry to invoke services that are not hosted on the local node")
    public void testCalculatorClientAPI() throws Exception {
        SCAClient scaClient = SCAClientFactory.newInstance();
        Helloworld service = scaClient.getService(Helloworld.class, "HelloworldService", null);
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));

        Helloworld client = scaClient.getService(Helloworld.class, "HelloworldClient", null);
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (clientNode != null) {
            clientNode.stop();
        }
        if (runner != null) {
            runner.afterClass();
        }
    }
}
