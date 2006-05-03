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

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.spi.loader.LoaderSupport;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.ImportWSDL;
import org.apache.tuscany.model.assembly.Wire;

/**
 * @version $Rev$ $Date$
 */
public abstract class CompositeLoader extends LoaderSupport {
    public void loadComposite(XMLStreamReader reader, Composite composite, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        composite.setName(reader.getAttributeValue(null, "name"));
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                AssemblyObject o = registry.load(reader, loaderContext);
                if (o instanceof EntryPoint) {
                    composite.getEntryPoints().add((EntryPoint) o);
                } else if (o instanceof ExternalService) {
                    composite.getExternalServices().add((ExternalService) o);
                } else if (o instanceof Component) {
                    composite.getComponents().add((Component) o);
                } else if (o instanceof Wire) {
                    composite.getWires().add((Wire) o);
                } else if (o instanceof ImportWSDL) {
                    composite.getWSDLImports().add((ImportWSDL) o);
                }
                reader.next();
                break;
            case END_ELEMENT:
                return;
            }
        }
    }
}
