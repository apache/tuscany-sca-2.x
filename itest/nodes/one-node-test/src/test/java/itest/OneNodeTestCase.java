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

import java.io.File;

import itest.nodes.Helloworld;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This shows how to test the Calculator service component.
 */
public class OneNodeTestCase{

    private static Node node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty("org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint.enabled", "false");
        NodeFactory factory = NodeFactory.newInstance();
        node = factory.createNode(
               new Contribution("service", getJar("../helloworld-service/target")),
               new Contribution("client", getJar("../helloworld-client/target")));
        node.start();
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
    
    @Test
    public void testCalculator() throws Exception {

        // Ideally this would use the SCAClient API but leaving that tillwe have the basics working

        Helloworld service = node.getService(Helloworld.class, "HelloworldService");
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));

        Helloworld client = node.getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
        }
    }
}
