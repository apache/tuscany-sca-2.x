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

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.services.contribution.model.Contribution;
import org.apache.tuscany.services.contribution.model.DeployedArtifact;
import org.apache.tuscany.services.contribution.util.IOHelper;
import org.apache.tuscany.services.spi.contribution.ContributionProcessorRegistry;
import org.easymock.EasyMock;

public class JarContributionProcessorTestCase extends TestCase {
    private static final String CONTRIBUTION_URI = "sca://contributions/001/";
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    
    private JarContributionProcessor jarProcessor;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        this.jarProcessor = new JarContributionProcessor();
    }
    
    public final void testProcessJarArtifacts() throws Exception {
        ContributionProcessorRegistry mockRegistry = EasyMock.createMock(ContributionProcessorRegistry.class);
        mockRegistry.register(JarContributionProcessor.CONTENT_TYPE, jarProcessor);
        mockRegistry.processContent((Contribution)EasyMock.anyObject(), (URI) EasyMock.anyObject(), (InputStream) EasyMock.anyObject() );
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockRegistry);
        jarProcessor.setContributionProcessorRegistry(mockRegistry);
        jarProcessor.start();
        EasyMock.verify(mockRegistry);


        //start processing the jar
        URL jarURL = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionURI = URI.create(CONTRIBUTION_URI);
        URI artifactURI = contributionURI.resolve(JAR_CONTRIBUTION);
       
        Contribution contribution = new Contribution(contributionURI);
        contribution.setLocation(jarURL);
        
        DeployedArtifact artifact = new DeployedArtifact(artifactURI);
        artifact.setLocation(jarURL);
        contribution.addArtifact(artifact);
        
        InputStream jarStream = jarURL.openStream();
        
        try{
            jarProcessor.processContent(contribution, contributionURI.resolve(JAR_CONTRIBUTION), jarStream);
        }finally{
            IOHelper.closeQuietly(jarStream);
        }
    }
}
