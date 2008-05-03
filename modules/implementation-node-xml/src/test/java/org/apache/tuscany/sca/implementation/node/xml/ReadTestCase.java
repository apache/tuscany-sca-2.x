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

package org.apache.tuscany.sca.implementation.node.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.implementation.node.impl.NodeImplementationFactoryImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.DefaultIntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Test reading Node implementations.
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
        ContributionFactory contributionFactory = new DefaultContributionFactory();
        factories.addFactory(contributionFactory);
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
        
        NodeImplementationFactory nodeFactory = new NodeImplementationFactoryImpl();
        factories.addFactory(nodeFactory);
        
        NodeImplementationProcessor nodeProcessor = new NodeImplementationProcessor(factories);
        staxProcessors.addArtifactProcessor(nodeProcessor);
        ConfiguredNodeImplementationProcessor configuredNodeProcessor = new ConfiguredNodeImplementationProcessor(factories);
        staxProcessors.addArtifactProcessor(configuredNodeProcessor);
    }

    @Override
    public void tearDown() throws Exception {
    }

    public void testReadComposite() throws Exception {
        StAXArtifactProcessor<Composite> compositeProcessor = staxProcessors.getProcessor(Composite.class);
        InputStream is = getClass().getResourceAsStream("TestNode.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeProcessor.read(reader);
        assertNotNull(composite);

        CompositeBuilderImpl compositeUtil = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, new DefaultIntentAttachPointTypeFactory(), mapper, null);
        compositeUtil.build(composite);
    }

}
