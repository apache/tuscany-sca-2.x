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
public abstract class PhysicalComponentDefinition extends ModelObject {

    // Component Id.
    private final URI componentId;

    // Inbound wires
    private final Set<WireDefinition> inboundWires = new HashSet<WireDefinition>();

    // Outbound wires
    private final Set<WireDefinition> outboundWires = new HashSet<WireDefinition>();

    /**
     * Initializes the component id.
     *
     * @param componentId The component id.
     */
    public PhysicalComponentDefinition(final URI componentId) {

        if (componentId == null) {
            throw new IllegalArgumentException("Component id is null");
        }
        this.componentId = componentId;

    }

    /**
     * Returns the absolute id for the phyiscal component.
     *
     * @return the absolute id for the phyiscal component
     */
    public URI getComponentId() {
        return componentId;
    }

    /**
     * Returns a read-only view of the inbound wires.
     *
     * @return List of inbound wires available on the component.
     */
    public Set<WireDefinition> getInboundWires() {
        return Collections.unmodifiableSet(inboundWires);
    }

    /**
     * Adds an inbound wire.
     *
     * @param inboundWire Inbound wire to add to the component.
     */
    public void addInboundWire(WireDefinition inboundWire) {

        if (inboundWire == null) {
            throw new IllegalArgumentException("Inbound wire is null");
        }
        inboundWires.add(inboundWire);

    }

    /**
     * Returns a read-only view of the outbound wires.
     *
     * @return List of outbound wires available on the component.
     */
    public Set<WireDefinition> getOutboundWires() {
        return Collections.unmodifiableSet(outboundWires);
    }

    /**
     * Adds an outbound wire.
     *
     * @param outboundWire Outbound wire to add to the component.
     */
    public void addOutboundWire(WireDefinition outboundWire) {

        if (outboundWire == null) {
            throw new IllegalArgumentException("Outbound wire is null");
        }
        outboundWires.add(outboundWire);

    }

}
