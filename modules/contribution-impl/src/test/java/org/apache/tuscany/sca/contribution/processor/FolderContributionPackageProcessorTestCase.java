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
package org.apache.tuscany.sca.contribution.processor;

import java.io.File;
import java.net.URI;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.processor.impl.FolderContributionProcessor;

/**
 * Folder Package Processor test case
 * Verifies proper handle of File System structured contributions
 * 
 * @version $Rev$ $Date$
 */
public class FolderContributionPackageProcessorTestCase extends TestCase {
    private static final String FOLDER_CONTRIBUTION = ".";
    
    private File contributionRoot;

    @Override
    protected void setUp() throws Exception {
        this.contributionRoot = new File(FOLDER_CONTRIBUTION);
    }
    
    public final void testProcessPackageArtifacts() throws Exception {
        FolderContributionProcessor folderProcessor = new FolderContributionProcessor();

        List<URI> artifacts = folderProcessor.getArtifacts(contributionRoot.toURL(), null);
        assertNotNull(artifacts);
    }
}
