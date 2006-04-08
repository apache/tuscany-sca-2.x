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

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 */
public class StAXLoaderRegistryImpl implements StAXLoaderRegistry {
    private final Map<QName, StAXElementLoader<? extends AssemblyModelObject>> loaders = new HashMap<QName, StAXElementLoader<? extends AssemblyModelObject>>();

    private Monitor monitor;

    @org.apache.tuscany.core.system.annotation.Monitor
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public <T extends AssemblyModelObject> void registerLoader(StAXElementLoader<T> loader) {
        QName xmlType = loader.getXMLType();
        monitor.registeringLoader(xmlType);
        loaders.put(xmlType, loader);
    }

    public <T extends AssemblyModelObject> void unregisterLoader(StAXElementLoader<T> loader) {
        QName xmlType = loader.getXMLType();
        monitor.unregisteringLoader(xmlType);
        loaders.remove(xmlType);
    }

    public AssemblyModelObject load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        QName name = reader.getName();
        monitor.elementLoad(name);
        StAXElementLoader<? extends AssemblyModelObject> loader = loaders.get(name);
        if (loader == null) {
            throw new ConfigurationLoadException("Unrecognized element: " + name);
        } else {
            return loader.load(reader, resourceLoader);
        }
    }


    private final ThreadLocal<AssemblyModelContext> modelContext = new ThreadLocal<AssemblyModelContext>();

    @Deprecated
    public AssemblyModelContext getContext() {
        return modelContext.get();
    }

    @Deprecated
    public void setContext(AssemblyModelContext context) {
        modelContext.set(context);
    }

    public static interface Monitor {
        /**
         * Event emitted when a StAX element loader is registered.
         *
         * @param xmlType the QName of the element the loader will handle
         */
        void registeringLoader(QName xmlType);

        /**
         * Event emitted when a StAX element loader is unregistered.
         *
         * @param xmlType the QName of the element the loader will handle
         */
        void unregisteringLoader(QName xmlType);

        /**
         * Event emitted when a request is made to load an element.
         *
         * @param xmlType the QName of the element that should be loaded
         */
        void elementLoad(QName xmlType);
    }
}
