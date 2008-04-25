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
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.AddService;
import calculator.CalculatorService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class DomainDrivenTestCaseOFF {
    
    private static SCADomain domain;
    private static SCANode   nodeA;
    private static SCANode   nodeB;
    private static SCANode   nodeC;
    private static CalculatorService calculatorServiceA;
    private static CalculatorService calculatorServiceB;
    private static AddService addServiceB;

    @BeforeClass
    public static void init() throws Exception {
             
        try {
            System.out.println("Setting up domain");
            
            SCADomainFactory domainFactory = SCADomainFactory.newInstance();
            domain = domainFactory.createSCADomain("http://localhost:9999");
            
            System.out.println("Setting up calculator nodes");
            
            ClassLoader cl = DomainDrivenTestCaseOFF.class.getClassLoader();
            
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            
            nodeA = nodeFactory.createSCANode("http://localhost:8100/nodeA", "http://localhost:9999");           
            nodeB = nodeFactory.createSCANode("http://localhost:8200/nodeB", "http://localhost:9999");
            nodeC = nodeFactory.createSCANode("http://localhost:8300/nodeC", "http://localhost:9999");

            domain.addContribution("nodeA", cl.getResource("nodeA/"));
            domain.addContribution("nodeB", cl.getResource("nodeB/"));
            domain.addContribution("nodeC", cl.getResource("nodeC/"));
            
            domain.addToDomainLevelComposite(new QName("http://sample", "CalculatorA"));
            domain.addToDomainLevelComposite(new QName("http://sample", "CalculatorB"));
            domain.addToDomainLevelComposite(new QName("http://sample", "CalculatorC"));
            
            domain.start();
            
            calculatorServiceA = domain.getService(CalculatorService.class, "CalculatorServiceComponentA");
            calculatorServiceB = domain.getService(CalculatorService.class, "CalculatorServiceComponentB");
 
        } catch(Exception ex){
            ex.printStackTrace();
        }  
        
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the domain
        domain.stop();
        
        // destroy the nodes
        nodeA.destroy();
        nodeB.destroy();
        nodeC.destroy();
        
        // destroy the domain
        domain.destroy();
    }
    
    //@Test
    public void testKeepServerRunning() throws Exception {
        System.out.println("press enter to continue");
        System.in.read();
    }      

    @Test
    public void testCalculator() throws Exception {       
        // Calculate
        Assert.assertEquals(calculatorServiceA.add(3, 2), 5.0);
        Assert.assertEquals(calculatorServiceA.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorServiceA.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorServiceA.divide(3, 2), 1.5);
        Assert.assertEquals(calculatorServiceB.add(3, 2), 5.0);
        Assert.assertEquals(calculatorServiceB.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorServiceB.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorServiceB.divide(3, 2), 1.5);
    }
}
