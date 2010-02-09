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

package org.apache.tuscany.sca.test;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CallBackTwoNodesTestCase {

    private static Node node1;
    private static Node node2;
    private CallBackBasicClient aCallBackClient;

    @Before
    public void setUp() throws Exception {
        try {         
            NodeFactory factory = NodeFactory.getInstance();
            
            NodeConfiguration configuration = factory.createNodeConfiguration();
            configuration.setDomainURI("tuscany:default");
            configuration.setURI("node1");
            configuration.addContribution("c1", "./target/classes");
            configuration.addDeploymentComposite("c1","CallBackService.composite");

            //node1 = factory.createNode("CallBackService.composite", new Contribution("c1", "./target/classes"));
            node1 = factory.createNode(configuration);
            node1.start();
            
            configuration = factory.createNodeConfiguration();
            configuration.setDomainURI("tuscany:default");
            configuration.setURI("node2");
            configuration.addContribution("c1", "./target/classes");
            configuration.addDeploymentComposite("c1","CallBackReference.composite");
            
            //node2 = factory.newInstance().createNode("CallBackReference.composite", new Contribution("c1", "./target/classes"));
            node2 = factory.createNode(configuration);
            node2.start();
    
            aCallBackClient = node2.getService(CallBackBasicClient.class, "CallBackBasicClient");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    @Test
    public void testCallBackTwoNodes() {
        aCallBackClient.run();
    }    

    @After
    public void tearDown() throws Exception {
        node2.stop();
        node1.stop();
    }

}
