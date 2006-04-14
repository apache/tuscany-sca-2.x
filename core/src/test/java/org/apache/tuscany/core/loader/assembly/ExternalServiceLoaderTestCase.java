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

import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.EXTERNAL_SERVICE;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class ExternalServiceLoaderTestCase extends LoaderTestSupport {

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<externalService xmlns='http://www.osoa.org/xmlns/sca/0.9' name='test'></externalService>";
        XMLStreamReader reader = getReader(xml);
        ExternalService es = (ExternalService) registry.load(reader, resourceLoader);
        assertNotNull(es);
        assertEquals("test", es.getName());
        reader.require(XMLStreamConstants.END_ELEMENT, EXTERNAL_SERVICE.getNamespaceURI(), EXTERNAL_SERVICE.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
    }

    public void testInterface() throws XMLStreamException, ConfigurationLoadException {
        String interfaceName = MockService.class.getName();
        String xml = "<externalService xmlns='http://www.osoa.org/xmlns/sca/0.9' name='test'><interface.java interface='" + interfaceName + "'/></externalService>";
        XMLStreamReader reader = getReader(xml);
        ExternalService es = (ExternalService) registry.load(reader, resourceLoader);
        reader.require(XMLStreamConstants.END_ELEMENT, EXTERNAL_SERVICE.getNamespaceURI(), EXTERNAL_SERVICE.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(es);
        assertEquals("test", es.getName());
        ConfiguredService configuredService = es.getConfiguredService();
        JavaServiceContract serviceContract = (JavaServiceContract) configuredService.getPort().getServiceContract();
        assertEquals(interfaceName, serviceContract.getInterfaceName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registerLoader(new ExternalServiceLoader());
        registerLoader(new InterfaceJavaLoader());
    }
}
