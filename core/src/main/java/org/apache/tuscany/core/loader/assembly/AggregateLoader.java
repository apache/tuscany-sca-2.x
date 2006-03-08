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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;

/**
 * @version $Rev$ $Date$
 */
public abstract class AggregateLoader extends AbstractLoader {
    public void loadAggregate(XMLStreamReader reader, Aggregate aggregate, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        aggregate.setName(reader.getAttributeValue(null, "name"));
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                AssemblyModelObject o = registry.load(reader, resourceLoader);
                if (o instanceof EntryPoint) {
                    aggregate.getEntryPoints().add((EntryPoint) o);
                } else if (o instanceof ExternalService) {
                    aggregate.getExternalServices().add((ExternalService) o);
                } else if (o instanceof Component) {
                    aggregate.getComponents().add((Component) o);
                } else if (o instanceof Wire) {
                    aggregate.getWires().add((Wire) o);
                }
                reader.next();
                break;
            case END_ELEMENT:
                return;
            }
        }
    }
}
