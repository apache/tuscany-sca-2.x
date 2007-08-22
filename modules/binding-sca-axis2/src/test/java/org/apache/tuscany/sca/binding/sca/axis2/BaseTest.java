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

    public static EmbeddedSCADomain createDomain(String nodeName) throws Exception {
        ClassLoader cl = BaseTest.class.getClassLoader();
        EmbeddedSCADomain domain = null;

        try {
            // Create the distributed domain representation
            TestDistributedSCADomainImpl distributedDomain = new TestDistributedSCADomainImpl(DEFULT_DOMAIN_NAME);
            distributedDomain.setNodeName(nodeName);

            // create and start domainA
            domain = new EmbeddedSCADomain(cl, DEFULT_DOMAIN_NAME);
            domain.start();

            // add a contribution to the domain
            ContributionService contributionService = domain.getContributionService();

            // find the current directory as a URL. This is where our contribution 
            // will come from
            URL contributionURL = Thread.currentThread().getContextClassLoader().getResource(nodeName + "/");

            // Contribute the SCA application
            Contribution contribution = contributionService.contribute("http://calculator", contributionURL, null, //resolver, 
                                                                       false);
            Composite composite = contribution.getDeployables().get(0);

            // Add the deployable composite to the domain
            domain.getDomainComposite().getIncludes().add(composite);
            domain.getCompositeBuilder().build(composite);

            distributedDomain.addDistributedDomainToBindings(composite);

            domain.getCompositeActivator().activate(composite);
        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }   
        return domain;
    }

    public static void startDomain(EmbeddedSCADomain domain) 
      throws Exception {
        try {
            // Start domain
            for (Composite composite : domain.getDomainComposite().getIncludes()) {
                domain.getCompositeActivator().start(composite);
            }

        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }     
    }

    public static void stopDomain(EmbeddedSCADomain domain) throws Exception {
        // stop the domain     
        domain.stop();
    }

}
