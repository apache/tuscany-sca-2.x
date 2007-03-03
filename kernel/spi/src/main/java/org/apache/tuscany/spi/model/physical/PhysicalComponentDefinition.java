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
package org.apache.tuscany.spi.model.physical;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Represents a physical component model.
 *
 * @version $Rev$ $Date$
 */
public abstract class PhysicalComponentDefinition<PSD extends PhysicalServiceDefinition, PRD extends PhysicalReferenceDefinition> extends ModelObject {

    // Component Id.
    private URI componentId;
    
    // Services exposed by this component
    private Set<PSD> services = new HashSet<PSD>();
    
    // References exposed by this component
    private Set<PRD> references = new HashSet<PRD>();

    /**
     * Gets the component id.
     * @return Component id.
     */
    public URI getComponentId() {
        return componentId;
    }

    /**
     * Sets the component id.
     * @param componentId
     */
    public void setComponentId(URI componentId) {
        this.componentId = componentId;
    }
    
    /**
     * Returns the service definitions available for this component.
     * @return Service definitions for this operation.
     */
    public Set<PSD> getServices() {
        return Collections.unmodifiableSet(services);
    }

    /**
     * Adds a service definition to the component.
     * @param service Service definition to be added to the component.
     */
    public void addService(PSD service) {
        services.add(service);
    }
    
    /**
     * Returns the reference definitions available for this component.
     * @return Reference definitions for this operation.
     */
    public Set<PRD> getReferences() {
        return Collections.unmodifiableSet(references);
    }

    /**
     * Adds a reference definition to the component.
     * @param reference Reference definition to be added to the component.
     */
    public void addReference(PRD reference) {
        references.add(reference);
    }

}
