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
import org.apache.tuscany.sca.host.embedded.test.extension.TestService;

/**
 * Test creation of an EmbeddedSCADomain and invocation of a service.
 * 
 * @version $Rev$ $Date$
 */
public class EmbeddedSCADomainTestCase extends TestCase {
    private EmbeddedSCADomain domain;

    @Override
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
        Composite myComposite = myResolver.getComposite(new QName("http://test", "test"));
        
        // Add the deployable composite to the domain
        domain.getDomainComposite().getIncludes().add(myComposite);
        
        
        domain.buildComposite(myComposite);

        // Start the composite
        domain.getCompositeActivator().activate(myComposite);
        domain.getCompositeActivator().start(myComposite);
        
        // At this point the domain contains my contribution, my composite and
        // it's started, my application code can start using it
        
        // Get the TestServiceComponent service
        TestService service = domain.getService(TestService.class, "TestServiceComponent");
        
        // Invoke the service
        String result = service.ping("Bob");
        assertEquals("Hello Bob", result);
        
        // Stop my composite
        domain.getCompositeActivator().stop(myComposite);
        domain.getCompositeActivator().deactivate(myComposite);
        
        // Remove my composite
        domain.getDomainComposite().getIncludes().remove(myComposite);
        
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
        Composite myComposite = myResolver.getComposite(new QName("http://test", "test"));
        
        // Add the deployable composite to the domain
        domain.getDomainComposite().getIncludes().add(myComposite);
        
        domain.buildComposite(myComposite);

        // Start the composite
        domain.getCompositeActivator().activate(myComposite);
        domain.getCompositeActivator().start(myComposite);
        
        // At this point the domain contains my contribution, my composite and
        // it's started, my application code can start using it

        ComponentManager componentManager = domain.getComponentManager();
        assertEquals(1, componentManager.getComponentNames().size());
        assertEquals("TestServiceComponent", componentManager.getComponentNames().iterator().next());
        
        Component component = componentManager.getComponent("TestServiceComponent");
        assertNotNull(component);
        assertEquals("TestServiceComponent", component.getName());
        
        MyComponentListener cl = new MyComponentListener();
        componentManager.addComponentListener(cl);

        assertTrue(componentManager.isComponentStarted("TestServiceComponent"));
        
        assertFalse(cl.stopCalled);
        componentManager.stopComponent("TestServiceComponent");
        assertTrue(cl.stopCalled);
        assertFalse(componentManager.isComponentStarted("TestServiceComponent"));
        
        assertFalse(cl.startCalled);
        componentManager.startComponent("TestServiceComponent");
        assertTrue(cl.startCalled);
        assertTrue(componentManager.isComponentStarted("TestServiceComponent"));

        // Stop my composite
        domain.getCompositeActivator().stop(myComposite);
        domain.getCompositeActivator().deactivate(myComposite);
        
        // Remove my composite
        domain.getDomainComposite().getIncludes().remove(myComposite);
        
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

    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

}
