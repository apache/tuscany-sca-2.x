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

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
import java.net.URI;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This shows how to test the Calculator service component.
 */
public class MultipleLocalNodesTestCase{

    private static final URI DOMAIN_URI = URI.create("OneNodeTestCase");
    private static Node ifaceNode, serviceNode, clientNode;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        serviceNode = NodeFactory.getInstance().createNode(DOMAIN_URI, "../test-iface-export/target/classes", "../test-service/target/classes");
        serviceNode.start();
        clientNode = NodeFactory.getInstance().createNode(DOMAIN_URI, "../test-iface-export/target/classes", "../test-client/target/classes");
        clientNode.start();
    }

    @Test
    public void testNode() throws Exception {

//        Helloworld service = node.getService(Helloworld.class, "HelloworldService");
//        assertNotNull(service);
//        assertEquals("Hello Petra", service.sayHello("Petra"));
//
//        Helloworld client = node.getService(Helloworld.class, "HelloworldClient");
//        assertNotNull(client);
//        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
//
//        Helloworld scaClientService = SCAClientFactory.newInstance(domainURI).getService(Helloworld.class, "HelloworldService");
//        assertNotNull(scaClientService);
//        assertEquals("Hello Petra", scaClientService.sayHello("Petra"));
//
//        Helloworld scaClientClient = SCAClientFactory.newInstance(domainURI).getService(Helloworld.class, "HelloworldClient");
//        assertNotNull(scaClientClient);
//        assertEquals("Hi Hello Petra", scaClientClient.sayHello("Petra"));
    
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (serviceNode != null) {
            serviceNode.stop();
        }
        if (clientNode != null) {
            clientNode.stop();
        }
    }
}
