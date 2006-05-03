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

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.ModelObject;
import org.apache.tuscany.spi.loader.LoaderContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.loader.StAXLoaderRegistry;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;

/**
 * @version $Rev$ $Date$
 */
public class StAXLoaderRegistryImpl implements StAXLoaderRegistry {
    private final Map<QName, StAXElementLoader<? extends ModelObject>> loaders = new HashMap<QName, StAXElementLoader<? extends ModelObject>>();

    private Monitor monitor;

    @org.apache.tuscany.spi.annotation.Monitor
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public <T extends ModelObject> void registerLoader(QName element, StAXElementLoader<T> loader) {
        monitor.registeringLoader(element);
        loaders.put(element, loader);
    }

    public <T extends ModelObject> void unregisterLoader(QName element, StAXElementLoader<T> loader) {
        monitor.unregisteringLoader(element);
        loaders.remove(element);
    }

    public ModelObject load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, LoaderException {
        QName name = reader.getName();
        monitor.elementLoad(name);
        StAXElementLoader<? extends ModelObject> loader = loaders.get(name);
        if (loader == null) {
            throw new UnrecognizedElementException(name);
        } else {
            return loader.load(reader, loaderContext);
        }
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
