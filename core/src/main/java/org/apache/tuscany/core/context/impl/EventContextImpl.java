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
package org.apache.tuscany.core.context.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeIdentifier;

/**
 * An implementation of an {@link org.apache.tuscany.core.context.EventContext} that handles event-to-thread associations using an
 * <code>InheritableThreadLocal</code>
 * 
 * @version $Rev$ $Date$
 */
public class EventContextImpl implements EventContext {

    // @TODO design a proper propagation strategy for creating new threads
    /*
     * a map ( associated with the current thread) of scope identifiers keyed on the event context id type. the scope identifier
     * may be a {@link ScopeIdentifier} or an opaque id
     */
    private ThreadLocal<Map> eventContext = new InheritableThreadLocal();

    public Object getIdentifier(Object type) {
        Map map = eventContext.get();
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
        Map map = eventContext.get();
        if (map == null) {
            map = new HashMap();
            eventContext.set(map);
        }
        map.put(type, identifier);
    }

    public void clearIdentifier(Object type) {
        if (type == null) {
            return;
        }
        Map map = eventContext.get();
        if (map != null) {
            map.remove(type);
        }
    }

    public EventContextImpl() {
        super();
    }

}
