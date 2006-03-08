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
package org.apache.tuscany.core.loader.impl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyModelObject;

/**
 * @version $Rev$ $Date$
 */
public class StAXLoaderRegistryImpl implements StAXLoaderRegistry {
    private final Map<QName, StAXElementLoader<? extends AssemblyModelObject>> loaders = new HashMap<QName, StAXElementLoader<? extends AssemblyModelObject>>();

    public <T extends AssemblyModelObject> void registerLoader(StAXElementLoader<T> loader) {
        loaders.put(loader.getXMLType(), loader);
    }

    public <T extends AssemblyModelObject> void unregisterLoader(StAXElementLoader<T> loader) {
        loaders.remove(loader.getXMLType());
    }

    public AssemblyModelObject load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        QName name = reader.getName();
        StAXElementLoader<? extends AssemblyModelObject> loader = loaders.get(name);
        if (loader == null) {
            throw new ConfigurationLoadException("Unrecognized element: " + name);
        } else {
            return loader.load(reader, resourceLoader);
        }
    }

}
