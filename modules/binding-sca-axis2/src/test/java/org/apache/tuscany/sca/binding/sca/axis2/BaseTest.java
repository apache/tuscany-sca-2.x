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
package org.apache.tuscany.sca.binding.sca.axis2;

import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldServiceLocal;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class BaseTest {

    public static String DEFULT_DOMAIN_NAME = "mydomain";

    public static DistributedSCADomain distributedDomainA;
    public static DistributedSCADomain distributedDomainB;
    public static EmbeddedSCADomain domainA;
    public static EmbeddedSCADomain domainB;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting up distributed nodes");
        ClassLoader cl = BaseTest.class.getClassLoader();

        try {
            // create nodeA to run clients
            String nodeName = "nodeA";

            // Create the distributed domain representation
            distributedDomainA = new TestDistributedSCADomainImpl(DEFULT_DOMAIN_NAME);
            distributedDomainA.setNodeName(nodeName);

            // create and start domainA
            domainA = new EmbeddedSCADomain(cl, DEFULT_DOMAIN_NAME);
            domainA.start();

            // add a contribution to A
            ContributionService contributionService = domainA.getContributionService();

            // find the current directory as a URL. This is where our contribution 
            // will come from
            URL contributionURL = Thread.currentThread().getContextClassLoader().getResource(nodeName + "/");

            // Contribute the SCA application
            Contribution contribution = contributionService.contribute("http://calculator", contributionURL, null, //resolver, 
                                                                       false);
            Composite composite = contribution.getDeployables().get(0);

            // Add the deployable composite to the domain
            domainA.getDomainComposite().getIncludes().add(composite);
            domainA.getCompositeBuilder().build(composite);

            distributedDomainA.addDistributedDomainToBindings(composite);

            domainA.getCompositeActivator().activate(composite);

            // create nodeB to run remote services
            nodeName = "nodeB";

            // Create the distributed domain representation
            distributedDomainB = new TestDistributedSCADomainImpl(DEFULT_DOMAIN_NAME);
            distributedDomainB.setNodeName(nodeName);

            // create and start domainB
            domainB = new EmbeddedSCADomain(cl, DEFULT_DOMAIN_NAME);
            domainB.start();

            // add a contribution to B
            contributionService = domainB.getContributionService();

            // find the current directory as a URL. This is where our contribution 
            // will come from
            contributionURL = Thread.currentThread().getContextClassLoader().getResource(nodeName + "/");

            // Contribute the SCA application
            contribution = contributionService.contribute("http://calculator", contributionURL, null, //resolver, 
                                                          false);
            composite = contribution.getDeployables().get(0);

            // Add the deployable composite to the domain
            domainB.getDomainComposite().getIncludes().add(composite);
            domainB.getCompositeBuilder().build(composite);

            distributedDomainB.addDistributedDomainToBindings(composite);

            domainB.getCompositeActivator().activate(composite);

            // Start node A
            for (Composite compositeA : domainA.getDomainComposite().getIncludes()) {
                domainA.getCompositeActivator().start(compositeA);
            }

            // start node B
            for (Composite compositeB : domainB.getDomainComposite().getIncludes()) {
                domainB.getCompositeActivator().start(compositeB);
            }

        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }     

    }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        domainA.stop();
        domainB.stop();
    }

}
