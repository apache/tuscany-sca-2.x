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

package org.apache.tuscany.sca.binding.ws.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.policy.DefaultPolicyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.builder.impl.DefaultCompositeBuilder;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.xml.WebServiceBindingProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.DefaultWSDLInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.WSDLInterfaceIntrospector;

/**
 * Test reading WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultStAXArtifactProcessorExtensionPoint staxProcessors;
    ExtensibleStAXArtifactProcessor staxProcessor;
    private AssemblyFactory factory;
    private PolicyFactory policyFactory;
    private InterfaceContractMapper mapper;
    private WebServiceBindingFactory wsFactory;
    private WSDLInterfaceIntrospector introspector;
    private WSDLFactory wsdlFactory;

    public void setUp() throws Exception {
        factory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
        mapper = new DefaultInterfaceContractMapper();
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        wsFactory = new DefaultWebServiceBindingFactory();
        wsdlFactory = new DefaultWSDLFactory();
        
        introspector = new DefaultWSDLInterfaceIntrospector(wsdlFactory);

        WebServiceBindingProcessor wsdlProcessor = new WebServiceBindingProcessor(
                                                                                  factory, policyFactory, wsFactory,
                                                                                  wsdlFactory, introspector);
        staxProcessors.addArtifactProcessor(wsdlProcessor);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        staxProcessors = null;
        staxProcessor = null;
        policyFactory = null;
        factory = null;
        mapper = null;
    }

    public void testReadComponentType() throws Exception {
        ComponentTypeProcessor componentTypeProcessor = new ComponentTypeProcessor(factory, policyFactory, staxProcessor);
        InputStream is = getClass().getResourceAsStream("CalculatorImpl.componentType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = componentTypeProcessor.read(reader);
        assertNotNull(componentType);

        //new PrintUtil(System.out).print(componentType);
    }

    public void testReadComposite() throws Exception {
        CompositeProcessor compositeProcessor = new CompositeProcessor(factory, policyFactory, mapper, staxProcessor);
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeProcessor.read(reader);
        assertNotNull(composite);

        DefaultCompositeBuilder compositeUtil = new DefaultCompositeBuilder(factory, mapper, null);
        compositeUtil.build(composite);

        //new PrintUtil(System.out).print(composite);
    }

}
