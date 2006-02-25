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

import javax.wsdl.Import;

/**
 * Represents an aggregate. An aggregate contains AggregateParts (e.g. components, entry points
 * and external services).
 */
public interface Aggregate extends Extensible {

    /**
     * Returns the name of the aggregate.
     * @return
     */
    String getName();

    /**
     * Sets the name of the aggregate.
     * @param value
     */
    void setName(String value);

    /**
     * Returns the entry points contained in this aggregate.
     */
    List<EntryPoint> getEntryPoints();

    /**
     * Returns the named entry point.
     * @param name
     * @return
     */
    EntryPoint getEntryPoint(String name);

    /**
     * Returns the components contained in this aggregate.
     * @return
     */
    List<Component> getComponents();

    /**
     * Returns the named component.
     * @param name
     * @return
     */
    Component getComponent(String name);

    /**
     * Returns external services contained in this aggregate.
     * @return
     */
    List<ExternalService> getExternalServices();

    /**
     * Returns the named external service.
     * @param name
     * @return
     */
    ExternalService getExternalService(String name);

    /**
     * Returns the configured service at the given address.
     * @param address
     * @return
     */
    ConfiguredService getConfiguredService(ServiceURI address);

    /**
     * Returns the AggregateParts in this aggregate
     * @return
     */
    List<AggregatePart> getAggregateParts();

    /**
     * Returns the wires.
     * @return
     */
    List<Wire> getWires();
    
    /**
     * Returns the WSDL imports.
     * @return
     */
    List<Import> getWSDLImports();
    
    /**
     * Returns the WSDL imports for the given namespace. 
     * @param namespace
     * @return
     */
    List<Import> getWSDLImports(String namespace);
    
}
