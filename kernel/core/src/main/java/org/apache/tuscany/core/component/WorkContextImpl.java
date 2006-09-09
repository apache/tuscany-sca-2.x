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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * An implementation of an {@link org.apache.tuscany.spi.component.WorkContext} that handles event-to-thread
 * associations using an <code>InheritableThreadLocal</code>
 *
 * @version $Rev$ $Date$
 */
public class WorkContextImpl implements WorkContext {

    private static final Object REMOTE_CONTEXT = new Object();
    private static final Object MESSAGE_ID = new Object();
    private static final Object CORRELATION_ID = new Object();

    // TODO implement propagation strategy for creating new threads

    //A map ( associated with the current thread) of scope identifiers keyed on the event context id type.
    //The scope identifier may be a {@link ScopeIdentifier} or an opaque id
    private ThreadLocal<Map<Object, Object>> workContext = new InheritableThreadLocal<Map<Object, Object>>();

    public WorkContextImpl() {
        super();
    }

    public Object getCurrentMessageId() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return map.get(MESSAGE_ID);
    }

    public void setCurrentMessageId(Object messageId) {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            workContext.set(map);
        }
        map.put(MESSAGE_ID, messageId);
    }

    public Object getCurrentCorrelationId() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return map.get(CORRELATION_ID);
    }

    public void setCurrentCorrelationId(Object correlationId) {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            workContext.set(map);
        }
        map.put(CORRELATION_ID, correlationId);
    }

    public CompositeComponent getRemoteComponent() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return (CompositeComponent) map.get(REMOTE_CONTEXT);
    }


    public void setRemoteComponent(CompositeComponent component) {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            workContext.set(map);
        }
        map.put(REMOTE_CONTEXT, component);
    }

    public Object getIdentifier(Object type) {
        Map<Object, Object> map = workContext.get();
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
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            workContext.set(map);
        }
        map.put(type, identifier);
    }

    public void clearIdentifier(Object type) {
        if (type == null) {
            return;
        }
        Map map = workContext.get();
        if (map != null) {
            map.remove(type);
        }
    }

    public void clearIdentifiers() {
        workContext.remove();
    }

}
