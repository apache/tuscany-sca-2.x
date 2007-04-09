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
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderTestCase extends TestCase {
    private static final QName COMPONENT = new QName(SCA_NS, "component");
    private static final String COMPONENT_NAME = "sca://localhost/parent/";
    private static final String NAME = "testComponent";
    private JavaImplementation impl;

    private XMLStreamReader mockReader;
    private LoaderRegistry mockRegistry;
    private PropertyObjectFactory mockPropertyFactory;
    private ComponentLoader loader;
    private Component parent;
    private DeploymentContext ctx;
    private URI componentId;

    public void testEmptyComponent() throws LoaderException, XMLStreamException {
        EasyMock.expect(mockReader.getName()).andReturn(COMPONENT).atLeastOnce();
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.isA(String.class)))
            .andReturn(NAME);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("initLevel")))
            .andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("autowire")))
            .andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue(null, "runtimeId")).andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue(EasyMock.isA(String.class), EasyMock.isA(String.class)))
            .andReturn(null);
        EasyMock.expect(mockReader.nextTag()).andReturn(0);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(mockReader);
        mockRegistry.loadComponentType(
            EasyMock.isA(Implementation.class),
            EasyMock.isA(DeploymentContext.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            @SuppressWarnings("unchecked")
            public Object answer() throws Throwable {
                Implementation impl = (Implementation) EasyMock.getCurrentArguments()[0];
                impl.setComponentType(new PojoComponentType());
                return impl;
            }
        });

        EasyMock.expect(mockRegistry.load(
            (ModelObject) isNull(),
            EasyMock.eq(mockReader),
            EasyMock.isA(DeploymentContext.class))).andReturn(impl);
        EasyMock.replay(mockRegistry);
        ComponentDefinition component = loader.load(null, mockReader, ctx);
        assertEquals(COMPONENT_NAME + NAME, component.getUri().toString());
        assertNull(component.getInitLevel());
    }

    public void testAutowire() throws LoaderException, XMLStreamException {
        EasyMock.expect(mockReader.getName()).andReturn(COMPONENT).atLeastOnce();
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.isA(String.class)))
            .andReturn(NAME);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("initLevel")))
            .andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("autowire")))
            .andReturn("true");
        EasyMock.expect(mockReader.getAttributeValue(null, "runtimeId")).andReturn(null);
        EasyMock.expect(mockReader.nextTag()).andReturn(0);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(mockReader);

        mockRegistry.loadComponentType(
            EasyMock.isA(Implementation.class),
            EasyMock.isA(DeploymentContext.class));

        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            @SuppressWarnings("unchecked")
            public Object answer() throws Throwable {
                Implementation impl = (Implementation) EasyMock.getCurrentArguments()[0];
                impl.setComponentType(new PojoComponentType());
                return impl;
            }
        });
        EasyMock.expect(mockRegistry.load(
            (ModelObject) isNull(),
            EasyMock.eq(mockReader),
            EasyMock.isA(DeploymentContext.class))).andReturn(impl);
        EasyMock.replay(mockRegistry);

        ComponentDefinition component = loader.load(null, mockReader, ctx);

        assertTrue(component.getAutowire());
    }

    public void testInitValue20() throws LoaderException, XMLStreamException {
        EasyMock.expect(mockReader.getName()).andReturn(COMPONENT).atLeastOnce();
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.isA(String.class)))
            .andReturn(NAME);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("initLevel")))
            .andReturn("20");
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("autowire")))
            .andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue(null, "runtimeId")).andReturn(null);
        EasyMock.expect(mockReader.nextTag()).andReturn(0);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(mockReader);

        mockRegistry.loadComponentType(
            EasyMock.isA(Implementation.class),
            EasyMock.isA(DeploymentContext.class));

        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            @SuppressWarnings("unchecked")
            public Object answer() throws Throwable {
                Implementation impl = (Implementation) EasyMock.getCurrentArguments()[0];
                impl.setComponentType(new PojoComponentType());
                return impl;
            }
        });
        EasyMock.expect(mockRegistry.load(
            (ModelObject) isNull(),
            EasyMock.eq(mockReader),
            EasyMock.isA(DeploymentContext.class))).andReturn(impl);
        EasyMock.replay(mockRegistry);

        ComponentDefinition component = loader.load(null, mockReader, ctx);
        assertEquals(COMPONENT_NAME + NAME, component.getUri().toString());
        assertEquals(Integer.valueOf(20), component.getInitLevel());
    }

    public void testLoadPropertyWithSource() throws LoaderException, XMLStreamException {
        PojoComponentType<?, ?, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        Property property = new Property();
        property.setName("name");
        type.add(property);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> defn = new ComponentDefinition<JavaImplementation>(impl);
        XMLStreamReader reader = createMock(XMLStreamReader.class);
        expect(reader.getAttributeValue(null, "name")).andReturn("name");
        expect(reader.getAttributeValue(null, "source")).andReturn("$source");
        expect(reader.getAttributeValue(null, "file")).andReturn(null);
        expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        replay(reader);
        loader.loadProperty(reader, defn, null);
        assertEquals("$source", defn.getPropertyValues().get("name").getSource());
        EasyMock.verify(reader);
    }

    public void testUnrecognizedElement() throws LoaderException, XMLStreamException {
        EasyMock.expect(mockReader.getName()).andReturn(COMPONENT);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.isA(String.class)))
            .andReturn(NAME);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("initLevel")))
            .andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("autowire")))
            .andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue(null, "runtimeId")).andReturn(null);
        EasyMock.expect(mockReader.getAttributeValue(EasyMock.isA(String.class), EasyMock.isA(String.class)))
            .andReturn(null);
        EasyMock.expect(mockReader.nextTag()).andReturn(0);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(mockReader.getName()).andReturn(new QName("foo", "bar"));
        EasyMock.replay(mockReader);
        mockRegistry.loadComponentType(
            EasyMock.isA(Implementation.class),
            EasyMock.isA(DeploymentContext.class));

        EasyMock.expect(mockRegistry.load(
            (ModelObject) isNull(),
            EasyMock.eq(mockReader),
            EasyMock.isA(DeploymentContext.class))).andReturn(impl);
        EasyMock.replay(mockRegistry);
        try {
            loader.load(null, mockReader, ctx);
            fail();
        } catch (UnrecognizedElementException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        componentId = URI.create(COMPONENT_NAME);
        impl = new JavaImplementation();
        mockReader = EasyMock.createMock(XMLStreamReader.class);
        mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        mockPropertyFactory = EasyMock.createMock(PropertyObjectFactory.class);
        loader = new ComponentLoader(mockRegistry, mockPropertyFactory);
        parent = EasyMock.createNiceMock(Component.class);
        URI uri = URI.create("foo");
        EasyMock.expect(parent.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.replay(parent);

        ctx = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(ctx.isAutowire()).andReturn(false);
        EasyMock.expect(ctx.getClassLoader()).andReturn(null);
        EasyMock.expect(ctx.getScdlLocation()).andReturn(null);
        EasyMock.expect(ctx.getComponentId()).andReturn(componentId);
        EasyMock.replay(ctx);
    }
}
