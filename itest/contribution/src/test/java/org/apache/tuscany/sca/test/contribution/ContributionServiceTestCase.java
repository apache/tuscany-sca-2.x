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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.contribution.service.util.IOHelper;
import org.apache.tuscany.host.embedded.SCARuntime;
import org.apache.tuscany.host.embedded.impl.DefaultSCARuntime;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceTestCase extends TestCase {
    private static final String CONTRIBUTION_001_ID = "contribution001/";
    private static final String CONTRIBUTION_002_ID = "contribution002/";
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    private static final String FOLDER_CONTRIBUTION = "target/classes/calculator/";

    private ContributionService contributionService;
    
    protected void setUp() throws Exception {
        super.setUp();
        SCARuntime.start("application.composite");
        
        this.contributionService = ((DefaultSCARuntime)SCARuntime.getInstance()).getExtensionPoint(ContributionService.class);
    }

    public void testContributeJAR() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId, contributionLocation, false);
        assertNotNull(contributionId);
    }

    public void testStoreContributionPackageInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId, contributionLocation, true);
        
        assertTrue(FileHelper.toFile(contributionService.getContribution(contributionId).getLocation()).exists());

        assertNotNull(contributionId);

        Contribution contributionModel = contributionService.getContribution(contributionId);
        
        File contributionFile = FileHelper.toFile(contributionModel.getLocation());
        assertTrue(contributionFile.exists());
    }
    
    public void testStoreContributionStreamInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        
        InputStream contributionStream = contributionLocation.openStream();
        try {
            contributionService.contribute(contributionId, contributionLocation, contributionStream);
        } finally {
            IOHelper.closeQuietly(contributionStream);
        }
        
        assertTrue(FileHelper.toFile(contributionService.getContribution(contributionId).getLocation()).exists());

        assertNotNull(contributionId);

        Contribution contributionModel = contributionService.getContribution(contributionId);
        
        File contributionFile = FileHelper.toFile(contributionModel.getLocation());
        assertTrue(contributionFile.exists());
    }    
    
    public void testStoreDuplicatedContributionInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId1 = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId1, contributionLocation, true);
        assertNotNull(contributionService.getContribution(contributionId1));
        URI contributionId2 = URI.create(CONTRIBUTION_002_ID);
        contributionService.contribute(contributionId2, contributionLocation, true);
        assertNotNull(contributionService.getContribution(contributionId2));
    }
    
    
    public void testContributeFolder() throws Exception {
        /*
        File rootContributionFolder = new File(FOLDER_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId, rootContributionFolder.toURL(), false);
        */
    }

}
