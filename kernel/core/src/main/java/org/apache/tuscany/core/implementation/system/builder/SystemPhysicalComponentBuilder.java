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
package org.apache.tuscany.core.implementation.system.builder;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.implementation.POJOPhysicalComponentBuilder;
import org.apache.tuscany.core.implementation.system.component.SystemComponent;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
public class SystemPhysicalComponentBuilder<T, GROUP>
    extends POJOPhysicalComponentBuilder<SystemPhysicalComponentDefinition<T, GROUP>, SystemComponent<T, GROUP>> {

    public SystemPhysicalComponentBuilder(
        @Reference(name = "builderRegistry")PhysicalComponentBuilderRegistry builderRegistry,
        @Reference(name = "scopeRegistry")ScopeRegistry scopeRegistry) {
        super(builderRegistry, scopeRegistry);
    }

    public SystemComponent<T, GROUP> build(SystemPhysicalComponentDefinition<T, GROUP> definition) {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        GROUP groupId = definition.getGroupId();
        ScopeContainer<GROUP, ?> scopeContainer = null;
        InstanceFactoryProvider<T> provider = definition.getProvider();
        return new SystemComponent<T, GROUP>(componentId, provider, scopeContainer, groupId, initLevel, -1, -1);
    }
}
