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
package org.apache.tuscany.sca.contribution.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;

/**
 *  Tuscany specialized class to handle NamespaceContext
 *
 */
public class TuscanyNamespaceContext implements NamespaceContext {

    private Stack<List<String>[]> context = null;

    public TuscanyNamespaceContext(Stack<List<String>[]> context) {
        this.context = context;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        return (String)getResult("getNSUri", prefix);
    }

    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        return (String)getResult("getPrefix", namespaceURI);
    }

    @SuppressWarnings("unchecked")
    public Iterator<String> getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }

        Iterator<String> iterator = (Iterator<String>)getResult("getPrefixes", namespaceURI);
        return iterator;
    }

    /*
     * Generic method to Iterate through the Stack and return required result(s) 
     */
    private Object getResult(String operation, String arg) {

        List<String>[] contextList = null;
        Iterator<String> prefItr = null;
        Iterator<String> uriItr = null;

        List<String> list = new ArrayList<String>();

        String toCompare = null;

        String tempPrefix = null;
        String tempUri = null;

        for (int i = context.size() - 1; i >= 0; i--) {
            contextList = context.get(i);
            prefItr = contextList[0].iterator();
            uriItr = contextList[1].iterator();
            while (uriItr.hasNext()) {
                tempPrefix = prefItr.next();
                tempUri = uriItr.next();
                if (operation.equalsIgnoreCase("getNSUri")) {
                    toCompare = tempPrefix;
                } else if (operation.equalsIgnoreCase("getPrefix")) {
                    toCompare = tempUri;
                } else if (operation.equalsIgnoreCase("getPrefixes")) {
                    toCompare = tempUri;
                }
                if (toCompare != null && arg.equalsIgnoreCase(toCompare)) {
                    if (operation.equalsIgnoreCase("getNSUri")) {
                        return tempUri;
                    } else if (operation.equalsIgnoreCase("getPrefix")) {
                        return tempPrefix;
                    } else if (operation.equalsIgnoreCase("getPrefixes")) {
                        list.add(tempPrefix);
                    }

                }
            }
        }

        if (operation.equalsIgnoreCase("getPrefixes")) {
            return Collections.unmodifiableList(list).iterator();
        }

        return null;
    }
}