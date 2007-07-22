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
package helloworld;

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

    protected void setUp() throws Exception {
        //Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();
        
        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();
        
        File compositeContribLocation = new File("../contrib-composite/target/classes");
        URL compositeContribURL = compositeContribLocation.toURL();
        Contribution compositeContribution = contributionService.contribute("http://import-export/contrib-composite", compositeContribURL, false);
        for (Composite deployable : compositeContribution.getDeployables() ) {
            domain.getDomainCompositeHelper().addComposite(deployable);
        }
        
        File helloWorldContribLocation = new File("./target/classes/");
        URL helloWorldContribURL = helloWorldContribLocation.toURL();
        Contribution helloWorldContribution = contributionService.contribute("http://import-export/helloworld", helloWorldContribURL, false);
        for (Composite deployable : helloWorldContribution.getDeployables() ) {
            domain.getDomainCompositeHelper().addComposite(deployable);
        }
        
        //activate SCA Domain
        domain.getDomainCompositeHelper().activateDomain();
        
        //Start Components from my composite
        domain.getDomainCompositeHelper().startComponent(domain.getDomainCompositeHelper().getComponent("HelloServiceComponent"));
        domain.getDomainCompositeHelper().startComponent(domain.getDomainCompositeHelper().getComponent("SourceHelloServiceComponent"));
        domain.getDomainCompositeHelper().startComponent(domain.getDomainCompositeHelper().getComponent("HelloWorldServiceComponent"));
    }
    
	public void testPing() throws IOException {
		new Socket("127.0.0.1", 8085);
	}

//    public void testServiceCall() throws IOException {
//        HelloWorldService helloWorldService = domain.getService(HelloWorldService.class, "HelloWorldServiceComponent/HelloWorldService");
//        assertNotNull(helloWorldService);
//        
//        assertEquals("Hello Smith", helloWorldService.getGreetings("Smith"));
//    }

    
	public void tearDown() throws Exception {
            domain.close();
	}

}
