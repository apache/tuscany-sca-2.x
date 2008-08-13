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

package org.apache.tuscany.sca.databinding.xml;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * @version $Rev$ $Date$
 */
public class XmlNodeIterator implements Iterator<XmlNode> {
    public static final int START = 0;
    public static final int END = 1;

    protected FastStack<ElementHolder> stack;
    protected int state;
    protected NamespaceContextImpl nsContext;

    public XmlNodeIterator(XmlNode rootNode) {
        super();
        List<XmlNode> v = new ArrayList<XmlNode>(1);
        v.add(rootNode);
        stack = new FastStack<ElementHolder>();
        Iterator<XmlNode> i = v.iterator();
        stack.push(new ElementHolder(null, i));
        this.state = START;
        this.nsContext = new NamespaceContextImpl(null);
    }

    public boolean hasNext() {
        return !(stack.empty() || (state == END && stack.peek().parent == null));
    }

    public XmlNode next() {
        this.state = START;
        ElementHolder element = stack.peek();
        Iterator<XmlNode> it = element.children;
        if (it == null || (!it.hasNext())) {
            // End of the children, return END event of parent
            stack.pop();
            this.state = END;
            this.nsContext = (NamespaceContextImpl)nsContext.getParent();
            return element.parent;
        }
        XmlNode node = it.next();
        stack.push(new ElementHolder(node, node.children()));
        this.nsContext = new NamespaceContextImpl(this.nsContext);
        populateNamespaces(node);
        return node;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int getState() {
        return state;
    }

    public NamespaceContext getNamespaceContext() {
        return nsContext;
    }

    private void populateNamespaces(XmlNode element) {
        if (element.getName() != null) {
            if (element.namespaces() != null) {
                for (Map.Entry<String, String> e : element.namespaces().entrySet()) {
                    nsContext.register(e.getKey(), e.getValue());
                }
            }
        }
    }

    private static class ElementHolder {
        private XmlNode parent;
        private Iterator<XmlNode> children;

        public ElementHolder(XmlNode parent, Iterator<XmlNode> children) {
            this.parent = parent;
            this.children = children;
        }
    }

    private static class NamespaceContextImpl implements NamespaceContext {
        private NamespaceContext parent;
        private Map<String, String> map = new HashMap<String, String>();

        /**
         * @param parent
         */
        public NamespaceContextImpl(NamespaceContext parent) {
            super();
            this.parent = parent;
            if (parent == null) {
                map.put("xml", "http://www.w3.org/XML/1998/namespace");
                map.put("xmlns", "http://www.w3.org/2000/xmlns/");
            }
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix is null");
            }

            String ns = (String)map.get(prefix);
            if (ns != null) {
                return ns;
            }
            if (parent != null) {
                return parent.getNamespaceURI(prefix);
            }
            return null;
        }

        public String getPrefix(String nsURI) {
            if (nsURI == null)
                throw new IllegalArgumentException("Namespace is null");
            for (Iterator<Map.Entry<String, String>> i = map.entrySet().iterator(); i.hasNext();) {
                Map.Entry<String, String> entry = i.next();
                if (entry.getValue().equals(nsURI)) {
                    return entry.getKey();
                }
            }
            if (parent != null) {
                return parent.getPrefix(nsURI);
            }
            return null;
        }

        public Iterator getPrefixes(String nsURI) {
            List<String> prefixList = new ArrayList<String>();
            for (Iterator<Map.Entry<String, String>> i = map.entrySet().iterator(); i.hasNext();) {
                Map.Entry<String, String> entry = i.next();
                if (entry.getValue().equals(nsURI)) {
                    prefixList.add(entry.getKey());
                }
            }
            final Iterator currentIterator = prefixList.iterator();
            final Iterator parentIterator = parent != null ? null : parent.getPrefixes(nsURI);
            return new Iterator() {

                public boolean hasNext() {
                    return currentIterator.hasNext() || (parentIterator != null && parentIterator.hasNext());
                }

                public Object next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("End of iterator has reached");
                    }
                    return currentIterator.hasNext() ? currentIterator.next() : parentIterator.next();
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

            };

        }

        public void register(String prefix, String ns) {
            map.put(prefix, ns);
        }

        public NamespaceContext getParent() {
            return parent;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer(map.toString());
            if (parent != null) {
                sb.append("\nParent: ");
                sb.append(parent);
            }
            return sb.toString();
        }
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

        /** Ensure Serialization compatibility */
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
