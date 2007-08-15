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
package org.apace.tuscany.sca.binding.sca;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.CompositeModelResolver;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SCABindingTestCase {
	
    private XMLInputFactory inputFactory;
    private DefaultStAXArtifactProcessorExtensionPoint staxProcessors;
    private ExtensibleStAXArtifactProcessor staxProcessor; 
    private CompositeModelResolver resolver; 
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private PolicyFactory policyFactory;
    private InterfaceContractMapper mapper;

    @Before
    public void init() throws Exception { 
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(new DefaultModelFactoryExtensionPoint());
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        resolver = new CompositeModelResolver(null, null);
        assemblyFactory = new DefaultAssemblyFactory();
        scaBindingFactory = new SCABindingFactoryImpl();
        policyFactory = new DefaultPolicyFactory();
        mapper = new InterfaceContractMapperImpl();    	
    }
    
    @After
    public void destroy() throws Exception {
        inputFactory = null;
        staxProcessors = null;
        resolver = null;
        policyFactory = null;
        assemblyFactory = null;
        mapper = null;    	
    }
    
    @Test
    public void testSCABindingFactory() {
        SCABindingFactory factory = new SCABindingFactoryImpl();
        Assert.assertNotNull(factory.createSCABinding());
    }
    
    @Test 
    public void testBuildModel() {
    	try{
	        InputStream is = getClass().getResourceAsStream("/Calculator.composite");      
	        CompositeProcessor compositeReader = new CompositeProcessor(new ContributionFactoryImpl(), assemblyFactory, policyFactory, mapper, staxProcessor);
	        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
	        Composite composite = compositeReader.read(reader);
	        
	        is.close();
	        Assert.assertNotNull(composite);
	        
	        resolver.addModel(composite);
	       
	        compositeReader.resolve(composite, resolver);
	        
	        CompositeBuilderImpl compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, mapper, null);
	        compositeBuilder.build(composite);
	        
	        SCABinding referenceSCABinding = (SCABinding) composite.getComponents().get(0).getReferences().get(0).getBindings().get(0);
	        SCABinding serviceSCABinding   = (SCABinding) composite.getComponents().get(1).getServices().get(0).getBindings().get(0);
	        
	        Assert.assertNotNull(referenceSCABinding);
	        Assert.assertNotNull(serviceSCABinding);
    	} catch (Exception ex) {
    		Assert.fail(ex.getMessage());
    	}
    	
    }
}
