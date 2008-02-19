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

package org.apache.tuscany.sca.contribution.resource.impl;



import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resource.ResourceExport;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Test NamespaceExportProcessorTestCase
 *  
 * @version $Rev$ $Date$
 */
public class ResourceExportProcessorTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<export.resource uri=\"helloworld/HelloWorldService.componentType\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<export.resource/>"
            + "</contribution>";

    private XMLInputFactory xmlFactory;

    @Override
    protected void setUp() throws Exception {
        xmlFactory = XMLInputFactory.newInstance();
    }

    /**
     * Test loading a valid export element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoad() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));

        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        factories.addFactory(new ResourceImportExportFactoryImpl());
        ResourceExportProcessor exportProcessor = new ResourceExportProcessor(factories);
        ResourceExport resourceExport = exportProcessor.read(reader);
        
        assertEquals("helloworld/HelloWorldService.componentType", resourceExport.getURI());
    }

    /**
     * Test loading an INVALID export element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));

        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        factories.addFactory(new ResourceImportExportFactoryImpl());
        ResourceExportProcessor exportProcessor = new ResourceExportProcessor(factories);
        try {
            exportProcessor.read(reader);
            fail("readerException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    
}
