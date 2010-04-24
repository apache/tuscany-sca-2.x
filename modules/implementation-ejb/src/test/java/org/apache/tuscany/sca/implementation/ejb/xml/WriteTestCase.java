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

package org.apache.tuscany.sca.implementation.ejb.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 * Test reading/write WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class WriteTestCase extends TestCase {
    private ExtensionPointRegistry registry;
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;

    @Override
    public void setUp() throws Exception {
        registry = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        inputFactory = factories.getFactory(XMLInputFactory.class);
        outputFactory = factories.getFactory(XMLOutputFactory.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(registry);
    }

    public void testReadWriteComposite() throws Exception {
        ProcessorContext context = new ProcessorContext(registry);
        InputStream is = getClass().getResourceAsStream("TestEJB.composite");
        Composite composite = (Composite) staxProcessor.read(inputFactory.createXMLStreamReader(is), context);
        assertNotNull(composite);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, outputFactory.createXMLStreamWriter(bos), context);
        
        assertTrue(bos.toString().contains("module.jar#TestEJB"));

    }

}
