/**
 *
 * Copyright 2006 The Apache Software Foundation
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

import java.net.URL;
import java.io.IOException;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * @version $Rev$ $Date$
 */
public interface WSDLDefinitionRegistry {
    /**
     * Loads and registers a WSDL Definition.
     *
     * @param namespace the expected namespace, or null if any namespace should be allowed
     * @param location  the location to load the definition from
     * @return the loaded Definition
     * @throws IOException   if there was a problem reading the document
     * @throws WSDLException if there was a problem parsing the definition
     */
    Definition loadDefinition(String namespace, URL location) throws IOException, WSDLException;

    /**
     * Load and register a WSDL definition as specified in a WSDL2.0 wsdlLocation attribute.
     *
     * @param wsdlLocation   the value of the wsdlLocation attribute
     * @param resourceLoader application resource loader used to support relative locations
     * @return the loaded Definition
     * @throws IOException   if there was a problem reading the document
     * @throws WSDLException if there was a problem parsing the definition
     */
    Definition loadDefinition(String wsdlLocation, ResourceLoader resourceLoader) throws IOException, WSDLException;

    /**
     * Returns the PortType with the supplied qualified name, or null if no such port has been defined.
     *
     * @param name the qualified name of the WSDL portType
     * @return the PortType for the supplied name, or null if none has been defined
     */
    PortType getPortType(QName name);

    /**
     * Returns the Service with the supplied qualified name, or null if no such service has been defined.
     *
     * @param name the qualified name of the WSDL service
     * @return the Service for the supplied name, or null if none has been defined
     */
    Service getService(QName name);


    /**
     * Returns the ExtensionRegistry that is used when parsing WSDL documents during the
     * loadDefinition call.
     *
     * @return the ExtensionRegistry that is used when parsing WSDL documents.
     */
    ExtensionRegistry getExtensionRegistry();
}
