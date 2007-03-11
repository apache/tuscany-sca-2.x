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

import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.osoa.sca.annotations.Reference;

/**
 * Abstract implementation that supports registering.
 * 
 * @param <PCD>
 * @param <C>
 */
public abstract class AbstractPhysicalComponentBuilder<PCD extends PhysicalComponentDefinition, C extends Component>
    implements PhysicalComponentBuilder<PCD, C> {
    
    /**
     * Gets the physical component definition this builder expects.
     * @return Physical component definition class.
     */
    protected abstract Class<PCD> getComponentType();

    /**
     * Injects builder registry.
     * @param scopeRegistry Scope registry.
     */
    @Reference
    public void setBuilderRegistry(PhysicalComponentBuilderRegistry registry) {
        registry.register(getComponentType(), this);
    }

}
