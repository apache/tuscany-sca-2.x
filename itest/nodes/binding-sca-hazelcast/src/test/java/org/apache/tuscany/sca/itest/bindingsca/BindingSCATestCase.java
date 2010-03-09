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

package org.apache.tuscany.sca.itest.bindingsca;

import java.io.File;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Test binding.sca in the same classloader
 */
public class BindingSCATestCase {
    static final String DOMAIN_URI = "my-domain";
    private static final String REGISTRY_URI = "tuscany:bindingScaTestDomain";
    private static final String PKG = "org/apache/tuscany/sca/itest/bindingsca/";
    private static final String CLIENT = "Client.composite";
    private static final String SERVICE = "Service.composite";
    private static final String ROOT = new File("target/classes/" + PKG).toURI().toString();

    /**
     * One NodeFactory and two nodes
     */
    @Test
    public void testOneFactoryTwoNodes() {
        NodeFactory factory1 = NodeFactory.getInstance();
        Node node1 = createClientNode(factory1);
        Node node2 = createServiceNode(factory1);
        node1.start();
        node2.start();
        try {
            runClient(node1);
        } finally {
            node2.stop();
            node1.stop();
            factory1.destroy();
        }
    }

    /**
     * Create the service node
     * @param factory
     * @return
     */
    static Node createServiceNode(NodeFactory factory) {
        NodeConfiguration config2 =
            factory.createNodeConfiguration().setDomainURI(DOMAIN_URI).setURI("node2").addContribution("c2", ROOT)
                .addDeploymentComposite("c2", SERVICE).setDomainRegistryURI(REGISTRY_URI)
                .addBinding(WebServiceBinding.TYPE, "http://localhost:8085/").addBinding(SCABinding.TYPE,
                                                                                         "http://localhost:8085/");

        Node node2 = factory.createNode(config2);
        return node2;
    }

    /**
     * Create the client node
     * @param factory
     * @return
     */
    static Node createClientNode(NodeFactory factory) {
        NodeConfiguration config1 =
            factory.createNodeConfiguration().setDomainURI(DOMAIN_URI).setURI("node1").addContribution("c1", ROOT)
                .addDeploymentComposite("c1", CLIENT).setDomainRegistryURI(REGISTRY_URI)
                .addBinding(WebServiceBinding.TYPE, "http://localhost:8085/").addBinding(SCABinding.TYPE,
                                                                                         "http://localhost:8085/");
        Node node1 = factory.createNode(config1);
        return node1;
    }

    /**
     * Two node factories and two nodes
     */
    @Test
    public void testTwoFactoriesTwoNodes() throws Exception {
        NodeFactory factory1 = NodeFactory.newInstance();
        Node node1 = createClientNode(factory1);
        NodeFactory factory2 = NodeFactory.newInstance();
        Node node2 = createServiceNode(factory2);
        node1.start();
        node2.start();
        Thread.sleep(1000);
        try {
            // This call doesn't require the Local service, it should be successful
            createCustomer(node1);
            try {
                runClient(node1);
                // We cannot make local call to remote endpoints
                Assert.fail("ServiceRuntimeException should have been thrown.");
            } catch (ServiceRuntimeException e) {
                // ignore
            }
        } finally {
            node2.stop();
            node1.stop();
            factory2.destroy();
            factory1.destroy();
        }
    }

    /**
     * Run the client
     * @param node
     */
    static void runClient(Node node) {
        Client client = node.getService(Client.class, "ClientComponent/Client");
        runClient(client);
    }

    static void runClient(Client client) {
        String id = client.create("Ray");
        Assert.assertEquals("Ray", client.getName(id));
    }

    static String createCustomer(Node node) {
        Client client = node.getService(Client.class, "ClientComponent/Client");
        String id = client.create("John");
        Assert.assertNotNull(id);
        return id;
    }

    /**
     * One node factory and one node for both composites
     */
    @Test
    public void testOneFactoryOneNode() {
        NodeFactory factory = NodeFactory.getInstance();
        NodeConfiguration config1 =
            factory.createNodeConfiguration().setDomainURI(DOMAIN_URI).setURI("node1").addContribution("c1", ROOT)
                .addDeploymentComposite("c1", CLIENT).addDeploymentComposite("c1", SERVICE);

        Node node1 = factory.createNode(config1);
        node1.start();
        try {
            runClient(node1);
        } finally {
            node1.stop();
            factory.destroy();
        }
    }

}
