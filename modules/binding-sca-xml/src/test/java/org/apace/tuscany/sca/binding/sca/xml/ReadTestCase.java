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

package org.apace.tuscany.sca.binding.sca.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * Test reading WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private CompositeBuilder compositeBuilder;

    @Override
    public void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();

        inputFactory = XMLInputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, null);

        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class); 
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, mapper, null);
        
    }

    public void testReadComponentType() throws Exception {
        InputStream is = getClass().getResourceAsStream("/CalculatorServiceImpl.componentType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = (ComponentType)staxProcessor.read(reader);
        assertNotNull(componentType);
        
        SCABinding referenceSCABinding = (SCABinding) componentType.getReferences().get(0).getBindings().get(0);
        assertNotNull(referenceSCABinding);
        
        SCABinding serviceSCABinding   = (SCABinding) componentType.getServices().get(0).getBindings().get(0);
        assertNotNull(serviceSCABinding);     

        //new PrintUtil(System.out).print(componentType);
    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("/Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader);
        assertNotNull(composite);

        compositeBuilder.build(composite);
        
        SCABinding referenceSCABinding = (SCABinding) composite.getComponents().get(0).getReferences().get(0).getBindings().get(0);
        SCABinding serviceSCABinding   = (SCABinding) composite.getComponents().get(1).getServices().get(0).getBindings().get(0);
        
        Assert.assertNotNull(referenceSCABinding);
        Assert.assertNotNull(serviceSCABinding);        
    }

}
