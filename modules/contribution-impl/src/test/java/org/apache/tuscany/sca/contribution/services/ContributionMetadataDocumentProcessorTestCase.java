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

package org.apache.tuscany.sca.contribution.services;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.impl.ContributionMetadataProcessor;

/**
 * @version $Rev$ $Date$
 */

public class ContributionMetadataDocumentProcessorTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<deployable composite=\"ns:Composite1\"/>"
            + "<deployable composite=\"ns:Composite2\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<deployable composite=\"ns:Composite1\"/>"
            + "<deployable/>"
            + "</contribution>";
    private XMLInputFactory xmlFactory;

    @Override
    protected void setUp() throws Exception {
        xmlFactory = XMLInputFactory.newInstance();
    }

    public void testLoad() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));

        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        ContributionFactory contributionFactory = new ContributionFactoryImpl();
        ContributionMetadataProcessor loader = 
            new ContributionMetadataProcessor(assemblyFactory, contributionFactory, null);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setModelResolver(new ModelResolverImpl(getClass().getClassLoader()));
        contribution = loader.read(reader);
        assertNotNull(contribution);
        assertEquals(2, contribution.getDeployables().size());
  }

    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        ContributionFactory contributionFactory = new ContributionFactoryImpl();
        ContributionMetadataProcessor loader = 
            new ContributionMetadataProcessor(assemblyFactory, contributionFactory, null);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setModelResolver(new ModelResolverImpl(getClass().getClassLoader()));
        try {
            loader.read(reader);
            fail("InvalidException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    
}
