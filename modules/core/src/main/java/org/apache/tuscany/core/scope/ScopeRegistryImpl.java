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
package org.apache.tuscany.core.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * The default implementation of a scope registry
 *
 * @version $Rev$ $Date$
 */
public class ScopeRegistryImpl implements ScopeRegistry {
    private final Map<Scope, ScopeContainer> scopeCache =
        new ConcurrentHashMap<Scope, ScopeContainer>();
    private final Map<Scope, ObjectFactory<? extends ScopeContainer>> factoryCache =
        new ConcurrentHashMap<Scope, ObjectFactory<? extends ScopeContainer>>();

    public void register(ScopeContainer container) {
        scopeCache.put(container.getScope(), container);
    }

    public ScopeContainer getScopeContainer(Scope scope) {
        return scopeCache.get(scope);
    }


}
