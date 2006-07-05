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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Property;

import static org.apache.tuscany.core.loader.AssemblyConstants.PROPERTY;

/**
 * Loads a property from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class PropertyLoader extends LoaderExtension<Property> {
    public PropertyLoader() {
    }

    public PropertyLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return PROPERTY;
    }

    public Property<?> load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext ctx)
        throws XMLStreamException, LoaderException {
        assert PROPERTY.equals(reader.getName());
        Property<?> property = new Property();
        property.setName(reader.getAttributeValue(null, "name"));
        String typeName = reader.getAttributeValue(null, "type");
        QName xmlType;
        int index = typeName.indexOf(':');
        if (index != -1) {
            // a prefix was specified
            String prefix = typeName.substring(0, index);
            String namespaceURI = reader.getNamespaceURI(prefix);
            xmlType = new QName(namespaceURI, typeName.substring(index + 1));
        } else {
            // no prefix was specified, use the default
            String namespaceURI = reader.getNamespaceURI();
            xmlType = new QName(namespaceURI, typeName);
        }
        property.setXmlType(xmlType);
        property.setMany(Boolean.parseBoolean(reader.getAttributeValue(null, "many")));
        property.setRequired(Boolean.parseBoolean(reader.getAttributeValue(null, "required")));

        // TODO support default values

        StAXUtil.skipToEndElement(reader);
        return property;
    }
}
