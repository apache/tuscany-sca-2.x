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

package org.apache.tuscany.core.services.deployment;

import org.apache.tuscany.spi.deployer.ContentTypeDescriber;
import org.apache.tuscany.spi.deployer.ContributionProcessorRegistry;
import org.apache.tuscany.spi.deployer.ContributionRepository;

import junit.framework.TestCase;
import org.apache.tuscany.host.deployment.ContributionService;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceImplTestCase extends TestCase {
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    private ContributionRepository repository;
    private ContentTypeDescriber contentTypeDescriber;
    private ContributionProcessorRegistry registry;
    private ContributionService contributionService;

    protected void setUp() throws Exception {
        super.setUp();

//        this.repository = new ContributionRepositoryImpl("target/repository");
//        
//        this.contentTypeDescriber = new ContentTypeDescriberImpl();
//        
//        this.registry = new ContributionProcessorRegistryImpl(contentTypeDescriber);
//        
//        JarContributionProcessor jarProcessor = new JarContributionProcessor();
//        jarProcessor.setContributionProcessorRegistry(this.registry);
//        this.registry.register(JarContributionProcessor.CONTENT_TYPE, jarProcessor);
//        
//        JavaContributionProcessor javaProcessor = new JavaContributionProcessor(null);
//        javaProcessor.setContributionProcessorRegistry(this.registry);
//        this.registry.register(JavaContributionProcessor.CONTENT_TYPE, javaProcessor);
//        
//        
//        contributionService = new ContributionServiceImpl(repository, registry);
    }

    public void testContributeURL() throws Exception {
//        URL contribution = getClass().getResource(JAR_CONTRIBUTION);
//
//        URI contributionURI = contributionService.contribute(contribution);
//        assertNotNull(contributionURI);
    }

}
