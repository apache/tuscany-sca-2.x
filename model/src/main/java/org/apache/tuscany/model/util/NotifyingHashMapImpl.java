/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;

/**
 *         <p/>
 *         A map that notifies an adapter of all changes.
 */
public class NotifyingHashMapImpl extends HashMap implements Map {

    private Adapter adapter;

    private class NotifyingCollectionImpl implements Collection {
        Collection collection;

        private NotifyingCollectionImpl(Collection collection) {
            this.collection = collection;
        }

        public boolean add(Object o) {
            boolean result = collection.add(o);
            adapter.notifyChanged(null);
            return result;
        }

        public boolean addAll(Collection c) {
            boolean result = collection.addAll(c);
            adapter.notifyChanged(null);
            return result;
        }

        public void clear() {
            collection.clear();
            adapter.notifyChanged(null);
        }

        public boolean contains(Object o) {
            return collection.contains(o);
        }

        public boolean containsAll(Collection c) {
            return collection.containsAll(c);
        }

        public boolean equals(Object obj) {
            return collection.equals(obj);
        }

        public int hashCode() {
            return collection.hashCode();
        }

        public boolean isEmpty() {
            return collection.isEmpty();
        }

        public Iterator iterator() {
            return new NotifyingIteratorImpl(collection.iterator());
        }

        public boolean remove(Object o) {
            boolean result = collection.remove(o);
            adapter.notifyChanged(null);
            return result;
        }

        public boolean removeAll(Collection c) {
            boolean result = collection.removeAll(c);
            adapter.notifyChanged(null);
            return result;
        }

        public boolean retainAll(Collection c) {
            boolean result = collection.retainAll(c);
            adapter.notifyChanged(null);
            return result;
        }

        public int size() {
            return collection.size();
        }

        public Object[] toArray() {
            return collection.toArray();
        }

        public Object[] toArray(Object[] a) {
            return collection.toArray(a);
        }

        public String toString() {
            return collection.toString();
        }
    }

    private class NotifyingSetImpl extends NotifyingCollectionImpl implements Set {
        private NotifyingSetImpl(Set set) {
            super(set);
        }
    }

    private class NotifyingIteratorImpl implements Iterator {
        Iterator iterator;

        private NotifyingIteratorImpl(Iterator iterator) {
            this.iterator = iterator;
        }

        public boolean equals(Object obj) {
            return iterator.equals(obj);
        }

        public int hashCode() {
            return iterator.hashCode();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return iterator.next();
        }

        public void remove() {
            iterator.remove();
            adapter.notifyChanged(null);
        }

        public String toString() {
            return iterator.toString();
        }
    }

    public NotifyingHashMapImpl(Adapter adapter) {
        this.adapter = adapter;
    }

    public void clear() {
        super.clear();
        adapter.notifyChanged(null);
    }

    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    public Set entrySet() {
        return new NotifyingSetImpl(super.entrySet());
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public Object get(Object key) {
        return super.get(key);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean isEmpty() {
        return super.isEmpty();
    }

    public Set keySet() {
        return new NotifyingSetImpl(super.keySet());
    }

    public Object put(Object key, Object value) {
        Object result = super.put(key, value);
        adapter.notifyChanged(null);
        return result;
    }

    public void putAll(Map t) {
        super.putAll(t);
        adapter.notifyChanged(null);
    }

    public Object remove(Object key) {
        Object result = super.remove(key);
        adapter.notifyChanged(null);
        return result;
    }

    public int size() {
        return super.size();
    }

    public String toString() {
        return super.toString();
    }

    public Collection values() {
        return new NotifyingCollectionImpl(super.values());
    }

}