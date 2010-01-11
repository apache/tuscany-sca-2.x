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

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.monitor.Monitor;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompositeProcessorTestCase {
    private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    
    private static final String OASIS_COMPOSITE =
             "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " 
           + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" "
           + "xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" "
           + "targetNamespace=\"http://store\" "
           + "name=\"store\">"
           + "<component name=\"Catalog\">"
           + "  <implementation.java class=\"services.FruitsCatalogImpl\"/>"
           + "</component>"
           + "</composite>";

    private static final String OSOA_COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
      + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" "
      + "xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.0\" "
      + "targetNamespace=\"http://store\" "
      + "name=\"store\">"
      + "<component name=\"Catalog\">"
      + "  <implementation.java class=\"services.FruitsCatalogImpl\"/>"
      + "</component>"
      + "</composite>";
    
    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> staxProcessor;
    private static Monitor monitor;
    private static ProcessorContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        monitor = context.getMonitor();        
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
    }
    
    @Test
    public void testReadOASISSpecVersion() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OASIS_COMPOSITE));
        Object result = staxProcessor.read(reader, context);
        assertNotNull(result);
        if( result instanceof Composite) {
            Composite composite = (Composite) result;
            assertEquals(SCA11_NS, composite.getSpecVersion());
        }
        reader.close();
    } 

    @Test
    public void testReadOSOASpecVersion() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OSOA_COMPOSITE));
        Object result = staxProcessor.read(reader, context);
        assertNotNull(result);
        if( result instanceof Composite) {
            throw new Exception("Error, unsupported OSOA namespace being parsed as valid !");
        }
        reader.close();
    } 
}
