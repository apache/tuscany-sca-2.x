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

package org.apache.tuscany.sca.itest.t3558;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.Test;

public class T3558TestCase {

    @Test
    public void testAllJar() throws Exception {
        Node node = NodeFactory.newInstance().createNode((String)null, new String[]{"src/test/resources/sample-store-all.jar"});
        node.start();
    }

    @Test
    public void testOneNode() throws Exception {
        Node node = NodeFactory.newInstance().createNode((String)null, new String[]{"src/test/resources/sample-store.jar","src/test/resources/sample-store-client.jar"});
        node.start();
    }
    
    @Test
    public void testTwoNodes() throws Exception {
        Node node2 = NodeFactory.newInstance().createNode((String)null, new String[]{"src/test/resources/sample-store.jar"});
        node2.start();
        Node node1 = NodeFactory.newInstance().createNode((String)null, new String[]{"src/test/resources/sample-store-client.jar"});
        node1.start();
    }
    
    @Test
    public void testTwoNodesJIRACode1() throws Exception {
        String storeLocation = "src/test/resources/sample-store.jar";
        String storeClientLocation = "src/test/resources/sample-store-client.jar";

        Node node1 = NodeFactory.newInstance().createNode(new Contribution("store",storeLocation));
        node1.start();
        // The dependent contributions need to be added in the Node and need to be following the main contribution
        Node node2 = NodeFactory.newInstance().createNode("store-client.composite",new Contribution("storeClient", storeClientLocation),new Contribution("store", storeLocation));
        node2.start();     
    }
    
    @Test
    public void testTwoNodesJIRACode2() throws Exception {
        String storeLocation = "src/test/resources/sample-store.jar";
        String storeClientLocation = "src/test/resources/sample-store-client.jar";

        Node node1 = NodeFactory.newInstance().createNode(new Contribution("store",storeLocation));
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode("store-client.composite",new Contribution("storeClient", storeClientLocation));
        node2.start();     
    }
}
