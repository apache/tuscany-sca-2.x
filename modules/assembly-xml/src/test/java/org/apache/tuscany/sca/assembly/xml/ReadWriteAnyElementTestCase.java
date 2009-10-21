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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ReadWriteAnyElementTestCase {
    private static final String XML_RECURSIVE_EXTENDED_ELEMENT =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://temp\" name=\"RecursiveExtendedElement\">" +
         "<unknownElement>" +
           "<subUnknownElement1 attribute=\"anyAttribute\" />" +
           "<subUnknownElement2 />" +
         "</unknownElement>" +
        "</composite>";

    private static final String XML_UNKNOWN_IMPL =
        "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://temp\" name=\"aaaa\" autowire=\"false\">" +
         "<component name=\"unknownImpl\">" +
           "<implementation.unknown class=\"raymond\" />" +
           "<service name=\"service\">" +
             "<binding.ws />" +
           "</service>" +
         "</component>" +
        "</composite>";

    private static final String XML_UNKNOWN_IMPL_WITH_INVALID_ATTRIBUTE =
        "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://temp\" name=\"aaaa\" autowire=\"false\">" +
         "<component name=\"unknownImpl\">" +
           "<implementation.unknown class=\"raymond\" />" +
           "<service name=\"service\" requires=\"\">" +
             "<binding.ws />" +
           "</service>" +
         "</component>" +
        "</composite>";
    
    
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
    public void testReadWriteExtendedRecursiveElement() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(XML_RECURSIVE_EXTENDED_ELEMENT));
        ValidatingXMLInputFactory.setMonitor(reader, context.getMonitor());
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);
        reader.close();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos, context);

        // used for debug comparison
        // System.out.println(XML_RECURSIVE_EXTENDED_ELEMENT);
        // System.out.println(bos.toString());

        assertEquals(XML_RECURSIVE_EXTENDED_ELEMENT, bos.toString());
        bos.close();
    }

    @Test
    public void testReadWriteUnknwonImpl() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(XML_UNKNOWN_IMPL));
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);
        reader.close();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos, context);

        // used for debug comparison
        // System.out.println(XML_UNKNOWN_IMPL);
        // System.out.println(bos.toString());

        assertEquals(XML_UNKNOWN_IMPL, bos.toString());
        bos.close();
    }

    // @Test
    @Ignore()
    public void testReadWriteInvalidAttribute() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(XML_UNKNOWN_IMPL_WITH_INVALID_ATTRIBUTE));
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);
        reader.close();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos, context);

        // used for debug comparison
        // System.out.println(XML_UNKNOWN_IMPL);
        // System.out.println(bos.toString());

        assertEquals(XML_UNKNOWN_IMPL, bos.toString());
        bos.close();
    }
}