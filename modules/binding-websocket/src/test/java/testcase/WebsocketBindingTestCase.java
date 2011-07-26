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

package testcase;

import junit.framework.Assert;

import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class WebsocketBindingTestCase {

    @BeforeClass
    public static void init() {
        JettyServer.portDefault = 8085;
    }

    @Test
    public void testSync() {
        try {
            Node node = NodeFactory.newInstance().createNode("sync.composite");
            node.start();
            node.stop();
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAsync() {
        try {
            Node node = NodeFactory.newInstance().createNode("async.composite");
            node.start();
            node.stop();
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}