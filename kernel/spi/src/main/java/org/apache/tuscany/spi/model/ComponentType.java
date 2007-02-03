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
package org.apache.tuscany.spi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>The definition of the configurable aspects of an implementation in terms of the services it exposes, the services
 * it references, and properties that can be used to configure it.</p> <p>A service represents an addressable interface
 * provided by the implementation. Such a service may be the target of a wire from another component.</p> <p>A reference
 * represents a requirement that an implementation has on a service provided by another component or by a resource
 * outside the SCA system. Such a reference may be the source of a wire to another component.</p> <p>A property allows
 * the behaviour of the implementation to be configured through externally set values.</p> <p>A component type may also
 * declare that it wishes to be initialized upon activation of the scope that contains it and may specify an order
 * relative to other eagerly initializing components. For example, an implementation that pre-loads some form of cache
 * could declare that it should be eagerly initialized at the start of the scope so that the cache load occured on
 * startup rather than first use.</p>
 *
 * @version $Rev$ $Date$
 */
public class ComponentType<S extends ServiceDefinition, R extends ReferenceDefinition, P extends Property<?>>
    extends ModelObject {
    protected Scope implementationScope = Scope.UNDEFINED;
    private int initLevel;
    private long maxAge = -1;
    private long maxIdleTime = -1;
    private final Map<String, S> services = new HashMap<String, S>();
    private final Map<String, R> references = new HashMap<String, R>();
    private final Map<String, P> properties = new HashMap<String, P>();

    /**
     * Returns the component implementation scope
     */
    public Scope getImplementationScope() {
        return implementationScope;
    }

    /**
     * Sets the component implementation scope
     */
    public void setImplementationScope(Scope implementationScope) {
        this.implementationScope = implementationScope;
    }

    /**
     * Returns the default initialization level for components of this type. A value greater than zero indicates that
     * components should be eagerly initialized.
     *
     * @return the default initialization level
     */
    public int getInitLevel() {
        return initLevel;
    }

    /**
     * Sets the default initialization level for components of this type. A value greater than zero indicates that
     * components should be eagerly initialized.
     *
     * @param initLevel default initialization level for components of this type
     */
    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
    }

    /**
     * Returns true if this component should be eagerly initialized.
     *
     * @return true if this component should be eagerly initialized
     */
    public boolean isEagerInit() {
        return initLevel > 0;
    }

    /**
     * Returns the idle time allowed between operations in milliseconds if the implementation is conversational
     *
     * @return the idle time allowed between operations in milliseconds if the implementation is conversational
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * Sets the idle time allowed between operations in milliseconds if the implementation is conversational
     */
    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    /**
     * Returns the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     *
     * @return the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     */
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Returns a live Map of the services provided by the implementation.
     *
     * @return a live Map of the services provided by the implementation
     */
    public Map<String, S> getServices() {
        return services;
    }

    /**
     * Add a service to those provided by the implementation. Any existing service with the same name is replaced.
     *
     * @param service a service provided by the implementation
     */
    public void add(S service) {
        services.put(service.getUri().getFragment(), service);
    }

    /**
     * Returns a live Map of references to services consumed by the implementation.
     *
     * @return a live Map of references to services consumed by the implementation
     */
    public Map<String, R> getReferences() {
        return references;
    }

    /**
     * Add a reference to a service consumed by the implementation. Any existing reference with the same name is
     * replaced.
     *
     * @param reference a reference to a service consumed by the implementation
     */
    public void add(R reference) {
        references.put(reference.getUri().getFragment(), reference);
    }

    /**
     * Returns a live Map of properties that can be used to configure the implementation.
     *
     * @return a live Map of properties that can be used to configure the implementation
     */
    public Map<String, P> getProperties() {
        return properties;
    }

    /**
     * Add a property that can be used to configure the implementation. Any existing property with the same name is
     * replaced.
     *
     * @param property a property that can be used to configure the implementation
     */
    public void add(P property) {
        properties.put(property.getName(), property);
    }
}
