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
package org.apache.tuscany.core.loader;

import java.net.URI;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderRefTestCase extends TestCase {
    private ComponentLoader loader;
    private final URI componentId = URI.create("sca://localhost/parent/");
    private DeploymentContext context;

    public void testLoadReferenceNoFragment() throws LoaderException, XMLStreamException {
        PojoComponentType<?, MockReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, MockReferenceDefinition, Property<?>>();
        MockReferenceDefinition reference = new MockReferenceDefinition();
        reference.setUri(URI.create("#reference"));
        type.add(reference);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> definition = new ComponentDefinition<JavaImplementation>(impl);
        definition.setUri(URI.create("component"));
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("reference");
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("target");
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.replay(reader);
        loader.loadReference(reader, definition, context);
        ReferenceTarget target = definition.getReferenceTargets().get("reference");
        assertEquals(1, target.getTargets().size());
        URI uri = target.getTargets().get(0);
        assertEquals(componentId.resolve("target"), uri);
        assertNull(uri.getFragment());
        EasyMock.verify(reader);
    }

    public void testLoadReferenceWithFragment() throws LoaderException, XMLStreamException {
        PojoComponentType<?, MockReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, MockReferenceDefinition, Property<?>>();
        MockReferenceDefinition reference = new MockReferenceDefinition();
        reference.setUri(URI.create("#reference"));
        type.add(reference);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> definition = new ComponentDefinition<JavaImplementation>(impl);
        definition.setUri(URI.create("component"));
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("reference");
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("target/fragment");
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.replay(reader);
        loader.loadReference(reader, definition, context);
        ReferenceTarget target = definition.getReferenceTargets().get("reference");
        assertEquals(1, target.getTargets().size());
        URI uri = target.getTargets().get(0);
        assertEquals(componentId.resolve("target#fragment"), uri);
        EasyMock.verify(reader);
    }

    public void testLoadReferenceWithMultipleTargetUris() throws LoaderException, XMLStreamException {
        PojoComponentType<?, MockReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, MockReferenceDefinition, Property<?>>();
        MockReferenceDefinition reference = new MockReferenceDefinition();
        reference.setUri(URI.create("#reference"));
        type.add(reference);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> definition = new ComponentDefinition<JavaImplementation>(impl);
        definition.setUri(URI.create("component"));
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("reference");
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("target1/fragment1 target2/fragment2");
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.replay(reader);
        loader.loadReference(reader, definition, context);
        ReferenceTarget target = definition.getReferenceTargets().get("reference");
        assertEquals(2, target.getTargets().size());
        URI uri1 = target.getTargets().get(0);
        assertEquals(componentId.resolve("target1#fragment1"), uri1);
        URI uri2 = target.getTargets().get(1);
        assertEquals(componentId.resolve("target2#fragment2"), uri2);
        EasyMock.verify(reader);
    }

    public void testLoadReferenceAutowire() throws LoaderException, XMLStreamException {
        PojoComponentType<?, MockReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, MockReferenceDefinition, Property<?>>();
        MockReferenceDefinition reference = new MockReferenceDefinition();
        reference.setUri(URI.create("#reference"));
        type.add(reference);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> definition = new ComponentDefinition<JavaImplementation>(impl);
        definition.setUri(URI.create("component"));
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("reference");
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("target/fragment");
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("true");
        EasyMock.replay(reader);
        loader.loadReference(reader, definition, context);
        ReferenceTarget target = definition.getReferenceTargets().get("reference");
        assertTrue(target.isAutowire());
        EasyMock.verify(reader);
    }



    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ComponentLoader(mockRegistry, null);
        Component parent = EasyMock.createNiceMock(Component.class);
        EasyMock.expect(parent.getUri()).andReturn(componentId).atLeastOnce();
        EasyMock.replay(parent);

        context = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(context.getComponentId()).andReturn(componentId);
        EasyMock.replay(context);
    }

    private class MockReferenceDefinition extends ReferenceDefinition {

    }
}
