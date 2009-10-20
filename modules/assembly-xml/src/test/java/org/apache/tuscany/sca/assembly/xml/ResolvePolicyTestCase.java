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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test reading SCA XML assembly documents.
 *
 * @version $Rev$ $Date$
 */
public class ResolvePolicyTestCase {

    private static URLArtifactProcessor<Object> documentProcessor;
    private static ModelResolver resolver;
    private static URLArtifactProcessor<Definitions> policyDefinitionsProcessor;
    private static ProcessorContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        resolver = new DefaultModelResolver();

        URLArtifactProcessorExtensionPoint documentProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);
        policyDefinitionsProcessor = documentProcessors.getProcessor(Definitions.class);

        // Create StAX processors
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());
    }

    private void preResolvePolicyTests(Composite composite) {
        assertNull(((PolicySubject)composite).getRequiredIntents().get(0).getDescription());
        assertTrue(((PolicySubject)composite).getPolicySets().get(0).getProvidedIntents().isEmpty());

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
        assertNotNull(((PolicySubject)composite).getRequiredIntents().get(0).getDescription());
        assertFalse(((PolicySubject)composite).getPolicySets().get(0).getProvidedIntents().isEmpty());
        assertNotNull(((PolicySubject)composite).getPolicySets().get(0).getProvidedIntents().get(1).getDescription());

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

    @Test
    public void testResolveComposite() throws Exception {
        URL url = getClass().getResource("Calculator.composite");
        URI uri = URI.create("Calculator.composite");
        Composite nestedComposite = (Composite)documentProcessor.read(null, uri, url, context);
        assertNotNull(nestedComposite);
        resolver.addModel(nestedComposite, context);

        url = getClass().getResource("TestAllCalculator.composite");
        uri = URI.create("TestAllCalculator.composite");
        Composite composite = (Composite)documentProcessor.read(null, uri, url, context);

        documentProcessor.resolve(composite, resolver, context);

        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
