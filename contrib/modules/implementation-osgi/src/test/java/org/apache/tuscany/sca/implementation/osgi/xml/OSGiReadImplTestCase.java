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

package org.apache.tuscany.sca.implementation.osgi.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.impl.OSGiImplementationImpl;
import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestBundles;
import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestImpl;
import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestInterface;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * Test reading OSGi implementations.
 *
 * @version $Rev$ $Date$
 */
public class OSGiReadImplTestCase extends TestCase {

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private CompositeBuilder compositeBuilder;

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, null);
        
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, mapper, null);

        OSGiTestBundles.createBundle("target/test-classes/OSGiTestService.jar", OSGiTestInterface.class, OSGiTestImpl.class);

    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("osgitest.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader);
        assertNotNull(composite);

        compositeBuilder.build(composite);
    }

    public void testReadAndResolveComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("osgitest.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader);
        assertNotNull(composite);
        
        is = getClass().getClassLoader().getResourceAsStream("OSGiTestService.componentType");
        reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = (ComponentType)staxProcessor.read(reader);

        ModelResolver resolver = new TestModelResolver(getClass().getClassLoader());
        staxProcessor.resolve(componentType, resolver);
        resolver.addModel(componentType);
        
        staxProcessor.resolve(composite, resolver);

        compositeBuilder.build(composite);
    }

    public void testReadOSGiImplementation() throws Exception {

        String str = "<?xml version=\"1.0\" encoding=\"ASCII\"?>" +
                     "<implementation.osgi xmlns=\"http://tuscany.apache.org/xmlns/sca/1.0\" targetNamespace=\"http://osgi\" " +
                     "bundleSymbolicName=\"OSGiTestService\" " +
                     "bundleVersion=\"2.0.0\" " +
                     "imports=\"import1.jar import2.jar\"" +
                     "/>";
        ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());

        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        reader.next();

        OSGiImplementationImpl osgiImpl = (OSGiImplementationImpl)staxProcessor.read(reader);

        assertEquals(osgiImpl.getBundleSymbolicName(), "OSGiTestService");
        assertEquals(osgiImpl.getBundleVersion(), "2.0.0");
        assertTrue(osgiImpl.getImports().length == 2);
        assertEquals(osgiImpl.getImports()[0], "import1.jar");
        assertEquals(osgiImpl.getImports()[1], "import2.jar");
    }

}
