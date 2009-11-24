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

package org.apache.tuscany.sca.domain.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import itest.nodes.Helloworld;

import org.junit.After;
import org.junit.Test;
import org.oasisopen.sca.client.SCAClient;

/**
 * This shows how to test the Calculator service component.
 */
public class StopStartNodesTestCase{

    private static DomainNode clientNode;
    private static DomainNode serviceNode;
    
    @Test
    public void testTwoNodesSameDomain() throws Exception {
        serviceNode = new DomainNode("vm://fooDomain", new String[]{"target/test-classes/itest-nodes-helloworld-service-2.0-SNAPSHOT.jar"});
        clientNode = new DomainNode("vm://fooDomain", new String[]{"target/test-classes/itest-nodes-helloworld-client-2.0-SNAPSHOT.jar"});

        Helloworld service = SCAClient.getService(Helloworld.class, "fooDomain/HelloworldService");
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));

        Helloworld client = SCAClient.getService(Helloworld.class, "fooDomain/HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));

        serviceNode.stop();

        client = SCAClient.getService(Helloworld.class, "fooDomain/HelloworldClient");
        assertNotNull(client);
        try {
            assertEquals("Hi Hello Petra", client.sayHello("Petra"));
            fail();
        } catch (Exception e) {
            // expected
        }

        serviceNode = new DomainNode("vm://fooDomain", new String[]{"target/test-classes/itest-nodes-helloworld-service-2.0-SNAPSHOT.jar"});
        client = SCAClient.getService(Helloworld.class, "fooDomain/HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }

    @After
    public void tearDownAfterClass() throws Exception {
        if (clientNode != null) {
            clientNode.stop();
        }
        if (serviceNode != null) {
            serviceNode.stop();
        }
    }
}
