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

package org.apache.tuscany.sca.host.embedded.impl;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.management.ComponentListener;
import org.apache.tuscany.sca.host.embedded.management.ComponentManager;

import crud.CRUD;

/**
 * @version $Rev$ $Date$
 */
public class EmbeddedSCADomainTestCase extends TestCase {
    private EmbeddedSCADomain domain;

    /**
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        
        // Create a test embedded SCA domain 
        domain = new EmbeddedSCADomain(getClass().getClassLoader(), "http://localhost");
    }

    public void testDomain() throws Exception {
        // Start the domain
        domain.start();
        
        // Determine my class loader and my test SCA contribution location  
        ClassLoader myClassLoader = getClass().getClassLoader();
        String url = myClassLoader.getResource("test.txt").toString();
        url = url.substring(0, url.length()-8);
        
        // Contribute the SCA contribution
        TestModelResolver myResolver = new TestModelResolver(myClassLoader);
        ContributionService contributionService = domain.getContributionService();
        Contribution contribution = contributionService.contribute("http://test/contribution", new URL(url), myResolver, false);
        assertNotNull(contribution);
        
        // Decide which SCA composite I want to deploy
        Composite myComposite = myResolver.getComposite(new QName("http://sample/crud", "crud"));
        
        // Add the deployable composite to the domain
        EmbeddedSCADomain.DomainCompositeHelper domainHelper = domain.getDomainCompositeHelper();
        domainHelper.addComposite(myComposite);

        // Activate the SCA Domain
        domainHelper.activateDomain();

        // Start the components in my composite
        domainHelper.startComponent(domainHelper.getComponent("CRUDServiceComponent"));
        
        // At this point the domain contains my contribution, my composite and
        // it's started, my application code can start using it
        
        // Get the CRUDServiceComponent service
        CRUD service = domain.getService(CRUD.class, "CRUDServiceComponent");
        
        // Invoke the service
        String id = service.create("ABC");
        Object result = service.retrieve(id);
        assertEquals("ABC", result);
        service.update(id, "EFG");
        result = service.retrieve(id);
        assertEquals("EFG", result);
        service.delete(id);
        result = service.retrieve(id);
        assertNull(result);
        
        // Stop my composite
        domainHelper.stopComposite(myComposite);
        
        // Remove my composite
        domainHelper.removeComposite(myComposite);
        
        // Remove my contribution
        contributionService.remove("http://test/contribution");
        
        // Stop the domain
        domain.stop();
    }

    public void testComponentManager() throws Exception {
        // Start the domain
        domain.start();
        
        // Determine my class loader and my test SCA contribution location  
        ClassLoader myClassLoader = getClass().getClassLoader();
        String url = myClassLoader.getResource("test.txt").toString();
        url = url.substring(0, url.length()-8);
        
        // Contribute the SCA contribution
        TestModelResolver myResolver = new TestModelResolver(myClassLoader);
        ContributionService contributionService = domain.getContributionService();
        Contribution contribution = contributionService.contribute("http://test/contribution", new URL(url), myResolver, false);
        assertNotNull(contribution);
        
        // Decide which SCA composite I want to deploy
        Composite myComposite = myResolver.getComposite(new QName("http://sample/crud", "crud"));
        
        // Add the deployable composite to the domain
        EmbeddedSCADomain.DomainCompositeHelper domainHelper = domain.getDomainCompositeHelper();
        domainHelper.addComposite(myComposite);

        // Activate the SCA Domain
        domainHelper.activateDomain();

        // Start the components in my composite
        domainHelper.startComponent(domainHelper.getComponent("CRUDServiceComponent"));
        
        // At this point the domain contains my contribution, my composite and
        // it's started, my application code can start using it

        ComponentManager componentManager = domain.getComponentManager();
        assertEquals(1, componentManager.getComponentNames().size());
        assertEquals("CRUDServiceComponent", componentManager.getComponentNames().iterator().next());
        
        Component component = componentManager.getComponent("CRUDServiceComponent");
        assertNotNull(component);
        assertEquals("CRUDServiceComponent", component.getName());
        
        MyComponentListener cl = new MyComponentListener();
        componentManager.addComponentListener(cl);

        assertTrue(componentManager.isComponentStarted("CRUDServiceComponent"));
        
        assertFalse(cl.stopCalled);
        componentManager.stopComponent("CRUDServiceComponent");
        assertTrue(cl.stopCalled);
        assertFalse(componentManager.isComponentStarted("CRUDServiceComponent"));
        
        assertFalse(cl.startCalled);
        componentManager.startComponent("CRUDServiceComponent");
        assertTrue(cl.startCalled);
        assertTrue(componentManager.isComponentStarted("CRUDServiceComponent"));

        // Stop my composite
        domainHelper.stopComposite(myComposite);
        
        // Remove my composite
        domainHelper.removeComposite(myComposite);
        
        // Remove my contribution
        contributionService.remove("http://test/contribution");
        
        // Stop the domain
        domain.stop();
    }
    
    class MyComponentListener implements ComponentListener {
        boolean startCalled;
        boolean stopCalled;

        public void componentStarted(String componentName) {
            startCalled = true;
        }

        public void componentStopped(String componentName) {
            stopCalled = true;
        }
        
    }

    /**
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        domain.close();
    }

}
