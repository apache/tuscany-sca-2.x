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
package org.apache.tuscany.sca.binding.jms.format;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WireFormatWritingTestCase {

    public static final String NO_WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <response>" 
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "              </response>"            
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     

    public static final String REQUEST_WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <tuscany:wireFormat.jmsObject/>"
                + "              <response>" 
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "              </response>"            
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     

    public static final String INVALID_REQUEST_WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <response>" 
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "              </response>"            
                + "              <tuscany:wireFormat.jmsObject/>"
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     

    public static final String RESPONSE_WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <response>" 
                + "                  <tuscany:wireFormat.jmsBytes/>"            
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "              </response>"            
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     

    public static final String INVALID_RESPONSE_WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <response>" 
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "                  <tuscany:wireFormat.jmsBytes/>"            
                + "              </response>"            
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     

    public static final String BOTH_WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <tuscany:wireFormat.jmsObject/>"
                + "              <response>" 
                + "                  <tuscany:wireFormat.jmsBytes/>"            
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "              </response>"            
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     

    public static final String WIRE_FORMAT =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
                + " <component name=\"HelloWorldComponent\">"
                + "   <implementation.java class=\"services.HelloWorld\"/>"
                + "      <service name=\"HelloWorldService\">"
                + "          <binding.jms >"
                + "              <tuscany:wireFormat.jmsObject/>"
                + "              <response>" 
                + "                  <tuscany:wireFormat.jmsBytes/>"            
                + "                  <destination jndiName=\"responseConnectionElementName\"/>"            
                + "              </response>"            
                + "          </binding.jms>"
                + "      </service>"
                + " </component>"
                + "</composite>";     


    private ValidatingXMLInputFactory inputFactory;
    private ExtensibleStAXArtifactProcessor staxProcessor;
    private ProcessorContext context;

    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);

        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, XMLOutputFactory.newInstance());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNoWireFormat() throws Exception {
    	doit(NO_WIRE_FORMAT);
    }
    @Test
    public void testRequestWireFormat() throws Exception {
    	doit(REQUEST_WIRE_FORMAT);
    }
    @Test
    public void testInvalidRequestWireFormat() throws Exception {
    	try {
        	doit(INVALID_REQUEST_WIRE_FORMAT);
            fail();
    	} catch (ValidationException e) {
    		// expected
    	}
    }
    @Test
    public void testResponseWireFormat() throws Exception {
    	doit(RESPONSE_WIRE_FORMAT);
    }
    @Test
    public void testInvalidResponseWireFormat() throws Exception {
    	try {
        	doit(INVALID_RESPONSE_WIRE_FORMAT);
            fail();
    	} catch (ValidationException e) {
    		// expected
    	}
    }

    public void doit(String xml) throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
        ValidatingXMLInputFactory.setMonitor(reader, context.getMonitor());
        Composite composite = (Composite)staxProcessor.read(reader, context);
        context.getMonitor().analyzeProblems();
        assertNotNull(composite);
        reader.close();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos, context);
        bos.close();
        
        String writtenXML = bos.toString();
        System.out.println(writtenXML);
        
        reader = inputFactory.createXMLStreamReader(new StringReader(writtenXML));
        ValidatingXMLInputFactory.setMonitor(reader, context.getMonitor());
        composite = (Composite)staxProcessor.read(reader, context);
        context.getMonitor().analyzeProblems();
        assertNotNull(composite);
        reader.close();
    }
    
    
    
    private void verifyExtendedElementComposite(Composite composite) throws XMLStreamException {

//		assertEquals("RecursiveExtendedElement", composite.getName().getLocalPart());
//		assertEquals(1, composite.getExtensions().size());
//		Extension ext1 = (Extension) composite.getExtensions().get(0);
//		assertEquals("unknownElement", ext1.getQName().getLocalPart());
//		assertEquals("http://docs.oasis-open.org/ns/opencsa/sca/200912", ext1.getQName().getNamespaceURI());
//	
//		XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader((String)ext1.getValue()));				
//		reader.next();
//		assertEquals("unknownElement", reader.getLocalName());
//		reader.next();
//		assertEquals("subUnknownElement1", reader.getLocalName());
//		assertEquals(1, reader.getAttributeCount());
//		assertEquals("attribute", reader.getAttributeLocalName(0));
//		assertEquals("anyAttribute", reader.getAttributeValue(0));
//
//		reader.close();
//					
	}

}