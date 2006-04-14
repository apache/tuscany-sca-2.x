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
package org.apache.tuscany.core.loader.assembly;

import java.io.StringReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.loader.impl.StAXLoaderRegistryImpl;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;

/**
 * Base class for loader tests with common fixture elements.
 *
 * @version $Rev$ $Date$
 */
public abstract class LoaderTestSupport extends TestCase {
    protected SystemAssemblyFactory assemblyFactory;
    protected ResourceLoader resourceLoader;
    protected AssemblyContext modelContext;
    protected XMLInputFactory xmlFactory;
    protected StAXLoaderRegistryImpl registry;

    protected static final StAXLoaderRegistryImpl.Monitor NULL_MONITOR = new StAXLoaderRegistryImpl.Monitor() {
        public void registeringLoader(QName xmlType) {
        }

        public void unregisteringLoader(QName xmlType) {
        }

        public void elementLoad(QName xmlType) {
        }
    };

    protected void setUp() throws Exception {
        super.setUp();
        assemblyFactory = new SystemAssemblyFactoryImpl();
        resourceLoader = new ResourceLoaderImpl(getClass().getClassLoader());
        modelContext = new AssemblyContextImpl(assemblyFactory, null, resourceLoader);
        xmlFactory = XMLInputFactory.newInstance();
        registry = new StAXLoaderRegistryImpl();
        registry.setMonitor(NULL_MONITOR);
    }

    protected XMLStreamReader getReader(String xml) throws XMLStreamException {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(xml));
        reader.next();
        return reader;
    }

    protected void registerLoader(AbstractLoader<?> loader) {
        loader.setFactory(assemblyFactory);
        loader.setRegistry(registry);
        loader.start();
    }
}
