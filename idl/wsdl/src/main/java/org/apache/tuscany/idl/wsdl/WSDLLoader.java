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

import java.net.URI;
import java.util.Collection;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

/**
 * Interface for implementations that allow WSDL definitions to be loaded.
 * Currently we do not have a complete solution for handling both WSDL2.0 and WSDL1.1 definitions
 * so the current implementation only deals with loading WSDL1.1. This will change in the near future
 * (for example when Woden supports both forms) so all WSDL1.1 specific methods are deprecated.
 *
 * @version $Rev$ $Date$
 */
public interface WSDLLoader {
    /**
     * Load a WSDL 1.1 Definition for a namespace from one of specified locations.
     *
     * @param namespace the namespace whose definition should be loaded
     * @param locations a set of possible locations to load from
     * @return the loaded Definition
     * @throws WSDLException if there was a problem loading the definition
     */
    @Deprecated
    Definition loadDefinition(String namespace, Collection<WSDLLocation> locations) throws WSDLException, UnresolveableResourceException;

    /**
     * Load a WSDL 1.1 Definition from the specified location
     *
     * @param location the location to load from
     * @return the loaded Definition
     * @throws WSDLException if there was a problem loading the definition
     */
    @Deprecated
    Definition loadDefinition(WSDLLocation location) throws WSDLException;

    /**
     * Return the WSDL1.1 PortType for the specified interface IRI.
     *
     * @param interfaceIRI the WSDL2.0 interface IRI
     * @param wsdlLocation the location of the WSDL instance
     * @param base         a Classloader from which to load
     * @return the specified port type
     */
    @Deprecated
    PortType loadPortType(URI interfaceIRI, String wsdlLocation, ClassLoader base)
        throws WSDLLoaderException, WSDLException;

    /**
     * Parses a WSDL2.0 wsdlLocation attribute definition and returns a Collection of all locations it contains.
     *
     * @param wsdlLocation a list of namespace/location pairs as specified by WSDL2.0
     * @param base         a ClassLoader to use to resolve relative URLs
     * @return a collection of locations parsed from the string
     * @throws InvalidWSDLLocationException
     */
    Collection<WSDLLocation> getLocations(String wsdlLocation, ClassLoader base) throws InvalidWSDLLocationException;

    /**
     * Returns the fully qualified name of a WSDL interface parsed from a IRI as defined by WSDL2.0.
     * The value of the IRI defines the namespace, the fragment specifies the interface component; for example
     * <code>http://example.org/TicketAgent.wsdl20#wsdl.interface(TicketAgent)</code>
     *
     * @param interfaceIRI the IRI for the interface
     * @return the qualified name of the interface
     * @throws UnresolveableResourceException if the URI is relative
     * @throws InvalidFragmentException       if the fragment is incorrectly formed
     */
    QName getInterfaceName(URI interfaceIRI) throws UnresolveableResourceException, InvalidFragmentException;

    /**
     * Returns an interface parsed from a wsdl.interface fragment.
     *
     * @param fragment the fragment value
     * @return the interface name
     * @throws InvalidFragmentException if the fragment is incorrectly formed
     */
    String getInterfaceName(String fragment) throws InvalidFragmentException;

    /**
     * Returns a namespace parsed from an IRI. This is the URI with any fragment removed.
     *
     * @param iri the IRI
     * @return a namespace created by stripping fragment information from the URI
     * @throws UnresolveableResourceException if the URI is relative
     */
    String getNamespace(URI iri) throws UnresolveableResourceException;
}
