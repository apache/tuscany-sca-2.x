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

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;

/**
 * Test writing Java implementations.
 * 
 * @version $Rev$ $Date$
 */
public class WriteTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultStAXArtifactProcessorExtensionPoint staxProcessors;
    private AssemblyFactory factory;
    private PolicyFactory policyFactory;
    private InterfaceContractMapper mapper;
    
    public void setUp() throws Exception {
        factory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
        mapper = new DefaultInterfaceContractMapper();
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();

        staxProcessors.addExtension(new CompositeProcessor(factory, policyFactory, mapper, staxProcessors));
        staxProcessors.addExtension(new ComponentTypeProcessor(factory, policyFactory, staxProcessors));
        staxProcessors.addExtension(new ConstrainingTypeProcessor(factory, policyFactory, staxProcessors));

        JavaImplementationProcessor javaProcessor = new JavaImplementationProcessor();
        staxProcessors.addExtension(javaProcessor);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        staxProcessors = null;
        policyFactory = null;
        factory = null;
        mapper = null;
    }

    public void testReadWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        Composite composite = staxProcessors.read(is, Composite.class);
        assertNotNull(composite);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessors.write(composite, bos);
    }

}
