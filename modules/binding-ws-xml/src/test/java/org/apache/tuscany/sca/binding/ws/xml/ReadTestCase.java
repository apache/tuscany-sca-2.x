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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.policy.DefaultIntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Test reading WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    private XMLInputFactory inputFactory;
    private DefaultStAXArtifactProcessorExtensionPoint staxProcessors;
    private ExtensibleStAXArtifactProcessor staxProcessor;
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private PolicyFactory policyFactory;
    private InterfaceContractMapper mapper;

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        assemblyFactory = new DefaultAssemblyFactory();
        factories.addFactory(assemblyFactory);
        scaBindingFactory = new SCABindingFactoryImpl();
        factories.addFactory(scaBindingFactory);
        policyFactory = new DefaultPolicyFactory();
        factories.addFactory(policyFactory);
        mapper = new InterfaceContractMapperImpl();
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        
        WebServiceBindingFactory wsFactory = new DefaultWebServiceBindingFactory();
        factories.addFactory(wsFactory);
        WSDLFactory wsdlFactory = new DefaultWSDLFactory();
        factories.addFactory(wsdlFactory);
        
        WebServiceBindingProcessor wsdlProcessor = new WebServiceBindingProcessor(factories);
        staxProcessors.addArtifactProcessor(wsdlProcessor);
    }

    @Override
    public void tearDown() throws Exception {
    }

    public void testReadComponentType() throws Exception {
        StAXArtifactProcessor<ComponentType> componentTypeProcessor = staxProcessors.getProcessor(ComponentType.class);
        InputStream is = getClass().getResourceAsStream("CalculatorImpl.componentType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = componentTypeProcessor.read(reader);
        assertNotNull(componentType);

        //new PrintUtil(System.out).print(componentType);
    }

    public void testReadComposite() throws Exception {
        StAXArtifactProcessor<Composite> compositeProcessor = staxProcessors.getProcessor(Composite.class);
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeProcessor.read(reader);
        assertNotNull(composite);

        CompositeBuilderImpl compositeUtil = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, new DefaultIntentAttachPointTypeFactory(), mapper, null);
        compositeUtil.build(composite);

        //new PrintUtil(System.out).print(composite);
    }
    
    public void testReadPolicies() throws Exception {
        StAXArtifactProcessor<Composite> compositeProcessor = staxProcessors.getProcessor(Composite.class);
        InputStream is = getClass().getResourceAsStream("PoliciedCalculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeProcessor.read(reader);
        assertNotNull(composite);

        CompositeBuilderImpl compositeUtil = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, new DefaultIntentAttachPointTypeFactory(), mapper, null);
        compositeUtil.build(composite);

        assertEquals(((OperationsConfigurator)composite.getServices().get(0).getBindings().get(0))
            .getConfiguredOperations().get(0).getRequiredIntents().size(), 2);
        //new PrintUtil(System.out).print(composite);
    }

    /**
     * This test makes sure that an exception is thrown when a bad wsdlElement is present along with EndpointReference.
     *
     * Ref: Web Service Binding Specification v1.0 - Sec 2.1 - Lines 61 to 65.
     * When an EndpointReference is present along with the wsdlElement attribute on the parent element, the wsdlElement attribute value MUST
     * be of the 'Binding' form.
     */
    public void testReadBadWsdlElement() throws Exception {
        StAXArtifactProcessor<Composite> compositeProcessor = staxProcessors.getProcessor(Composite.class);
        InputStream is = getClass().getResourceAsStream("Calculator-bad-wsdlElement.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        try {
            Composite composite = compositeProcessor.read(reader);
            fail("ContributionReadException expected.");
        } catch(ContributionReadException e) {
            // Expected
            assertNotSame(-1, e.getMessage().indexOf("must use wsdl.binding when using wsa:EndpointReference"));
        }
    }
}
