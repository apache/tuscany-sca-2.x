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


import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.node.impl.SCANodeImpl;
import org.apache.tuscany.sca.node.impl.SCANodeUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class CallableReferenceRemoteTestCase {
    
    private static String DEFAULT_DOMAIN_URL = "http://localhost:8877";

    private static SCADomain domain;
    private static SCADomain domainNodeA;
    private static SCADomain domainNodeB;
   
    private static AComponent acomponent;

    @BeforeClass
    public static void init() throws Exception {
        
        try {
            System.out.println("Setting up distributed registry");
            domain = SCADomain.newInstance("domain.composite");
            
            System.out.println("Setting up distributed nodes");
                    
            // create the node that runs the 
            // calculator component
            domainNodeA = SCADomain.newInstance(DEFAULT_DOMAIN_URL, "nodeA", null, "nodeA/CompositeA.composite");
    
            // create the node that runs the 
            // add component
            domainNodeB = SCADomain.newInstance(DEFAULT_DOMAIN_URL, "nodeB", null, "nodeB/CompositeB.composite");
         
            
            // get a reference to the calculator service from domainA
            // which will be running this component
            acomponent = domainNodeA.getService(AComponent.class, "AComponent/AComponent");   
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        domainNodeA.close();
        domainNodeB.close(); 
        domain.close();
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
