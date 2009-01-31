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

package helloworld.sdo;

import helloworld.sdo.client.HelloWorldClient;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

import util.OSGiTestUtil;


public class SdoTestCase extends TestCase {

    
    private EmbeddedSCADomain scaDomain;
    
    
    protected void setUp() throws Exception {

        OSGiTestUtil.setUpOSGiTestRuntime();

        scaDomain = new EmbeddedSCADomain(this.getClass().getClassLoader(), "http://localhost");
        scaDomain.start();
        ContributionService contributionService = scaDomain.getContributionService();
        
        URL serviceURL = new File("./target/classes/sdo/ds/HelloWorldService.jar").toURI().toURL();
        URL clientURL = new File("./target/classes/sdo/ds/HelloWorldClient.jar").toURI().toURL();
        
        Contribution serviceContribution = contributionService.contribute("HelloWorldService", serviceURL, true);
        Contribution clientContribution = contributionService.contribute("HelloWorldClient", clientURL, true);
        
        for (Composite deployable : serviceContribution.getDeployables()) {
            scaDomain.getDomainComposite().getIncludes().add(deployable);
            scaDomain.buildComposite(deployable);
        }
        for (Composite deployable : clientContribution.getDeployables()) {
            scaDomain.getDomainComposite().getIncludes().add(deployable);
            scaDomain.buildComposite(deployable);
        }
        for (Composite deployable : clientContribution.getDeployables() ) {
            scaDomain.getCompositeActivator().activate(deployable);
            scaDomain.getCompositeActivator().start(deployable);
        }
    }

    protected void tearDown() throws Exception {
        scaDomain.close();

        OSGiTestUtil.shutdownOSGiRuntime();
    }
    

    public void testJavaToOSGi() throws Exception {

        HelloWorldClient helloWorldClient = scaDomain.getService(HelloWorldClient.class, "JavaHelloWorldClientComponent");
        String greetings = helloWorldClient.getGreetings("Apache", "Tuscany");
        System.out.println(greetings);
        assertEquals("Hello Apache Tuscany", greetings);
    }
    
    public void testOSGiToJava() throws Exception {

        HelloWorldClient helloWorldClient = scaDomain.getService(HelloWorldClient.class, "OSGiHelloWorldClientComponent");
        String greetings = helloWorldClient.getGreetings("Apache", "Tuscany");
        System.out.println(greetings);
        assertEquals("Hello Apache Tuscany", greetings);
    }

    
}
