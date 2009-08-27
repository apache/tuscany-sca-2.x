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
import static org.junit.Assert.fail;
import itest.nodes.Helloworld;

import java.io.File;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Tests using two nodes and stopping and restarting a service node.
 */
public class StopStartNodesTestCaseFIXME {

    private static Node serviceNode;
    private static Node clientNode;

    @Test
    public void testCalculator() throws Exception {
        NodeFactory factory = NodeFactory.newInstance();
        serviceNode = factory.createNode(new Contribution("service", getJar("../helloworld-service/target")));
        serviceNode.start();
        clientNode = factory.createNode(new Contribution("client", getJar("../helloworld-client/target")));
        clientNode.start();

        Helloworld service = serviceNode.getService(Helloworld.class, "HelloworldService");
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));

        Helloworld client = clientNode.getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));

        serviceNode.stop();

        client = clientNode.getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        try {
            client.sayHello("Petra");
            fail();
        } catch (Exception e) {
            // expected
            // TODO: better exception than NPE
        }

        serviceNode = factory.createNode(new Contribution("service", getJar("../helloworld-service/target")));
        serviceNode.start();

        client = clientNode.getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }

    /**
     * Get the jar in the target folder without being dependent on the version name to
     * make tuscany releases easier
     */
    private static String getJar(String targetDirectory) {
        File f = new File(targetDirectory);
        for (File file : f.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                return file.toURI().toString();
            }
        }
        throw new IllegalStateException("Can't find jar in: " + targetDirectory);
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
