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

import static org.junit.Assert.assertTrue;
import itest.test.TestIt;

import java.net.URI;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OneNodeTestCase{

    private static Node node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node = NodeFactory.newInstance().createNode(URI.create("OneNodeTestCase"), "../iface-export/target/classes", "../service/target/classes", "../client/target/classes");
        node.start();
    }

    @Test
    public void testNode() throws Exception {
      TestIt service = node.getService(TestIt.class, "TestClient");
      assertTrue(service.testIt());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
        }
    }
}
