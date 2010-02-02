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

package org.apache.tuscany.sca.common.java.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Map with Collection values
 */
public class CollectionMap<K, V> extends ConcurrentHashMap<K, Collection<V>> {
    private static final long serialVersionUID = -8926174610229029369L;

    public boolean putValue(K key, V value) {
        Collection<V> collection = get(key);
        if (collection == null) {
            collection = createCollection();
            put(key, collection);
        }
        return collection.add(value);
    }

    public boolean putValues(K key, Collection<? extends V> value) {
        Collection<V> collection = get(key);
        if (collection == null) {
            collection = createCollection();
            put(key, collection);
        }
        return collection.addAll(value);
    }

    public boolean removeValue(K key, V value) {
        return removeValue(key, value, false);
    }

    /**
     * Remove an entry from the collection for a key
     * @param key The key
     * @param value The value in the collection
     * @param removeEmptyEntry Indicate if the entry should be removed if the collection is empty
     * @return
     */
    public boolean removeValue(K key, V value, boolean removeEmptyEntry) {
        Collection<V> collection = get(key);
        if (collection == null) {
            return false;
        }
        boolean result = collection.remove(value);
        if(removeEmptyEntry && collection.isEmpty()) {
            remove(key);
        }
        return result;
    }
    
    protected Collection<V> createCollection() {
        return new ArrayList<V>();
    }

}
