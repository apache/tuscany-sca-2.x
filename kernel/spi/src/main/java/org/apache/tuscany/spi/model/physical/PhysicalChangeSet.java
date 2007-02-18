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

import java.util.HashSet;
import java.util.Set;

/**
 * Models a physical change set, sent from the master to the slave.
 * 
 * @version $Revsion$ $Date$
 *
 */
public class PhysicalChangeSet {
    
    // Set of physical component definitions
    private Set<PhysicalComponentDefinition> physicalComponentDefinitions = new HashSet<PhysicalComponentDefinition>();
    
    // Set of wire definitions
    private Set<WireDefinition> wireDefinitions = new HashSet<WireDefinition>();

    /**
     * Get all the physical component definitions.
     * @return Physical component definitions in the changeset.
     */
    public Set<? extends PhysicalComponentDefinition> getPhysicalComponentDefinitions() {
        return physicalComponentDefinitions;
    }

    /**
     * Adds a physical component definition to the physical change set.
     * @param physicalComponentDefinition Physical component definition.
     */
    public void addPhysicalComponentDefinition(PhysicalComponentDefinition physicalComponentDefinition) {
        physicalComponentDefinitions.add(physicalComponentDefinition);
    }

    /**
     * Get all the wire definitions.
     * @return Wire definitions in the changeset.
     */
    public Set<? extends WireDefinition> getWireDefinitions() {
        return wireDefinitions;
    }

    /**
     * Adds a wire definition to the physical change set.
     * @param wireDefinition Wire definition.
     */
    public void addWireDefinition(WireDefinition wireDefinition) {
        wireDefinitions.add(wireDefinition);
    }

}
