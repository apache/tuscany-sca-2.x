/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.idl.wsdl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

/**
 * @version $Rev$ $Date$
 */
public class WSDLLoaderImpl implements WSDLLoader {
    private final WSDLFactory wsdlFactory;

    private final ExtensionRegistry registry;

    public WSDLLoaderImpl() throws WSDLException {
        wsdlFactory = WSDLFactory.newInstance();
        registry = wsdlFactory.newPopulatedExtensionRegistry();
    }

    public PortType loadPortType(URI interfaceIRI, String wsdlLocation, ClassLoader base)
        throws WSDLLoaderException, WSDLException {
        Collection<WSDLLocation> locations = getLocations(wsdlLocation, base);
        QName interfaceName = getInterfaceName(interfaceIRI);

        Definition definition = loadDefinition(interfaceName.getNamespaceURI(), locations);
        return definition.getPortType(interfaceName);
    }

    public Definition loadDefinition(String namespace, Collection<WSDLLocation> locations)
        throws WSDLException, UnresolveableResourceException {
        for (WSDLLocation location : locations) {
            if (namespace.equals(location.getNamespace()) && location.getLocation() != null) {
                return loadDefinition(location);
            }
        }
        throw new UnresolveableResourceException(namespace);
    }

    public Definition loadDefinition(WSDLLocation location) throws WSDLException {
        String namespace = location.getNamespace();

        WSDLReader reader = wsdlFactory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setExtensionRegistry(registry);

        Definition definition = reader.readWSDL(location.getLocation().toString());
        String definitionNamespace = definition.getTargetNamespace();
        if (namespace != null && !namespace.equals(definitionNamespace)) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, namespace + " != "
                    + definition.getTargetNamespace());
        }
        return definition;
    }

    public Collection<WSDLLocation> getLocations(String wsdlLocation, ClassLoader base)
        throws InvalidWSDLLocationException {
        String parts[] = wsdlLocation.split("\\s");
        // check the number of parts is a multiple of two
        if ((parts.length & 1) != 0) {
            throw new InvalidWSDLLocationException(wsdlLocation);
        }
        List<WSDLLocation> locations = new ArrayList<WSDLLocation>(parts.length >>> 1);
        for (int i = 0; i < parts.length; i += 2) {
            URL url;
            try {
                URI uri = new URI(parts[i + 1]);
                if (uri.isAbsolute()) {
                    url = uri.toURL();
                } else {
                    url = base.getResource(uri.toString());
                }
            } catch (MalformedURLException e) {
                throw new InvalidWSDLLocationException(e);
            } catch (URISyntaxException e) {
                throw new InvalidWSDLLocationException(e);
            }
            WSDLLocation location = new WSDLLocation(parts[i], url);
            locations.add(location);
        }
        return locations;
    }

    public QName getInterfaceName(URI interfaceIRI) throws UnresolveableResourceException, InvalidFragmentException {
        String namespace = getNamespace(interfaceIRI);
        String interfaceName = getInterfaceName(interfaceIRI.getFragment());
        return new QName(namespace, interfaceName);
    }

    public String getInterfaceName(String fragment) throws InvalidFragmentException {
        if (fragment == null) {
            throw new InvalidFragmentException("missing fragment");
        }
        String[] parts = fragment.split("\\s");
        for (String part : parts) {
            if (part.startsWith("wsdl.interface(") && part.charAt(part.length() - 1) == ')') {
                return part.substring(15, part.length() - 1);
            }
        }
        throw new InvalidFragmentException(fragment);
    }

    public String getNamespace(URI iri) throws UnresolveableResourceException {
        if (!iri.isAbsolute()) {
            UnresolveableResourceException ure = new UnresolveableResourceException("no namespace defined in " + iri);
            ure.setIdentifier(iri.toString());
            throw ure;
        }
        StringBuilder s = new StringBuilder();
        s.append(iri.getScheme());
        s.append(':');
        s.append(iri.getSchemeSpecificPart());
        return s.toString();
    }
}
