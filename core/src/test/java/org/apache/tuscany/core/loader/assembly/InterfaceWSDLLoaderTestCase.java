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

import java.net.URL;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.INTERFACE_WSDL;
import org.apache.tuscany.core.loader.impl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceWSDLLoaderTestCase extends LoaderTestSupport {
    private WSDLDefinitionRegistryImpl wsdlRegistry;
    private ResourceLoader resourceLoader;

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<interface.wsdl xmlns='http://www.osoa.org/xmlns/sca/0.9'></interface.wsdl>";
        XMLStreamReader reader = getReader(xml);
        WSDLServiceContract sc = (WSDLServiceContract) registry.load(reader, loaderContext);
        reader.require(XMLStreamConstants.END_ELEMENT, INTERFACE_WSDL.getNamespaceURI(), INTERFACE_WSDL.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(sc);
    }

    public void testInterface() throws Exception {
        wsdlRegistry.loadDefinition("http://www.example.org", getClass().getResource("example.wsdl"), resourceLoader);
        String xml = "<interface.wsdl xmlns='http://www.osoa.org/xmlns/sca/0.9' interface='http://www.example.org#HelloWorld'></interface.wsdl>";
        XMLStreamReader reader = getReader(xml);
        WSDLServiceContract sc = (WSDLServiceContract) registry.load(reader, loaderContext);
        reader.require(XMLStreamConstants.END_ELEMENT, INTERFACE_WSDL.getNamespaceURI(), INTERFACE_WSDL.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(sc);
    }

    public void testInterfaceWithLocation() throws Exception {
        wsdlRegistry.loadDefinition("http://www.example.org", getClass().getResource("example.wsdl"), resourceLoader);
        String xml = "<interface.wsdl xmlns='http://www.osoa.org/xmlns/sca/0.9' xmlns:wsdli='http://www.w3.org/2006/01/wsdl-instance' " +
                "wsdli:wsdlLocation='http://www.example.org " + getClass().getResource("example.wsdl") + "' "+
                "interface='http://www.example.org#HelloWorld'"+
                "></interface.wsdl>";
        XMLStreamReader reader = getReader(xml);
        WSDLServiceContract sc = (WSDLServiceContract) registry.load(reader, loaderContext);
        reader.require(XMLStreamConstants.END_ELEMENT, INTERFACE_WSDL.getNamespaceURI(), INTERFACE_WSDL.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(sc);
    }

    protected void setUp() throws Exception {
        super.setUp();
        wsdlRegistry = new WSDLDefinitionRegistryImpl();
        wsdlRegistry.setMonitor(NULL_MONITOR);
        resourceLoader = new ResourceLoaderImpl(getClass().getClassLoader());
        wsdlRegistry.loadDefinition("http://www.example.org", getClass().getResource("example.wsdl"), resourceLoader);
        InterfaceWSDLLoader loader = new InterfaceWSDLLoader();
        loader.setWsdlRegistry(wsdlRegistry);
        registerLoader(loader);
    }

    private static final WSDLDefinitionRegistryImpl.Monitor NULL_MONITOR = new WSDLDefinitionRegistryImpl.Monitor() {
        public void readingWSDL(String namespace, URL location) {
        }

        public void cachingDefinition(String namespace, URL location) {
        }
    };
}
