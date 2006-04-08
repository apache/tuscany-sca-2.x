/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.loader;

import junit.framework.TestCase;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.assembly.AbstractLoader;
import org.apache.tuscany.core.loader.assembly.ComponentTypeLoader;
import org.apache.tuscany.core.loader.assembly.ServiceLoader;
import org.apache.tuscany.core.loader.impl.StAXLoaderRegistryImpl;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoaderTestCase extends TestCase {
    private XMLInputFactory xmlFactory;
    private StAXLoaderRegistryImpl registry;
    private SystemAssemblyFactoryImpl assemblyFactory;
    private ResourceLoaderImpl resourceLoader;

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        XMLStreamReader is = xmlFactory.createXMLStreamReader(getClass().getResourceAsStream("minimal.componentType"));
        is.next();
        ComponentType type = (ComponentType) registry.load(is, resourceLoader);
        type.initialize(null);
        assertNotNull(type);
        assertEquals(1, type.getServices().size());
        Service service = type.getService("service1");
        assertEquals("service1", service.getName());
        assertEquals(XMLStreamConstants.END_DOCUMENT, is.next());
    }

    protected void setUp() throws Exception {
        super.setUp();
        xmlFactory = XMLInputFactory.newInstance();
        assemblyFactory = new SystemAssemblyFactoryImpl();
        registry = new StAXLoaderRegistryImpl();
        registry.setMonitor(NULL_MONITOR);
        register(new ComponentTypeLoader());
        register(new ServiceLoader());
        resourceLoader = new ResourceLoaderImpl(getClass().getClassLoader());
    }

    private void register(AbstractLoader<?> loader) {
        loader.setFactory(assemblyFactory);
        loader.setRegistry(registry);
        loader.start();
    }

    private static final StAXLoaderRegistryImpl.Monitor NULL_MONITOR = new StAXLoaderRegistryImpl.Monitor() {
        public void registeringLoader(QName xmlType) {
        }

        public void unregisteringLoader(QName xmlType) {
        }

        public void elementLoad(QName xmlType) {
        }
    };
}
