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

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test reading SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class ReadAllTestCase extends TestCase {
    private StAXArtifactProcessor<Object> staxProcessor;
    private XMLInputFactory inputFactory;

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        inputFactory = XMLInputFactory.newInstance();
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, null);
    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = (Composite)staxProcessor.read(inputFactory.createXMLStreamReader(is));
        assertNotNull(composite);
        assertEquals(composite.getName(), new QName("http://calc", "TestAllCalculator"));
        assertEquals(composite.getConstrainingType().getName(), new QName("http://calc", "CalculatorComponent"));
        assertTrue(composite.isLocal());
        assertFalse(composite.getAutowire() == Boolean.TRUE);
        assertEquals(((PolicySetAttachPoint)composite).getRequiredIntents().get(0).getName(), new QName("http://test",
                                                                                "confidentiality"));
        assertEquals(((PolicySetAttachPoint)composite).getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));

        Composite include = composite.getIncludes().get(0);
        assertEquals(include.getName(), new QName("http://calc", "TestAllDivide"));

        CompositeService calcCompositeService = (CompositeService)composite.getServices().get(0);
        assertEquals(calcCompositeService.getName(), "CalculatorService");
        assertTrue(calcCompositeService.getPromotedService().isUnresolved());
        assertEquals(calcCompositeService.getPromotedService().getName(),
                     "CalculatorService");
        assertEquals(calcCompositeService.getRequiredIntents().get(0).getName(),
                     new QName("http://test", "confidentiality"));
        assertEquals(calcCompositeService.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));
        // TODO test operations
        Callback calcServiceCallback = calcCompositeService.getCallback();
        assertNotNull(calcServiceCallback);
        assertEquals(calcServiceCallback.getRequiredIntents().get(0).getName(),
                     new QName("http://test", "confidentiality"));
        assertEquals(calcServiceCallback.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));
        // TODO test operations

        Component calcComponent = composite.getComponents().get(0);
        assertEquals(calcComponent.getName(), "CalculatorServiceComponent");
        assertEquals(calcComponent.getAutowire(), Boolean.FALSE);
        assertEquals(calcComponent.getConstrainingType().getName(), new QName("http://calc",
                                                                              "CalculatorComponent"));
        assertEquals(calcComponent.getRequiredIntents().get(0).getName(), new QName("http://test",
                                                                                    "confidentiality"));
        assertEquals(calcComponent.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));

        ComponentService calcComponentService = calcComponent.getServices().get(0);
        assertEquals(calcComponentService.getName(), "CalculatorService");
        assertEquals(calcComponentService.getRequiredIntents().get(0).getName(),
                     new QName("http://test", "confidentiality"));
        assertEquals(calcComponentService.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));
        // TODO test operations

        ComponentReference calcComponentReference = calcComponent.getReferences().get(0);
        assertEquals(calcComponentReference.getName(), "addService");
        assertEquals(calcComponentReference.getAutowire(), Boolean.FALSE);
        assertEquals(calcComponentReference.isWiredByImpl(), false);
        assertEquals(calcComponentReference.getRequiredIntents().get(0).getName(),
                     new QName("http://test", "confidentiality"));
        assertEquals(calcComponentReference.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));
        // TODO test operations

        Property property = calcComponent.getProperties().get(0);
        assertEquals(property.getName(), "round");
        Document doc = (Document) property.getValue();
        Element element = doc.getDocumentElement();
        String value = element.getTextContent();
        assertEquals(value, "true");
        assertEquals(property.getXSDType(), new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        assertEquals(property.isMany(), false);

        CompositeReference calcCompositeReference = (CompositeReference)composite.getReferences().get(0);
        assertEquals(calcCompositeReference.getName(), "MultiplyService");
        assertTrue(calcCompositeReference.getPromotedReferences().get(0).isUnresolved());
        assertEquals(calcCompositeReference.getPromotedReferences().get(0).getName(),
                     "CalculatorServiceComponent/multiplyService");
        assertEquals(calcCompositeReference.getRequiredIntents().get(0).getName(),
                     new QName("http://test", "confidentiality"));
        assertEquals(calcCompositeReference.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));
        // TODO test operations
        Callback calcCallback = calcCompositeReference.getCallback();
        assertEquals(calcCompositeReference.getRequiredIntents().get(0).getName(),
                     new QName("http://test", "confidentiality"));
        assertEquals(calcCompositeReference.getPolicySets().get(0).getName(), new QName("http://test", "SecureReliablePolicy"));
        assertNotNull(calcCallback);
        // TODO test operations

    }

}
