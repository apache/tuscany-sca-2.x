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

package org.apache.tuscany.sca.assembly.xml.osoa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
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
import org.apache.tuscany.sca.policy.PolicySubject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test reading SCA XML assembly documents.
 *
 * @version $Rev$ $Date$
 */
public class BuildPolicyTestCase {
    private static URLArtifactProcessor<Object> documentProcessor;
    private static URLArtifactProcessor<Definitions> policyDefinitionsProcessor;
    private static ModelResolver resolver;
    private static CompositeBuilder compositeBuilder;
    private static Composite composite;
    private static Monitor monitor;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        SCABindingFactory scaBindingFactory = new TestSCABindingFactoryImpl();
        modelFactories.addFactory(scaBindingFactory);
        compositeBuilder = extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class).getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");

        List<Definitions> policyDefinitions = new ArrayList<Definitions>();
        resolver = new DefaultModelResolver();

        MonitorFactory monitorFactory = new DefaultMonitorFactory();
        monitor = monitorFactory.createMonitor();

        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);

        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint(extensionPoints);
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors, null);
        policyDefinitionsProcessor = documentProcessors.getProcessor(Definitions.class);

        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());

        URL url = BuildPolicyTestCase.class.getResource("CalculatorComponent.constrainingType");
        URI uri = URI.create("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = (ConstrainingType)documentProcessor.read(null, uri, url);
        assertNotNull(constrainingType);
        resolver.addModel(constrainingType);

        url = BuildPolicyTestCase.class.getResource("TestAllPolicyCalculator.composite");
        uri = URI.create("TestAllCalculator.composite");
        composite = (Composite)documentProcessor.read(null, uri, url);
        assertNotNull(composite);

        url = BuildPolicyTestCase.class.getResource("another_test_definitions.xml");
        uri = URI.create("another_test_definitions.xml");
        Definitions definitions = (Definitions)policyDefinitionsProcessor.read(null, uri, url);
        assertNotNull(definitions);
        policyDefinitions.add(definitions);

        documentProcessor.resolve(definitions, resolver);
        documentProcessor.resolve(composite, resolver);

        compositeBuilder.build(composite, null, monitor);
    }

    @Test
    @Ignore("The inheritance will be calculated differently in OASIS SCA")
    public void testPolicyIntentInheritance() throws Exception {
        String namespaceUri = "http://test";

        PolicySubject policiedComposite = (PolicySubject)composite;
        assertEquals(policiedComposite.getRequiredIntents().size(), 1);
        assertEquals(policiedComposite.getRequiredIntents().get(0).getName(), new QName(namespaceUri, "tuscanyIntent_1"));

        //1 defined for composite, 2 defined for the service, 1 defined and 3 inherited for the promoted service (4)
        assertEquals(composite.getServices().get(0).getRequiredIntents().size(), 7);
        //1 from the operation defined in this service and 2 from the operation defined in the promoted service
        assertEquals(composite.getServices().get(0).getRequiredIntents().get(3).getName(), new QName(namespaceUri, "tuscanyIntent_3"));
        //bindings will have only 2 intents since duplications will be cut out
        assertEquals(((PolicySubject)composite.getServices().get(0).getBindings().get(0)).getRequiredIntents().size(), 3);

        assertEquals(composite.getReferences().get(0).getRequiredIntents().size(), 5);
        assertEquals(composite.getReferences().get(0).getRequiredIntents().get(1).getName(), new QName(namespaceUri, "tuscanyIntent_1"));
        assertEquals(((PolicySubject)composite.getReferences().get(0).getBindings().get(0)).getRequiredIntents().size(), 3);

        assertEquals(composite.getComponents().get(0).getRequiredIntents().size(), 3);
        assertEquals(composite.getComponents().get(0).getRequiredIntents().get(2).getName(), new QName(namespaceUri, "tuscanyIntent_1"));
        assertEquals(composite.getComponents().get(0).getServices().get(0).getRequiredIntents().size(), 4);
        assertEquals(composite.getComponents().get(0).getServices().get(0).getCallback().getRequiredIntents().size(), 4);
        assertEquals(composite.getComponents().get(0).getReferences().get(0).getRequiredIntents().size(), 5);
    }
}
