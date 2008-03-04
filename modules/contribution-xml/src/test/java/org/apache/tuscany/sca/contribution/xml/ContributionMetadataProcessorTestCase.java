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

package org.apache.tuscany.sca.contribution.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Test the contribution metadata processor.
 * 
 * @version $Rev$ $Date$
 */

public class ContributionMetadataProcessorTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<deployable composite=\"ns:Composite1\"/>"
            + "<deployable composite=\"ns:Composite2\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<deployable composite=\"ns:Composite1\"/>"
            + "<deployable/>"
            + "</contribution>";
    
    private XMLInputFactory xmlInputFactory;
    private XMLOutputFactory xmlOutputFactory;

    @Override
    protected void setUp() throws Exception {
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlOutputFactory = XMLOutputFactory.newInstance();
        xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    }

    public void testRead() throws Exception {
        XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new StringReader(VALID_XML));

        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        ContributionFactory contributionFactory = new DefaultContributionFactory();
        ContributionMetadataProcessor processor = 
            new ContributionMetadataProcessor(assemblyFactory, contributionFactory, null);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setModelResolver(new TestModelResolver(contribution, null));
        contribution = processor.read(reader);
        assertNotNull(contribution);
        assertEquals(2, contribution.getDeployables().size());
  }

    public void testReadInvalid() throws Exception {
        XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new StringReader(INVALID_XML));
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        ContributionFactory contributionFactory = new DefaultContributionFactory();
        ContributionMetadataProcessor processor = 
            new ContributionMetadataProcessor(assemblyFactory, contributionFactory, null);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setModelResolver(new TestModelResolver(contribution, null));
        try {
            processor.read(reader);
            fail("InvalidException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    

    public void testWrite() throws Exception {
        XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new StringReader(VALID_XML));

        //read the original contribution metadata file
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        ContributionFactory contributionFactory = new DefaultContributionFactory();
        ContributionMetadataProcessor processor = 
            new ContributionMetadataProcessor(assemblyFactory, contributionFactory, null);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setModelResolver(new TestModelResolver(contribution, null));
        contribution = processor.read(reader);

        validateContribution(contribution);
        
        //write the contribution metadata contents
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(stringWriter);
        processor.write(contribution, writer);
        stringWriter.close();

        reader = xmlInputFactory.createXMLStreamReader(new StringReader(stringWriter.toString()));
        contribution = processor.read(reader);
        
        validateContribution(contribution);
  }
    
  public void validateContribution(Contribution contribution) {
	  QName deployable;
	  
	  assertNotNull(contribution);
	  assertEquals(2, contribution.getDeployables().size());
	  deployable = new QName("http://ns", "Composite1");
	  assertEquals(deployable, contribution.getDeployables().get(0).getName());
	  deployable = new QName("http://ns", "Composite2");
	  assertEquals(deployable, contribution.getDeployables().get(1).getName());	  
  }
    
}
