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

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class ReadDocumentTestCase extends TestCase {

    private DefaultURLArtifactProcessorExtensionPoint documentProcessors;

    public void setUp() throws Exception {
        documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        staxProcessors.addExtension(new CompositeProcessor(staxProcessors));
        staxProcessors.addExtension(new ComponentTypeProcessor(staxProcessors));
        staxProcessors.addExtension(new ConstrainingTypeProcessor(staxProcessors));
        
        // Create document processors
        documentProcessors.addExtension(new CompositeDocumentProcessor(staxProcessors));
        documentProcessors.addExtension(new ComponentTypeDocumentProcessor(staxProcessors));
        documentProcessors.addExtension(new ConstrainingTypeDocumentProcessor(staxProcessors));
    }

    public void tearDown() throws Exception {
        documentProcessors = null;
    }

    public void testResolveConstrainingType() throws Exception {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();
        
        URL url = getClass().getResource("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = (ConstrainingType)documentProcessors.read(url);
        assertNotNull(constrainingType);
        resolver.add(constrainingType);

        url = getClass().getResource("TestAllCalculator.composite");
        Composite composite = (Composite)documentProcessors.read(url);
        assertNotNull(composite);
        
        documentProcessors.resolve(composite, resolver);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }

    public void testResolveComposite() throws Exception {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();
        
        URL url = getClass().getResource("Calculator.composite");
        Composite nestedComposite = (Composite)documentProcessors.read(url);
        assertNotNull(nestedComposite);
        resolver.add(nestedComposite);

        url = getClass().getResource("TestAllCalculator.composite");
        Composite composite = (Composite)documentProcessors.read(url);
        
        documentProcessors.resolve(composite, resolver);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
