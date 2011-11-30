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

package org.apache.tuscany.sca.common.xml.dom;

import java.util.IdentityHashMap;
import java.util.Map;

public abstract class ParserPool<V> {
    private int maxSize;
    private Map<V, Boolean> objects;

    public ParserPool() {
        this(32, 0);
    }

    public ParserPool(int maxSize, int initialSize) {
        super();
        this.maxSize = maxSize;
        this.objects = new IdentityHashMap<V, Boolean>(maxSize);
        for (int i = 0; i < Math.min(initialSize, maxSize); i++) {
            objects.put(newInstance(), Boolean.FALSE);
        }
    }

    public synchronized V borrowFromPool() {
        while (true) {
            for (Map.Entry<V, Boolean> e : objects.entrySet()) {
                if (Boolean.FALSE.equals(e.getValue())) {
                    // setValue fails on some Harmony based JDKs, see https://issues.apache.org/jira/browse/HARMONY-6419
                    //e.setValue(Boolean.TRUE); // in use
                    V key = e.getKey();
                    objects.put(key, Boolean.TRUE); // in use
                    return key;
                }
            }
            if (objects.size() < maxSize) {
                V obj = newInstance();
                objects.put(obj, Boolean.TRUE);
                return obj;
            }
            try {
                wait();
            } catch (InterruptedException e1) {
                throw new IllegalStateException(e1);
            }
        }
    }

    public synchronized void returnToPool(V obj) {
        resetInstance(obj);
        objects.put(obj, Boolean.FALSE);
        notifyAll();
    }

    public synchronized void clear() {
        objects.clear();
    }

    public synchronized int inUse() {
        int size = 0;
        for (Map.Entry<V, Boolean> e : objects.entrySet()) {
            if (Boolean.TRUE.equals(e.getValue())) {
                size++;
            }
        }
        return size;
    }

    /**
     * Create a new instance
     * @return
     */
    protected abstract V newInstance();

    /**
     * Reset the instance before returning to the pool
     * @param obj
     */
    protected abstract void resetInstance(V obj);

    // Expose it for testing purpose
    public Map<V, Boolean> getObjects() {
        return objects;
    }
}
