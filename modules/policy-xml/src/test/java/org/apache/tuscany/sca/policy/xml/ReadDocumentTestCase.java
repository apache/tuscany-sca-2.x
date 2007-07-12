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

package org.apache.tuscany.sca.policy.xml;

import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.SCADefinitions;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 551296 $ $Date: 2007-06-28 01:18:35 +0530 (Thu, 28 Jun 2007) $
 */
public class ReadDocumentTestCase extends TestCase {

    private TestModelResolver resolver; 
    private SCADefinitionsDocumentProcessor scaDefnDocProcessor = null;
    private SCADefinitionsProcessor scaDefnProcessor = null;
    

    public void setUp() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        
        scaDefnDocProcessor = new SCADefinitionsDocumentProcessor(staxProcessor, inputFactory);
        scaDefnProcessor = new SCADefinitionsProcessor(policyFactory, staxProcessor);
        staxProcessors.addArtifactProcessor(scaDefnProcessor);
        staxProcessors.addArtifactProcessor(new PolicyIntentProcessor(policyFactory));
        
        resolver = new TestModelResolver(getClass().getClassLoader());
    }

    public void tearDown() throws Exception {
        resolver = null;
        scaDefnDocProcessor = null;
        scaDefnProcessor = null;
    }

    public void testLoadSCADefinitions() throws Exception {
        
        URL url = getClass().getResource("definitions.xml");
        URI uri = URI.create("definitions.xml");
        SCADefinitions scaDefinitions = (SCADefinitions)scaDefnDocProcessor.read(null, uri, url);
        assertNotNull(scaDefinitions);
        
        resolver.addModel(scaDefinitions);
        scaDefnDocProcessor.resolve(scaDefinitions, resolver);
        
        /*assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);*/
    }

    /*public void testResolveComposite() throws Exception {
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
*/
}
