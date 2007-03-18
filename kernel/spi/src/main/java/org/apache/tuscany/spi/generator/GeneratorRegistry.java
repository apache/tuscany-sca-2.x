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

import java.net.URI;

import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.IntentDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ResourceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * A registry for generators
 *
 * @version $Rev$ $Date$
 */
public interface GeneratorRegistry {

    <T extends ComponentDefinition<? extends Implementation>> void register(Class<T> clazz,
                                                                            ComponentGenerator<T> generator);

    void register(Class<?> clazz, BindingGenerator generator);

    <T extends IntentDefinition> void register(Class<T> phase, InterceptorGenerator<T> generator);

    void register(Class<?> clazz, ResourceGenerator generator);

    /**
     * Generates a PhysicalComponentDefinition
     *
     * @param definition
     * @param context
     * @throws GenerationException
     */
    <C extends ComponentDefinition<? extends Implementation>> void generate(C definition, GeneratorContext context)
        throws GenerationException;


    /**
     * Generates a PhysicalWireDefinition from a service binding to a component service
     *
     * @param contract
     * @param bindingDefinition
     * @param componentDefinition
     * @param context
     * @param serviceDefinition
     * @throws GenerationException
     */
    <C extends ComponentDefinition<? extends Implementation>> void generateWire(ServiceContract<?> contract,
                                                                                BindingDefinition bindingDefinition,
                                                                                ServiceDefinition serviceDefinition,
                                                                                C componentDefinition,
                                                                                GeneratorContext context)
        throws GenerationException;


    <C extends ComponentDefinition<? extends Implementation>> void generateWire(C componentDefinition,
                                                                                ReferenceDefinition referenceDefinition,
                                                                                BindingDefinition bindingDefinition,
                                                                                GeneratorContext context)
        throws GenerationException;

    <S extends ComponentDefinition<? extends Implementation>, T extends ComponentDefinition<? extends Implementation>>
        void generateWire(S sourceDefinition,
                          ReferenceDefinition referenceDefinition,
                          ServiceDefinition serviceDefinition,
                          T targetDefinition,
                          GeneratorContext context) throws GenerationException;

    URI generate(ResourceDefinition definition, GeneratorContext context) throws GenerationException;

}
