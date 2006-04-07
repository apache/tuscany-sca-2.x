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
import org.apache.tuscany.core.loader.StAXUtil;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.PROPERTY;
import org.apache.tuscany.model.assembly.Property;
import org.osoa.sca.annotations.Scope;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class PropertyLoader extends AbstractLoader {
    private static final String XSD = "http://www.w3.org/2001/XMLSchema";

    private static final Map<QName, Class<?>> TYPE_MAP;
    static {
        // todo support more XSD types, or remove if we store the QName
        TYPE_MAP = new HashMap<QName, Class<?>>(17);
        TYPE_MAP.put(new QName(XSD, "string"), String.class);
    }

    public QName getXMLType() {
        return PROPERTY;
    }

    public Class<Property> getModelType() {
        return Property.class;
    }

    public Property load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert PROPERTY.equals(reader.getName());
        Property property = factory.createProperty();
        property.setName(reader.getAttributeValue(null, "name"));
        String typeName = reader.getAttributeValue(null, "type");
        // support XSD type or Java class name
        // todo perhaps we should just store the QName for the PropertyFactory to use
        int index = typeName.indexOf(':');
        if (index != -1) {
            String prefix = typeName.substring(0, index);
            String namespaceURI = reader.getNamespaceURI(prefix);
            QName qname = new QName(namespaceURI, typeName.substring(index+1));
            property.setType(TYPE_MAP.get(qname));
        } else {
            try {
                Class<?> type = resourceLoader.loadClass(typeName);
                property.setType(type);
            } catch (ClassNotFoundException e) {
                throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
            }
        }
        property.setMany(Boolean.parseBoolean(reader.getAttributeValue(null, "many")));
        property.setDefaultValue(reader.getAttributeValue(null, "default"));
        String required = reader.getAttributeValue(null, "required");
        property.setRequired(required != null && Boolean.valueOf(required));

        StAXUtil.skipToEndElement(reader);
        return property;
    }
}
