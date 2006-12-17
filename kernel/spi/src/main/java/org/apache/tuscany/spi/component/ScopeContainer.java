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
package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Scope;


/**
 * Manages the lifecycle and visibility of instances associated with a set of {@link AtomicComponent}s.
 *
 * @version $Rev$ $Date$
 * @see SCAObject
 */
public interface ScopeContainer extends Lifecycle, RuntimeEventListener {

    /**
     * Returns the scope value representing the scope context
     */
    Scope getScope();

    /**
     * Sets the work context used by the scope container
     */
    void setWorkContext(WorkContext workContext);

    /**
     * Registers a component with the scope component
     */
    void register(AtomicComponent component);

    /**
     * Returns an implementation instance associated with the current request context, creating one if necessary
     *
     * @throws TargetException
     */
    Object getInstance(AtomicComponent component) throws TargetException;

    /**
     * Returns an implementation instance associated with the current context. If no instance is found, a {@link
     * TargetNotFoundException} is thrown
     *
     * @throws TargetException
     */
    Object getAssociatedInstance(AtomicComponent component) throws TargetException;

    /**
     * Persists a new component implementation instance, equivalent to an insert or append operation
     *
     * @param component  the owning component
     * @param id         the id associated with the instance
     * @param instance   the instance to persist
     * @param expiration the expiration in milliseconds
     * @throws PersistenceException
     */
    void persistNew(AtomicComponent component, String id, Object instance, long expiration) throws PersistenceException;

    /**
     * Persists a component implementation instance, equivalent to an update operation
     *
     * @param component  the owning component
     * @param id         the id associated with the instance
     * @param instance   the instance to persist
     * @param expiration the expiration in milliseconds
     * @throws PersistenceException
     */
    void persist(AtomicComponent component, String id, Object instance, long expiration) throws PersistenceException;

    /**
     * Removes a component implementation instance associated with the current context from persistent storage
     *
     * @param component the owning component
     * @throws PersistenceException
     */
    void remove(AtomicComponent component) throws PersistenceException;
}
