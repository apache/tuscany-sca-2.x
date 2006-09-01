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
package org.apache.tuscany.core.component.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeNotFoundException;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

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
    private WorkContext workContext;

    public ScopeRegistryImpl(WorkContext workContext) {
        assert workContext != null;
        this.workContext = workContext;
    }

    // TODO remove and replace with CDI
    public ScopeRegistryImpl() {
    }

    @Autowire
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    public ScopeContainer getScopeContainer(Scope scope) {
        assert Scope.MODULE != scope : "Cannot get MODULE scope from the registry";
        ScopeContainer container = scopeCache.get(scope);
        if (container == null) {
            ObjectFactory<? extends ScopeContainer> factory = factoryCache.get(scope);
            if (factory == null) {
                ScopeNotFoundException e = new ScopeNotFoundException("Scope object factory not registered for scope");
                switch (scope) {
                    case SESSION:
                        e.setIdentifier("SESSION");
                        break;
                    case REQUEST:
                        e.setIdentifier("REQUEST");
                        break;
                    case STATELESS:
                        e.setIdentifier("STATELESS");
                        break;
                    default:
                        e.setIdentifier("UNKNOWN");
                        break;
                }
                throw e;
            }
            container = factory.getInstance();
            container.setWorkContext(workContext);
            container.start();
            scopeCache.put(scope, container);
        }
        return container;
    }

    public <T extends ScopeContainer> void registerFactory(Scope scope, ObjectFactory<T> factory) {
        factoryCache.put(scope, factory);
    }

    public void deregisterFactory(Scope scope) {
        factoryCache.remove(scope);
    }


}
