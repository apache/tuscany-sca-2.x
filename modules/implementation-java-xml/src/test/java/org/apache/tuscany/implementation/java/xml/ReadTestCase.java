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

package org.apache.tuscany.implementation.java.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorRegistry;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;

/**
 * Test reading Java implementations.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultStAXArtifactProcessorRegistry registry;
    
    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        registry = new DefaultStAXArtifactProcessorRegistry();
        
        CompositeProcessor compositeProcessor = new CompositeProcessor(registry);
        registry.addArtifactProcessor(compositeProcessor);

        JavaImplementationProcessor javaProcessor = new JavaImplementationProcessor();
        registry.addArtifactProcessor(javaProcessor);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        registry = null;
    }

    public void testReadComposite() throws Exception {
        CompositeProcessor compositeProcessor = new CompositeProcessor(registry);
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeProcessor.read(reader);
        assertNotNull(composite);

        new CompositeUtil(composite).configure(null);

        //new PrintUtil(System.out).print(composite);
    }

    public void testReadAndResolveComposite() throws Exception {
        CompositeProcessor compositeProcessor = new CompositeProcessor(registry);
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeProcessor.read(reader);
        assertNotNull(composite);
        
        ArtifactResolver resolver = new DefaultArtifactResolver();
        registry.resolve(composite, resolver);

        new CompositeUtil(composite).configure(null);

        //new PrintUtil(System.out).print(composite);
    }

}
