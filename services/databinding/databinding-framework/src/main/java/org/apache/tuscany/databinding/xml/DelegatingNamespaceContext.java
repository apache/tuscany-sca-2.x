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
package org.apache.tuscany.databinding.xml;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;

public class DelegatingNamespaceContext implements NamespaceContext {
    private static int count;

    private class WrappingIterator implements Iterator {

        private Iterator containedIterator;

        public WrappingIterator(Iterator containedIterator) {
            this.containedIterator = containedIterator;
        }

        public Iterator getContainedIterator() {
            return containedIterator;
        }

        public boolean hasNext() {
            return containedIterator.hasNext();
        }

        public Object next() {
            return containedIterator.next();
        }

        /**
         * As per the contract on the API of Namespace context the returned iterator should be immutable
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void setContainedIterator(Iterator containedIterator) {
            this.containedIterator = containedIterator;
        }
    }

    private NamespaceContext parentNsContext;

    private FastStack<String> prefixStack = new FastStack<String>();

    // Keep two arraylists for the prefixes and namespaces. They should be in
    // sync
    // since the index of the entry will be used to relate them
    // use the minimum initial capacity to let things handle memory better

    private FastStack<String> uriStack = new FastStack<String>();

    /**
     * Generates a unique namespace prefix that is not in the scope of the NamespaceContext
     * 
     * @return string
     */
    public String generateUniquePrefix() {
        String prefix = "p" + count++;
        // null should be returned if the prefix is not bound!
        while (getNamespaceURI(prefix) != null) {
            prefix = "p" + count++;
        }

        return prefix;
    }

    public String getNamespaceURI(String prefix) {
        // do the corrections as per the javadoc
        int index = prefixStack.search(prefix);
        if (index != -1) {
            return (String)uriStack.get(index);
        }
        if (parentNsContext != null) {
            return parentNsContext.getPrefix(prefix);
        }
        return null;
    }

    public NamespaceContext getParentNsContext() {
        return parentNsContext;
    }

    public String getPrefix(String uri) {
        // do the corrections as per the javadoc
        int index = uriStack.search(uri);
        if (index != -1) {
            return (String)prefixStack.get(index);
        }

        if (parentNsContext != null) {
            return parentNsContext.getPrefix(uri);
        }
        return null;
    }

    public Iterator getPrefixes(String uri) {
        // create an arraylist that contains the relevant prefixes
        String[] uris = (String[])uriStack.toArray(new String[uriStack.size()]);
        List<String> tempList = new ArrayList<String>();
        for (int i = uris.length - 1; i >= 0; i--) {
            if (uris[i].equals(uri)) {
                tempList.add(prefixStack.get(i));
                // we assume that array conversion preserves the order
            }
        }
        // by now all the relevant prefixes are collected
        // make a new iterator and provide a wrapper iterator to
        // obey the contract on the API
        return new WrappingIterator(tempList.iterator());
    }

    /**
     * Pop a namespace
     */
    public void popNamespace() {
        prefixStack.pop();
        uriStack.pop();
    }

    /**
     * Register a namespace in this context
     * 
     * @param prefix
     * @param uri
     */
    public void pushNamespace(String prefix, String uri) {
        prefixStack.push(prefix);
        uriStack.push(uri);

    }

    public void setParentNsContext(NamespaceContext parentNsContext) {
        this.parentNsContext = parentNsContext;
    }

    /**
     * An implementation of the {@link java.util.Stack} API that is based on an <code>ArrayList</code> instead of a
     * <code>Vector</code>, so it is not synchronized to protect against multi-threaded access. The implementation is
     * therefore operates faster in environments where you do not need to worry about multiple thread contention.
     * <p>
     * The removal order of an <code>ArrayStack</code> is based on insertion order: The most recently added element is
     * removed first. The iteration order is <i>not</i> the same as the removal order. The iterator returns elements
     * from the bottom up, whereas the {@link #remove()} method removes them from the top down.
     * <p>
     * Unlike <code>Stack</code>, <code>ArrayStack</code> accepts null entries.
     */
    public static class FastStack<T> extends ArrayList<T> {

        /** Ensure serialization compatibility */
        private static final long serialVersionUID = 2130079159931574599L;

        /**
         * Constructs a new empty <code>ArrayStack</code>. The initial size is controlled by <code>ArrayList</code>
         * and is currently 10.
         */
        public FastStack() {
            super();
        }

        /**
         * Constructs a new empty <code>ArrayStack</code> with an initial size.
         * 
         * @param initialSize the initial size to use
         * @throws IllegalArgumentException if the specified initial size is negative
         */
        public FastStack(int initialSize) {
            super(initialSize);
        }

        /**
         * Return <code>true</code> if this stack is currently empty.
         * <p>
         * This method exists for compatibility with <code>java.util.Stack</code>. New users of this class should use
         * <code>isEmpty</code> instead.
         * 
         * @return true if the stack is currently empty
         */
        public boolean empty() {
            return isEmpty();
        }

        /**
         * Returns the top item off of this stack without removing it.
         * 
         * @return the top item on the stack
         * @throws EmptyStackException if the stack is empty
         */
        public T peek() throws EmptyStackException {
            int n = size();
            if (n <= 0) {
                throw new EmptyStackException();
            } else {
                return get(n - 1);
            }
        }

        /**
         * Returns the n'th item down (zero-relative) from the top of this stack without removing it.
         * 
         * @param n the number of items down to go
         * @return the n'th item on the stack, zero relative
         * @throws EmptyStackException if there are not enough items on the stack to satisfy this request
         */
        public T peek(int n) throws EmptyStackException {
            int m = (size() - n) - 1;
            if (m < 0) {
                throw new EmptyStackException();
            } else {
                return get(m);
            }
        }

        /**
         * Pops the top item off of this stack and return it.
         * 
         * @return the top item on the stack
         * @throws EmptyStackException if the stack is empty
         */
        public T pop() throws EmptyStackException {
            int n = size();
            if (n <= 0) {
                throw new EmptyStackException();
            } else {
                return remove(n - 1);
            }
        }

        /**
         * Pushes a new item onto the top of this stack. The pushed item is also returned. This is equivalent to calling
         * <code>add</code>.
         * 
         * @param item the item to be added
         * @return the item just pushed
         */
        public Object push(T item) {
            add(item);
            return item;
        }

        /**
         * Returns the top-most index for the object in the stack
         * 
         * @param object the object to be searched for
         * @return top-most index, or -1 if not found
         */
        public int search(T object) {
            int i = size() - 1; // Current index
            while (i >= 0) {
                T current = get(i);
                if ((object == null && current == null) || (object != null && object.equals(current))) {
                    return i;
                }
                i--;
            }
            return -1;
        }

        /**
         * Returns the element on the top of the stack.
         * 
         * @return the element on the top of the stack
         * @throws EmptyStackException if the stack is empty
         */
        public T get() {
            int size = size();
            if (size == 0) {
                throw new EmptyStackException();
            }
            return get(size - 1);
        }

        /**
         * Removes the element on the top of the stack.
         * 
         * @return the removed element
         * @throws EmptyStackException if the stack is empty
         */
        public T remove() {
            int size = size();
            if (size == 0) {
                throw new EmptyStackException();
            }
            return remove(size - 1);
        }

    }

}
