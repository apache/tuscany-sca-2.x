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
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.wsdl.Definition;

import junit.framework.TestCase;

import org.apache.tuscany.spi.deployer.ArtifactResolverRegistry;
import org.apache.tuscany.spi.deployer.ContributionProcessorRegistry;
import org.apache.tuscany.spi.model.Contribution;
import org.apache.tuscany.spi.model.DeployedArtifact;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;

/**
 * @version $Rev$ $Date$
 */
public class XSDContributionProcessorTestCase extends TestCase {
    private XSDContributionProcessor processor;

    protected void setUp() throws Exception {
        super.setUp();
        processor = new XSDContributionProcessor();
        ArtifactResolverRegistry registry = createMock(ArtifactResolverRegistry.class);
        URL url = getClass().getResource("ipo.xsd");
        expect(registry.resolve(isA(Contribution.class), isA(String.class), isA(String.class), (String)isNull())).andReturn(url)
            .anyTimes();
        processor.setArtifactResolverRegistry(registry);
        replay(registry);

        ContributionProcessorRegistry processorRegistry = createMock(ContributionProcessorRegistry.class);
        processorRegistry.processModel(isA(Contribution.class), isA(URI.class), isA(Definition.class));
        replay(processorRegistry);
        processor.setContributionProcessorRegistry(processorRegistry);
    }

    public void testLoad() throws Exception {
        URI uri = URI.create("sca://contribution/001");
        Contribution contribution = new Contribution(uri);

        URI a1 = URI.create("sca://contribution/001/test1.xsd");
        addArtifact(contribution, a1);
        URI a2 = URI.create("sca://contribution/001/ipo.xsd");
        addArtifact(contribution, a2);

        URL url = getClass().getResource("test1.xsd");
        processor.processContent(contribution, new URI("sca://contribution/001/test1.xsd"), url.openStream());
        DeployedArtifact da1 = contribution.getArtifact(a1);
        Map<String, Object> schemas = da1.getModelObjects(XmlSchema.class);
        assertEquals(1, schemas.size());
        assertTrue(schemas.containsKey("http://www.example.com/Customer"));
        XmlSchema schema = (XmlSchema) schemas.values().iterator().next();
        XmlSchemaObjectCollection includes = schema.getIncludes();
        assertEquals(1, includes.getCount());
        XmlSchemaImport imported = (XmlSchemaImport) includes.getItem(0);
        assertEquals("http://www.example.com/IPO", imported.getSchema().getTargetNamespace());
    }

    private DeployedArtifact addArtifact(Contribution contribution, URI artifact) {
        DeployedArtifact a1 = new DeployedArtifact(artifact);
        contribution.addArtifact(a1);
        return a1;
    }
}
