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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.junit.Before;
import org.junit.Test;

/**
 * Test reading WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class WSDLTestCase {

    private ExtensibleURLArtifactProcessor documentProcessor;
    private ContributionFactory contributionFactory;
    private WSDLModelResolver wsdlResolver;
    private ModelResolver resolver;

    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        URLArtifactProcessorExtensionPoint documentProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);
        
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        Contribution contribution = contributionFactory.createContribution();
        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        resolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
        contribution.setModelResolver(resolver);
        wsdlResolver = new WSDLModelResolver(contribution, modelFactories);
    }

    @Test
    public void testReadWSDLDocument() throws Exception {
        URL url = getClass().getResource("example.wsdl");
        WSDLDefinition definition = documentProcessor.read(null, new URI("example.wsdl"), url, null, WSDLDefinition.class);
        assertNotNull(definition);
        assertNull(definition.getDefinition());
        assertEquals(definition.getNamespace(), "http://www.example.org");
    }

    @Test
    public void testReadWSDLImports() throws Exception {
        QName aBinding = new QName("http://helloworld", "HelloWorldSoapBinding");
        QName aPortType = new QName("http://helloworld", "HelloWorld");

        URL url = getClass().getResource("test1.wsdl");
        WSDLDefinition test1Defn = documentProcessor.read(null, new URI("test1.wsdl"), url, null, WSDLDefinition.class);
        assertNotNull(test1Defn);
        ProcessorContext context = new ProcessorContext();
        wsdlResolver.addModel(test1Defn, context);
        test1Defn = wsdlResolver.resolveModel(WSDLDefinition.class, test1Defn, context);
        //binding is a part of test1.wsdl
        assertNotNull(test1Defn.getDefinition().getBinding(aBinding));
        //porttype is part of test2.wsdl
        assertNotNull(test1Defn.getDefinition().getPortType(aPortType));
    }

    @Test
    public void testReadSameNamespaceWSDLDocument() throws Exception {
        QName aBinding = new QName("http://helloworld", "HelloWorldSoapBinding");
        QName aPortType = new QName("http://helloworld", "HelloWorld");

        ProcessorContext context = new ProcessorContext();
        URL url = getClass().getResource("test2.wsdl");
        WSDLDefinition test2Defn = documentProcessor.read(null, new URI("test2.wsdl"), url, null, WSDLDefinition.class);
        assertNotNull(test2Defn);
        wsdlResolver.addModel(test2Defn, context);
        test2Defn = wsdlResolver.resolveModel(WSDLDefinition.class, test2Defn, context);

        //bindings are a part of test1.wsdl so should not be found
        assertNull(test2Defn.getDefinition().getBinding(aBinding));
        assertNotNull(test2Defn.getDefinition().getPortType(aPortType));

        url = getClass().getResource("test1.wsdl");
        WSDLDefinition test1Defn = documentProcessor.read(null, new URI("test1.wsdl"), url, null, WSDLDefinition.class);
        assertNotNull(test1Defn);
        wsdlResolver.addModel(test1Defn, context);

        test1Defn = wsdlResolver.resolveModel(WSDLDefinition.class, test1Defn, context);

        assertNotNull(test1Defn.getDefinition().getPortType(aPortType));
        assertNotNull(test1Defn.getDefinition().getBinding(aBinding));
    }

}
