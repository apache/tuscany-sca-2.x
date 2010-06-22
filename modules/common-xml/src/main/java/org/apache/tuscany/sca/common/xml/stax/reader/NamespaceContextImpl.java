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

package org.apache.tuscany.sca.common.xml.stax.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class NamespaceContextImpl implements NamespaceContext {
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
        final Iterator<String> currentIterator = prefixList.iterator();
        final Iterator parentIterator = parent == null ? null : parent.getPrefixes(nsURI);
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
