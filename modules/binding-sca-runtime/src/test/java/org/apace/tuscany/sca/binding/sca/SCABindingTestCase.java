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
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.CompositeModelResolver;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * SCABindingTestCase
 *
 * @version $Rev$ $Date$
 */
public class SCABindingTestCase {

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private CompositeModelResolver resolver;
    private ExtensionPointRegistry extensionPoints;

    @Before
    public void init() throws Exception {
        extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();

        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);

        resolver = new CompositeModelResolver(null, null);
    }

    @Test
    public void testSCABindingFactory() {
        SCABindingFactory factory =
            extensionPoints.getExtensionPoint(FactoryExtensionPoint.class).getFactory(SCABindingFactory.class);

        Assert.assertNotNull(factory.createSCABinding());
        Assert.assertNotNull(factory.createDistributedSCABinding());
    }

    @Test
    @Ignore
    public void testBuildModel() {
        try {
            InputStream is = getClass().getResourceAsStream("/Calculator.composite");
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            ProcessorContext context = new ProcessorContext(extensionPoints);
            Composite composite = (Composite)staxProcessor.read(reader, context);

            is.close();
            Assert.assertNotNull(composite);

            resolver.addModel(composite, context);

            staxProcessor.resolve(composite, resolver, new ProcessorContext());

            SCABinding referenceSCABinding =
                (SCABinding)composite.getComponents().get(0).getReferences().get(0).getBindings().get(0);
            SCABinding serviceSCABinding =
                (SCABinding)composite.getComponents().get(1).getServices().get(0).getBindings().get(0);

            Assert.assertNotNull(referenceSCABinding);
            Assert.assertNotNull(serviceSCABinding);
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }

    }
}
