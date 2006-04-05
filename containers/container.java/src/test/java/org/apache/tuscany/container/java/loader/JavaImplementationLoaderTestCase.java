/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.loader;

import java.net.URL;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldWithFieldProperties;
import org.apache.tuscany.container.java.assembly.mock.NakedHelloWorld;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.loader.assembly.AssemblyConstants;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Service;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationLoaderTestCase extends TestCase {
    private JavaImplementationLoader loader;
    private ComponentType mockType;

    public void testNakedHelloWorld() throws ConfigurationLoadException {
        ComponentType type = loader.loadComponentTypeByIntrospection(NakedHelloWorld.class);
        Assert.assertNotNull(type);
        Assert.assertTrue(type.getProperties().isEmpty());
        Assert.assertTrue(type.getReferences().isEmpty());
        List<Service> services = type.getServices();
        Assert.assertEquals(1, services.size());
        Assert.assertEquals("NakedHelloWorld", services.get(0).getName());
    }

    public void testHelloWorldWithSidefile() throws XMLStreamException, ConfigurationLoadException {
        StAXLoaderRegistry mockRegistry = new MockRegistry(mockType);
        loader.setRegistry(mockRegistry);
        URL sidefile = HelloWorldImpl.class.getResource("HelloWorldImpl.componentType");
        ComponentType type = loader.loadComponentTypeFromSidefile(sidefile, null);
        assertSame(mockType, type);
    }

    public void testHelloWorldWithFieldProperties() throws ConfigurationLoadException {
        ComponentType type = loader.loadComponentTypeByIntrospection(HelloWorldWithFieldProperties.class);
        type.initialize(null);
        Assert.assertNotNull(type);
        List<Property> props = type.getProperties();
        Assert.assertEquals(3, props.size());

        Property prop = type.getProperty("text");
        Assert.assertNotNull(prop);
        Assert.assertEquals("text", prop.getName());
        Assert.assertEquals(false, prop.isRequired());
        Assert.assertEquals(String.class, prop.getType());

        prop = type.getProperty("text2");
        Assert.assertNotNull(prop);
        Assert.assertEquals("text2", prop.getName());
        Assert.assertEquals(true, prop.isRequired());
        Assert.assertEquals(Integer.class, prop.getType());

        prop = type.getProperty("foo");
        Assert.assertNotNull(prop);
        Assert.assertEquals("foo", prop.getName());
        Assert.assertEquals(false, prop.isRequired());
        Assert.assertEquals(Integer.TYPE, prop.getType());
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();
        mockType = factory.createComponentType();

        loader = new JavaImplementationLoader();
        loader.setFactory(factory);
    }

    private static class MockRegistry implements StAXLoaderRegistry {
        private final ComponentType mockType;

        public MockRegistry(ComponentType mockType) {
            this.mockType = mockType;
        }

        public AssemblyModelObject load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
            assertEquals(AssemblyConstants.COMPONENT_TYPE, reader.getName());
            return mockType;
        }

        public <T extends AssemblyModelObject> void registerLoader(StAXElementLoader<T> loader) {
            throw new UnsupportedOperationException();
        }

        public <T extends AssemblyModelObject> void unregisterLoader(StAXElementLoader<T> loader) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public AssemblyModelContext getContext() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public void setContext(AssemblyModelContext context) {
            throw new UnsupportedOperationException();
        }
    }
}
