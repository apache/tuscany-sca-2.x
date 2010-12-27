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
package itest.helloworld;

import java.io.IOException;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class UnknownEndpointTestCase {

    static {
        org.apache.tuscany.sca.http.jetty.JettyServer.portDefault = 8085;
    }

    Node servicesnode;
    Node node;
    
    @Test
    public void testUnknownEndpoints() throws IOException, InterruptedException {
        servicesnode = NodeFactory.getInstance().createNode("services.composite", new String[]{"target/test-classes"}) ;
        servicesnode.start();
        node = NodeFactory.getInstance().createNode("clients.composite", new String[]{"target/test-classes"}) ;
        node.start();
        
        // test the service invocations work
        Helloworld helloworld = node.getService(Helloworld.class, "Client1");
        Assert.assertEquals("Hello Petra", helloworld.sayHello("Petra"));

        helloworld = node.getService(Helloworld.class, "Client2");
        Assert.assertEquals("Hello Petra", helloworld.sayHello("Petra"));
    }

    @After
    public void shutdown() throws InterruptedException {
        node.stop();
        servicesnode.stop();
    }
}
