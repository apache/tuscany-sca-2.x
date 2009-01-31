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
package test;

import helloworld.HelloWorldService;

import java.io.IOException;
import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

import junit.framework.TestCase;

/**
 * Test multiple contribution scenario
 * Contributed by TUSCANY-1756
 */
public class ContributionTestCase extends TestCase {

    private String helloContribution_dir = "target/test-classes/contribution-export";
    //private String helloWorldContribution_one_dir = "target/test-classes/contribution-import-one";
    private String helloWorldContribution_two_dir = "target/test-classes/contribution-import-two";

    private ClassLoader cl;
    private EmbeddedSCADomain domain;
    private Contribution helloContribution;
    //private Contribution helloWorldContribution_one;
    private Contribution helloWorldContribution_two;

    protected void setUp() throws Exception {
        URL helloContribution_URL = new java.io.File(helloContribution_dir).toURI().toURL();
        //URL helloWorldContribution_one_URL = new java.io.File(helloWorldContribution_one_dir).toURI().toURL();
        URL helloWorldContribution_two_URL = new java.io.File(helloWorldContribution_two_dir).toURI().toURL();

        // Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        // Start the domain
        domain.start();

        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();

        helloContribution =
            contributionService.contribute("http://contribution-multiple/helloworld", helloContribution_URL, false);

        //helloWorldContribution_one =
         //   contributionService.contribute("http://contribution-multiple/helloworld_one", helloWorldContribution_one_URL, false);

        helloWorldContribution_two =
            contributionService.contribute("http://contribution-multiple/helloworld_two", helloWorldContribution_two_URL, false);

        for (Composite deployable : helloContribution.getDeployables()) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.buildComposite(deployable);
        }

        //for (Composite deployable : helloWorldContribution_one.getDeployables()) {
        //    domain.getDomainComposite().getIncludes().add(deployable);
        //    domain.buildComposite(deployable);
        //}

        for (Composite deployable : helloWorldContribution_two.getDeployables()) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.buildComposite(deployable);
        }
        
        for (Composite deployable : helloContribution.getDeployables()) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }

        //for (Composite deployable : helloWorldContribution_one.getDeployables()) {
        //    domain.getCompositeActivator().activate(deployable);
        //    domain.getCompositeActivator().start(deployable);
        //}

        for (Composite deployable : helloWorldContribution_two.getDeployables()) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }        

    }

    public void testServiceCall() throws IOException {
        //HelloWorldService helloWorldService_one =
            //domain.getService(HelloWorldService.class, "HelloWorldServiceComponent_one/HelloWorldService");
        //assertNotNull(helloWorldService_one);

        //assertEquals("Hello Smith", helloWorldService_one.getGreetings("Smith"));

        HelloWorldService helloWorldService_two =
            domain.getService(HelloWorldService.class, "HelloWorldServiceComponent_two/HelloWorldService");
        assertNotNull(helloWorldService_two);

        assertEquals("Hello Smith", helloWorldService_two.getGreetings("Smith"));
        
    }

    public void tearDown() throws Exception {
        ContributionService contributionService = domain.getContributionService();

        // Remove the contribution from the in-memory repository
        contributionService.remove("http://contribution-multiple/helloworld");
        //contributionService.remove("http://contribution-multiple/helloworld_one");
        contributionService.remove("http://contribution-multiple/helloworld_two");

        // Stop Components from my composite
        for (Composite deployable : helloWorldContribution_two.getDeployables()) {
            domain.getCompositeActivator().stop(deployable);
            domain.getCompositeActivator().deactivate(deployable);
        }        
        //for (Composite deployable : helloWorldContribution_one.getDeployables()) {
        //    domain.getCompositeActivator().stop(deployable);
        //    domain.getCompositeActivator().deactivate(deployable);
        //}
        for (Composite deployable : helloContribution.getDeployables()) {
            domain.getCompositeActivator().stop(deployable);
            domain.getCompositeActivator().deactivate(deployable);
        }
        // domain.stop();
        domain.close();
    }

}
