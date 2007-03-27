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
package org.apache.tuscany.services.contribution.processor;

import java.io.File;

import junit.framework.TestCase;

public class FolderContributionProcessorTestCase extends TestCase {
    private static final String DIRECTORY_CONTRIBUTION = "//D:/DEV/Projects/Tuscany/source/java-sca-integration/samples/sca/calculator";
    
    private File contributionRoot;
    

    protected void setUp() throws Exception {
        super.setUp();
        this.contributionRoot = new File(DIRECTORY_CONTRIBUTION);
    }
    
    public final void testProcessJarArtifacts() throws Exception {
//        FolderContributionProcessor folderContribution = new FolderContributionProcessor();
//        ContributionProcessorRegistry mockRegistry = EasyMock.createMock(ContributionProcessorRegistry.class);
//        mockRegistry.register(FolderContributionProcessor.CONTENT_TYPE, folderContribution);
//        EasyMock.expectLastCall().anyTimes();
//        EasyMock.replay(mockRegistry);
//        folderContribution.setContributionProcessorRegistry(mockRegistry);
//        folderContribution.start();
//        EasyMock.verify(mockRegistry);
//        
//        Contribution contribution = new Contribution(URI.create("sca://contributions/001"));
//        contribution.setLocation(this.contributionRoot.toURL());
//        folderContribution.processContent(contribution, contribution.getUri(), null);
    }
}
