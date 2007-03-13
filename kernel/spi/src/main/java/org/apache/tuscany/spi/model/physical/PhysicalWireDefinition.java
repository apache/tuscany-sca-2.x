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
 * Model class representing the portable definition of a wire. This class is used to describe the inbound and outbound
 * wires on a physical component definition.
 *
 * @version $Rev$ $Date$
 */
public class PhysicalWireDefinition extends ModelObject {

    // Target definition
    private PhysicalWireTargetDefinition target;

    // Source definition
    private PhysicalWireSourceDefinition source;

    // Collection of operations
    private Set<PhysicalOperationDefinition> operations = new HashSet<PhysicalOperationDefinition>();

    /**
     * Adds an operation definition.
     *
     * @param operation Operation to be added.
     */
    public void addOperation(PhysicalOperationDefinition operation) {
        operations.add(operation);
    }


    /**
     * Returns a read-only view of the available operations.
     *
     * @return Collection of operations.
     */
    public Set<PhysicalOperationDefinition> getOperations() {
        return Collections.unmodifiableSet(operations);
    }


    /**
     * Returns a read-only view of the available non callback operations.
     *
     * @return Collection of non-callback operations.
     */
    public Set<PhysicalOperationDefinition> getNonCallbackOperations() {
        Set<PhysicalOperationDefinition> nonCallbackOperations = new HashSet<PhysicalOperationDefinition>();
        for (PhysicalOperationDefinition operation : operations) {
            if (!operation.isCallback()) {
                nonCallbackOperations.add(operation);
            }
        }
        return nonCallbackOperations;
    }

    /**
     * Returns a read-only view of the available callback operations.
     *
     * @return Collection of callback operations.
     */
    public Set<PhysicalOperationDefinition> getCallbackOperations() {
        Set<PhysicalOperationDefinition> callbackOperations = new HashSet<PhysicalOperationDefinition>();
        for (PhysicalOperationDefinition operation : operations) {
            if (operation.isCallback()) {
                callbackOperations.add(operation);
            }
        }
        return callbackOperations;
    }

    /**
     * Gets the Wire source URI.
     *
     * @return Wire source URI.
     */
    public URI getSourceUri() {
        return source.getUri();
    }

    /**
     * Gets the Wire target URI.
     *
     * @return Wire target URI.
     */
    public URI getTargetUri() {
        return target.getUri();
    }

    /**
     * Gets the wire source.
     *
     * @return Wire source.
     */
    public PhysicalWireSourceDefinition getSource() {
        return source;
    }

    /**
     * Sets the wire source.
     *
     * @param source Wire source.
     */
    public void setSource(PhysicalWireSourceDefinition source) {
        this.source = source;
    }

    /**
     * Gets the wire target.
     *
     * @return Wire target.
     */
    public PhysicalWireTargetDefinition getTarget() {
        return target;
    }

    /**
     * Sets the wire target.
     *
     * @param target Wire target.
     */
    public void setTarget(PhysicalWireTargetDefinition target) {
        this.target = target;

    }

}
