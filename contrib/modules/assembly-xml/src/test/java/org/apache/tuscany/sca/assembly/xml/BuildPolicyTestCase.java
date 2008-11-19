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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorFactoryImpl;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 561254 $ $Date: 2007-07-31 13:16:27 +0530 (Tue, 31 Jul 2007) $
 */
public class BuildPolicyTestCase extends TestCase { 
    private URLArtifactProcessor<Object> documentProcessor;
    private URLArtifactProcessor<SCADefinitions> policyDefinitionsProcessor;
    private ModelResolver resolver; 
    private CompositeBuilder compositeBuilder;
    private Composite composite;
    private Monitor monitor;

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        SCABindingFactory scaBindingFactory = new TestSCABindingFactoryImpl();
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        List<SCADefinitions> policyDefinitions = new ArrayList<SCADefinitions>();
        resolver = new DefaultModelResolver();
        
        MonitorFactory monitorFactory = new DefaultMonitorFactoryImpl();
        monitor = monitorFactory.createMonitor();
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);
        
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, mapper, monitor);
        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint(extensionPoints);
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors, null);
        policyDefinitionsProcessor = documentProcessors.getProcessor(SCADefinitions.class);
        
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());
        
        URL url = getClass().getResource("CalculatorComponent.constrainingType");
        URI uri = URI.create("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = (ConstrainingType)documentProcessor.read(null, uri, url);
        assertNotNull(constrainingType);
        resolver.addModel(constrainingType);

        url = getClass().getResource("TestAllPolicyCalculator.composite");
        uri = URI.create("TestAllCalculator.constrainingType");
        composite = (Composite)documentProcessor.read(null, uri, url);
        assertNotNull(composite);
        
        url = getClass().getResource("another_test_definitions.xml");
        uri = URI.create("another_test_definitions.xml");
        SCADefinitions definitions = (SCADefinitions)policyDefinitionsProcessor.read(null, uri, url);
        assertNotNull(definitions);
        policyDefinitions.add(definitions);
        
        documentProcessor.resolve(definitions, resolver);
        documentProcessor.resolve(composite, resolver);
        
        compositeBuilder.build(composite);
    }

    public void testPolicyIntentInheritance() throws Exception {
        String namespaceUri = "http://test";
        
        IntentAttachPoint policiedComposite = (IntentAttachPoint)composite;
        assertEquals(policiedComposite.getRequiredIntents().size(), 1);
        assertEquals(policiedComposite.getRequiredIntents().get(0).getName(), new QName(namespaceUri, "tuscanyIntent_1"));
        
        //1 defined for composite, 2 defined for the service, 1 defined and 3 inherited for the promoted service (4)
        assertEquals(composite.getServices().get(0).getRequiredIntents().size(), 7);
        //1 from the operation defined in this service and 2 from the operation defined in the promoted service 
        assertEquals(composite.getServices().get(0).getConfiguredOperations().get(0).getRequiredIntents().size(), 5);
        assertEquals(composite.getServices().get(0).getRequiredIntents().get(3).getName(), new QName(namespaceUri, "tuscanyIntent_3"));
        //bindings will have only 2 intents since duplications will be cut out
        assertEquals(((IntentAttachPoint)composite.getServices().get(0).getBindings().get(0)).getRequiredIntents().size(), 3);
        assertEquals(((OperationsConfigurator)composite.getServices().get(0).getBindings().get(0)).getConfiguredOperations().size(), 1);
        assertEquals(((OperationsConfigurator)composite.getServices().get(0).getBindings().get(0)).getConfiguredOperations().get(0).getRequiredIntents().size(), 5);
        
        assertEquals(composite.getReferences().get(0).getRequiredIntents().size(), 5);
        assertEquals(composite.getReferences().get(0).getConfiguredOperations().size(), 1);
        assertEquals(composite.getReferences().get(0).getConfiguredOperations().get(0).getRequiredIntents().size(), 4);
        assertEquals(composite.getReferences().get(0).getRequiredIntents().get(1).getName(), new QName(namespaceUri, "tuscanyIntent_1"));
        assertEquals(((IntentAttachPoint)composite.getReferences().get(0).getBindings().get(0)).getRequiredIntents().size(), 3);

        assertEquals(composite.getComponents().get(0).getRequiredIntents().size(), 3);
        assertEquals(composite.getComponents().get(0).getRequiredIntents().get(2).getName(), new QName(namespaceUri, "tuscanyIntent_1"));
        assertEquals(composite.getComponents().get(0).getServices().get(0).getRequiredIntents().size(), 4);
        assertEquals(composite.getComponents().get(0).getServices().get(0).getCallback().getRequiredIntents().size(), 4);
        assertEquals(composite.getComponents().get(0).getServices().get(0).getConfiguredOperations().get(0).getRequiredIntents().size(), 5);
        assertEquals(composite.getComponents().get(0).getReferences().get(0).getRequiredIntents().size(), 5);
        assertEquals(composite.getComponents().get(0).getReferences().get(0).getConfiguredOperations().get(0).getRequiredIntents().size(), 5);
    }
}
