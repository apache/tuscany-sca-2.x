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

package org.apache.tuscany.sca.implementation.ejb.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementation;

/**
 * Test reading Node implementations.
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    private ExtensionPointRegistry registry;
    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private CompositeBuilder compositeBuilder;

    @Override
    public void setUp() throws Exception {
        registry = new DefaultExtensionPointRegistry();
        inputFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(XMLInputFactory.class);
        // StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(registry);
        
        compositeBuilder = registry.getExtensionPoint(BuilderExtensionPoint.class).getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");
    }

    public void testReadComposite() throws Exception {
        ProcessorContext context = new ProcessorContext(registry);
        InputStream is = getClass().getResourceAsStream("TestEJB.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite) staxProcessor.read(reader, context);
        assertNotNull(composite);

        BuilderContext builderContext = new BuilderContext(registry);
        compositeBuilder.build(composite, builderContext);
        
        assertTrue(((EJBImplementation) composite.getComponents().get(0).getImplementation()).getEJBLink().equals("module.jar#TestEJB"));
    }

}
