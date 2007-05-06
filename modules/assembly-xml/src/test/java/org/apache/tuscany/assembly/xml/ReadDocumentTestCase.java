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

import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessor;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class ReadDocumentTestCase extends TestCase {

    private DefaultURLArtifactProcessor documentProcessor;
    private DefaultArtifactResolver resolver; 

    public void setUp() throws Exception {
        AssemblyFactory factory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        InterfaceContractMapper mapper = new DefaultInterfaceContractMapper();
        
        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        documentProcessor = new DefaultURLArtifactProcessor(documentProcessors); 
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        DefaultStAXArtifactProcessor staxProcessor = new DefaultStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new CompositeProcessor(factory, policyFactory, mapper, staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(factory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(factory, policyFactory, staxProcessor));
        
        // Create document processors
        XMLInputFactory inputFactory = XMLInputFactory.newInstance(); 
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));

        resolver = new DefaultArtifactResolver(getClass().getClassLoader());
    }

    public void tearDown() throws Exception {
        documentProcessor = null;
        resolver = null;
    }

    public void testResolveConstrainingType() throws Exception {
        
        URL url = getClass().getResource("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = (ConstrainingType)documentProcessor.read(null, null, url);
        assertNotNull(constrainingType);
        resolver.add(constrainingType);

        url = getClass().getResource("TestAllCalculator.composite");
        Composite composite = (Composite)documentProcessor.read(null, null, url);
        assertNotNull(composite);
        
        documentProcessor.resolve(composite, resolver);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }

    public void testResolveComposite() throws Exception {
        URL url = getClass().getResource("Calculator.composite");
        Composite nestedComposite = (Composite)documentProcessor.read(null, null, url);
        assertNotNull(nestedComposite);
        resolver.add(nestedComposite);

        url = getClass().getResource("TestAllCalculator.composite");
        Composite composite = (Composite)documentProcessor.read(null, null, url);
        
        documentProcessor.resolve(composite, resolver);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
