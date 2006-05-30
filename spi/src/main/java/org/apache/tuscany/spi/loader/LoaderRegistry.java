/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.loader;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Registry for XML loaders that can parse a StAX input stream and return model objects.
 * <p/>
 * Loaders will typically be contributed to the system by any extension that needs to
 * handle extension specific information contained in some XML configuration file.
 * The loader can be contributed as a system component with an autowire reference
 * to this builderRegistry which is used during initialization to actually register.
 * </p>
 * This builderRegistry can also be used to parse an input stream, dispatching to the
 * appropriate loader for each element accepted. Loaders can call back to the
 * builderRegistry to load sub-elements that they are not able to handle directly.
 *
 * @version $Rev$ $Date$
 */
public interface LoaderRegistry {
    /**
     * Register a loader. This operation will typically be called by a loader
     * during its initialization.
     *
     * @param element the element that should be delegated to the contibuted loader
     * @param loader  a loader that is being contributed to the system
     */
    <T extends ModelObject> void registerLoader(QName element, StAXElementLoader<T> loader);

    /**
     * Unregister a loader. This will typically be called by a loader as it is being destroyed.
     *
     * @param element the element that was being delegated to the contibuted loader
     * @param loader  a loader that should no longer be used
     */
    <T extends ModelObject> void unregisterLoader(QName element, StAXElementLoader<T> loader);

    /**
     * Parse the supplied XML stream dispatching to the appropriate registered loader
     * for each element encountered in the stream.
     * <p/>
     * This method must be called with the XML cursor positioned on a START_ELEMENT event.
     * When this method returns, the stream will be positioned on the corresponding
     * END_ELEMENT event.
     *
     * @param reader            the XML stream to parse
     * @param deploymentContext the current deployment context
     * @return the model object obtained by parsing the current element on the stream
     * @throws XMLStreamException if there was a problem reading the stream
     */
    ModelObject load(XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException;

    /**
     * Load a model object from a specified locations.
     *
     * @param url               the location of an XML document to be loaded
     * @param type              the type of ModelObject that is expected to be in the document
     * @param deploymentContext the current deployment context
     * @return the model ojbect loaded from the document
     * @throws LoaderException if there was a problem loading the document
     */
    <MO extends ModelObject> MO load(URL url, Class<MO> type, DeploymentContext deploymentContext) throws LoaderException;

    /**
     * Regsiter a component type loader.
     *
     * @param key    a type of implementation this loader can load component types for
     * @param loader the loader that is being contributed to the system
     */
    <I extends Implementation<?>> void registerLoader(Class<I> key, ComponentTypeLoader<I> loader);

    /**
     * Unregister a component type loader form the system.
     *
     * @param key a type of implementation whose loader should be unregistered
     */
    <I extends Implementation<?>> void unregisterLoader(Class<I> key);

    /**
     * Load the component type definition for a given implementation.
     * How the component type information is located is defined by the implementation specification.
     * It may include loading from an XML sidefile, introspection of some artifact related to the
     * implementation, some combination of those techniques or any other implementation-defined mechanism.
     *
     * @param implementation    the implementation whose component type should be loaded
     * @param deploymentContext the current deployment context
     * @throws LoaderException if there was a problem loading the component type definition
     */
    <I extends Implementation<?>> void loadComponentType(I implementation, DeploymentContext deploymentContext) throws LoaderException;
}
