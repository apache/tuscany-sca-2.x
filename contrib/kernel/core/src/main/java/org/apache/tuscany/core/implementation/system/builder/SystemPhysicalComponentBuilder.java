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
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.core.implementation.POJOPhysicalComponentBuilder;
import org.apache.tuscany.core.implementation.system.component.SystemComponent;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalWireSourceDefinition;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalWireTargetDefinition;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import static org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType.REFERENCE;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.builder.physical.WireAttacherRegistry;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(interfaces = {PhysicalComponentBuilder.class, WireAttacher.class})
public class SystemPhysicalComponentBuilder<T>
    extends POJOPhysicalComponentBuilder<SystemPhysicalComponentDefinition<T>, SystemComponent<T>>
    implements WireAttacher<SystemComponent, SystemPhysicalWireSourceDefinition, SystemPhysicalWireTargetDefinition> {

    public SystemPhysicalComponentBuilder(
        @Reference(name = "builderRegistry")PhysicalComponentBuilderRegistry builderRegistry,
        @Reference(name = "wireAttacherRegistry")WireAttacherRegistry wireAttacherRegistry,
        @Reference(name = "scopeRegistry")ScopeRegistry scopeRegistry,
        @Reference(name = "providerBuilders")IFProviderBuilderRegistry providerBuilders,
        @Reference(name = "classloaderRegistry")ClassLoaderRegistry classLoaderRegistry) {
        super(builderRegistry, wireAttacherRegistry, scopeRegistry, providerBuilders, classLoaderRegistry);
    }

    @Init
    public void init() {
        builderRegistry.register(SystemPhysicalComponentDefinition.class, this);
        wireAttacherRegistry.register(SystemPhysicalWireSourceDefinition.class, this);
        wireAttacherRegistry.register(SystemPhysicalWireTargetDefinition.class, this);
    }

    public SystemComponent<T> build(SystemPhysicalComponentDefinition<T> definition) throws BuilderException {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        URI groupId = definition.getGroupId();
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(definition.getClassLoaderId());

        // get the scope container for this component
        Scope scope = definition.getScope();
        ScopeContainer<?> scopeContainer = scopeRegistry.getScopeContainer(scope);

        // create the InstanceFactoryProvider based on the definition in the model
        InstanceFactoryProviderDefinition<T> providerDefinition = definition.getInstanceFactoryProviderDefinition();
        InstanceFactoryProvider<T> provider = providerBuilders.build(providerDefinition, classLoader);

        return new SystemComponent<T>(componentId, provider, scopeContainer, groupId, initLevel, -1, -1);
    }

    public void attachToSource(SystemComponent source,
                               SystemPhysicalWireSourceDefinition sourceDefinition, Component target,
                               PhysicalWireTargetDefinition targetDefinition, Wire wire
    ) throws WiringException {
        assert target instanceof AtomicComponent;
        AtomicComponent<?> targetComponent = (AtomicComponent<?>) target;
        URI sourceUri = sourceDefinition.getUri();
        InjectionSource referenceSource = new InjectionSource(REFERENCE, sourceUri.getFragment());
        ObjectFactory<?> factory = targetComponent.createObjectFactory();
        source.setObjectFactory(referenceSource, factory);
    }

    public void attachToTarget(Component source, PhysicalWireSourceDefinition sourceDefinition, SystemComponent component,
                               SystemPhysicalWireTargetDefinition targetDefinition, Wire wire
    ) throws WiringException {
        // nothing to do here as the wire will always be optimized
    }
}
