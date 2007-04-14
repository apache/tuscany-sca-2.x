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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorRegistry;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;

/**
 * Test writing SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class WriteAllTestCase extends TestCase {
    private DefaultStAXArtifactProcessorRegistry registry;

    public void setUp() throws Exception {
        registry = new DefaultStAXArtifactProcessorRegistry();
        registry.addArtifactProcessor(new CompositeProcessor(registry));
        registry.addArtifactProcessor(new ComponentTypeProcessor(registry));
        registry.addArtifactProcessor(new ConstrainingTypeProcessor(registry));
    }

    public void tearDown() throws Exception {
        registry = null;
    }

    public void testReadWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = registry.read(is, Composite.class);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        registry.write(composite, bos);
    }

    public void testReadWireWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("TestAllCalculator.composite");
        Composite composite = registry.read(is, Composite.class);
        registry.resolve(composite, new DefaultArtifactResolver());
        registry.wire(composite);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        registry.write(composite, bos);
    }
    
    public void testReadWriteComponentType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorImpl.componentType");
        ComponentType componentType = registry.read(is, ComponentType.class);
        registry.resolve(componentType, new DefaultArtifactResolver());
        registry.wire(componentType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        registry.write(componentType, bos);
    }

    public void testReadWriteConstrainingType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorComponent.constrainingType");
        ConstrainingType constrainingType = registry.read(is, ConstrainingType.class);
        registry.resolve(constrainingType, new DefaultArtifactResolver());
        registry.wire(constrainingType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        registry.write(constrainingType, bos);
    }

}
