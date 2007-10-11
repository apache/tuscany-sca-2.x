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
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.sca.databinding.xml.DelegatingNamespaceContext.FastStack;

/**
 * @version $Rev$ $Date$
 */
public class XmlElementIterator implements Iterator<XmlElement> {
    public final static int START = 0;
    public final static int END = 1;

    protected FastStack<ElementHolder> stack;
    protected int state;

    public XmlElementIterator(XmlElement rootNode) {
        super();
        List<XmlElement> v = new ArrayList<XmlElement>(1);
        v.add(rootNode);
        stack = new FastStack<ElementHolder>();
        Iterator<XmlElement> i = v.iterator();
        stack.push(new ElementHolder(null, i));
        this.state = START;
    }

    public boolean hasNext() {
        return !(stack.empty() || (state == END && stack.peek().parent == null));
    }

    public XmlElement next() {
        this.state = START;
        ElementHolder element = stack.peek();
        Iterator<XmlElement> it = element.children;
        if (it == null || (!it.hasNext())) {
            // End of the children, return END event of parent
            stack.pop();
            this.state = END;
            return element.parent;
        }
        XmlElement node = it.next();
        stack.push(new ElementHolder(node, node.children()));
        return node;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int getState() {
        return state;
    }

    private static class ElementHolder {
        private XmlElement parent;
        private Iterator<XmlElement> children;

        public ElementHolder(XmlElement parent, Iterator<XmlElement> children) {
            this.parent = parent;
            this.children = children;
        }
    }

}
