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

import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.loader.ComponentTypeLoader;

/**
 * @version $Rev$ $Date$
 */
public class LoaderRegistryImpl implements LoaderRegistry {
    private Monitor monitor;
    private final Map<QName, StAXElementLoader<? extends ModelObject>> loaders =
            new HashMap<QName, StAXElementLoader<? extends ModelObject>>();
    private final Map<Class<? extends Implementation<?>>, ComponentTypeLoader<? extends Implementation<?>>> componentTypeLoaders =
            new HashMap<Class<? extends Implementation<?>>, ComponentTypeLoader<? extends Implementation<?>>>();


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

    public ModelObject load(XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        QName name = reader.getName();
        monitor.elementLoad(name);
        StAXElementLoader<? extends ModelObject> loader = loaders.get(name);
        if (loader == null) {
            throw new UnrecognizedElementException(name);
        }
        return loader.load(reader, deploymentContext);
    }

    public <I extends Implementation<?>> void registerLoader(Class<I> key, ComponentTypeLoader<I> loader) {
        componentTypeLoaders.put(key, loader);
    }

    @SuppressWarnings("unchecked")
    public <I extends Implementation<?>> void loadComponentType(I implementation, DeploymentContext deploymentContext) {
        Class<I> key = (Class<I>) implementation.getClass();
        ComponentTypeLoader<I> loader = (ComponentTypeLoader<I>) componentTypeLoaders.get(key);
        if (loader == null) {
            throw new UnsupportedOperationException();
        }
        loader.load(implementation, deploymentContext);
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
