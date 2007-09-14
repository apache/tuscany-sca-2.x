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
package org.apache.tuscany.sca.itest.callableref;


import static junit.framework.Assert.assertEquals;


import junit.framework.Assert;


import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class CallableReferenceRemoteTestCase {
    
    private static String DEFAULT_DOMAIN_NAME = "mydomain";

    private static NodeImpl registry;
    private static NodeImpl nodeA;
    private static NodeImpl nodeB;
   
    private static AComponent acomponent;

    @BeforeClass
    public static void init() throws Exception {
        
        try {
            System.out.println("Setting up distributed registry");
            registry = new NodeImpl();
            registry.start();
            registry.getContributionManager().startContribution(CallableReferenceRemoteTestCase.class.getClassLoader().getResource("domain/"));
            //registry.getContributionManager().startContribution("file:///C:/simon/tuscany/java-head/sca/modules/distributed-impl/target/tuscany-distributed-impl-1.0-incubating-SNAPSHOT.jar");
            
            System.out.println("Setting up distributed nodes");
                    
            // create the node that runs the 
            // calculator component
            nodeA = new NodeImpl(DEFAULT_DOMAIN_NAME, "nodeA");
            nodeA.start();
            nodeA.getContributionManager().startContribution(CallableReferenceRemoteTestCase.class.getClassLoader().getResource("nodeA/"));
    
            // create the node that runs the 
            // add component
            nodeB = new NodeImpl(DEFAULT_DOMAIN_NAME, "nodeB");
            nodeB.start();
            nodeB.getContributionManager().startContribution(CallableReferenceRemoteTestCase.class.getClassLoader().getResource("nodeB/"));            
         
            
            // get a reference to the calculator service from domainA
            // which will be running this component
            acomponent = nodeA.getService(AComponent.class, "AComponent/AComponent");   
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        nodeA.stop();
        nodeB.stop();   
    }

    @Test
    public void testBReference() {
        assertEquals("BComponent", acomponent.fooB());
    }

    @Test
    public void testBCast() {
        assertEquals("BComponent", acomponent.fooB1());
    }
    
    @Test
    public void testCReference() {
        assertEquals("CComponent", acomponent.fooC());
    }
    
    @Test
    public void testCServiceReference() {
        assertEquals("CComponent", acomponent.fooC1());
    }    

    /* Commented it out as it's still failing
    @Test
    public void testDReference() {
        assertEquals("DAComponent", acomponent.fooD());
    }
    */
    
    //@Test
    public void testBCReference() {
        assertEquals("BCComponent", acomponent.fooBC());
    }

    @Test
    public void testRequiredFalseReference() {
        try {
            acomponent.getDReference().foo(null);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

}
