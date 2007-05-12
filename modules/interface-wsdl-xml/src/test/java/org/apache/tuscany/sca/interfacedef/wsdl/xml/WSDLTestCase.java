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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLDocumentProcessor;

/**
 * Test reading WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class WSDLTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultURLArtifactProcessorExtensionPoint documentProcessors;
    ExtensibleURLArtifactProcessor documentProcessor;

    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);

        WSDLDocumentProcessor wsdlProcessor = new WSDLDocumentProcessor(new DefaultWSDLFactory(), null);
        documentProcessors.addArtifactProcessor(wsdlProcessor);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        documentProcessors = null;
    }

    public void testReadWSDLDocument() throws Exception {
        URL url = getClass().getResource("example.wsdl");
        WSDLDefinition definition = documentProcessor.read(null, new URI("example.wsdl"), url, WSDLDefinition.class);
        assertNotNull(definition);
        assertNotNull(definition.getDefinition());
        assertEquals(definition.getNamespace(), "http://www.example.org");
    }

}
