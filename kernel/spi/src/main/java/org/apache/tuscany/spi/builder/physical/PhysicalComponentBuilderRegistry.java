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

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

/**
 * A registry of physical component builders.
 *
 * @version $Rev$ $Date$
 */
public interface PhysicalComponentBuilderRegistry {

    /**
     * Registers a physical component builder.
     *
     * @param <PCD>           Type of the physical component definition.
     * @param definitionClass Class of the physical component definition.
     * @param builder         Builder for the physical component definition.
     */
    <PCD extends PhysicalComponentDefinition, C extends Component>
        void register(Class<?> definitionClass,
                      PhysicalComponentBuilder<PCD, C> builder);

    /**
     * Builds a physical component from component definition.
     *
     * @param componentDefinition Component definition.
     * @return Component to be built.
     */
    Component build(PhysicalComponentDefinition componentDefinition) throws BuilderException;

}
