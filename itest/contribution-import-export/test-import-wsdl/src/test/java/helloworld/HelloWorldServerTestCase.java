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
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        
        ModelResolver providerResolver = new ModelResolverImpl(cl);
        File xsdContribLocation = new File("../contrib-wsdl/target/tuscany-itest-contribution-import-export-contrib-wsdl-1.0-incubating-SNAPSHOT.jar");
        URL xsdContribURL = xsdContribLocation.toURL();
        Contribution providerContribution = contributionService.contribute("http://import-export/contrib-wsdl", xsdContribURL, providerResolver, false);
        //Composite providerComposite = providerContribution.getDeployables().get(0);
        //domain.getDomainCompositeHelper().addComposite(providerComposite);
        
        ModelResolver helloWorldResolver = new ModelResolverImpl(cl);
        File helloWorldContrib = new File("./target/classes/");
        URL helloWorldURL = helloWorldContrib.toURL();
        Contribution consumerContribution = contributionService.contribute("http://import-export/helloworld", helloWorldURL, helloWorldResolver, false);
        Composite consumerComposite = consumerContribution.getDeployables().get(0);
        domain.getDomainCompositeHelper().addComposite(consumerComposite);
        
        //activate SCA Domain
        domain.getDomainCompositeHelper().activateDomain();
    }
    
    @Before
	public void startServer() throws Exception {
            //domain = SCADomain.newInstance("helloworldws.composite");
	}

	@Test
	public void testPing() throws IOException {
		new Socket("127.0.0.1", 8085);
	}

	@After
	public void stopServer() throws Exception {
            domain.close();
	}

}
