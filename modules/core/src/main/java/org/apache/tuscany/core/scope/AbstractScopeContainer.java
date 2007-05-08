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

import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.ScopedImplementationProvider;
import org.apache.tuscany.event.Event;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.component.GroupInitializationException;
import org.apache.tuscany.spi.component.PersistenceException;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetResolutionException;

/**
 * Implements functionality common to scope contexts.
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainer<KEY> extends AbstractLifecycle implements ScopeContainer<KEY> {
    protected Map<KEY, InstanceWrapper<?>> wrappers = new ConcurrentHashMap<KEY, InstanceWrapper<?>>();
    protected final Scope scope;

    protected RuntimeComponent component;

    public AbstractScopeContainer(Scope scope, RuntimeComponent component) {
        this.scope = scope;
        this.component = component;
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope container not running [" + getLifecycleState() + "]");
        }
    }

    /**
     * Creates a new physical instance of a component, wrapped in an
     * InstanceWrapper.
     * 
     * @param component the component whose instance should be created
     * @return a wrapped instance that has been injected but not yet started
     * @throws TargetResolutionException if there was a problem creating the
     *             instance
     */
    protected InstanceWrapper createInstanceWrapper() throws TargetResolutionException {
        Implementation impl = component.getImplementation();
        if (impl instanceof ScopedImplementationProvider) {
            return ((ScopedImplementationProvider)impl).createInstanceWrapper(component);
        }
        return null;
    }

    public InstanceWrapper getAssociatedWrapper(KEY contextId) throws TargetResolutionException {
        return null;
    }

    public Scope getScope() {
        return scope;
    }

    public InstanceWrapper getWrapper(KEY contextId) throws TargetResolutionException {
        return wrappers.get(contextId);
    }

    public void onEvent(Event event) {
    }

    protected boolean isEagerInit() {
        Implementation impl = component.getImplementation();
        if (impl instanceof ScopedImplementationProvider) {
            return ((ScopedImplementationProvider)impl).isEagerInit(component);
        }
        return false;
    }

    public void remove() throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    public void returnWrapper(InstanceWrapper wrapper, KEY contextId) throws TargetDestructionException {
    }

    public synchronized void start() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        setLifecycleState(RUNNING);
    }

    public void startContext(KEY contextId) throws GroupInitializationException {
        if(isEagerInit()) {
            try {
                getWrapper(contextId);
            } catch (TargetResolutionException e) {
                // 
            }
        }
    }

    public synchronized void stop() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        setLifecycleState(STOPPED);
    }

    public void stopContext(KEY contextId) {
        wrappers.remove(contextId);
    }

    public String toString() {
        return "In state [" + super.toString() + ']';
    }

    public RuntimeComponent getComponent() {
        return component;
    }

    public void setComponent(RuntimeComponent component) {
        this.component = component;
    }
}
