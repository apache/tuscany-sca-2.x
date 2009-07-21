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

package org.apache.tuscany.sca.endpoint.tribes;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Map that can fire events on put/remove of entries
 */
public abstract class MapStore extends ConcurrentHashMap<Object, Object> {
    private static final long serialVersionUID = -2127235547082144368L;
    private List<MapListener> listeners = new CopyOnWriteArrayList<MapListener>();

    protected MapStore(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public Object put(Object key, Object value) {
        Object old = super.put(key, value);
        if (old != null) {
            for (MapListener listener : listeners) {
                listener.entryUpdated(key, old, value);
            }
        } else {
            for (MapListener listener : listeners) {
                listener.entryAdded(key, value);
            }

        }
        return old;
    }

    @Override
    public Object remove(Object key) {
        Object old = super.remove(key);
        if (old != null) {
            for (MapListener listener : listeners) {
                listener.entryRemoved(key, old);
            }
        }
        return old;
    }

    public void addListener(MapListener listener) {
        listeners.add(listener);
    }

    public List<MapListener> getListeners() {
        return listeners;
    }

    public boolean removeListener(MapListener listener) {
        return listeners.remove(listener);
    }

    public static interface MapListener {
        void entryAdded(Object key, Object value);

        void entryUpdated(Object key, Object oldValue, Object newValue);

        void entryRemoved(Object key, Object value);
    }
}
