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

package org.apache.tuscany.assembly.xml;

import java.io.InputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Callback;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.util.PrintUtil;
import org.apache.tuscany.assembly.xml.impl.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.impl.CompositeProcessor;
import org.apache.tuscany.assembly.xml.impl.ConstrainingTypeProcessor;
import org.apache.tuscany.services.spi.contribution.DefaultArtifactResolver;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test the usability of the assembly model API when loading SCDL
 * 
 * @version $Rev$ $Date$
 */
public class ReadAllTestCase extends TestCase {
    private DefaultStAXArtifactProcessorRegistry registry;

    public void setUp() throws Exception {
        registry = new DefaultStAXArtifactProcessorRegistry();
        registry.addArtifactProcessor(new CompositeProcessor(registry));
        registry.addArtifactProcessor(new ComponentTypeProcessor(registry));
        registry.addArtifactProcessor(new ConstrainingTypeProcessor(registry));
    }

    public void tearDown() throws Exception {
        registry = null;
    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = registry.read(is, Composite.class);
        assertNotNull(composite);
        assertEquals(composite.getName(), new QName("http://calc", "TestAllCalculator"));
        assertEquals(composite.getConstrainingType().getName(), new QName("http://calc", "CalculatorComponent"));
        assertTrue(composite.isLocal());
        assertFalse(composite.isAutowire());
        assertEquals(composite.getRequiredIntents().get(0).getName(), new QName("http://test/confidentiality",
                                                                                "confidentiality"));
        assertEquals(composite.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));

        Composite include = composite.getIncludes().get(0);
        assertEquals(include.getName(), new QName("http://calc", "TestAllDivide"));

        CompositeService calcCompositeService = (CompositeService)composite.getServices().get(0);
        assertEquals(calcCompositeService.getName(), "CalculatorService");
        assertTrue(calcCompositeService.getPromotedService().isUnresolved());
        assertEquals(calcCompositeService.getPromotedService().getName(),
                     "CalculatorServiceComponent/CalculatorService");
        assertEquals(calcCompositeService.getRequiredIntents().get(0).getName(),
                     new QName("http://test/confidentiality", "confidentiality"));
        assertEquals(calcCompositeService.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));
        // TODO test operations
        Callback calcServiceCallback = calcCompositeService.getCallback();
        assertNotNull(calcServiceCallback);
        assertEquals(calcServiceCallback.getRequiredIntents().get(0).getName(),
                     new QName("http://test/confidentiality", "confidentiality"));
        assertEquals(calcServiceCallback.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));
        // TODO test operations

        Component calcComponent = composite.getComponents().get(0);
        assertEquals(calcComponent.getName(), "CalculatorServiceComponent");
        assertEquals(calcComponent.isAutowire(), false);
        assertEquals(calcComponent.getConstrainingType().getName(), new QName("http://calc",
                                                                              "CalculatorComponent"));
        assertEquals(calcComponent.getRequiredIntents().get(0).getName(), new QName("http://test/confidentiality",
                                                                                    "confidentiality"));
        assertEquals(calcComponent.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));

        ComponentService calcComponentService = calcComponent.getServices().get(0);
        assertEquals(calcComponentService.getName(), "CalculatorService");
        assertEquals(calcComponentService.getRequiredIntents().get(0).getName(),
                     new QName("http://test/confidentiality", "confidentiality"));
        assertEquals(calcComponentService.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));
        // TODO test operations

        ComponentReference calcComponentReference = calcComponent.getReferences().get(0);
        assertEquals(calcComponentReference.getName(), "addService");
        assertEquals(calcComponentReference.isAutowire(), false);
        assertEquals(calcComponentReference.isWiredByImpl(), false);
        assertEquals(calcComponentReference.getMultiplicity(), Multiplicity.ONE_ONE);
        assertEquals(calcComponentReference.getRequiredIntents().get(0).getName(),
                     new QName("http://test/confidentiality", "confidentiality"));
        assertEquals(calcComponentReference.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));
        // TODO test operations

        Property property = calcComponent.getProperties().get(0);
        assertEquals(property.getName(), "round");
        Document doc = (Document) property.getDefaultValue();
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
                     new QName("http://test/confidentiality", "confidentiality"));
        assertEquals(calcCompositeReference.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));
        // TODO test operations
        Callback calcCallback = calcCompositeReference.getCallback();
        assertEquals(calcCompositeReference.getRequiredIntents().get(0).getName(),
                     new QName("http://test/confidentiality", "confidentiality"));
        assertEquals(calcCompositeReference.getPolicySets().get(0).getName(), new QName("http://test/secure", "secure"));
        assertNotNull(calcCallback);
        // TODO test operations

        new PrintUtil(System.out).print(composite);
    }

    public void testReadCompositeAndWireIt() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = registry.read(is, Composite.class);
        assertNotNull(composite);
        registry.resolve(composite, new DefaultArtifactResolver());
        registry.wire(composite);

        Component calcComponent = composite.getComponents().get(0);
        CompositeService calcCompositeService = (CompositeService)composite.getServices().get(0);
        assertEquals(calcComponent.getServices().get(0), calcCompositeService.getPromotedService());

        ComponentReference multiplyReference = calcComponent.getReferences().get(2);
        CompositeReference calcCompositeReference = (CompositeReference)composite.getReferences().get(0);
        assertEquals(multiplyReference, calcCompositeReference.getPromotedReferences().get(0));

        Component addComponent = composite.getComponents().get(1);
        ComponentReference addReference = calcComponent.getReferences().get(0);
        assertEquals(addReference.getTargets().get(0), addComponent.getServices().get(0));

        new PrintUtil(System.out).print(composite);
    }

}
