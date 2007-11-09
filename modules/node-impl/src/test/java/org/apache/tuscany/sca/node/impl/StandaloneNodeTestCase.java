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

package org.apache.tuscany.sca.node.impl;


import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainFactory;
import org.apache.tuscany.sca.node.SCADomainFinder;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.AddService;
import calculator.CalculatorService;
import calculator.SubtractService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class StandaloneNodeTestCase {
    
    private static SCANode   node;
    private static CalculatorService calculatorServiceD;
    private static AddService addServiceD;
    private static SubtractService subtractServiceC;
    private static ClassLoader cl;
    

    @BeforeClass
    public static void init() throws Exception {
             
        try {
            System.out.println("Setting up add node");
            
            cl = StandaloneNodeTestCase.class.getClassLoader();
            
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            
            // rely on meta data to start composite
            node = nodeFactory.createSCANode("http://localhost:8200/node", null);
            node.addContribution("nodeC", cl.getResource("nodeC/"));
            node.addToDomainLevelComposite(new QName("http://sample", "Calculator"));
            node.start();
           
            // get a reference to various services in the node
            subtractServiceC = node.getDomain().getService(SubtractService.class, "SubtractServiceComponentC");            
            
        } catch(Exception ex){
            System.err.println(ex.toString());
        }  
        
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the node      
        node.destroy();    
    }

    @Test
    public void testSubtract() throws Exception {       
        Assert.assertEquals(subtractServiceC.subtract(3, 2), 1.0);        
    }
    
    @Test
    public void testAddSecondContribution() throws Exception {       
        node.stop();
        try {
            subtractServiceC.subtract(3, 2); 
        } catch (Exception ex) {
           // System.out.println(ex.toString());
        }
        
        node.addContribution("nodeD", cl.getResource("nodeD/"));
        node.start();
        subtractServiceC = node.getDomain().getService(SubtractService.class, "SubtractServiceComponentC");
        calculatorServiceD = node.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentD");
        addServiceD = node.getDomain().getService(AddService.class, "AddServiceComponentD");        
        
    } 
    
    @Test
    public void testCalculate() throws Exception {       
        
        // Calculate       
        Assert.assertEquals(calculatorServiceD.add(3, 2), 5.0);
        Assert.assertEquals(calculatorServiceD.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorServiceD.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorServiceD.divide(3, 2), 1.5);
        Assert.assertEquals(addServiceD.add(3, 2), 5.0);
        Assert.assertEquals(subtractServiceC.subtract(3, 2), 1.0);        
        
    }    
}
