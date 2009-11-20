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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CompoundIterator<T> implements Iterator<T> {
    private Iterator<T>[] iterators = null;
    private int index = 0;

    public CompoundIterator(Iterator<T>... iterators) {
        this.iterators = iterators;
    }

    @SuppressWarnings("unchecked")
    public CompoundIterator(Collection<T>... collections) {
        this.iterators = new Iterator[collections.length];
        for (int i = 0; i < collections.length; i++) {
            this.iterators[i] = collections[i].iterator();
        }
    }

    public boolean hasNext() {
        // if the current enum is null that means this enum is finished
        if (currentIterator() == null) {
            // No next enum
            return false;
        }
        // If the current enum has more elements, lets go
        return currentIterator().hasNext();
    }

    private Iterator<T> findNextIterator(boolean moveCursor) {
        return findNextIterator(index, moveCursor);
    }

    private Iterator<T> findNextIterator(int cursor, boolean moveCursor) {
        // next place in the array
        int next = cursor + 1;
        // If the cursor is still in the array
        if (next < iterators.length) {

            // If there is something in that place
            // AND the enum is not empty
            if (iterators[next] != null && iterators[next].hasNext()) {
                // OK
                if (moveCursor) {
                    index = next;
                }
                return iterators[next];
            }
            // Try next element
            return findNextIterator(next, moveCursor);
        }
        // No more elements available
        return null;
    }

    public T next() {
        // ask for the next element of the current enum.
        if (currentIterator() != null) {
            return currentIterator().next();
        }

        // no more elements in this Enum
        // We must throw a NoSuchElementException
        throw new NoSuchElementException("No more elements");
    }

    public void remove() {
        // ask for the next element of the current enum.
        if (currentIterator() != null) {
            currentIterator().remove();
        }

        // no more elements in this Enum
        // We must throw a NoSuchElementException
        throw new NoSuchElementException("No more elements");
    }
    
    private Iterator<T> currentIterator() {
        if (iterators != null) {
            if (index < iterators.length) {
                Iterator<T> e = iterators[index];
                if (e == null || !e.hasNext()) {
                    // the current enum is null or empty
                    // we probably want to switch to the next one
                    e = findNextIterator(true);
                }
                return e;
            }
        }
        return null;
    }
}
