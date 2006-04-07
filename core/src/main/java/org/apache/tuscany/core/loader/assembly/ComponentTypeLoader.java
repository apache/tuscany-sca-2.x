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
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.COMPONENT_TYPE;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
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
public class ComponentTypeLoader extends AbstractLoader {
    public QName getXMLType() {
        return COMPONENT_TYPE;
    }

    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }

    public ComponentType load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert COMPONENT_TYPE.equals(reader.getName());
        ComponentType componentType = factory.createComponentType();

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                AssemblyModelObject o = registry.load(reader, resourceLoader);
                if (o instanceof Service) {
                    componentType.getServices().add((Service) o);
                } else if (o instanceof Reference) {
                    componentType.getReferences().add((Reference) o);
                } else if (o instanceof Property) {
                    componentType.getProperties().add((Property) o);
                }
                reader.next();
                break;
            case END_ELEMENT:
                return componentType;
            }
        }
    }
}
