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
 *
 * @version $Rev$ $Date$
 */
public class NodeDrivenTestCaseOFF {
    
    private static SCADomain domain;
    private static SCANode   nodeA;
    private static SCANode   nodeB;
    private static SCANode   nodeC;
    private static SCADomain domainProxy;
    private static CalculatorService calculatorServiceA;
    private static CalculatorService calculatorServiceB;
    private static AddService addServiceBDomainFinder;
    private static AddService addServiceBDomainProxy;
    private static AddService addServiceBDomain;

    @BeforeClass
    public static void init() throws Exception {
             
        try {
            System.out.println("Setting up domain");
            
            SCADomainFactory domainFactory = SCADomainFactory.newInstance();
            domain= domainFactory.createSCADomain("http://localhost:9999");
            
            System.out.println("Setting up calculator nodes");
            
            ClassLoader cl = NodeDrivenTestCaseOFF.class.getClassLoader();
            
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            
            // sca-contribution.xml test
            nodeA = nodeFactory.createSCANode("http://localhost:8100/nodeA", "http://localhost:9999");
            nodeA.addContribution("nodeA", cl.getResource("nodeA/"));
            nodeA.addToDomainLevelComposite(new QName("http://sample", "CalculatorA"));

            // sca-deployables test
            nodeB = nodeFactory.createSCANode("http://localhost:8200/nodeB", "http://localhost:9999");
            nodeB.addContribution("nodeB", cl.getResource("nodeB/"));
            nodeB.addToDomainLevelComposite(new QName("http://sample", "CalculatorB"));

            // sca-deployables test
            nodeC = nodeFactory.createSCANode("http://localhost:8300/nodeC", "http://localhost:9999");
            nodeC.addContribution("nodeC", cl.getResource("nodeC/"));
            nodeC.addToDomainLevelComposite(new QName("http://sample", "CalculatorC")); 
            nodeC.addToDomainLevelComposite(new QName("http://sample", "CalculatorC"));
            
            // start the domain
            domain.start();
            
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
    public void testDomainProxyNode() throws Exception {   
        // the domain proxy associated with each node used to get local services
        calculatorServiceA = nodeA.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentA");
        calculatorServiceB = nodeB.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentB");
        
        // Calculate
        Assert.assertEquals(calculatorServiceA.add(3, 2), 5.0);
        Assert.assertEquals(calculatorServiceA.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorServiceA.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorServiceA.divide(3, 2), 1.5);
        Assert.assertEquals(calculatorServiceB.add(3, 2), 5.0);
        Assert.assertEquals(calculatorServiceB.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorServiceB.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorServiceB.divide(3, 2), 1.5);
        
        // the domain proxy associate with each node used to get remote services
        addServiceBDomainProxy = nodeA.getDomain().getService(AddService.class, "AddServiceComponentB");
        
        Assert.assertEquals(addServiceBDomainProxy.add(3, 2), 5.0);        
    }
    
    @Test
    public void testDomain() throws Exception {
        // the domain itself 
        addServiceBDomain = domain.getService(AddService.class, "AddServiceComponentB");
        
        System.out.println(((SCADomainProxyImpl)nodeA.getDomain()).getComposite(new QName("http://sample", "CalculatorA")));

        Assert.assertEquals(addServiceBDomain.add(3, 2), 5.0);
    }    
    
    @Test
    public void testDomainProxyFinder() throws Exception {
        // the domain proxy retrieved via the domain finder
        SCADomainFinder domainFinder = SCADomainFinder.newInstance();
        domainProxy = domainFinder.getSCADomain("http://localhost:9999");
        addServiceBDomainFinder = domainProxy.getService(AddService.class, "AddServiceComponentB");
  
        Assert.assertEquals(addServiceBDomainFinder.add(3, 2), 5.0);
        
        System.out.println(domainProxy.getDomainLevelComposite());
    }
      
}
