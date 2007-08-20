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
package org.apache.tuscany.sca.test.contribution;

import helloworld.HelloWorldService;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/**
 * Tests that the helloworld server is available
 */
public class HelloWorldServerTestCase extends TestCase{
    private ClassLoader cl;
    private EmbeddedSCADomain domain;
    private Contribution wsdlContribution;
    private Contribution consumerContribution;

    @Override
    protected void setUp() throws Exception {
        // Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();

        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();

        File wsdlContribLocation = new File("../export-wsdl/target/classes");
        URL wsdlContribURL = wsdlContribLocation.toURL();
        wsdlContribution = contributionService.contribute("http://import-export/export-wsdl", wsdlContribURL, false);
        for (Composite deployable : wsdlContribution.getDeployables() ) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.getCompositeBuilder().build(deployable);
        }

        File helloWorldContribLocation = new File("./target/classes/");
        URL helloWorldContribURL = helloWorldContribLocation.toURL();
        consumerContribution = contributionService.contribute("http://import-export/helloworld", helloWorldContribURL, false);
        for (Composite deployable : consumerContribution.getDeployables() ) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.getCompositeBuilder().build(deployable);
        }

        // Start Components from my composite
        for (Composite deployable : consumerContribution.getDeployables() ) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }
    }

	public void testPing() throws IOException {
		new Socket("127.0.0.1", 8085);
	}

    public void testServiceCall() throws IOException {
        HelloWorldService helloWorldService = domain.getService(HelloWorldService.class, "HelloWorldServiceComponent/HelloWorldService");
        assertNotNull(helloWorldService);

        assertEquals("Hello Smith", helloWorldService.getGreetings("Smith"));
    }

    @Override
    public void tearDown() throws Exception {
        ContributionService contributionService = domain.getContributionService();

        // Remove the contribution from the in-memory repository
        contributionService.remove("http://import-export/helloworld");
        contributionService.remove("http://import-export/export-wsdl");

        // Stop Components from my composite
        for (Composite deployable : consumerContribution.getDeployables() ) {
            domain.getCompositeActivator().stop(deployable);
            domain.getCompositeActivator().deactivate(deployable);
        }

        domain.stop();

        domain.close();
    }

}
