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
 * Represents a physical service.
 * 
 * @version $Revision$ $Date$
 *
 */
public class PhysicalServiceDefinition extends ModelObject {
    
    // The name of the service
    private String name;
    
    // Operations available on this wire
    private Set<PhysicalOperationDefinition> operations = new HashSet<PhysicalOperationDefinition>();

    /**
     * Returns a read-only view of the available operations.
     * @return Operations available on the wire.
     */
    public Set<PhysicalOperationDefinition> getOperations() {
        return Collections.unmodifiableSet(operations);
    }

    /**
     * Adds an operation definition.
     * @param operation Operation to be added to the wire.
     */
    public void addOperation(PhysicalOperationDefinition operation) {
        operations.add(operation);
    }

    /**
     * Sets the name of the service.
     * @param name Name of the service.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the service.
     * @return Name of the service.
     */
    public String getName() {
        return name;
    }

}
