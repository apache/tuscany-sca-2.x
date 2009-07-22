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

package org.apache.tuscany.sca.interfacedef.java.xml;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test reading Java interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase {

    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> staxProcessor;
    private static CompositeBuilder compositeBuilder;
    private static Monitor monitor;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, null);
        
        compositeBuilder = extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class).getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
    }

    @Test
    public void testReadComponentType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorImpl.componentType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = (ComponentType)staxProcessor.read(reader);
        assertNotNull(componentType);
    }

    @Test
    public void testReadConstrainingType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorComponent.constrainingType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ConstrainingType constrainingType = (ConstrainingType)staxProcessor.read(reader);
        assertNotNull(constrainingType);
    }

    @Test
    @Ignore
    public void testReadComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader);
        assertNotNull(composite);

        compositeBuilder.build(composite, null, monitor);
    }

}
