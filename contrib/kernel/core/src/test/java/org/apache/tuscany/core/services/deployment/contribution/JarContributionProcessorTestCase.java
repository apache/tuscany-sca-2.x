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
package org.apache.tuscany.core.services.deployment.contribution;

import junit.framework.TestCase;

public class JarContributionProcessorTestCase extends TestCase {
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";

    protected void setUp() throws Exception {
        super.setUp();
    }

    public final void testProcessJarArtifacts() throws Exception {
        /*
        JarContributionProcessor jarContribution = new JarContributionProcessor();
        ContributionProcessorRegistry mockRegistry = EasyMock.createMock(ContributionProcessorRegistry.class);
        mockRegistry.register(JarContributionProcessor.CONTENT_TYPE, jarContribution);
        EasyMock.expectLastCall().once();
        EasyMock.replay(mockRegistry);
        jarContribution.setContributionProcessorRegistry(mockRegistry);
        jarContribution.start();
        EasyMock.verify(mockRegistry);
        URL jarURL = getClass().getResource(JarContributionProcessorTestCase.JAR_CONTRIBUTION);
        Contribution contribution = new Contribution(URI.create("sca://contributions/001"));
        contribution.setLocation(jarURL);
        jarContribution.processContent(contribution, contribution.getUri(), jarURL.openStream());
        */
    }
}
