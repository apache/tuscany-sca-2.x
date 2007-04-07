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

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.xml.impl.CompositeProcessor;
import org.apache.tuscany.assembly.xml.impl.ConstrainingTypeProcessor;
import org.apache.tuscany.services.spi.contribution.DefaultArtifactResolver;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;

/**
 * Test resolving SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class ResolveTestCase extends TestCase {

    private XMLInputFactory inputFactory;
    private DefaultStAXArtifactProcessorRegistry registry;

    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        registry = new DefaultStAXArtifactProcessorRegistry();
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        registry = null;
    }

    public void testResolveConstrainingType() throws Exception {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();
        
        InputStream is = getClass().getResourceAsStream("CalculatorComponent.constrainingType");
        ConstrainingTypeProcessor constrainingTypeReader = new ConstrainingTypeProcessor(registry);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ConstrainingType constrainingType = constrainingTypeReader.read(reader);
        is.close();
        assertNotNull(constrainingType);
        resolver.put(constrainingType, constrainingType);

        is = getClass().getResourceAsStream("TestAllCalculator.composite");
        CompositeProcessor compositeReader = new CompositeProcessor(registry);
        reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeReader.read(reader);
        is.close();
        assertNotNull(composite);
        
        compositeReader.resolve(composite, resolver);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }

    public void testResolveComposite() throws Exception {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();
        
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        CompositeProcessor compositeReader = new CompositeProcessor(registry);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite nestedComposite = compositeReader.read(reader);
        is.close();
        assertNotNull(nestedComposite);
        resolver.put(nestedComposite, nestedComposite);

        is = getClass().getResourceAsStream("TestAllCalculator.composite");
        compositeReader = new CompositeProcessor(registry);
        reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeReader.read(reader);
        is.close();
        
        compositeReader.resolve(composite, resolver);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
