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

package org.apache.tuscany.sca.implementation.java.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test reading Java implementations.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase {

    private static XMLInputFactory inputFactory;
    private static StAXArtifactProcessor<Object> staxProcessor;
    private static URLArtifactProcessor<Definitions> policyDefinitionsProcessor;
    private static CompositeBuilder compositeBuilder;
    private static Monitor monitor;
    private static ProcessorContext context;
    private static BuilderContext builderContext;
    
    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        builderContext = new BuilderContext(extensionPoints);
        inputFactory = XMLInputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);

        compositeBuilder = extensionPoints.getExtensionPoint(BuilderExtensionPoint.class).getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");
        
        URLArtifactProcessorExtensionPoint documentProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class); 
        policyDefinitionsProcessor = documentProcessors.getProcessor(Definitions.class);
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
    }

    @Test
    public void testReadComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);

        // remove dependency on builder
        //compositeBuilder.build(composite, builderContext);

    }

    @Ignore("To be fixed")
    @Test
    public void testPolicyIntents() throws Exception {
        ModelResolver resolver = new TestModelResolver(getClass().getClassLoader());
        
        URL url = getClass().getResource("definitions.xml");
        URI uri = URI.create("definitions.xml");
        Definitions scaDefns = policyDefinitionsProcessor.read(null, uri, url, context);
                
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);
        
        staxProcessor.resolve(scaDefns, resolver, context);
        staxProcessor.resolve(composite, resolver, context);

        builderContext.setDefinitions(scaDefns);
        compositeBuilder.build(composite, builderContext);
        
        //intents are computed and aggregate intents from ancestor elements
        assertEquals(((PolicySubject)composite.getComponents().get(0)).getRequiredIntents().size(), 3);
        assertEquals(((PolicySubject)composite.getComponents().get(5)).getRequiredIntents().size(), 3);
        
        //assertEquals(((OperationsConfigurator)composite.getComponents().get(0)).getConfiguredOperations().isEmpty(), true);
        //assertEquals(((OperationsConfigurator)composite.getComponents().get(5)).getConfiguredOperations().isEmpty(), false);
        
        
        //test for proper aggregation of policy intents on implementation elements
        for ( Intent intent : ((PolicySubject)composite.getComponents().get(0).getImplementation()).getRequiredIntents() ) {
            String intentName = intent.getName().getLocalPart();
            if ( !(intentName.equals("tuscanyIntent_1") || intentName.equals("tuscanyIntent_2") ||
                intentName.equals("tuscanyIntent_3")) ) {
                fail();
            }
        }
        
        for ( Intent intent : ((PolicySubject)composite.getComponents().get(5)).getRequiredIntents() ) {
            String intentName = intent.getName().getLocalPart();
            if ( !(intentName.equals("tuscanyIntent_1") || intentName.equals("tuscanyIntent_4") ||
                intentName.equals("tuscanyIntent_5")) ) {
                fail();
            }
        }

        /*
        //test for proper aggregation of policy intents and policysets on operations of implementation
        OperationsConfigurator opConf = (OperationsConfigurator)composite.getComponents().get(5);
        assertEquals(opConf.getConfiguredOperations().get(0).getRequiredIntents().size(), 4);
        for ( Intent intent :  opConf.getConfiguredOperations().get(0).getRequiredIntents()) {
            String intentName = intent.getName().getLocalPart();
            if ( !(intentName.equals("tuscanyIntent_1") || intentName.equals("tuscanyIntent_4") ||
                intentName.equals("tuscanyIntent_5") || intentName.equals("tuscanyIntent_6") ) ) {
                fail();
            }
        }
        
        opConf = (OperationsConfigurator)composite.getComponents().get(6);
        assertEquals(opConf.getConfiguredOperations().get(0).getRequiredIntents().size(), 3);
        for ( Intent intent :  opConf.getConfiguredOperations().get(0).getRequiredIntents()) {
            String intentName = intent.getName().getLocalPart();
            if ( !(intentName.equals("tuscanyIntent_1") || intentName.equals("tuscanyIntent_4") ||
                intentName.equals("tuscanyIntent_6.qualified2") ) ) {
                fail();
            }
        }
        */
    }
    
    @Test
    @Ignore("Broken for now in 2.0 bringup")
    public void testPolicySets() throws Exception {
        ModelResolver resolver = new TestModelResolver(getClass().getClassLoader());
        
        URL url = getClass().getResource("definitions_with_policysets.xml");
        URI uri = URI.create("definitions_with_policysets.xml");
        Definitions policyDefinitions = policyDefinitionsProcessor.read(null, uri, url, context);
                
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);
        
        for ( Component component : composite.getComponents() ) {
            for ( PolicySet policySet : policyDefinitions.getPolicySets() ) {
                component.getPolicySets().add(policySet);
            }
        }
        
        staxProcessor.resolve(policyDefinitions, resolver, context);
        staxProcessor.resolve(composite, resolver, context);

        builderContext.setDefinitions(policyDefinitions);
        compositeBuilder.build(composite, builderContext);
        
        //test for determination of policysets for implementation
        assertEquals(((PolicySubject)composite.getComponents().get(0)).getPolicySets().size(), 1);
        for ( PolicySet policySet : ((PolicySubject)composite.getComponents().get(0).getImplementation()).getPolicySets() ) {
            String policySetName = policySet.getName().getLocalPart();
            if ( !(policySetName.equals("tuscanyPolicySet_1")) ) {
                fail();
            }
        }
        
        assertEquals(((PolicySubject)composite.getComponents().get(5)).getPolicySets().size(), 2);
        for ( PolicySet policySet : ((PolicySubject)composite.getComponents().get(5).getImplementation()).getPolicySets() ) {
            String policySetName = policySet.getName().getLocalPart();
            if ( !(policySetName.equals("tuscanyPolicySet_1") || policySetName.equals("tuscanyPolicySet_2")) ) {
                fail();
            }
        }

        /*
        //test for computation of policysets on operations of implementation
        OperationsConfigurator opConf = (OperationsConfigurator)composite.getComponents().get(5);
        assertEquals(opConf.getConfiguredOperations().get(0).getPolicySets().size(), 3);
        for ( PolicySet policySet : opConf.getConfiguredOperations().get(0).getPolicySets() ) {
            String policySetName = policySet.getName().getLocalPart();
            if ( !(policySetName.equals("tuscanyPolicySet_1") || policySetName.equals("tuscanyPolicySet_2")
                    || policySetName.equals("tuscanyPolicySet_3")) ) {
                fail();
            }
        }
        
        opConf = (OperationsConfigurator)composite.getComponents().get(6);
        assertEquals(opConf.getConfiguredOperations().get(0).getPolicySets().size(), 4);
        for ( PolicySet policySet : opConf.getConfiguredOperations().get(0).getPolicySets() ) {
            String policySetName = policySet.getName().getLocalPart();
            if ( !(policySetName.equals("tuscanyPolicySet_1") || policySetName.equals("tuscanyPolicySet_2")
                    || policySetName.equals("tuscanyPolicySet_3")
                    || policySetName.equals("tuscanyPolicySet_4")) ) {
                fail();
            }
        }
        */
        
        //new PrintUtil(System.out).print(composite);
    }

}
