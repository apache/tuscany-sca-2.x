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

import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ScopeInitializationException;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.EventException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lifecycle of composite component contexts, i.e. contexts which contain child contexts
 * 
 * @see org.apache.tuscany.core.context.CompositeContext
 * @version $Rev$ $Date$
 */
public class CompositeScopeContext extends AbstractContext implements ScopeContext {

    private List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();

    // Composite component contexts in this scope keyed by name
    private Map<String, CompositeContext> contexts = new ConcurrentHashMap<String, CompositeContext>();

    // indicates if a module start event has been previously propagated so child contexts added after can be notified
    private boolean moduleScopeStarted;

    public CompositeScopeContext(EventContext eventContext) {
        assert (eventContext != null) : "Event context was null";
        name = "Composite Scope";
    }

    public void start() throws ScopeInitializationException {
        for (ContextFactory<Context> configuration : configs) {
            Context context = configuration.createContext();
            if (!(context instanceof CompositeContext)) {
                ScopeInitializationException e = new ScopeInitializationException("Context not an composite type");
                e.addContextName(context.getName());
                throw e;
            }
            CompositeContext compositeCtx = (CompositeContext) context;
            compositeCtx.start();
            contexts.put(compositeCtx.getName(), compositeCtx);
        }
        lifecycleState = RUNNING;
    }

    public void stop() throws ScopeRuntimeException {
        for (CompositeContext context : contexts.values()) {
            context.stop();
        }
    }

    public void registerFactories(List<ContextFactory<Context>> configurations) {
        this.configs = configurations;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        assert (configuration != null) : "Configuration was null";
        configs.add(configuration);
        if (lifecycleState == RUNNING) {
            Context context = configuration.createContext();
            if (!(context instanceof CompositeContext)) {
                ScopeInitializationException e = new ScopeInitializationException("Context not an composite type");
                e.setIdentifier(context.getName());
                throw e;
            }
            CompositeContext compositeCtx = (CompositeContext) context;
            compositeCtx.start();
            if (moduleScopeStarted) {
                compositeCtx.fireEvent(EventContext.MODULE_START, null);
            }
            contexts.put(compositeCtx.getName(), compositeCtx);
        }
    }

    public boolean isCacheable() {
        return false;
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        Context context = getContext(qName.getPartName());
        if (context == null) {
            TargetException e = new TargetException("Component not found");
            e.setIdentifier(qName.getQualifiedName());
            throw e;
        }
        return context.getInstance(qName);
    }

    public Context getContext(String ctxName) {
        checkInit();
        return contexts.get(ctxName);
    }

    public Context getContextByKey(String ctxName, Object key) {
        return getContext(ctxName);
    }

    public void removeContext(String ctxName) throws ScopeRuntimeException {
        Context context = contexts.remove(ctxName);
        if (context != null) {
            context.stop();
        }
    }

    public void removeContextByKey(String ctxName, Object key) throws ScopeRuntimeException {
    }

    public void onEvent(int type, Object message) throws EventException {
        if (type == EventContext.MODULE_START) {
            // track module starting so that composite contexts registered after the event are notified properly
            moduleScopeStarted = true;
        } else if (type == EventContext.MODULE_STOP) {
            moduleScopeStarted = false;
        }
        // propagate events to child contexts
        for (CompositeContext context : contexts.values()) {
            context.fireEvent(type, message);
        }
    }


    private void checkInit() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope not running [" + lifecycleState + "]");
        }
    }
}
