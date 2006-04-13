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

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.AssemblyObject;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A loader that creates a model object from a StAX input stream.
 *
 * @version $Rev$ $Date$
 */
public interface StAXElementLoader<T extends AssemblyObject> {
    /**
     * Returns the XML element that this loader can handle.
     *
     * @return the XML element that this loader can handle
     */
    QName getXMLType();

    /**
     * Returns the type of model object that this loader will produce.
     *
     * @return the type of model object that this loader will produce
     */
    Class<T> getModelType();

    /**
     * Build the model object for an element in an XML stream.
     * When this method returns the stream will be positioned on the corresponding END_ELEMENT.
     *
     * @param reader the XML stream reader positioned on the applicable START_ELEMENT
     * @param resourceLoader a resource loader for application artifacts
     * @return the model object for that element
     */
    T load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException;
}
