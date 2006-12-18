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


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.PersistenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.event.TrueFilter;

/**
 * Implements functionality common to scope contexts.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainer extends AbstractLifecycle implements ScopeContainer {
    private static final EventFilter TRUE_FILTER = new TrueFilter();

    protected WorkContext workContext;
    protected ScopeContainerMonitor monitor;
    private Map<EventFilter, List<RuntimeEventListener>> listeners;

    public AbstractScopeContainer(WorkContext workContext, ScopeContainerMonitor monitor) {
        this.workContext = workContext;
        this.monitor = monitor;
    }

    public void addListener(RuntimeEventListener listener) {
        addListener(TRUE_FILTER, listener);
    }

    public void removeListener(RuntimeEventListener listener) {
        assert listener != null;
        synchronized (getListeners()) {
            for (List<RuntimeEventListener> currentList : getListeners().values()) {
                for (RuntimeEventListener current : currentList) {
                    if (current == listener) {
                        currentList.remove(current);
                        return;
                    }
                }
            }
        }
    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {
        assert listener != null;
        synchronized (getListeners()) {
            List<RuntimeEventListener> list = getListeners().get(filter);
            if (list == null) {
                list = new CopyOnWriteArrayList<RuntimeEventListener>();
                listeners.put(filter, list);
            }
            list.add(listener);
        }
    }

    public void publish(Event event) {
        assert event != null;
        for (Map.Entry<EventFilter, List<RuntimeEventListener>> entry : getListeners().entrySet()) {
            if (entry.getKey().match(event)) {
                for (RuntimeEventListener listener : entry.getValue()) {
                    listener.onEvent(event);
                }
            }
        }
    }

    public Object getInstance(AtomicComponent component) throws TargetResolutionException {
        InstanceWrapper ctx = getInstanceWrapper(component, true);
        if (ctx != null) {
            if (!ctx.isStarted()) {
                ctx.start();
            }
            return ctx.getInstance();
        }
        return null;
    }

    public Object getAssociatedInstance(AtomicComponent component) throws TargetResolutionException {
        InstanceWrapper ctx = getInstanceWrapper(component, false);
        if (ctx != null) {
            if (!ctx.isStarted()) {
                ctx.start();
            }
            return ctx.getInstance();
        }
        throw new TargetNotFoundException(component.getName());
    }

    public void persistNew(AtomicComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");

    }

    public void persist(AtomicComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    public void remove(AtomicComponent component) throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    protected Map<EventFilter, List<RuntimeEventListener>> getListeners() {
        if (listeners == null) {
            listeners = new ConcurrentHashMap<EventFilter, List<RuntimeEventListener>>();
        }
        return listeners;
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

    protected abstract InstanceWrapper getInstanceWrapper(AtomicComponent component, boolean create)
        throws TargetResolutionException;
}
