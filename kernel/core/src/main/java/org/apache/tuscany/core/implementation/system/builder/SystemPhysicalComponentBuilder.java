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
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.core.implementation.POJOPhysicalComponentBuilder;
import org.apache.tuscany.core.implementation.system.component.SystemComponent;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
public class SystemPhysicalComponentBuilder<T>
    extends POJOPhysicalComponentBuilder<SystemPhysicalComponentDefinition<T>, SystemComponent<T>> {

    public SystemPhysicalComponentBuilder(
        @Reference(name = "builderRegistry")PhysicalComponentBuilderRegistry builderRegistry,
        @Reference(name = "scopeRegistry")ScopeRegistry scopeRegistry,
        @Reference(name = "providerBuilders")IFProviderBuilderRegistry providerBuilders) {
        super(builderRegistry, scopeRegistry, providerBuilders);
    }

    public SystemComponent<T> build(SystemPhysicalComponentDefinition<T> definition) throws BuilderException {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        URI groupId = definition.getGroupId();

        ScopeContainer<?> scopeContainer = null;

        InstanceFactoryProviderDefinition<T> providerDefinition = definition.getInstanceFactoryProviderDefinition();
        InstanceFactoryProvider<T> provider = providerBuilders.build(providerDefinition, null);
        return new SystemComponent<T>(componentId, provider, scopeContainer, groupId, initLevel, -1, -1);
    }
}
