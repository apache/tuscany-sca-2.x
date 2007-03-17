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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.GroupInitializationException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.PersistenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

/**
 * Implements functionality common to scope contexts.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainer<GROUP, KEY> extends AbstractLifecycle
    implements ScopeContainer<GROUP, KEY> {

    private static final Comparator<AtomicComponent<?>> COMPARATOR = new Comparator<AtomicComponent<?>>() {
        public int compare(AtomicComponent<?> o1, AtomicComponent<?> o2) {
            return o1.getInitLevel() - o2.getInitLevel();
        }
    };

    private final Scope scope;
    protected final ScopeContainerMonitor monitor;

    protected final Map<GROUP, Set<AtomicComponent<?>>> groups =
        new ConcurrentHashMap<GROUP, Set<AtomicComponent<?>>>();
    protected final Map<AtomicComponent<?>, GROUP> componentGroups =
        new ConcurrentHashMap<AtomicComponent<?>, GROUP>();

    protected final Map<KEY, GROUP> contextGroups = new ConcurrentHashMap<KEY, GROUP>();

    // the queue of components to eagerly initialize in each group
    protected final Map<GROUP, List<AtomicComponent<?>>> initQueues =
        new ConcurrentHashMap<GROUP, List<AtomicComponent<?>>>();

    // the queue of instanceWrappers to destroy, in the order that their instances were created
    protected final Map<KEY, List<InstanceWrapper<?>>> destroyQueues =
        new ConcurrentHashMap<KEY, List<InstanceWrapper<?>>>();


    protected WorkContext workContext;

    @Deprecated
    public AbstractScopeContainer(Scope scope, WorkContext workContext, ScopeContainerMonitor monitor) {
        this.scope = scope;
        this.workContext = workContext;
        this.monitor = monitor;
    }

    public AbstractScopeContainer(Scope scope, ScopeContainerMonitor monitor) {
        this.scope = scope;
        this.monitor = monitor;
    }

    public Scope getScope() {
        return scope;
    }

    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        scopeRegistry.register(this);
    }

    @Init
    public synchronized void start() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        setLifecycleState(RUNNING);
    }

    @Destroy
    public synchronized void stop() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        setLifecycleState(STOPPED);
        groups.clear();
        componentGroups.clear();
        contextGroups.clear();
        initQueues.clear();
        destroyQueues.clear();
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope container not running [" + getLifecycleState() + "]");
        }
    }

    public void onEvent(Event event) {
    }

    public <T> void register(AtomicComponent<T> component, GROUP groupId) {
        checkInit();
        assert groups.containsKey(groupId);
        Set<AtomicComponent<?>> components = groups.get(groupId);
        components.add(component);
        componentGroups.put(component, groupId);
        if (component.isEagerInit()) {
            List<AtomicComponent<?>> initQueue = initQueues.get(groupId);
            // FIXME it would be more efficient to binary search and then insert
            initQueue.add(component);
            Collections.sort(initQueue, COMPARATOR);
        }
    }

    public <T> void unregister(AtomicComponent<T> component) {
        GROUP groupId = componentGroups.remove(component);
        assert groupId != null;
        Set<AtomicComponent<?>> components = groups.get(groupId);
        components.remove(component);
    }

    public void createGroup(GROUP groupId) {
        assert !groups.containsKey(groupId);
        groups.put(groupId, new HashSet<AtomicComponent<?>>());
        initQueues.put(groupId, new ArrayList<AtomicComponent<?>>());
    }

    protected Set<AtomicComponent<?>> getGroupMembers(GROUP groupId) {
        return groups.get(groupId);
    }

    public void removeGroup(GROUP groupId) {
        assert groups.containsKey(groupId);
        groups.remove(groupId);
        initQueues.remove(groupId);
    }

    public void startContext(KEY contextId, GROUP groupId) throws GroupInitializationException {
        assert !contextGroups.containsKey(contextId);
        contextGroups.put(contextId, groupId);
        destroyQueues.put(contextId, new ArrayList<InstanceWrapper<?>>());
        initializeComponents(contextId, initQueues.get(groupId));
    }

    protected GROUP getContextGroup(KEY contextId) {
        return contextGroups.get(contextId);
    }

    public void stopContext(KEY contextId) {
        assert contextGroups.containsKey(contextId);
        shutdownComponents(destroyQueues.get(contextId));
        contextGroups.remove(contextId);
        destroyQueues.remove(contextId);
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public <T> void returnWrapper(AtomicComponent<T> component, InstanceWrapper<T> wrapper, KEY contextId)
        throws TargetDestructionException {
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent component) throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component) throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper)
        throws TargetDestructionException {
    }

    public <T> void remove(AtomicComponent<T> component) throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    protected WorkContext getWorkContext() {
        return workContext;
    }

    /**
     * Initialise an ordered list of components.
     * The list is traversed in order and the getWrapper() method called for each to
     * associate an instance with the supplied context.
     *
     * @param contextId the contextId to associated with the component instances
     * @param components the components to be initialized
     * @throws GroupInitializationException if one or more components threw an exception during initialization
     */
    protected void initializeComponents(KEY contextId, List<AtomicComponent<?>> components)
        throws GroupInitializationException {
        List<Exception> causes = null;
        for (AtomicComponent<?> component : components) {
            try {
                getWrapper(component, contextId);

            } catch (Exception e) {
                if (causes == null) {
                    causes = new ArrayList<Exception>();
                }
                causes.add(e);
            }
        }
        if (causes != null) {
            throw new GroupInitializationException(String.valueOf(contextId), causes);
        }
    }

    /**
     * Shut down an ordered list of instances.
     * The list passed to this method is treated as a live, mutable list
     * so any instances added to this list as shutdown is occuring will also be shut down.
     *
     * @param instances the list of instances to shutdown
     */
    protected void shutdownComponents(List<InstanceWrapper<?>> instances) {
        while (true) {
            InstanceWrapper<?> toDestroy;
            synchronized(instances) {
                if (instances.size() == 0) {
                    return;
                }
                toDestroy = instances.remove(instances.size()-1);
            }
            try {
                toDestroy.stop();
            } catch (TargetDestructionException e) {
                // log the error from destroy but continue
                monitor.destructionError(e);
            }
        }
    }

    public String toString() {
        return "In state [" + super.toString() + ']';
    }

    /**
     * Creates a new physical instance of a component, wrapped in an InstanceWrapper.
     *
     * @param component the component whose instance should be created
     * @return a wrapped instance that has been injected but not yet started
     * @throws TargetResolutionException if there was a problem creating the instance
     */
    protected <T> InstanceWrapper<T> createInstance(AtomicComponent<T> component) throws TargetResolutionException {
        return component.createInstanceWrapper();
    }
}
