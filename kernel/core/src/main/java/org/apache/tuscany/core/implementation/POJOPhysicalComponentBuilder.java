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
package org.apache.tuscany.core.implementation;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;

/**
 * Base class for PhysicalComponentBuilders that build components based on POJOs.
 *
 * @version $Rev$ $Date$
 */
public abstract class POJOPhysicalComponentBuilder<PCD extends PhysicalComponentDefinition, C extends Component>
    implements PhysicalComponentBuilder<PCD, C> {

    private final PhysicalComponentBuilderRegistry builderRegistry;
    private final ScopeRegistry scopeRegistry;

    protected POJOPhysicalComponentBuilder(
        @Reference(name = "builderRegistry")PhysicalComponentBuilderRegistry builderRegistry,
        @Reference(name = "scopeRegistry")ScopeRegistry scopeRegistry) {
        this.builderRegistry = builderRegistry;
        this.scopeRegistry = scopeRegistry;
    }

    @Init
    void init() {
        builderRegistry.register(SystemPhysicalComponentDefinition.class, this);
    }
}
