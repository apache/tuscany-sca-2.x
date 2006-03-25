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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;

/**
 * Registry for XML loaders that can parse a StAX input stream and return model objects.
 * <p/>
 * Loaders will typically be contributed to the system by any extension that needs to
 * handle extension specific information contained in some XML configuration file.
 * The loader can be contributed as a system component with an autowire reference
 * to this registry which is used during initialization to actually register.
 * </p>
 * This registry can also be used to parse an input stream, dispatching to the
 * appropriate loader for each element accepted. Loaders can call back to the
 * registry to load sub-elements that they are not able to handle directly.
 *
 * @version $Rev$ $Date$
 */
public interface StAXLoaderRegistry {
    /**
     * Register a loader. This operation will typically be called by a loader
     * during its initialization.
     *
     * @param loader a loader that is being contributed to the system
     */
    <T extends AssemblyModelObject> void registerLoader(StAXElementLoader<T> loader);

    /**
     * Unregister a loader. This will typically be called by a loader as it is being destroyed.
     * @param loader a loader that should no longer be used
     */
    <T extends AssemblyModelObject> void unregisterLoader(StAXElementLoader<T> loader);

    /**
     * Parse the supplied XML stream dispatching to the appropriate registered loader
     * for each element encountered in the stream.
     * <p/>
     * This method must be called with the XML cursor positioned on a START_ELEMENT event.
     * When this method returns, the stream will be positioned on the corresponding
     * END_ELEMENT event.
     *
     * @param reader the XML stream to parse
     * @param resourceLoader a resource loader for application artifacts
     * @return the model object obtained by parsing the current element on the stream
     * @throws XMLStreamException if there was a problem reading the stream
     */
    AssemblyModelObject load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException;

    /**
     * Hack to allow loaders to initialize model objects on the fly.
     * Remove when initialization has been moved from the model implementation to the loader.
     *
     * @return the model context for this load operation
     */
    @Deprecated
    AssemblyModelContext getContext();

    /**
     * Hack to allow loaders to initialize model objects on the fly.
     * Remove when initialization has been moved from the model implementation to the loader.
     *
     * @param context the model context for this load operation
     */
    @Deprecated
    void setContext(AssemblyModelContext context);
}
