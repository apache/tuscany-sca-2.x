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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Scope;

import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.COMPONENT;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.PROPERTIES;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.REFERENCES;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ComponentLoader extends AbstractLoader {
    public QName getXMLType() {
        return COMPONENT;
    }

    public Class<Component> getModelType() {
        return Component.class;
    }

    public Component load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert COMPONENT.equals(reader.getName());
        Component component = factory.createSimpleComponent();
        component.setName(reader.getAttributeValue(null, "name"));

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                QName name = reader.getName();
                if (PROPERTIES.equals(name)) {
                    loadProperties(reader, component);
                } else if (REFERENCES.equals(name)) {
                    loadReferences(reader, component);
                } else {
                    AssemblyModelObject o = registry.load(reader, resourceLoader);
                    if (o instanceof ComponentImplementation) {
                        component.setComponentImplementation((ComponentImplementation) o);
                    }
                }
                reader.next();
                break;
            case END_ELEMENT:
                return component;
            }
        }
    }

    protected void loadProperties(XMLStreamReader reader, Component component) throws XMLStreamException {
        List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                String name = reader.getLocalName();
                String value = reader.getElementText();
                ConfiguredProperty configuredProperty = factory.createConfiguredProperty();
                // todo set property override
                configuredProperty.setValue(value);
                configuredProperties.add(configuredProperty);
                break;
            case END_ELEMENT:
                return;
            }
        }
    }

    protected void loadReferences(XMLStreamReader reader, Component component) throws XMLStreamException {
        List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                String name = reader.getLocalName();
                String value = reader.getElementText();
                ConfiguredReference configuredReference = factory.createConfiguredReference();
                configuredReferences.add(configuredReference);
                break;
            case END_ELEMENT:
                return;
            }
        }
    }
}
