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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;

/**
 * A scope context which manages atomic component instances keyed by module
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeContainer extends AbstractScopeContainer {

    private static final InstanceWrapper EMPTY = new EmptyWrapper();
    private static final ComponentInitComparator COMPARATOR = new ComponentInitComparator();

    private final Map<AtomicComponent, InstanceWrapper> instanceWrappers;

    // the queue of instanceWrappers to destroy, in the order that their instances were created
    private final List<InstanceWrapper> destroyQueue;

    public ModuleScopeContainer() {
        this(null);
    }

    public ModuleScopeContainer(WorkContext workContext) {
        super("Module Scope", workContext);
        instanceWrappers = new ConcurrentHashMap<AtomicComponent, InstanceWrapper>();
        destroyQueue = new ArrayList<InstanceWrapper>();
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof CompositeStart) {
            eagerInitComponents();
            lifecycleState = RUNNING;
        } else if (event instanceof CompositeStop) {
            shutdownContexts();
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        checkInit();
        instanceWrappers.clear();
        synchronized (destroyQueue) {
            destroyQueue.clear();
        }
        lifecycleState = STOPPED;
    }

    /**
     * Notifies instanceWrappers of a shutdown in reverse order to which they were started
     */
    private void shutdownContexts() {
        if (destroyQueue.size() == 0) {
            return;
        }
        synchronized (destroyQueue) {
            // shutdown destroyable instances in reverse instantiation order
            ListIterator<InstanceWrapper> iter = destroyQueue.listIterator(destroyQueue.size());
            while (iter.hasPrevious()) {
                iter.previous().stop();
            }
            destroyQueue.clear();
        }
    }

    public void register(AtomicComponent component) {
        checkInit();
        instanceWrappers.put(component, EMPTY);
    }

    protected InstanceWrapper getInstanceWrapper(AtomicComponent component) throws TargetException {
        checkInit();
        InstanceWrapper ctx = instanceWrappers.get(component);
        assert ctx != null : "Component not registered with scope: " + component;
        if (ctx == EMPTY) {
            ctx = new InstanceWrapperImpl(component, component.createInstance());
            ctx.start();
            instanceWrappers.put(component, ctx);
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void eagerInitComponents() throws CoreRuntimeException {
        List<AtomicComponent> componentList = new ArrayList<AtomicComponent>(instanceWrappers.keySet());
        Collections.sort(componentList, COMPARATOR);
       // start each group
        for (AtomicComponent component : componentList) {
            if (component.getInitLevel() <= 0) {
                // Don't eagerly init
                continue;
            }
            // the instance could have been created from a depth-first traversal
            InstanceWrapper ctx = instanceWrappers.get(component);
            if (ctx == EMPTY) {
                ctx = new InstanceWrapperImpl(component, component.createInstance());
                ctx.start();
                instanceWrappers.put(component, ctx);
                destroyQueue.add(ctx);
            }
        }
    }

    static private class ComponentInitComparator implements Comparator<AtomicComponent> {
        public int compare(AtomicComponent o1, AtomicComponent o2) {
            if (o1.getInitLevel() > o2.getInitLevel()) {
                return -1; // The lower level starts first (except nagative)
            } else if (o1.getInitLevel() < o2.getInitLevel()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private static class EmptyWrapper extends AbstractLifecycle implements InstanceWrapper {
        public Object getInstance() {
            return null;
        }
    }
}
