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

package org.apache.tuscany.interfacedef.wsdl.xml;

import java.net.URL;

import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.contribution.service.processor.DefaultURLArtifactProcessorRegistry;
import org.apache.tuscany.interfacedef.wsdl.WSDLDefinition;

/**
 * Test reading WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class WSDLTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultURLArtifactProcessorRegistry registry;

    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        registry = new DefaultURLArtifactProcessorRegistry();

        WSDLDocumentProcessor wsdlProcessor = new WSDLDocumentProcessor();
        registry.addArtifactProcessor(wsdlProcessor);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        registry = null;
    }

    public void testReadWSDLDocument() throws Exception {
        URL url = getClass().getResource("example.wsdl");
        WSDLDefinition definition = registry.read(url, WSDLDefinition.class);
        assertNotNull(definition);
        assertNotNull(definition.getDefinition());
        assertEquals(definition.getNamespace(), "http://www.example.org");
    }

}
