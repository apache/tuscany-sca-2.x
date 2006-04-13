/**
 *
 *  Copyright 2005 BEA Systems Inc.
 *  Copyright 2005 International Business Machines Corporation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A list that invokes added or removed methods when elements are added or removed.
 *
 */
public abstract class NotifyingList<E> extends ArrayList<E> {
    
    private boolean frozen;
    
    public NotifyingList() {
    }
    
    public void freeze() {
        frozen=true;
    }
    
    protected void checkNotFrozen() {
        if (frozen)
            throw new IllegalStateException("Attempt to modify a frozen list");
    }
    
    public boolean add(E element) {
        checkNotFrozen();
        boolean result=super.add(element);
        added(element);
        return result;
    }
    
    public void add(int index, E element) {
        checkNotFrozen();
        super.add(index, element);
        added(element);
    }
    
    public boolean addAll(Collection<? extends E> c) {
        checkNotFrozen();
        boolean result=super.addAll(c);
        for (E element : c)
            added(element);
        return result;
    }
    
    public boolean addAll(int index, Collection<? extends E> c) {
        checkNotFrozen();
        boolean result=super.addAll(index, c);
        for (E element : c)
            added(element);
        return result;
    }
    
    public void clear() {
        checkNotFrozen();
        List<E> l=new ArrayList<E>(this);
        super.clear();
        for (E element : l)
            removed(element);
    }
    
    public E remove(int index) {
        checkNotFrozen();
        E element=super.remove(index);
        removed(element);
        return element;
    }
    
    public boolean remove(Object element) {
        checkNotFrozen();
        boolean result=super.remove(element);
        removed((E)element);
        return result;
    }
    
    public boolean removeAll(Collection<?> c) {
        checkNotFrozen();
        boolean result=super.removeAll(c);
        for (E element : (Collection<? extends E>)c)
            removed(element);
        return result;
    }

    protected abstract void added(E element);
    
    protected abstract void removed(E element);
    
}
