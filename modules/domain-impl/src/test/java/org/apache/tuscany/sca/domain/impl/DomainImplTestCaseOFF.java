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
package org.apache.tuscany.sca.domain.impl;

import java.io.Externalizable;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainFactory;
import org.apache.tuscany.sca.domain.spi.SCADomainEventService;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osoa.sca.CallableReference;


/**
 * This server program that loads a composite to provide simple registry function.
 * This server can be replaced with any registry that is appropriate but the components
 * in each node that talk to the registry should be replaced also. 
 */
public class DomainImplTestCaseOFF {

    private static SCADomain domain;
    private static SCADomainEventService domainEventService;
    private static ClassLoader cl;

    @BeforeClass
    public static void init() throws Exception {
             
        try {
            cl = DomainImplTestCaseOFF.class.getClassLoader();
            SCADomainFactory domainFactory = SCADomainFactory.newInstance();
            domain = domainFactory.createSCADomain("http://localhost:9999"); 
            domainEventService = (SCADomainEventService)domain;

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Domain started");
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        // stop the domain    
        domain.destroy();
        System.out.println("Domain stopped");
    }  
    
    @Test
    public void testRegisterNodes() throws Exception { 
        CallableReference<SCANodeManagerService> node1Ref = new TestCallableReferenceImpl<SCANodeManagerService>();
        domainEventService.registerNode("http://localhost:8100/mynode1", "http://localhost:9999", (Externalizable)node1Ref);
        
        CallableReference<SCANodeManagerService> node2Ref = new TestCallableReferenceImpl<SCANodeManagerService>();
        domainEventService.registerNode("http://localhost:8200/mynode2", "http://localhost:9999", (Externalizable)node2Ref);
    }
    
    @Test
    public void testAddContributionWithMetaData() throws Exception {    
        domain.addContribution("contributionNodeA", cl.getResource("nodeA/"));
    }  
    
    @Test
    public void testAddContributionWithoutMetaData() throws Exception {    
        domain.addContribution("contributionNodeB", cl.getResource("nodeB/"));
    }  
    
    @Test
    public void testAddToDomainLevelComposite() throws Exception {    
        domain.addToDomainLevelComposite(new QName("http://sample", "CalculatorA"));
    } 
    
    @Test
    public void testGetDomainLevelComposite() throws Exception { 
        System.out.println(domain.getDomainLevelComposite());
    }  
    
    @Test
    public void testGetComposite() throws Exception { 
        System.out.println(((SCADomainImpl)domain).getComposite(new QName("http://sample", "CalculatorA")));
    }      
   
    @Test
    public void testStartComposite() throws Exception {   
        domain.startComposite(new QName("http://sample", "CalculatorA"));
    } 
    
    @Test
    public void testStopComposite() throws Exception {   
        domain.stopComposite(new QName("http://sample", "CalculatorA"));
    }     
    
    @Test
    public void testRemoveContributions() throws Exception { 
        domain.removeContribution("contributionNodeA");
        domain.removeContribution("contributionNodeB");
    }   
    
    @Test
    public void testUnregisterNodes() throws Exception { 
        domainEventService.unregisterNode("http://localhost:8100/mynode1");
        domainEventService.unregisterNode("http://localhost:8200/mynode2");
    }     

    //@Test
    public void testKeepServerRunning() throws Exception {
        System.out.println("press enter to continue");
        System.in.read();
    }

}
