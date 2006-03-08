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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.core.loader.StAXUtil;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.PROPERTY;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class PropertyLoader extends AbstractLoader {

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
        try {
            // todo the type information should not require loading of an application class, save until build time
            Class<?> type = resourceLoader.loadClass(typeName);
            property.setType(type);
        } catch (ClassNotFoundException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
        }
        property.setMany(Boolean.parseBoolean(reader.getAttributeValue(null, "many")));
        property.setDefaultValue(reader.getAttributeValue(null, "default"));
        String required = reader.getAttributeValue(null, "required");
        property.setRequired(required != null && Boolean.valueOf(required));

        StAXUtil.skipToEndElement(reader);
        return property;
    }
}
