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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.SCADefinitions;
import org.apache.tuscany.sca.policy.xml.PolicySetProcessor;
import org.apache.tuscany.sca.policy.xml.ProfileIntentProcessor;
import org.apache.tuscany.sca.policy.xml.QualifiedIntentProcessor;
import org.apache.tuscany.sca.policy.xml.SCADefinitionsDocumentProcessor;
import org.apache.tuscany.sca.policy.xml.SCADefinitionsProcessor;
import org.apache.tuscany.sca.policy.xml.SimpleIntentProcessor;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 561254 $ $Date: 2007-07-31 13:16:27 +0530 (Tue, 31 Jul 2007) $
 */
public class ResolvePolicyTestCase extends TestCase {

    private ExtensibleURLArtifactProcessor documentProcessor;
    private TestModelResolver resolver; 
    SCADefinitionsDocumentProcessor scaDefnDocProcessor;

    public void setUp() throws Exception {
        AssemblyFactory factory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        InterfaceContractMapper mapper = new InterfaceContractMapperImpl();
        resolver = new TestModelResolver();
        
        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors); 
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new CompositeProcessor(new ContributionFactoryImpl(), factory, policyFactory, mapper, staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(factory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(factory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new SCADefinitionsProcessor(policyFactory, staxProcessor, resolver));
        staxProcessors.addArtifactProcessor(new SimpleIntentProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ProfileIntentProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new QualifiedIntentProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new PolicySetProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new MockPolicyProcessor());
        
        // Create document processors
        XMLInputFactory inputFactory = XMLInputFactory.newInstance(); 
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));
        scaDefnDocProcessor = new SCADefinitionsDocumentProcessor(staxProcessor, inputFactory);
        documentProcessors.addArtifactProcessor(scaDefnDocProcessor);
    }

    public void tearDown() throws Exception {
        documentProcessor = null;
        resolver = null;
    }

    public void testResolveConstrainingType() throws Exception {
        
        URL url = getClass().getResource("CalculatorComponent.constrainingType");
        URI uri = URI.create("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = (ConstrainingType)documentProcessor.read(null, uri, url);
        assertNotNull(constrainingType);
        resolver.addModel(constrainingType);

        url = getClass().getResource("TestAllCalculator.composite");
        uri = URI.create("TestAllCalculator.constrainingType");
        Composite composite = (Composite)documentProcessor.read(null, uri, url);
        assertNotNull(composite);
        
        url = getClass().getResource("definitions.xml");
        uri = URI.create("definitions.xml");
        SCADefinitions scaDefns = (SCADefinitions)scaDefnDocProcessor.read(null, uri, url);
        assertNotNull(scaDefns);
        
        preResolvePolicyTests(composite);
        documentProcessor.resolve(scaDefns, resolver);
        documentProcessor.resolve(composite, resolver);
        postResolvePolicyTests(composite);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }
    
    private void preResolvePolicyTests(Composite composite) {
        assertNull(composite.getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getPolicySets().get(0).getProvidedIntents().isEmpty());
        
        assertNull(composite.getServices().get(0).getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getServices().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
    }
    
    private void postResolvePolicyTests(Composite composite) {
        assertNotNull(composite.getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        
        assertNotNull(composite.getServices().get(0).getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getServices().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getServices().get(0).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
    }

    public void testResolveComposite() throws Exception {
        URL url = getClass().getResource("Calculator.composite");
        URI uri = URI.create("Calculator.composite");
        Composite nestedComposite = (Composite)documentProcessor.read(null, uri, url);
        assertNotNull(nestedComposite);
        resolver.addModel(nestedComposite);

        url = getClass().getResource("TestAllCalculator.composite");
        uri = URI.create("TestAllCalculator.composite");
        Composite composite = (Composite)documentProcessor.read(null, uri, url);
        
        documentProcessor.resolve(composite, resolver);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
