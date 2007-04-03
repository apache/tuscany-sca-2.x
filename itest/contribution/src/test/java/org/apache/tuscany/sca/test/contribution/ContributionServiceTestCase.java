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
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.api.SCARuntime;
import org.apache.tuscany.core.bootstrap.DefaultSCARuntime;
import org.apache.tuscany.core.util.FileHelper;
import org.apache.tuscany.host.deployment.ContributionService;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.model.Contribution;
import org.apache.tuscany.test.SCATestCase;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceTestCase extends SCATestCase {
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    private static final String FOLDER_CONTRIBUTION = "/repository/calculator/";

    private ContributionService contributionService;

    protected void setUp() throws Exception {
        super.setUp();
        
        this.contributionService = (ContributionService) ((DefaultSCARuntime)SCARuntime.getInstance()).getSystemService(ComponentNames.TUSCANY_CONTRIBUTION_SERVICE);
    }

    public void testContributeJAR() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = contributionService.contribute(contributionLocation, false);
        assertNotNull(contributionId);
    }

    public void testStoreContributionInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = contributionService.contribute(contributionLocation, true);

        assertNotNull(contributionId);

        Contribution contributionModel = (Contribution) contributionService.getContribution(contributionId);
        
        File contributionFile = FileHelper.toFile(contributionModel.getLocation());
        assertTrue(contributionFile.exists());
    }
    
    public void testStoreDuplicatedContributionInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId1 = contributionService.contribute(contributionLocation, true);
        assertNotNull(contributionId1);
        URI contributionId2 = contributionService.contribute(contributionLocation, true);
        assertNotNull(contributionId2);
    }
    
    
//    public void testContributeFolder() throws Exception {
//        File rootContributionFolder = new File(FOLDER_CONTRIBUTION);
//        
//        URI contributionURI = contributionService.contribute(rootContributionFolder.toURL(), false);
//        assertNotNull(contributionURI);
//    }

}
