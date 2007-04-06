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

package org.apache.tuscany.services.contribution;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.services.contribution.processor.FolderContributionProcessor;
import org.apache.tuscany.services.contribution.processor.JarContributionProcessor;
import org.apache.tuscany.services.spi.contribution.ArtifactProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.ContributionService;
import org.apache.tuscany.services.spi.contribution.DefaultURLArtifactProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.TypeDescriber;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceImplTestCase extends TestCase {
    private static final String CONTRIBUTION_URI = "sca://contributions/002/";
    private static final String CONTRIBUTION = "/repository/sample-calculator.jar";
    
    private TypeDescriber contentTypeDescriber;
    private ContributionPackageProcessorRegistry packageProcessorRegistry;
    private ArtifactProcessorRegistry artifactProcessorRegistry;
    private ContributionService contributionService;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        //boostrap contribution service
        this.contentTypeDescriber = new ArtifactTypeDescriberImpl();
        
 
        this.packageProcessorRegistry = new ContributionPackageProcessorRegistryImpl(contentTypeDescriber);
        new JarContributionProcessor(this.packageProcessorRegistry);
        new FolderContributionProcessor(this.packageProcessorRegistry);
        
        this.artifactProcessorRegistry = new DefaultURLArtifactProcessorRegistry();
        //new CompositeDocumentProcessor(this.artifactProcessorRegistry);
        
        this.contributionService = new ContributionServiceImpl(null, this.packageProcessorRegistry, null, null);
    }

    public void testContributeURL() throws Exception {
        URI contributionURI = URI.create(CONTRIBUTION_URI);
        URL contributionURL = getClass().getResource(CONTRIBUTION);

        this.contributionService.contribute(contributionURI, contributionURL, false);
        assertNotNull(contributionURI);
    }

}
