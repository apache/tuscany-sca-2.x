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
package org.apache.tuscany.core.implementation.composite;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.ModelObject;

import junit.framework.TestCase;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class CompositeLoaderTestCase extends TestCase {
    public static final QName COMPOSITE = new QName(SCA_NS, "composite");
    private CompositeLoader loader;
    private QName name;

    public void testLoadNameAndDefaultAutowire() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader);
        CompositeComponentType<?, ?, ?> type = loader.load(null, reader, null);
        assertEquals(name, type.getName());
        assertFalse(type.isAutowire());
        EasyMock.verify(reader);
    }

    public void testAutowire() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("true");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader);
        CompositeComponentType<?, ?, ?> type = loader.load(null, reader, null);
        assertTrue(type.isAutowire());
        EasyMock.verify(reader);
    }

    /**
     * Tests autowire enabled is propagated when children are loaded
     */
    public void testAutowireContextEnabledPropagation() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(EasyMock.isA(ModelObject.class),
            EasyMock.isA(XMLStreamReader.class),
            EasyMock.isA(DeploymentContext.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                DeploymentContext context = (DeploymentContext) EasyMock.getCurrentArguments()[2];
                assertTrue("autowire not propagated", context.isAutowire());
                return null;
            }
        });
        EasyMock.replay(registry);
        DeploymentContext context = new RootDeploymentContext(null, null, null, null, null, true);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("true");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader);
        loader.load(null, reader, context);
        assertTrue(context.isAutowire());
    }

    /**
     * Tests autowire enabled is propagated when children are loaded and composite is set to use inherited autowire
     * settings
     */
    public void testAutowireContextEnabledInheritedPropagation() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(EasyMock.isA(ModelObject.class),
            EasyMock.isA(XMLStreamReader.class),
            EasyMock.isA(DeploymentContext.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                DeploymentContext context = (DeploymentContext) EasyMock.getCurrentArguments()[2];
                assertTrue("autowire not propagated", context.isAutowire());
                return null;
            }
        });
        EasyMock.replay(registry);
        DeploymentContext context = new RootDeploymentContext(null, null, null, null, null, true);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader);
        loader.load(null, reader, context);
        assertTrue(context.isAutowire());
    }

    /**
     * Tests autowire false is propagated when children are loaded and composite inherits autowire settings
     */
    public void testAutowireFalseContextPropagation() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(EasyMock.isA(ModelObject.class),
            EasyMock.isA(XMLStreamReader.class),
            EasyMock.isA(DeploymentContext.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                DeploymentContext context = (DeploymentContext) EasyMock.getCurrentArguments()[2];
                assertFalse("autowire not propagated", context.isAutowire());
                return null;
            }
        });
        EasyMock.replay(registry);
        DeploymentContext context = new RootDeploymentContext(null, null, null, null, null, false);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader);
        loader.load(null, reader, context);
        assertFalse(context.isAutowire());
    }

    /**
     * Tests autowire false is propagated when children are loaded and composite autowire is set to false
     */
    public void testAutowireCompositeFalsePropagation() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(EasyMock.isA(ModelObject.class),
            EasyMock.isA(XMLStreamReader.class),
            EasyMock.isA(DeploymentContext.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                DeploymentContext context = (DeploymentContext) EasyMock.getCurrentArguments()[2];
                assertFalse("autowire not propagated", context.isAutowire());
                return null;
            }
        });
        EasyMock.replay(registry);
        DeploymentContext context = new RootDeploymentContext(null, null, null, null, null, true);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("false");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader);
        loader.load(null, reader, context);
        assertTrue(context.isAutowire());
    }


    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        loader = new CompositeLoader(registry, null);
        name = new QName("http://example.com", "composite");
    }
}
