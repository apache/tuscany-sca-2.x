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

package org.apache.tuscany.sca.binding.hazelcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;

public class HazelcastBindingTestCase {

    private static Node serviceNode;
    private static Node clientNode;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Note use of NodeFactory.newInstance() so as to start separate runtimes
        serviceNode = NodeFactory.newInstance().createNode(URI.create("tuscany:HazelcastBindingTestCase"), "service.composite", new String[]{"target/test-classes"});
        serviceNode.start();
        clientNode = NodeFactory.newInstance().createNode(URI.create("tuscany:HazelcastBindingTestCase"),  "client.composite", new String[]{"target/test-classes"});
        clientNode.start();
    }

    @Test
    public void testNestedClient() throws Exception {
        Node client2Node = NodeFactory.newInstance().createNode(URI.create("tuscany:HazelcastBindingTestCase"),  "client2.composite", new String[]{"target/test-classes"});
        client2Node.start();
        TestService service = client2Node.getService(TestService.class, "TestServiceClient2");
        assertNotNull(service);
        assertEquals("Petra", service.echoString("Petra"));
        client2Node.stop();
    }

    @Test
    public void testEchoString() throws Exception {
        TestService service = clientNode.getService(TestService.class, "TestServiceClient");
        assertNotNull(service);
        assertEquals("Petra", service.echoString("Petra"));
    }

    @Test
    public void testOnewayString() throws Exception {
        TestService service = clientNode.getService(TestService.class, "TestServiceClient");
        assertNotNull(service);
        service.onewayString("Petra");
    }

    @Test
    public void testEchoComplexType() throws Exception {
        TestService service = clientNode.getService(TestService.class, "TestServiceClient");
        assertNotNull(service);
        ComplexType ct = new ComplexType();
        ct.setString("beate");
        assertEquals("beate", service.echoComplexType(ct).getString());
    }

    @Test
    public void testDeclaredException() throws Exception {
        TestService service = clientNode.getService(TestService.class, "TestServiceClient");
        assertNotNull(service);
        try {
            service.testExceptions("Sue");
            fail();
        } catch (BadStringException e) {
            assertEquals("Sue", e.getMessage());
        }
    }

    @Test
    public void testRuntimeException() throws Exception {
        TestService service = clientNode.getService(TestService.class, "TestServiceClient");
        assertNotNull(service);
        try {
            service.testExceptions("runtime");
            fail();
        } catch (ServiceRuntimeException e) {
            assertEquals("org.oasisopen.sca.ServiceRuntimeException: Remote exception: class java.lang.RuntimeException:runtime", e.getMessage());
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (clientNode != null) {
            clientNode.stop();
        }
        if (serviceNode != null) {
            serviceNode.stop();
        }
    }
}

