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

import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorFactoryImpl;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorImpl;

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
    
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private Monitor monitor;

    @Override
    protected void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = new DefaultMonitorFactoryImpl();  
        if (monitorFactory != null) {
        	monitor = monitorFactory.createMonitor();
        	utilities.addUtility(monitorFactory);
        }        
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, null);
    }

    public void testRead() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(VALID_XML));
        ContributionMetadata contribution = (ContributionMetadata)staxProcessor.read(reader);
        assertNotNull(contribution);
        assertEquals(2, contribution.getDeployables().size());
  }

    public void testReadInvalid() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(INVALID_XML));
        /*try {
            staxProcessor.read(reader);
            fail("InvalidException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }*/
        staxProcessor.read(reader);
        Problem problem = ((DefaultMonitorImpl)monitor).getLastLoggedProblem();           
        assertNotNull(problem);
        assertEquals("AttributeCompositeMissing", problem.getMessageId());
    }    

    public void testWrite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(VALID_XML));
        ContributionMetadata contribution = (ContributionMetadata)staxProcessor.read(reader);

        validateContribution(contribution);
        
        //write the contribution metadata contents
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);
        staxProcessor.write(contribution, writer);
        stringWriter.close();

        reader = inputFactory.createXMLStreamReader(new StringReader(stringWriter.toString()));
        contribution = (ContributionMetadata)staxProcessor.read(reader);
        
        validateContribution(contribution);
  }
    
  private void validateContribution(ContributionMetadata contribution) {
	  QName deployable;
	  
	  assertNotNull(contribution);
	  assertEquals(2, contribution.getDeployables().size());
	  deployable = new QName("http://ns", "Composite1");
	  assertEquals(deployable, contribution.getDeployables().get(0).getName());
	  deployable = new QName("http://ns", "Composite2");
	  assertEquals(deployable, contribution.getDeployables().get(1).getName());	  
  }
    
}
