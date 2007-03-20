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

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.implementation.system.component.SystemComponent;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;

/**
 * @version $Rev$ $Date$
 */
public class SystemPhysicalComponentBuilderTestCase<T> extends TestCase {
    private PhysicalComponentBuilderRegistry builderRegistry;
    private ScopeRegistry scopeRegistry;
    private IFProviderBuilderRegistry providerBuilders;
    private InstanceFactoryProviderDefinition<T> providerDefinition;
    private InstanceFactoryProvider<T> instanceFactoryProvider;
    private SystemPhysicalComponentBuilder<T> builder;
    private SystemPhysicalComponentDefinition<T> definition;
    private URI componentId;
    private URI groupId;
    private ClassLoaderRegistry classLoaderRegistry;
    private ClassLoader classLoader;

    public void testBuildSimplePOJO() throws Exception {
        SystemComponent<T> component = builder.build(definition);
        assertEquals(componentId, component.getUri());
        assertEquals(-1, component.getInitLevel());
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        groupId = URI.create("sca://./composite");
        componentId = URI.create("sca://./component");

        builderRegistry = EasyMock.createMock(PhysicalComponentBuilderRegistry.class);
        scopeRegistry = EasyMock.createMock(ScopeRegistry.class);

        classLoader = getClass().getClassLoader();
        classLoaderRegistry = EasyMock.createMock(ClassLoaderRegistry.class);
        EasyMock.expect(classLoaderRegistry.getClassLoader(groupId)).andStubReturn(classLoader);
        EasyMock.replay(classLoaderRegistry);

        providerBuilders = EasyMock.createMock(IFProviderBuilderRegistry.class);
        providerDefinition = new InstanceFactoryProviderDefinition();
        instanceFactoryProvider = EasyMock.createMock(InstanceFactoryProvider.class);
        EasyMock.expect(providerBuilders.build(providerDefinition, classLoader)).andStubReturn(instanceFactoryProvider);
        EasyMock.replay(providerBuilders);

        builder = new SystemPhysicalComponentBuilder<T>(builderRegistry,
                                                        null,
                                                        scopeRegistry,
                                                        providerBuilders,
                                                        classLoaderRegistry);

        definition = new SystemPhysicalComponentDefinition<T>();
        definition.setGroupId(groupId);
        definition.setComponentId(componentId);
        definition.setClassLoaderId(groupId);
        definition.setScope(Scope.COMPOSITE);
        definition.setInitLevel(-1);
        definition.setInstanceFactoryProviderDefinition(providerDefinition);
    }
}
