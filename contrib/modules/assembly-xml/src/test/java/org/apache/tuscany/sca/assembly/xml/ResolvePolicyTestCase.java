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

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 561254 $ $Date: 2007-07-31 13:16:27 +0530 (Tue, 31 Jul 2007) $
 */
public class ResolvePolicyTestCase extends TestCase {

    private URLArtifactProcessor<Object> documentProcessor;
    private ModelResolver resolver;  
    private URLArtifactProcessor<SCADefinitions> policyDefinitionsProcessor;

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        resolver = new DefaultModelResolver();
        
        URLArtifactProcessorExtensionPoint documentProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors, null);
        policyDefinitionsProcessor = documentProcessors.getProcessor(SCADefinitions.class);
        
        // Create StAX processors
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());
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
        
        url = getClass().getResource("test_definitions.xml");
        uri = URI.create("test_definitions.xml");
        SCADefinitions scaDefns = (SCADefinitions)policyDefinitionsProcessor.read(null, uri, url);
        assertNotNull(scaDefns);
        
        preResolvePolicyTests(composite);
        documentProcessor.resolve(scaDefns, resolver);
        documentProcessor.resolve(composite, resolver);
        postResolvePolicyTests(composite);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }
    
    private void preResolvePolicyTests(Composite composite) {
        assertNull(((PolicySetAttachPoint)composite).getRequiredIntents().get(0).getDescription());
        assertTrue(((PolicySetAttachPoint)composite).getPolicySets().get(0).getProvidedIntents().isEmpty());
        
        assertNull(composite.getServices().get(0).getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getServices().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNull(composite.getServices().get(0).getCallback().getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getServices().get(0).getCallback().getPolicySets().get(0).getProvidedIntents().isEmpty());
        
        assertNull(composite.getComponents().get(0).getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getComponents().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNull(composite.getComponents().get(0).getServices().get(0).getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getComponents().get(0).getServices().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNull(composite.getComponents().get(0).getReferences().get(0).getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getComponents().get(0).getReferences().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        
        assertNull(composite.getReferences().get(0).getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getReferences().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNull(composite.getReferences().get(0).getCallback().getRequiredIntents().get(0).getDescription());
        assertTrue(composite.getReferences().get(0).getCallback().getPolicySets().get(0).getProvidedIntents().isEmpty());
    }
    
    private void postResolvePolicyTests(Composite composite) {
        assertNotNull(((PolicySetAttachPoint)composite).getRequiredIntents().get(0).getDescription());
        assertFalse(((PolicySetAttachPoint)composite).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(((PolicySetAttachPoint)composite).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        
        assertNotNull(composite.getServices().get(0).getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getServices().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getServices().get(0).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        assertNotNull(composite.getServices().get(0).getCallback().getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getServices().get(0).getCallback().getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getServices().get(0).getCallback().getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        
        assertNotNull(composite.getComponents().get(0).getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getComponents().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getComponents().get(0).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        assertNotNull(composite.getComponents().get(0).getServices().get(0).getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getComponents().get(0).getServices().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getComponents().get(0).getServices().get(0).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        assertNotNull(composite.getComponents().get(0).getReferences().get(0).getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getComponents().get(0).getReferences().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getComponents().get(0).getReferences().get(0).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        
        assertNotNull(composite.getReferences().get(0).getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getReferences().get(0).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getReferences().get(0).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        assertNotNull(composite.getReferences().get(0).getCallback().getRequiredIntents().get(0).getDescription());
        assertFalse(composite.getReferences().get(0).getCallback().getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(composite.getReferences().get(0).getCallback().getPolicySets().get(0).getProvidedIntents().get(1).getDescription());
        
        
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
