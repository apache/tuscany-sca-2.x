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

package org.apache.tuscany.spi.builder.physical;

import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Responsible for attaching a wire to a source and target component. The mechanism for perforing the attach is specific
 * to the component implementation type.
 *
 * @version $Date$ $Revision$
 */
public interface WireAttacher<C extends Component,
    PWSD extends PhysicalWireSourceDefinition,
    PWTD extends PhysicalWireTargetDefinition> {

    /**
     * Attaches a wire to a source component.
     *
     * @param source           the source component
     * @param sourceDefinition metadata for performing the attach
     * @param target           the target component
     * @param targetDefinition metadata for performing the attach
     * @param wire             the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachToSource(C source,
                        PWSD sourceDefinition,
                        Component target,
                        PhysicalWireTargetDefinition targetDefinition,
                        Wire wire) throws WiringException;

    /**
     * Attaches a wire to a target component.
     *
     * @param source           the source component
     * @param sourceDefinition metadata for performing the attach
     * @param target           the target component
     * @param targetDefinition metadata for performing the attach
     * @param wire             the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachToTarget(Component source,
                        PhysicalWireSourceDefinition sourceDefinition,
                        C target,
                        PWTD targetDefinition,
                        Wire wire) throws WiringException;

}
