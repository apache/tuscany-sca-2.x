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
package org.apache.tuscany.core.loader.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Service(interfaces = {WSDLDefinitionRegistry.class})
@Scope("MODULE")
public class WSDLDefinitionRegistryImpl implements WSDLDefinitionRegistry {
    private final WSDLFactory wsdlFactory;
    private final ExtensionRegistry registry;

    private final Map<URL, Definition> definitionsByLocation = new HashMap<URL, Definition>();
    private final Map<String, List<Definition>> definitionsByNamespace = new HashMap<String, List<Definition>>();

    private Monitor monitor;

    public WSDLDefinitionRegistryImpl() throws WSDLException {
        wsdlFactory = WSDLFactory.newInstance();
        registry = wsdlFactory.newPopulatedExtensionRegistry();
    }

    @org.apache.tuscany.spi.annotation.Monitor
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return registry;
    }

    public Definition loadDefinition(String wsdlLocation, ResourceLoader resourceLoader) throws IOException, WSDLException {
        int index = wsdlLocation.indexOf(' ');
        if (index == -1) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "Invalid wsdlLocation: " + wsdlLocation);
        }
        String namespace = wsdlLocation.substring(0, index).trim();
        URL url;
        URI uri;
        try {
            uri = new URI(wsdlLocation.substring(index + 1).trim());
        } catch (URISyntaxException e) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "Invalid wsdlLocation: " + wsdlLocation);
        }
        if (uri.isAbsolute()) {
            url = uri.toURL();
        } else {
            url = resourceLoader.getResource(uri.toString());
            if (url == null) {
                throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "Resource not found: " + uri);
            }
        }
        return loadDefinition(namespace, url);
    }

    public Definition loadDefinition(String namespace, URL location) throws IOException, WSDLException {
        Definition definition = definitionsByLocation.get(location);
        if (definition != null) {
            // return cached copy
            return definition;
        }

        monitor.readingWSDL(namespace, location);
        WSDLReader reader = wsdlFactory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setExtensionRegistry(registry);

        definition = reader.readWSDL(location.toString());
        String definitionNamespace = definition.getTargetNamespace();
        if (namespace != null && !namespace.equals(definitionNamespace)) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, namespace + " != " + definition.getTargetNamespace());
        }

        monitor.cachingDefinition(definitionNamespace, location);
        definitionsByLocation.put(location, definition);
        List<Definition> definitions = definitionsByNamespace.get(definitionNamespace);
        if (definitions == null) {
            definitions = new ArrayList<Definition>();
            definitionsByNamespace.put(definitionNamespace, definitions);
        }
        definitions.add(definition);

        return definition;
    }

    public PortType getPortType(QName name) {
        String namespace = name.getNamespaceURI();
        List<Definition> definitions = definitionsByNamespace.get(namespace);
        if (definitions == null) {
            return null;
        }
        for (Definition definition : definitions) {
            PortType portType = definition.getPortType(name);
            if (portType != null) {
                return portType;
            }
        }
        return null;
    }

    public Service getService(QName name) {
        String namespace = name.getNamespaceURI();
        List<Definition> definitions = definitionsByNamespace.get(namespace);
        if (definitions == null) {
            return null;
        }
        for (Definition definition : definitions) {
            Service service = definition.getService(name);
            if (service != null) {
                return service;
            }
        }
        return null;
    }

    public static interface Monitor {
        /**
         * Monitor event emitted immediately before an attempt is made to
         * read WSDL for the supplied namespace from the supplied location.
         *
         * @param namespace the target namespace expected in the WSDL; may be null
         * @param location the location where we will attempt to read the WSDL definition from
         */
        void readingWSDL(String namespace, URL location);

        /**
         * Monitor event emitted immediately before registering a WSDL definition
         * in the cache.
         *
         * @param namespace the target namespace for the WSDL
         * @param location the location where the WSDL definition was read from
         */
        void cachingDefinition(String namespace, URL location);
    }

}
