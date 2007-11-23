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

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class NodeDrivenTestCase {
    
    private static SCADomain domain;
    private static SCANode   nodeA;
    private static SCANode   nodeB;
    private static SCANode   nodeC;
    private static SCADomain domainProxy;
    private static CalculatorService calculatorServiceA;
    private static CalculatorService calculatorServiceB;
    private static AddService addServiceB;

    @BeforeClass
    public static void init() throws Exception {
             
        try {
            System.out.println("Setting up domain");
            
            SCADomainFactory domainFactory = SCADomainFactory.newInstance();
            domain= domainFactory.createSCADomain("http://localhost:9999");
            
            System.out.println("Setting up calculator nodes");
            
            ClassLoader cl = NodeDrivenTestCase.class.getClassLoader();
            
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            
            // sca-contribution.xml test
            nodeA = nodeFactory.createSCANode("http://localhost:8100/nodeA", "http://localhost:9999");
            nodeA.addContribution("nodeA", cl.getResource("nodeA/"));
            nodeA.addToDomainLevelComposite(new QName("http://sample", "CalculatorA"));
            nodeA.start();

            // sca-deployables test
            nodeB = nodeFactory.createSCANode("http://localhost:8200/nodeB", "http://localhost:9999");
            nodeB.addContribution("nodeB", cl.getResource("nodeB/"));
            nodeB.addToDomainLevelComposite(new QName("http://sample", "CalculatorB"));
            nodeB.start();

            // sca-deployables test
            nodeC = nodeFactory.createSCANode("http://localhost:8300/nodeC", "http://localhost:9999");
            nodeC.addContribution("nodeC", cl.getResource("nodeC/"));
            nodeC.addToDomainLevelComposite(new QName("http://sample", "CalculatorC")); 
            nodeC.addToDomainLevelComposite(new QName("http://sample", "CalculatorC"));
            nodeC.start();

            SCADomainFinder domainFinder = SCADomainFinder.newInstance();
            domainProxy = domainFinder.getSCADomain("http://localhost:9999");
            
            // get a reference to various services in the domain
            calculatorServiceA = nodeA.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentA");
            calculatorServiceB = nodeB.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentB");
            
            //addServiceB = domainProxy.getService(AddService.class, "AddServiceComponentB");
            addServiceB = nodeA.getDomain().getService(AddService.class, "AddServiceComponentB");
            
        } catch(Exception ex){
            ex.printStackTrace();
        }  
        
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        nodeA.destroy();
        nodeB.destroy();    
        nodeC.destroy();
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
        Assert.assertEquals(addServiceB.add(3, 2), 5.0);
        
    }
}
