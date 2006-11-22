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
package org.apache.tuscany.persistence.store.journal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements a simple LRU cache
 */
public class LRUCache extends LinkedHashMap<RecordKey, RecordEntry> {
    private List<CacheEventListener> listeners;
    private int maxCacheSize;

    public LRUCache(int maxCacheSize) {
        super(0, 0.75f, true);
        this.maxCacheSize = maxCacheSize;
    }

    public void addListener(CacheEventListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<CacheEventListener>();
        }
        listeners.add(listener);
    }

    public void removeListener(CacheEventListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    protected boolean removeEldestEntry(Map.Entry<RecordKey, RecordEntry> eldest) {
        boolean ret = maxCacheSize != -1 && size() > maxCacheSize;
        if (ret && listeners != null) {
            for (CacheEventListener listener : listeners) {
                listener.onEviction(eldest.getKey(), eldest.getValue());
            }
        }
        return ret;
    }
}
