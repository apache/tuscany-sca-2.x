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
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.services.contribution.ContributionPackageProcessorRegistryImpl;
import org.apache.tuscany.services.contribution.PackageTypeDescriberImpl;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessorRegistry;

public class FolderContributionPackageProcessorTestCase extends TestCase {
    private static final String FOLDER_CONTRIBUTION = "../../../../core-samples/common/calculator";
    
    private File contributionRoot;

    protected void setUp() throws Exception {
        super.setUp();
        this.contributionRoot = new File(FOLDER_CONTRIBUTION);
    }
    
    public final void testProcessPackageArtifacts() throws Exception {
        ContributionPackageProcessorRegistry packageProcessorRegistry = new ContributionPackageProcessorRegistryImpl(new PackageTypeDescriberImpl()); 
        FolderContributionProcessor folderProcessor = new FolderContributionProcessor(packageProcessorRegistry);

        List<URL> artifacts = folderProcessor.getArtifacts(contributionRoot.toURL(), null);
        assertNotNull(artifacts);
    }
}
