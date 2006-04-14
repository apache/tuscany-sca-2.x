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

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.SERVICE;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Scope;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ServiceLoader extends AbstractLoader {
    public QName getXMLType() {
        return SERVICE;
    }

    public Service load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert SERVICE.equals(reader.getName());
        Service service = factory.createService();
        service.setName(reader.getAttributeValue(null, "name"));

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                AssemblyObject o = registry.load(reader, resourceLoader);
                if (o instanceof ServiceContract) {
                    service.setServiceContract((ServiceContract) o);
                }
                reader.next();
                break;
            case END_ELEMENT:
                return service;
            }
        }
    }
}
