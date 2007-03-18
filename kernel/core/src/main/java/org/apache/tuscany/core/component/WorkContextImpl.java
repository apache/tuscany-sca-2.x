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
package org.apache.tuscany.core.component;

import java.net.URI;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Wire;

/**
 * An implementation of an {@link org.apache.tuscany.spi.component.WorkContext} that handles event-to-thread
 * associations using an <code>InheritableThreadLocal</code>
 *
 * @version $Rev$ $Date$
 */
public class WorkContextImpl implements WorkContext {
    private static final Object CORRELATION_ID = new Object();
    private static final Object CALLBACK_URIS = new Object();
    private static final Object CURRENT_ATOMIC = new Object();
    private static final Object CURRENT_SERVICE_NAMES = new Object();
    private static final Object CALLBACK_WIRES = new Object();

    // [rfeng] We cannot use InheritableThreadLocal for message ids here since it's shared by parent and children
    private ThreadLocal<Map<Object, Object>> workContext = new ThreadLocal<Map<Object, Object>>();

    // [rfeng] Session id requires InheritableThreadLocal
    private ThreadLocal<Map<Object, Object>> inheritableContext = new InheritableThreadLocal<Map<Object, Object>>();

    public WorkContextImpl() {
        super();
    }

    public Object getCorrelationId() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return map.get(CORRELATION_ID);
    }

    public void setCorrelationId(Object id) {
        Map<Object, Object> map = getWorkContextMap();
        map.put(CORRELATION_ID, id);
    }

    public AtomicComponent getCurrentAtomicComponent() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return (AtomicComponent) map.get(CURRENT_ATOMIC);
    }

    public void setCurrentAtomicComponent(AtomicComponent component) {
        Map<Object, Object> map = getWorkContextMap();
        map.put(CURRENT_ATOMIC, component);
    }

    @SuppressWarnings("unchecked")
    public LinkedList<URI> getCallbackUris() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return (LinkedList<URI>) map.get(CALLBACK_URIS);
    }

    public void setCallbackUris(LinkedList<URI> uris) {
        Map<Object, Object> map = getWorkContextMap();
        map.put(CALLBACK_URIS, uris);
    }


    @SuppressWarnings({"unchecked"})
    public LinkedList<Wire> getCallbackWires() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return (LinkedList<Wire>) map.get(CALLBACK_WIRES);
    }

    public void setCallbackWires(LinkedList<Wire> wires) {
        Map<Object, Object> map = getWorkContextMap();
        map.put(CALLBACK_WIRES, wires);
    }


    public Object getIdentifier(Object type) {
        Map<Object, Object> map = inheritableContext.get();
        if (map == null) {
            return null;
        }
        Object currentId = map.get(type);
        if (currentId instanceof ScopeIdentifier) {
            currentId = ((ScopeIdentifier) currentId).getIdentifier();
            // once we have accessed the id, replace the lazy wrapper
            map.put(type, currentId);
        }
        return currentId;
    }

    public void setIdentifier(Object type, Object identifier) {
        Map<Object, Object> map = inheritableContext.get();
        if (map == null) {
            map = new IdentityHashMap<Object, Object>();
            inheritableContext.set(map);
        }
        map.put(type, identifier);
    }

    public void clearIdentifier(Object type) {
        if (type == null) {
            return;
        }
        Map map = inheritableContext.get();
        if (map != null) {
            map.remove(type);
        }
    }

    public void clearIdentifiers() {
        inheritableContext.remove();
    }

    @SuppressWarnings({"unchecked"})
    public String popServiceName() {
        Map<Object, Object> map = inheritableContext.get();
        if (map == null) {
            return null;
        }
        List<String> stack = (List) map.get(CURRENT_SERVICE_NAMES);
        if (stack == null || stack.size() < 1) {
            return null;
        }
        String name = stack.remove(stack.size() - 1);
        if (stack.size() == 0) {
            // cleanup to avoid leaks
            map.remove(CURRENT_SERVICE_NAMES);
        }
        return name;
    }

    @SuppressWarnings({"unchecked"})
    public String getCurrentServiceName() {
        Map<Object, Object> map = inheritableContext.get();
        if (map == null) {
            return null;
        }
        List<String> stack = (List) map.get(CURRENT_SERVICE_NAMES);
        if (stack == null || stack.size() < 1) {
            return null;
        }
        return stack.get(stack.size() - 1);
    }

    @SuppressWarnings({"unchecked"})
    public void pushServiceName(String name) {
        Map<Object, Object> map = inheritableContext.get();
        List<String> names;
        if (map == null) {
            map = new IdentityHashMap<Object, Object>();
            inheritableContext.set(map);
            names = new ArrayList<String>();
            map.put(CURRENT_SERVICE_NAMES, names);
        } else {
            names = (List<String>) map.get(CURRENT_SERVICE_NAMES);
            if (names == null) {
                names = new ArrayList<String>();
                map.put(CURRENT_SERVICE_NAMES, names);
            }
        }
        names.add(name);
    }

    public void clearServiceNames() {
        Map<Object, Object> map = inheritableContext.get();
        if (map == null) {
            return;
        }
        map.remove(CURRENT_SERVICE_NAMES);
    }

    private Map<Object, Object> getWorkContextMap() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new IdentityHashMap<Object, Object>();
            workContext.set(map);
        }
        return map;
    }
}
