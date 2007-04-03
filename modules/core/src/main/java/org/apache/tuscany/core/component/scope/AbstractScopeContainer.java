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


import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

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
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

/**
 * Implements functionality common to scope contexts.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainer<KEY> extends AbstractLifecycle
    implements ScopeContainer<KEY> {

    private static final Comparator<AtomicComponent<?>> COMPARATOR = new Comparator<AtomicComponent<?>>() {
        public int compare(AtomicComponent<?> o1, AtomicComponent<?> o2) {
            return o1.getInitLevel() - o2.getInitLevel();
        }
    };

    protected final Map<AtomicComponent<?>, URI> componentGroups =
        new ConcurrentHashMap<AtomicComponent<?>, URI>();
    protected final Map<KEY, URI> contextGroups = new ConcurrentHashMap<KEY, URI>();

    // the queue of instanceWrappers to destroy, in the order that their instances were created
    protected final Map<KEY, List<InstanceWrapper<?>>> destroyQueues =
        new ConcurrentHashMap<KEY, List<InstanceWrapper<?>>>();

    // the queue of components to eagerly initialize in each group
    protected final Map<URI, List<AtomicComponent<?>>> initQueues =
        new HashMap<URI, List<AtomicComponent<?>>>();

    protected final ScopeContainerMonitor monitor;

    private final Scope scope;

    public AbstractScopeContainer(Scope scope, ScopeContainerMonitor monitor) {
        this.scope = scope;
        this.monitor = monitor;
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope container not running [" + getLifecycleState() + "]");
        }
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

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public Scope getScope() {
        return scope;
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    /**
     * Initialise an ordered list of components.
     * The list is traversed in order and the getWrapper() method called for each to
     * associate an instance with the supplied context.
     *
     * @param contextId  the contextId to associated with the component instances
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

    public void onEvent(Event event) {
    }

    public <T> void register(AtomicComponent<T> component, URI groupId) {
        checkInit();
        if (component.isEagerInit()) {
            componentGroups.put(component, groupId);
            synchronized (initQueues) {
                List<AtomicComponent<?>> initQueue = initQueues.get(groupId);
                if (initQueue == null) {
                    initQueue = new ArrayList<AtomicComponent<?>>();
                    initQueues.put(groupId, initQueue);
                }
                // FIXME it would be more efficient to binary search and then insert
                initQueue.add(component);
                Collections.sort(initQueue, COMPARATOR);
            }
        }
    }

    public <T> void remove(AtomicComponent<T> component) throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    public <T> void returnWrapper(AtomicComponent<T> component, InstanceWrapper<T> wrapper, KEY contextId)
        throws TargetDestructionException {
    }

    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        scopeRegistry.register(this);
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
            synchronized (instances) {
                if (instances.size() == 0) {
                    return;
                }
                toDestroy = instances.remove(instances.size() - 1);
            }
            try {
                toDestroy.stop();
            } catch (TargetDestructionException e) {
                // log the error from destroy but continue
                monitor.destructionError(e);
            }
        }
    }

    @Init
    public synchronized void start() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        setLifecycleState(RUNNING);
    }

    public void startContext(KEY contextId, URI groupId) throws GroupInitializationException {
        assert !contextGroups.containsKey(contextId);
        contextGroups.put(contextId, groupId);
        destroyQueues.put(contextId, new ArrayList<InstanceWrapper<?>>());

        // get and clone initialization queue
        List<AtomicComponent<?>> initQueue;
        synchronized (initQueues) {
            initQueue = initQueues.get(groupId);
            if (initQueue != null) {
                initQueue = new ArrayList<AtomicComponent<?>>(initQueue);
            }
        }
        if (initQueue != null) {
            initializeComponents(contextId, initQueue);
        }
    }

    @Destroy
    public synchronized void stop() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        setLifecycleState(STOPPED);
        componentGroups.clear();
        contextGroups.clear();
        synchronized (initQueues) {
            initQueues.clear();
        }
        destroyQueues.clear();
    }

    public void stopContext(KEY contextId) {
        assert contextGroups.containsKey(contextId);
        shutdownComponents(destroyQueues.get(contextId));
        contextGroups.remove(contextId);
        destroyQueues.remove(contextId);
    }

    public String toString() {
        return "In state [" + super.toString() + ']';
    }

    public <T> void unregister(AtomicComponent<T> component) {
        if (component.isEagerInit()) {
            URI groupId = componentGroups.remove(component);
            synchronized (initQueues) {
                List<AtomicComponent<?>> initQueue = initQueues.get(groupId);
                initQueue.remove(component);
                if (initQueue.isEmpty()) {
                    initQueues.remove(groupId);
                }
            }
        }
    }
}
