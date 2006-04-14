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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.ENTRY_POINT;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class EntryPointLoaderTestCase extends LoaderTestSupport {

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<entryPoint xmlns='http://www.osoa.org/xmlns/sca/0.9' name='test'></entryPoint>";
        XMLStreamReader reader = getReader(xml);
        EntryPoint ep = (EntryPoint) registry.load(reader, resourceLoader);
        assertNotNull(ep);
        assertEquals("test", ep.getName());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
    }

    public void testInterface() throws XMLStreamException, ConfigurationLoadException {
        String interfaceName = MockService.class.getName();
        String xml = "<entryPoint xmlns='http://www.osoa.org/xmlns/sca/0.9' name='test'><interface.java interface='" + interfaceName + "'/></entryPoint>";
        XMLStreamReader reader = getReader(xml);
        EntryPoint ep = (EntryPoint) registry.load(reader, resourceLoader);
        reader.require(XMLStreamConstants.END_ELEMENT, ENTRY_POINT.getNamespaceURI(), ENTRY_POINT.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(ep);
        assertEquals("test", ep.getName());
        ConfiguredService configuredService = ep.getConfiguredService();
        JavaServiceContract serviceContract = (JavaServiceContract) configuredService.getPort().getServiceContract();
        assertEquals(interfaceName, serviceContract.getInterfaceName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registerLoader(new EntryPointLoader());
        registerLoader(new InterfaceJavaLoader());
    }
}
