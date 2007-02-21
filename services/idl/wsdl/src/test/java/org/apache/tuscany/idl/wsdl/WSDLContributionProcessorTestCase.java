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
package org.apache.tuscany.idl.wsdl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;

import java.net.URI;
import java.net.URL;

import javax.wsdl.Definition;

import junit.framework.TestCase;

import org.apache.tuscany.spi.deployer.ArtifactResolverRegistry;
import org.apache.tuscany.spi.deployer.ContributionProcessorRegistry;
import org.apache.tuscany.spi.model.Contribution;
import org.apache.tuscany.spi.model.DeployedArtifact;

/**
 * @version $Rev$ $Date$
 */
public class WSDLContributionProcessorTestCase extends TestCase {
    private WSDLContributionProcessor processor;

    protected void setUp() throws Exception {
        super.setUp();
        processor = new WSDLContributionProcessor();
        ArtifactResolverRegistry registry = createMock(ArtifactResolverRegistry.class);
        URL url = getClass().getResource("test2.wsdl");
        expect(registry.resolve(isA(URI.class),
                                (String)isNull(),
                                isA(String.class),
                                isA(String.class))).andReturn(url).anyTimes();
        processor.setArtifactResolverRegistry(registry);
        replay(registry);

        ContributionProcessorRegistry processorRegistry = createMock(ContributionProcessorRegistry.class);
        processorRegistry.processModel(isA(Contribution.class), isA(URI.class), isA(Definition.class));
        expectLastCall().anyTimes();
        replay(processorRegistry);
        processor.setContributionProcessorRegistry(processorRegistry);
    }

    public void testLoad() throws Exception {
        URI uri = URI.create("sca://contribution/001");
        Contribution contribution = new Contribution(uri);

        addArtifact(contribution, "sca://contribution/001/test1.wsdl");
        addArtifact(contribution, "sca://contribution/001/test2.wsdl");
        addArtifact(contribution, "sca://contribution/001/ipo.xsd");

        URL url = getClass().getResource("test1.wsdl");
        try {
            processor.processContent(contribution, URI.create("sca://contribution/001/test1.wsdl"), url.openStream());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    private DeployedArtifact addArtifact(Contribution contribution, String artifact) {
        DeployedArtifact a1 = new DeployedArtifact(URI.create(artifact));
        contribution.addArtifact(a1);
        return a1;
    }
}
