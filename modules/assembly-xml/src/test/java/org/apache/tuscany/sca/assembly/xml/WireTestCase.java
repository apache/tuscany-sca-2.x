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

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.builder.impl.DefaultCompositeBuilder;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Test the wiring of SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class WireTestCase extends TestCase {

    private XMLInputFactory inputFactory;
    private DefaultStAXArtifactProcessorExtensionPoint staxProcessors;
    private ExtensibleStAXArtifactProcessor staxProcessor; 
    private DefaultModelResolver resolver; 
    private AssemblyFactory factory;
    private PolicyFactory policyFactory;
    private InterfaceContractMapper mapper;

    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        resolver = new DefaultModelResolver(getClass().getClassLoader());
        factory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
        mapper = new DefaultInterfaceContractMapper();
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        staxProcessors = null;
        resolver = null;
        policyFactory = null;
        factory = null;
        mapper = null;
    }

    public void testResolveConstrainingType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorComponent.constrainingType");
        ConstrainingTypeProcessor constrainingTypeReader = new ConstrainingTypeProcessor(factory, policyFactory, staxProcessor);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ConstrainingType constrainingType = constrainingTypeReader.read(reader);
        is.close();
        assertNotNull(constrainingType);
        resolver.addModel(constrainingType);

        is = getClass().getResourceAsStream("TestAllCalculator.composite");
        CompositeProcessor compositeReader = new CompositeProcessor(factory, policyFactory, mapper, staxProcessor);
        reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeReader.read(reader);
        is.close();
        assertNotNull(composite);
        
        compositeReader.resolve(composite, resolver);
        DefaultCompositeBuilder compositeUtil = new DefaultCompositeBuilder(factory, mapper, null);
        compositeUtil.build(composite);
        
        assertEquals(composite.getConstrainingType(), constrainingType);
        assertEquals(composite.getComponents().get(0).getConstrainingType(), constrainingType);
    }

    public void testResolveComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        CompositeProcessor compositeReader = new CompositeProcessor(factory, policyFactory, mapper, staxProcessor);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite nestedComposite = compositeReader.read(reader);
        is.close();
        assertNotNull(nestedComposite);
        resolver.addModel(nestedComposite);

        is = getClass().getResourceAsStream("TestAllCalculator.composite");
        compositeReader = new CompositeProcessor(factory, policyFactory, mapper, staxProcessor);
        reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeReader.read(reader);
        is.close();
        
        compositeReader.resolve(composite, resolver);
        DefaultCompositeBuilder compositeUtil = new DefaultCompositeBuilder(factory, mapper, null);
        compositeUtil.build(composite);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
