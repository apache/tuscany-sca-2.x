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
package org.apache.tuscany.spi.generator;

import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * Implementations are responsible for generating {@link org.apache.tuscany.spi.model.physical.PhysicalChangeSet}
 * metadata used to provision components to service nodes
 *
 * @version $Rev$ $Date$
 */
public interface ComponentGenerator<C extends ComponentDefinition<? extends Implementation>> {

    /**
     * Generates an {@link org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition} based on a {@link
     * org.apache.tuscany.spi.model.ComponentDefinition}. The resulting PhysicalComponentDefinition is added to the
     * PhysicalChangeSet associated with the current GeneratorContext.
     *
     * @param definition the component definition to evaluate
     * @param context    the current generator context, which contains the PhysicalChangeSet to be marshalled
     * @throws GenerationException if an error occurs during the generation process
     */
    void generate(C definition, GeneratorContext context) throws GenerationException;

    /**
     * Generates a {@link PhysicalWireSourceDefinition} used to attach a wire to a source component. Metadata contained
     * in the PhysicalWireSourceDefinition is specific to the component implementation type and used when the wire is
     * attached to its source on a service node.
     *
     * @param definition          the component definition for the wire source
     * @param referenceDefinition the source reference definition
     * @param optimizable         true is the wire may be optimized. ComponentGenerator implementations may decide to
     *                            attach optimizable wires is a specialized manner, such as by not generating proxies
     * @param context             the current generator context
     * @return the meta data used to attach the wire to its source on the service node
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalWireSourceDefinition generateWireSource(C definition,
                                                    ReferenceDefinition referenceDefinition,
                                                    boolean optimizable,
                                                    GeneratorContext context) throws GenerationException;

    /**
     * Generates a {@link PhysicalWireTargetDefinition} used to attach a wire to a target component. Metadata contained
     * in the PhysicalWireSourceDefinition is specific to the component implementation type and used when the wire is
     * attached to its target on a service node.
     *
     * @param definition        the component definition for the wire source
     * @param serviceDefinition the target refernce definition
     * @param context           the current generator context
     * @return the meta data used to attach the wire to its target on the service node
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalWireTargetDefinition generateWireTarget(C definition,
                                                    ServiceDefinition serviceDefinition,
                                                    GeneratorContext context) throws GenerationException;

}
