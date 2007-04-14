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

package org.apache.tuscany.contribution.services;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.TypeDescriber;
import org.apache.tuscany.contribution.service.impl.ArtifactTypeDescriberImpl;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceImplTestCase extends TestCase {
    private static final String CONTRIBUTION_URI = "sca://contributions/002/";
    private static final String CONTRIBUTION = "/repository/sample-calculator.jar";
    
    private TypeDescriber contentTypeDescriber;
    private PackageProcessorExtensionPoint packageProcessors;
    private ContributionService contributionService;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        //boostrap contribution service
        this.contentTypeDescriber = new ArtifactTypeDescriberImpl();
 
        this.packageProcessors = new DefaultPackageProcessorExtensionPoint(contentTypeDescriber);
        new JarContributionProcessor(this.packageProcessors);
        new FolderContributionProcessor(this.packageProcessors);

        /*
        staxArtifactProcessorRegistry = new DefaultStAXArtifactProcessorRegistry();
        staxArtifactProcessorRegistry.addArtifactProcessor(new CompositeProcessor(staxArtifactProcessorRegistry));
        staxArtifactProcessorRegistry.addArtifactProcessor(new ComponentTypeProcessor(staxArtifactProcessorRegistry));
        staxArtifactProcessorRegistry.addArtifactProcessor(new ConstrainingTypeProcessor(staxArtifactProcessorRegistry));
        
        // Create document processors
        documentArtifactProcessorRegistry = new DefaultURLArtifactProcessorRegistry();
        documentArtifactProcessorRegistry.addArtifactProcessor(new CompositeDocumentProcessor(staxArtifactProcessorRegistry));
        documentArtifactProcessorRegistry.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxArtifactProcessorRegistry));
        documentArtifactProcessorRegistry.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxArtifactProcessorRegistry));
        
        this.contributionService = new ContributionServiceImpl(null, packageProcessorRegistry, documentArtifactProcessorRegistry, null);
        */
    }

    public void testContributeURL() throws Exception {
//        URI contributionURI = URI.create(CONTRIBUTION_URI);
//        URL contributionURL = getClass().getResource(CONTRIBUTION);
//
//        this.contributionService.contribute(contributionURI, contributionURL, false);
//        assertNotNull(contributionURI);
    }

}
