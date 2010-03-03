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
package org.apache.tuscany.sca.binding.sca.rmi;

import junit.framework.Assert;

import org.apache.tuscany.sca.binding.sca.rmi.helloworld.HelloWorldClient;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PromotionTestCase {
    
    
    public static Node nodeA;
    public static Node nodeB;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting up nodes");
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://tuscany.apache.org/xmlns/sca/1.1}binding.rmi");

        try {
            // create and start nodes
            Contribution contrib = new Contribution("reference", "./target/test-classes/rmi/promotionReference");
            nodeA = NodeFactory.getInstance().createNode("HelloWorld.composite", contrib);
            nodeA.start();
            
            contrib = new Contribution("service", "./target/test-classes/rmi/promotionService");
            nodeB = NodeFactory.getInstance().createNode("HelloWorld.composite", contrib);
            nodeB.start();

        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }     
    }    

    @AfterClass
    public static void destroy() throws Exception {
        nodeA.stop();
        nodeB.stop();
        System.clearProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding");

    }     
    
    @Test
    public void testHelloWorldPromotion() throws Exception {  
        HelloWorldClient helloWorldClientA;
        helloWorldClientA = nodeA.getService(HelloWorldClient.class, "AHelloWorldClientRemotePromotion");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");

    }      
  
}
