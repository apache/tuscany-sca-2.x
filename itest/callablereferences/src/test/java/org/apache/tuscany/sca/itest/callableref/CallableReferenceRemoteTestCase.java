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

import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class CallableReferenceRemoteTestCase {
    
    private static SCANode nodeA;
    private static SCANode nodeB;
   
    private static AComponent acomponent;

    @BeforeClass
    public static void init() throws Exception {
        
        try {
            
            System.out.println("Setting up nodes");
                  
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            nodeA = nodeFactory.createSCANode(new File("src/main/resources/nodeA/CompositeA.composite").toURL().toString(),
                                             new SCAContribution("TestContribution", 
                                                                 new File("src/main/resources/nodeA").toURL().toString()));
            
     
            nodeB = nodeFactory.createSCANode(new File("src/main/resources/nodeB/CompositeB.composite").toURL().toString(),
                                             new SCAContribution("TestContribution", 
                                                                 new File("src/main/resources/nodeB").toURL().toString()));
            
            
            nodeA.start();
            nodeB.start();
            
            acomponent = ((SCAClient)nodeA).getService(AComponent.class, "AComponent/AComponent");
               
        } catch (Throwable ex) {
            System.out.println(ex.toString());
            // Print detailed cause information.
            ex.printStackTrace();
            StringBuffer sb = new StringBuffer();
            Throwable cause = ex.getCause();
            while ( cause != null ) {
                sb.append( "   " );
                System.out.println( sb.toString() + "Cause: " + cause );
                if (cause instanceof java.lang.reflect.InvocationTargetException)
                    System.out.println( sb.toString() + "Target Exception: " + ((java.lang.reflect.InvocationTargetException)cause).getTargetException() );
                cause = cause.getCause();                
            }
        }
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        nodeA.stop();
        nodeB.stop();
    }

    //@Test
    public void testKeepServerRunning1() throws Exception {
        System.out.println("press enter to continue");
        System.in.read();
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

    @Test
    public void testDReferenceString() {
        assertEquals("DAComponent", acomponent.fooStringD());
    }
  
    @Test
    public void testDReference() {
        assertEquals("DAComponent", acomponent.fooD());
    }    

    
    @Test
    public void testBCReference() {
        assertEquals("BCComponent", acomponent.fooBC());
    }

    @Test
    public void testRequiredFalseReference() {
        try {
            acomponent.invokeDReference();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

}
