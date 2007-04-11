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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;

/**
 * Test writing Java implementations.
 * 
 * @version $Rev$ $Date$
 */
public class WriteTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultStAXArtifactProcessorRegistry registry;
    
    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        registry = new DefaultStAXArtifactProcessorRegistry();

        registry.addArtifactProcessor(new CompositeProcessor(registry));
        registry.addArtifactProcessor(new ComponentTypeProcessor(registry));
        registry.addArtifactProcessor(new ConstrainingTypeProcessor(registry));

        JavaImplementationProcessor javaProcessor = new JavaImplementationProcessor();
        registry.addArtifactProcessor(javaProcessor);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        registry = null;
    }

    public void testReadWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        Composite composite = registry.read(is, Composite.class);
        assertNotNull(composite);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        registry.write(composite, bos);
    }

}
