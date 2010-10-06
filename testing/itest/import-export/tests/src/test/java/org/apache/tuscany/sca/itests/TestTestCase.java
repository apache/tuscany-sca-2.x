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

package org.apache.tuscany.sca.itests;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Test;

public class TestTestCase {

    private Node node;
    private Node node2;

   @Test
    public void testOneNode() throws Exception {

        node = NodeFactory.newInstance().createNode((String)null, new String[] {"../exports/target/classes", "../imports/target/classes"});
        node.start();

    }

    @Test
    public void testSeparateNodes() throws Exception {

        node = NodeFactory.newInstance().createNode((String)null, new String[] {"../exports/target/classes"});
        node.start();

        node2 = NodeFactory.newInstance().createNode((String)null, new String[] {"../imports/target/classes"});
        node2.start();
    }

    @After
    public void tearDown() throws Exception {
        if (node != null) {
            node.stop();
        }
        if (node2 != null) {
            node2.stop();
        }
    }

}
