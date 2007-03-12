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

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.ReflectiveInstanceFactoryProvider;
import org.apache.tuscany.core.implementation.system.component.SystemComponent;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.component.ScopeContainer;

/**
 * @version $Rev$ $Date$
 */
public class SystemPhysicalComponentBuilder<T>
    implements PhysicalComponentBuilder<SystemPhysicalComponentDefinition<T>, SystemComponent<T>> {

    public SystemComponent<T> build(SystemPhysicalComponentDefinition<T> definition) {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        Method initMethod = definition.getInitMethod();
        Method destroyMethod = definition.getDestroyMethod();
        Constructor<T> constructor = null;
        List<URI> constructorNames = null;
        Map<URI, Member> injectionSites = null;
        ScopeContainer scopeContainer = null;
        InstanceFactoryProvider<T> provider = new ReflectiveInstanceFactoryProvider<T>(constructor,
                                                                                       constructorNames,
                                                                                       injectionSites,
                                                                                       initMethod,
                                                                                       destroyMethod);
        SystemComponent<T> component = new SystemComponent<T>(componentId, provider, scopeContainer, initLevel, -1, -1);
        return component;
    }
}
