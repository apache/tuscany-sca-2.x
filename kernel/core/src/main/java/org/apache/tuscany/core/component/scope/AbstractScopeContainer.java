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


import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.PersistenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * Implements functionality common to scope contexts.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainer<GROUP, KEY> extends AbstractLifecycle
    implements ScopeContainer<GROUP, KEY> {
    private final Scope scope;

    protected WorkContext workContext;
    protected ScopeContainerMonitor monitor;

    public AbstractScopeContainer(Scope scope, WorkContext workContext, ScopeContainerMonitor monitor) {
        this.scope = scope;
        this.workContext = workContext;
        this.monitor = monitor;
    }

    public Scope getScope() {
        return scope;
    }

    public void register(GROUP groupId, AtomicComponent component) {
        checkInit();
    }

    public void unregister(AtomicComponent component) {
    }

    public void createGroup(GROUP groupId) {
    }

    public void removeGroup(GROUP groupId) {
    }

    public void startContext(KEY contextId) {
    }

    public void stopContext(KEY contextId) {
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper, KEY contextId)
        throws TargetDestructionException {
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent component) throws TargetResolutionException {
        return getInstanceWrapper(component, true);
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component) throws TargetResolutionException {
        InstanceWrapper<T> wrapper = getInstanceWrapper(component, false);
        if (wrapper == null) {
            throw new TargetNotFoundException(component.getUri().toString());
        }
        return wrapper;
    }

    public <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper)
        throws TargetDestructionException {
    }

    public void remove(AtomicComponent component) throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope container not running [" + getLifecycleState() + "]");
        }
    }

    protected WorkContext getWorkContext() {
        return workContext;
    }

    public String toString() {
        return "In state [" + super.toString() + ']';
    }

    protected abstract <T> InstanceWrapper<T> getInstanceWrapper(AtomicComponent component, boolean create)
        throws TargetResolutionException;
}
