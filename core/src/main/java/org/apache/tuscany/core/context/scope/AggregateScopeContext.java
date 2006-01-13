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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeInitializationException;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.TargetException;

/**
 * Manages the lifecycle of aggregate component contexts, i.e. contexts which contain child contexts
 * 
 * @see org.apache.tuscany.core.context.AggregateContext
 * @version $Rev$ $Date$
 */
public class AggregateScopeContext extends AbstractContext implements ScopeContext {

    // ----------------------------------
    // Fields
    // ----------------------------------

    private EventContext eventContext;

    private List<RuntimeConfiguration<InstanceContext>> configs = new ArrayList();

    // Aggregate component contexts in this scope keyed by name
    private Map<String, AggregateContext> contexts = new ConcurrentHashMap();

    // indicates if a module start event has been previously propagated so child contexts added after can be notified
    private boolean moduleScopeStarted;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public AggregateScopeContext(EventContext eventContext) {
        assert (eventContext != null) : "Event context was null";
        this.eventContext = eventContext;
        name = "Aggregate Scope";
    }

    // ----------------------------------
    // Lifecycle methods
    // ----------------------------------

    public void start() throws ScopeInitializationException {
        for (RuntimeConfiguration<InstanceContext> configuration : configs) {
            InstanceContext context = configuration.createInstanceContext();
            if (!(context instanceof AggregateContext)) {
                ScopeInitializationException e = new ScopeInitializationException("Context not an aggregate type");
                e.addContextName(context.getName());
                throw e;
            }
            AggregateContext aggregateCtx = (AggregateContext) context;
            aggregateCtx.start();
            contexts.put(aggregateCtx.getName(), aggregateCtx);
        }
        lifecycleState = RUNNING;
    }

    public void stop() throws ScopeRuntimeException {
        for (AggregateContext context : contexts.values()) {
            context.stop();
        }
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void registerConfigurations(List<RuntimeConfiguration<InstanceContext>> configurations) {
        this.configs = configurations;
    }

    public void registerConfiguration(RuntimeConfiguration<InstanceContext> configuration) {
        assert (configuration != null) : "Configuration was null";
        configs.add(configuration);
        if (lifecycleState == RUNNING) {
            InstanceContext context = configuration.createInstanceContext();
            if (!(context instanceof AggregateContext)) {
                ScopeInitializationException e = new ScopeInitializationException("Context not an aggregate type");
                e.setIdentifier(context.getName());
                throw e;
            }
            AggregateContext aggregateCtx = (AggregateContext) context;
            aggregateCtx.start();
            if (moduleScopeStarted) {
                aggregateCtx.fireEvent(EventContext.MODULE_START, null);
            }
            contexts.put(aggregateCtx.getName(), aggregateCtx);
        }
    }

    public boolean isCacheable() {
        return false;
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        Object instance = null;
        InstanceContext context = getContext(qName.getPartName());
        if (context == null) {
            TargetException e = new TargetException("Component not found");
            e.setIdentifier(qName.getQualifiedName());
            throw e;
        }
        return context.getInstance(qName);
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        return getInstance(qName);
    }
    
    public InstanceContext getContext(String ctxName) {
        checkInit();
        return contexts.get(ctxName);
    }

    public InstanceContext getContextByKey(String ctxName, Object key) {
        return getContext(ctxName);
    }

    public void removeContext(String ctxName) throws ScopeRuntimeException {
        InstanceContext context = contexts.remove(ctxName);
        if (context != null) {
            context.stop();
        }
    }

    public void removeContextByKey(String ctxName, Object key) throws ScopeRuntimeException {
    }

    public void onEvent(int type, Object message) throws EventException {
        if (type == EventContext.MODULE_START) {
            // track module starting so that aggregate contexts registered after the event are notified properly
            moduleScopeStarted = true;
        } else if (type == EventContext.MODULE_STOP) {
            moduleScopeStarted = false;
        }
        // propagate events to child contexts
        for (AggregateContext context : contexts.values()) {
            context.fireEvent(type, message);
        }
    }

    private void checkInit() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope not running [" + lifecycleState + "]");
        }
    }
}
