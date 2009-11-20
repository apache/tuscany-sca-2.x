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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test resolving SCA XML assemblies.
 * 
 * @version $Rev$ $Date$
 */
public class ResolveTestCase {

    private static XMLInputFactory inputFactory;
    private static StAXArtifactProcessorExtensionPoint staxProcessors;
    private static ModelResolver resolver; 
    private static ProcessorContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        resolver = new DefaultModelResolver();
    }

    @Test
    public void testResolveComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        StAXArtifactProcessor<Composite> compositeReader = staxProcessors.getProcessor(Composite.class);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite nestedComposite = compositeReader.read(reader, context);
        is.close();
        assertNotNull(nestedComposite);
        resolver.addModel(nestedComposite, context);

        is = getClass().getResourceAsStream("TestAllCalculator.composite");
        reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeReader.read(reader, context);
        is.close();
        
        compositeReader.resolve(composite, resolver, context);
        
        assertEquals(composite.getComponents().get(2).getImplementation(), nestedComposite);
    }

}
