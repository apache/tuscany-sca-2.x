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

package org.apache.tuscany.sca.contribution.jee;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.contribution.jee.impl.EarContributionProcessor;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;
import org.junit.Test;

/**
 * Ear Contribution package processor test case.
 * Verifies proper handling of EAR contributions.
 * 
 * @version $Rev$ $Date$
 */
public class EarContributionProcessorTestCase {
    private static final String EAR_CONTRIBUTION = "/ejb-injection-sample.ear";

    @Test
    public void testProcessPackageArtifacts() throws Exception {
        EarContributionProcessor earProcessor = new EarContributionProcessor();

        URL earURL = getClass().getResource(EAR_CONTRIBUTION);
        InputStream earStream = earURL.openStream();
        List<URI> artifacts = null;
        try {
            artifacts = earProcessor.getArtifacts(earURL, earStream);
        } finally {
            IOHelper.closeQuietly(earStream);
        }
        
        Assert.assertNotNull(artifacts);
    }
}
