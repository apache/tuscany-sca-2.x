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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * Test reading SCA XML assemblies.
 * 
 * @version $Rev: 711584 $ $Date: 2008-11-05 15:07:03 +0000 (Wed, 05 Nov 2008) $
 */
public class ReadWriteLocalCompositeTestCase extends TestCase {

    private XMLInputFactory inputFactory;
    private ExtensibleStAXArtifactProcessor staxProcessor;

    private static final String LOCAL_COMPOSITE_XML = "<?xml version='1.0' encoding='UTF-8'?>"+
    "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns1=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://localcalc\" name=\"LocalCalculator\" local=\"true\">"+
    "</composite>";
    
    @Override
    public void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        StAXAttributeProcessorExtensionPoint staxAttributeProcessors = extensionPoints.getExtensionPoint(StAXAttributeProcessorExtensionPoint.class);
        staxAttributeProcessors.addArtifactProcessor(new TestAttributeProcessor());
        
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance(), null);
    }

    @Override
    public void tearDown() throws Exception {
    	
    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("local.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite) staxProcessor.read(reader);
        assertNotNull(composite);
        assertTrue(composite.isLocal());
        is.close();
    }
    
    public void testWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("local.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite) staxProcessor.read(reader);
        assertNotNull(composite);
        assertTrue(composite.isLocal());
        is.close();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos);
        System.out.println(bos.toString());
        
        assertEquals(LOCAL_COMPOSITE_XML, bos.toString());
    }
}
