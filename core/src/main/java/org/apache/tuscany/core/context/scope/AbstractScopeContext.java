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
package org.apache.tuscany.core.context.scope;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.core.context.impl.AbstractLifecycle;

/**
 * Implements functionality common to scope contexts.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContext extends AbstractLifecycle implements ScopeContext {

    // The collection of runtime configurations for the scope
    protected Map<String, ContextFactory<Context>> contextFactories = new ConcurrentHashMap<String, ContextFactory<Context>>();

    // The event context the scope container is associated with
    protected EventContext eventContext;

    public AbstractScopeContext(EventContext eventContext) {
        assert (eventContext != null) : "Event context was null";
        this.eventContext = eventContext;
    }

    public void registerFactories(List<ContextFactory<Context>> configurations) {
        for (ContextFactory<Context> configuration : configurations) {
            contextFactories.put(configuration.getName(), configuration);
        }
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        Context context = getContext(qName.getPartName());
        if (context == null) {
            TargetException e = new TargetException("Target not found");
            e.setIdentifier(qName.getQualifiedName());
            throw e;
        }
        return context.getInstance(qName);
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope not running [" + getLifecycleState() + "]");
        }
    }

    protected EventContext getEventContext() {
        return eventContext;
    }
}
