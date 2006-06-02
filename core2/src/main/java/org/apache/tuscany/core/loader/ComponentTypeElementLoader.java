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
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.extension.LoaderExtension;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeElementLoader extends LoaderExtension<ComponentType> {
    public ComponentTypeElementLoader() {
    }

    public ComponentTypeElementLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return AssemblyConstants.COMPONENT_TYPE;
    }

    public ComponentType load(XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert AssemblyConstants.COMPONENT_TYPE.equals(reader.getName());
        ComponentType<Service,Reference,Property<?>> componentType = new ComponentType<Service, Reference, Property<?>>();

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    ModelObject o = registry.load(reader, deploymentContext);
                    if (o instanceof Service) {
                        componentType.add((Service) o);
                    } else if (o instanceof Reference) {
                        componentType.add((Reference) o);
                    } else if (o instanceof Property) {
                        componentType.add((Property<?>) o);
                    }
                    break;
                case END_ELEMENT:
                    return componentType;
            }
        }
    }
}
