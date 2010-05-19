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

package helloworld.jaxrs.test;

import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.RestClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class HelloWorldTestCase {
    private static Node node;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node =
            NodeFactory.getInstance().createNode("helloworld/jaxrs/HelloWorld.composite",
                                                 new Contribution("c1", new File("target/test-classes").toURI()
                                                     .toString())).start();
    }

    @Test
    public void testDummy() {
        RestClient client = new RestClient();
        ClientResponse response = client.resource("http://localhost:8080/world").get();
        String out = response.getEntity(String.class);
        System.out.println(out);
        Assert.assertEquals(200, response.getStatusCode());
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

}
