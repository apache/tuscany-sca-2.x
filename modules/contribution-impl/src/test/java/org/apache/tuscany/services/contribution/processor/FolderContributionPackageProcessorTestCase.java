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
import java.io.InputStream;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.services.contribution.model.Contribution;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessorRegistry;
import org.easymock.EasyMock;

public class FolderContributionPackageProcessorTestCase extends TestCase {
    private static final String CONTRIBUTION_URI = "sca://contributions/002/";
    private static final String DIRECTORY_CONTRIBUTION = "../../../../core-samples/common/calculator";
    
    private File contributionRoot;
    private FolderContributionProcessor folderProcessor = new FolderContributionProcessor();
    

    protected void setUp() throws Exception {
        super.setUp();
        this.contributionRoot = new File(DIRECTORY_CONTRIBUTION);
    }
    
    public final void testProcessJarArtifacts() throws Exception {
        ContributionPackageProcessorRegistry mockRegistry = EasyMock.createMock(ContributionPackageProcessorRegistry.class);
        mockRegistry.register(FolderContributionProcessor.PACKAGE_TYPE, folderProcessor);
        mockRegistry.processContent((Contribution)EasyMock.anyObject(), (URI) EasyMock.anyObject(), (InputStream) EasyMock.anyObject() );
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockRegistry);
        folderProcessor.setContributionProcessorRegistry(mockRegistry);
        folderProcessor.start();
        EasyMock.verify(mockRegistry);
        
        Contribution contribution = new Contribution(URI.create(CONTRIBUTION_URI));
        contribution.setLocation(this.contributionRoot.toURL());
        folderProcessor.processContent(contribution, contribution.getUri(), null);
    }
}
