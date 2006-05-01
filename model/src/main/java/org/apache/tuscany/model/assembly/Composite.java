/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.model.assembly;

import java.util.List;

/**
 * A model object that describes a container for parts.
 */
public interface Composite extends Implementation {

    /**
     * Returns the name of the composite.
     * @return the name of the composite
     */
    String getName();

    /**
     * Sets the name of the composite.
     * @param name the name of the composite
     */
    void setName(String name);

    /**
     * Returns the named part.
     * @param name
     */
    Part getPart(String name);

    /**
     * Returns all entry points contained in this composite.
     * @return a list of all EntryPoint model objects contained in this composite
     */
    List<EntryPoint> getEntryPoints();

    /**
     * Returns all components contained in this composite.
     * @return a list of all Component model objects contained in this composite
     */
    List<Component> getComponents();

    /**
     * Returns all external services contained in this composite.
     * @return a list of all ExternalService model objects contained in this composite
     */
    List<ExternalService> getExternalServices();

    /**
     * Returns the wires contained in this composite.
     */
    List<Wire> getWires();
    
    /**
     * Returns the WSDL imports declared in this composite.
     */
    List<ImportWSDL> getWSDLImports();
    
    /**
     * Returns the WSDL imports for the given namespace.
     * @param namespace
     */
    List<ImportWSDL> getWSDLImports(String namespace);

    /**
     * Returns the configured service at the given address.
     * @param address
     */
    ConfiguredService getConfiguredService(ServiceURI address);

    /**
     * Returns the implementation class.
     */
    Class<?> getImplementationClass();

    /**
     * Sets the implementation class.
     */
    void setImplementationClass(Class<?> value);

}
