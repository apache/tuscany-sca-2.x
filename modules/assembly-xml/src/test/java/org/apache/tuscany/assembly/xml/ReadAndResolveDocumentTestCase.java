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
import org.apache.tuscany.assembly.xml.impl.ComponentTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.impl.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.impl.CompositeDocumentProcessor;
import org.apache.tuscany.assembly.xml.impl.CompositeProcessor;
import org.apache.tuscany.assembly.xml.impl.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.impl.ConstrainingTypeProcessor;
import org.apache.tuscany.services.spi.contribution.DefaultArtifactResolver;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.DefaultURLArtifactProcessorRegistry;

/**
 * Test the resolving SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class ReadAndResolveDocumentTestCase extends TestCase {

    private DefaultURLArtifactProcessorRegistry registry;

    public void setUp() throws Exception {
        registry = new DefaultURLArtifactProcessorRegistry();
        
        // Create Stax processors
        DefaultStAXArtifactProcessorRegistry staxRegistry = new DefaultStAXArtifactProcessorRegistry();
        staxRegistry.addArtifactProcessor(new CompositeProcessor(staxRegistry));
        staxRegistry.addArtifactProcessor(new ComponentTypeProcessor(staxRegistry));
        staxRegistry.addArtifactProcessor(new ConstrainingTypeProcessor(staxRegistry));
        
        // Create document processors
        registry.addArtifactProcessor(new CompositeDocumentProcessor(staxRegistry));
        registry.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxRegistry));
        registry.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxRegistry));
    }

    public void tearDown() throws Exception {
        registry = null;
    }

    public void testResolveConstrainingType() throws Exception {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();
        
        URL url = getClass().getClassLoader().getResource("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = (ConstrainingType)registry.read(url);
        assertNotNull(constrainingType);
        resolver.put(constrainingType, constrainingType);

        url = getClass().getClassLoader().getResource("TestAllCalculator.composite");
        Composite composite = (Composite)registry.read(url);
        assertNotNull(composite);
        
        registry.resolve(composite, resolver);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }

    public void testResolveComposite() throws Exception {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();
        
        URL url = getClass().getClassLoader().getResource("Calculator.composite");
        Composite nestedComposite = (Composite)registry.read(url);
        assertNotNull(nestedComposite);
        resolver.put(nestedComposite, nestedComposite);

        url = getClass().getClassLoader().getResource("TestAllCalculator.composite");
        Composite composite = (Composite)registry.read(url);
        
        registry.resolve(composite, resolver);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
