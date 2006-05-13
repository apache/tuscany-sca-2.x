/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.core.AbstractLifecycle;
import org.apache.tuscany.core.context.event.filter.TrueFilter;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;

/**
 * Functionality common to all <code>Context<code> implementations
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public abstract class AbstractContext<T> extends AbstractLifecycle implements Context<T> {

    protected CompositeContext parentContext;
    protected Map<EventFilter, List<RuntimeEventListener>> listeners;
    protected static final EventFilter TRUE_FILTER = new TrueFilter();

    public AbstractContext() {
    }

    public AbstractContext(String name) {
        super(name);
    }

    public CompositeContext getParent() {
        return parentContext;
    }

    public void setParent(CompositeContext parent) {
        parentContext = parent;
    }

    public void addListener(RuntimeEventListener listener) {
        addListener(TRUE_FILTER, listener);
    }

    public void removeListener(RuntimeEventListener listener) {
        assert (listener != null) : "Listener cannot be null";
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
        assert (listener != null) : "Listener cannot be null";
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
        assert(event != null): "Event object was null";
        for (Map.Entry<EventFilter, List<RuntimeEventListener>> entry : getListeners().entrySet()) {
            if (entry.getKey().match(event)) {
                for (RuntimeEventListener listener : entry.getValue()) {
                    listener.onEvent(event);
                }
            }
        }
    }

    protected Map<EventFilter, List<RuntimeEventListener>> getListeners() {
        if (listeners == null) {
            listeners = new ConcurrentHashMap<EventFilter, List<RuntimeEventListener>>();
        }
        return listeners;
    }

}
