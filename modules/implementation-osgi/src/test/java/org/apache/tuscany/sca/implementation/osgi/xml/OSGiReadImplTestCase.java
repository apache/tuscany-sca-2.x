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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test reading OSGi implementations.
 *
 * @version $Rev$ $Date$
 */
public class OSGiReadImplTestCase {

    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> staxProcessor;
    private static ProcessorContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        StAXArtifactProcessorExtensionPoint staxProcessors =
            new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);
    }

    @Test
    public void testReadComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("osgitest.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);
    }

    @Test
    public void testReadAndResolveComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("osgitest.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader, context);
        assertNotNull(composite);

        is = getClass().getClassLoader().getResourceAsStream("OSGI-INF/sca/bundle.componentType");
        reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = (ComponentType)staxProcessor.read(reader, context);

        assertEquals(1, componentType.getServices().size());
        Object prop1 = componentType.getServices().get(0).getExtensions().get(0);
        assertTrue(prop1 instanceof OSGiProperty);
        OSGiProperty osgiProp1 = (OSGiProperty)prop1;
        assertEquals("1", osgiProp1.getValue());
        assertEquals("prop1", osgiProp1.getName());

        assertEquals(4, componentType.getReferences().size());
        Object prop2 = componentType.getReferences().get(0).getExtensions().get(1);
        assertTrue(prop2 instanceof OSGiProperty);
        OSGiProperty osgiProp2 = (OSGiProperty)prop2;
        assertEquals("ABC", osgiProp2.getValue());
        assertEquals("prop2", osgiProp2.getName());

        ModelResolver resolver = new TestModelResolver(getClass().getClassLoader());
        staxProcessor.resolve(componentType, resolver, context);
        resolver.addModel(componentType, context);

        staxProcessor.resolve(composite, resolver, context);
    }

    @Test
    public void testReadOSGiImplementation() throws Exception {

        String str =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" + "<implementation.osgi xmlns=\"http://tuscany.apache.org/xmlns/sca/1.1\" targetNamespace=\"http://test\" "
                + "bundleSymbolicName=\"osgi.test\" "
                + "bundleVersion=\"1.0.0\" "
                + "/>";
        ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());

        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        reader.nextTag();

        OSGiImplementation osgiImpl = (OSGiImplementation)staxProcessor.read(reader, context);

        assertEquals(osgiImpl.getBundleSymbolicName(), "osgi.test");
        assertEquals(osgiImpl.getBundleVersion(), "1.0.0");

        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
        staxProcessor.write(osgiImpl, writer, context);
        writer.flush();
        Assert.assertTrue(sw.toString().contains("bundleSymbolicName=\"osgi.test\" bundleVersion=\"1.0.0\""));
    }

}
