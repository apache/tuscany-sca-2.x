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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Models a physical change set, sent from the master to the slave.
 * 
 * @version $Revsion$ $Date$
 *
 */
public class PhysicalChangeSet extends ModelObject {
    
    // Set of physical component definitions
    private Set<PhysicalComponentDefinition> componentDefinitions = new HashSet<PhysicalComponentDefinition>();
    
    // Set of wire definitions
    private Set<PhysicalWireDefinition> wireDefinitions = new HashSet<PhysicalWireDefinition>();

    /**
     * Get all the physical component definitions.
     * @return Physical component definitions in the changeset.
     */
    public Set<PhysicalComponentDefinition> getComponentDefinitions() {
        return Collections.unmodifiableSet(componentDefinitions);
    }

    /**
     * Get all the wire definitions.
     * @return Wire definitions in the changeset.
     */
    public Set<PhysicalWireDefinition> getWireDefinitions() {
        return Collections.unmodifiableSet(wireDefinitions);
    }

    /**
     * Adds a physical component definition to the physical change set.
     * @param componentDefinition Physical component definition.
     */
    public void addComponentDefinition(PhysicalComponentDefinition componentDefinition) {
        componentDefinitions.add(componentDefinition);
    }

    /**
     * Adds a physical wire definition to the physical change set.
     * @param wireDefinition Physical wire definition.
     */
    public void addWireDefinition(PhysicalWireDefinition wireDefinition) {
        wireDefinitions.add(wireDefinition);
    }

}
