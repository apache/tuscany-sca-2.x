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
package org.apache.tuscany.core.builder.physical;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

/**
 * Default map-based implementation of the physical component builder registry.
 * <p/>
 * TODO may be we can factor out all the registries into a parameterized one.
 *
 * @version $Rev$ $Date$
 */
public class DefaultPhysicalComponentBuilderRegistry implements PhysicalComponentBuilderRegistry {

    // Internal cache
    private Map<Class<?>,
        PhysicalComponentBuilder<? extends PhysicalComponentDefinition, ? extends Component>> registry =
        new ConcurrentHashMap<Class<?>,
            PhysicalComponentBuilder<? extends PhysicalComponentDefinition, ? extends Component>>();

    /**
     * Registers a physical component builder.
     *
     * @param <PCD>           Type of the physical component definition.
     * @param definitionClass Class of the physical component definition.
     * @param builder         Builder for the physical component definition.
     */
    public <PCD extends PhysicalComponentDefinition,
        C extends Component> void register(Class<?> definitionClass, PhysicalComponentBuilder<PCD, C> builder) {
        registry.put(definitionClass, builder);
    }

    /**
     * Builds a physical component from component definition.
     *
     * @param componentDefinition Component definition.
     * @return Component to be built.
     */
    @SuppressWarnings("unchecked")
    public Component build(PhysicalComponentDefinition componentDefinition) throws BuilderException {

        PhysicalComponentBuilder builder = registry.get(componentDefinition.getClass());
        if(builder == null) {
            throw new BuilderConfigException("Builder not found for " + componentDefinition.getClass());
        }
        return builder.build(componentDefinition);

    }

}
