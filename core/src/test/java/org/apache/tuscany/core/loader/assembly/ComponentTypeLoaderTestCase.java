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
package org.apache.tuscany.core.loader.assembly;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Service;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoaderTestCase extends LoaderTestSupport {

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        XMLStreamReader reader = getReader("<componentType xmlns='http://www.osoa.org/xmlns/sca/0.9'><service name='service1'/></componentType>");
        ComponentInfo type = (ComponentInfo) registry.load(reader, loaderContext);
        type.initialize(null);
        assertNotNull(type);
        assertEquals(1, type.getServices().size());
        Service service = type.getService("service1");
        assertEquals("service1", service.getName());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registerLoader(new ComponentTypeLoader());
        registerLoader(new ServiceLoader());
    }

}
