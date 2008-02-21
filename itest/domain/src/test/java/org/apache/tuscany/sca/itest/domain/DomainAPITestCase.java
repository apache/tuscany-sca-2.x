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

package org.apache.tuscany.sca.itest.domain;


import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainFactory;
import org.apache.tuscany.sca.domain.impl.SCADomainImpl;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osoa.sca.ServiceRuntimeException;

import calculator.CalculatorService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class DomainAPITestCase {
    
    private static ClassLoader cl;
    private static SCADomain domain;
    private static SCANode   nodeA;
    private static SCANode   nodeB;
    private static CalculatorService calculatorService;

    @BeforeClass
    public static void init() throws Exception {
             
        try {
            System.out.println("Setting up domain");
            
            SCADomainFactory domainFactory = SCADomainFactory.newInstance();
            domain = domainFactory.createSCADomain("http://localhost:9999");
            
            System.out.println("Setting up nodes");
            
            cl = DomainAPITestCase.class.getClassLoader();
            
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            
            nodeA = nodeFactory.createSCANode("http://localhost:8100/nodeA", "http://localhost:9999");           
            nodeB = nodeFactory.createSCANode("http://localhost:8200/nodeB", "http://localhost:9999");      
            
        } catch(Exception ex){
            ex.printStackTrace();
        }  
        
   }

    @AfterClass
    public static void destroy() throws Exception {
        
        // destroy the nodes
        nodeA.destroy();
        nodeB.destroy();
        
        // destroy the domain      
        domain.destroy();
    }
    
    @Test
    public void testStartWithNoNodeContributions() throws Exception { 
        try {
            domain.start();
            domain.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testAddContribution() throws Exception {  
        try {
            domain.addContribution("contributionD", cl.getResource("contributionD/"));
            domain.addToDomainLevelComposite(new QName("http://sample", "CalculatorD"));
            //domain.startComposite(new QName("http://sample", "CalculatorD"));
            domain.start();
            
            calculatorService = domain.getService(CalculatorService.class, "CalculatorServiceComponentD");
            
            Assert.assertEquals(calculatorService.add(3, 2), 5.0);
            Assert.assertEquals(calculatorService.subtract(3, 2), 1.0);
            Assert.assertEquals(calculatorService.multiply(3, 2), 6.0);
            Assert.assertEquals(calculatorService.divide(3, 2), 1.5);
            
            domain.stop();
            
            try {
                calculatorService.add(3, 2);
                Assert.fail();
            } catch (ServiceRuntimeException ex){
                // do nothing
            }
            
            domain.start();
            
            Assert.assertEquals(calculatorService.add(3, 2), 5.0);
            
     
            domain.stopComposite(new QName("http://sample", "CalculatorD"));
            
            try {
                calculatorService.add(3, 2);
                Assert.fail();
            } catch (ServiceRuntimeException ex){
                // do nothing
            }            
            
            domain.startComposite(new QName("http://sample", "CalculatorD"));
            
            
            // TODO - this hangs for some reason
            //Assert.assertEquals(calculatorService.add(3, 2), 5.0);
            //Assert.assertEquals(calculatorService.subtract(3, 2), 1.0);            
            
            domain.stop();
            domain.removeContribution("contributionD");
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testAddDependentContribution() throws Exception {  
        try {
            domain.addContribution("contributionDependent", cl.getResource("contributionDependent/"));
            domain.addContribution("contributionPrimary", cl.getResource("contributionPrimary/"));
            ((SCADomainImpl)domain).addToDomainLevelComposite(new QName("http://primary", "CalculatorA"), 
                                                              "http://localhost:8200/nodeB"); 
            domain.start();
            
            calculatorService = domain.getService(CalculatorService.class, "CalculatorServiceComponentA");
            
            Assert.assertEquals(calculatorService.add(3, 2), 5.0);
            Assert.assertEquals(calculatorService.subtract(3, 2), 1.0);
            Assert.assertEquals(calculatorService.multiply(3, 2), 6.0);
            Assert.assertEquals(calculatorService.divide(3, 2), 1.5);
            
            domain.stop();
            domain.removeContribution("contributionPrimary");    
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }  
    
    @Test
    public void testAddMultipleContributions() throws Exception {  
        try {                     
            domain.addContribution("contributionA", cl.getResource("contributionA/"));
            domain.addContribution("contributionB", cl.getResource("contributionB/"));
            ((SCADomainImpl)domain).addToDomainLevelComposite(new QName("http://sample", "CalculatorA"),
                                                              "http://localhost:8100/nodeA");
            ((SCADomainImpl)domain).addToDomainLevelComposite(new QName("http://sample", "CalculatorB"),
                                                              "http://localhost:8200/nodeB");
            domain.start();
            
           
            //Assert.assertEquals("<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://tuscany.apache.org/domain\" xmlns:domain=\"http://tuscany.apache.org/domain\" xmlns:include0=\"http://sample\" xmlns:include1=\"http://sample\" name=\"DomainLevelComposite\"><include name=\"include0:CalculatorB\"/><include name=\"include1:CalculatorA\"/></composite>", 
            //                    domain.getDomainLevelComposite());
                               
            calculatorService = domain.getService(CalculatorService.class, "CalculatorServiceComponentA");
            
            Assert.assertEquals(calculatorService.add(3, 2), 5.0);
            
            domain.stop();
            domain.removeContribution("contributionA");
            domain.removeContribution("contributionB");
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }     
    
    @Test
    public void testAddAndUpdateContribution() throws Exception {  
        try {        
            domain.addContribution("contributionA", cl.getResource("contributionA/"));
            domain.addContribution("contributionB", cl.getResource("contributionB/"));
            ((SCADomainImpl)domain).addToDomainLevelComposite(new QName("http://sample", "CalculatorA"),
                                                              "http://localhost:8100/nodeA");
            ((SCADomainImpl)domain).addToDomainLevelComposite(new QName("http://sample", "CalculatorB"),
                                                              "http://localhost:8200/nodeB");
            domain.start();
            
            //Assert.assertEquals("<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://tuscany.apache.org/domain\" xmlns:domain=\"http://tuscany.apache.org/domain\" xmlns:include0=\"http://sample\" xmlns:include1=\"http://sample\" name=\"DomainLevelComposite\"><include name=\"include0:CalculatorB\"/><include name=\"include1:CalculatorA\"/></composite>", 
            //                    domain.getDomainLevelComposite());
                               
            calculatorService = domain.getService(CalculatorService.class, "CalculatorServiceComponentA");
            
            Assert.assertEquals(calculatorService.add(3, 2), 5.0);

            domain.updateContribution("contributionB", cl.getResource("contributionBupdate/"));
            
            Assert.assertEquals(calculatorService.add(3, 2), 5.0);
            
            domain.stop();
            domain.removeContribution("contributionA");
            domain.removeContribution("contributionB");
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }    

}
