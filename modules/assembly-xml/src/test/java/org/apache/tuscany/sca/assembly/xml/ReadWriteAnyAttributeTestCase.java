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

package org.apache.tuscany.sca.assembly.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.junit.Test;

/**
 * Test reading SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class ReadWriteAnyAttributeTestCase {

    private static final String XML = "<?xml version='1.0' encoding='UTF-8'?>"+
		 	 "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" " +
		 	            "targetNamespace=\"http://calc\" " +
		 	            "name=\"Calculator\">"+
 	 	 	    "<component name=\"AddServiceComponent\" xmlns:test=\"http://test\" test:customAttribute=\"customValue\">"+
 	 	 	      "<implementation.java class=\"calculator.AddServiceImpl\" />"+
 	 	 	    "</component>"+
 	 	 	  "</composite>";
    
    private XMLInputFactory inputFactory;
    private ExtensibleStAXArtifactProcessor staxProcessor;
    private ProcessorContext context;

    /**
     * Initialize the test environment
     * This takes care to register attribute processors when provided
     *
     * @param attributeProcessor
     * @throws Exception
     */
    private void init(StAXAttributeProcessor<?> attributeProcessor) throws Exception {
    	ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
    	context = new ProcessorContext(extensionPoints);
    	inputFactory = XMLInputFactory.newInstance();
    	StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

    	if(attributeProcessor != null) {
    		StAXAttributeProcessorExtensionPoint staxAttributeProcessors = extensionPoints.getExtensionPoint(StAXAttributeProcessorExtensionPoint.class);
    		staxAttributeProcessors.addArtifactProcessor(attributeProcessor);
    	}
    	
    	staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
    }

    /**
     * Read and Write a composite that has a extended attribute
     * and a particular attribute processor
     * @throws Exception
     */
    @Test
    public void testReadWriteCompositeWithAttributeProcessor() throws Exception {
    	init(new TestAttributeProcessor());

    	XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(XML));
    	Composite composite = (Composite) staxProcessor.read(reader, context);
    	assertNotNull(composite);
    	reader.close();

    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	staxProcessor.write(composite, bos, context);

    	// used for debug comparison
    	// System.out.println(XML);
    	// System.out.println(bos.toString());

    	assertEquals(XML, bos.toString());    	
    }

    /**
     * Read and Write a composite that has a extended attribute
     * but no particular processor for it
     * @throws Exception
     */
    @Test
    public void testDefaultReadWriteComposite() throws Exception {
    	init(null);

    	XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(XML));
    	Composite composite = (Composite) staxProcessor.read(reader, context);
    	assertNotNull(composite);
    	reader.close();

    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	staxProcessor.write(composite, bos, context);

    	// used for debug comparison
    	// System.out.println(XML);
    	// System.out.println(bos.toString());

    	assertEquals(XML, bos.toString());
    }
}