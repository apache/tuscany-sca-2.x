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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.DefaultMonitorFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test writing SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class WriteAllTestCase {
    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outputFactory;
    private static ExtensibleStAXArtifactProcessor staxProcessor;
    private static ModelResolver resolver; 
    private static CompositeBuilder compositeBuilder;
    private static URLArtifactProcessor<Definitions> policyDefinitionsProcessor;
    private static Monitor monitor;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, null);
        resolver = new DefaultModelResolver();
        
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        SCABindingFactory scaBindingFactory = new TestSCABindingFactoryImpl();
        modelFactories.addFactory(scaBindingFactory);
        compositeBuilder = extensionPoints.getExtensionPoint(BuilderExtensionPoint.class).getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");

        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);
        
        MonitorFactory monitorFactory = new DefaultMonitorFactory();
        monitor = monitorFactory.createMonitor();
        
        URLArtifactProcessorExtensionPoint documentProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        policyDefinitionsProcessor = documentProcessors.getProcessor(Definitions.class);
    }

    @Test
    public void testReadWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = staxProcessor.read(is, Composite.class);
        
        verifyComposite(composite);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos);
        bos.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        composite = staxProcessor.read(bis, Composite.class);
        
        verifyComposite(composite);
        
    }

    @Test
    public void testReadWireWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = staxProcessor.read(is, Composite.class);
        
        URL url = getClass().getResource("test_definitions.xml");
        URI uri = URI.create("test_definitions.xml");
        Definitions scaDefns = (Definitions)policyDefinitionsProcessor.read(null, uri, url);
        assertNotNull(scaDefns);
        policyDefinitionsProcessor.resolve(scaDefns, resolver);
        
        staxProcessor.resolve(composite, resolver);
        compositeBuilder.build(composite, null, monitor);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos);
    }
    
    @Test
    public void testReadWriteComponentType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorImpl.componentType");
        ComponentType componentType = staxProcessor.read(is, ComponentType.class);
        staxProcessor.resolve(componentType, resolver);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(componentType, bos);
    }

    @Test
    public void testReadWriteConstrainingType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = staxProcessor.read(is, ConstrainingType.class);
        staxProcessor.resolve(constrainingType, resolver);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(constrainingType, bos);
    }
    
    private void verifyComposite(Composite composite) {
        assertEquals(composite.getProperties().get(0).getName(),"prop1");
        assertEquals(composite.getProperties().get(0).isMany(), true);
        assertEquals(composite.getProperties().get(1).getName(),"prop2");
        assertEquals(composite.getProperties().get(1).isMustSupply(), true);
        assertEquals(composite.getProperties().get(0).getXSDType(), new QName("http://foo", "MyComplexType"));
        assertEquals(composite.getProperties().get(1).getXSDElement(), new QName("http://foo", "MyComplexPropertyValue1"));
    }

}
