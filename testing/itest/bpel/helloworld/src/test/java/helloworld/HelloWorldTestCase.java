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

package helloworld;

import junit.framework.Assert;

import org.apache.tuscany.implementation.bpel.example.helloworld.HelloPortType;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the BPEL Helloworld Service
 * 
 * @version $Rev$ $Date$
 */
@Ignore
public class HelloWorldTestCase {

	private static Node node;
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
    	String location = ContributionLocationHelper.getContributionLocation("helloworld/helloworld.composite");
		node = NodeFactory.newInstance().createNode("helloworld/helloworld.composite", new Contribution("c1", location));
        node.start();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
    	node.stop();
    }
    
    @Test
    public void testServiceInvocation() throws Exception {
        HelloPortType bpelService = node.getService(HelloPortType.class, "BPELHelloWorldService");
        String response = bpelService.hello("Hello");
        Assert.assertEquals("Hello World", response);
    }
    
    @Test
    public void testReferenceInvocation() throws Exception {
        HelloWorld bpelService = node.getService(HelloWorld.class, "BPELHelloWorld");
        String response = bpelService.hello("Hello");
        Assert.assertEquals("Hello World", response);        
    }
}
