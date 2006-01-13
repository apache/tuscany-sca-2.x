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

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.SimpleComponentContext;

/**
 * An implementation of an HTTP session-scoped component container where each HTTP session is mapped to a context in the scope
 * 
 * @version $Rev$ $Date$
 */
public class HttpSessionScopeContext extends AbstractScopeContext implements RuntimeEventListener, LifecycleEventListener {

    // The collection of service component contexts keyed by session
    private Map<Object, Map<String, InstanceContext>> contexts;

    // Stores ordered lists of contexts to shutdown keyed by session
    private Map<Object, Queue<InstanceContext>> destroyableContexts;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public HttpSessionScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Http Session Scope");
    }

    // ----------------------------------
    // Lifecycle methods
    // ----------------------------------

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope container must be in UNINITIALIZED state");
        }
        super.start();
        contexts = new ConcurrentHashMap();
        destroyableContexts = new ConcurrentHashMap();
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope container in wrong state");
        }
        super.stop();
        contexts = null;
        contexts = null;
        destroyableContexts = null;
        lifecycleState = STOPPED;
    }

    // ----------------------------------
    // Listener methods
    // ----------------------------------

    public void onEvent(int type, Object key) {
        checkInit();
        if (key == null) {
            return;
        }
        if (type == EventContext.SESSION_END) {
            notifyInstanceShutdown(key);
            destroyComponentContext(key);
        }
    }

    // ----------------------------------
    // Scope methods
    // ----------------------------------

    public boolean isCacheable() {
        return true;
    }

    public void registerConfiguration(RuntimeConfiguration<InstanceContext> configuration) {
        runtimeConfigurations.put(configuration.getName(), configuration);
    }

    public InstanceContext getContext(String ctxName) {
        checkInit();
        if (ctxName == null) {
            return null;
        }
        // try{
        Map<String, InstanceContext> ctxs = getSessionContext();
        if (ctxs == null) {
            return null;
        }
        InstanceContext ctx = ctxs.get(ctxName);
        if (ctx == null) {
            // the configuration was added after the session had started, so create a context now and start it
            RuntimeConfiguration<InstanceContext> configuration = runtimeConfigurations.get(ctxName);
            if (configuration != null) {
                ctx = configuration.createInstanceContext();
                ctx.addContextListener(this);
                ctx.start();
                ctxs.put(ctx.getName(), ctx);
            }
        }
        return ctx;
    }

    public InstanceContext getContextByKey(String ctxName, Object key) {
        checkInit();
        if (key == null && ctxName == null) {
            return null;
        }
        Map components = (Map) contexts.get(key);
        if (components == null) {
            return null;
        }
        return (InstanceContext) components.get(ctxName);
    }

    public void removeContext(String ctxName) {
        checkInit();
        Object key = getEventContext().getIdentifier(EventContext.HTTP_SESSION);
        removeContextByKey(ctxName, key);
    }

    public void removeContextByKey(String ctxName, Object key) {
        checkInit();
        if (key == null || ctxName == null) {
            return;
        }
        Map components = (Map) contexts.get(key);
        if (components == null) {
            return;
        }
        components.remove(ctxName);
        Map definitions = contexts.get(key);
        InstanceContext meta = (InstanceContext) definitions.get(ctxName);
        destroyableContexts.get(key).remove(meta);
        definitions.remove(ctxName);
    }
 
    public void onInstanceCreate(Context context) throws ScopeRuntimeException {
        checkInit();
        if (context instanceof SimpleComponentContext) {
            // if destroyable, queue the context to have its component implementation instance released
            if (((SimpleComponentContext) context).isDestroyable()) {
                Object key = getEventContext().getIdentifier(EventContext.HTTP_SESSION);
                Queue comps = destroyableContexts.get(key);
                if (comps == null) {
                    ScopeRuntimeException e = new ScopeRuntimeException("Shutdown queue not found for key");
                    e.setIdentifier(key.toString());
                    throw e;
                }
                comps.add(context);
            }
        }
    }

    /**
     * Returns an array of {@link SimpleComponentContext}s representing components that need to be notified of scope shutdown or
     * null if none found.
     */
    protected InstanceContext[] getShutdownContexts(Object key) {
        /*
         * This method will be called from the Listener which is associated with a different thread than the request. So, just
         * grab the key directly
         */
        Queue queue = destroyableContexts.get(key);
        if (queue != null) {
            // create 0-length array since Queue.size() has O(n) traversal
            return (InstanceContext[]) queue.toArray(new InstanceContext[0]);
        } else {
            return null;
        }
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    /**
     * Returns and, if necessary, creates a context for the current sesion
     */
    private Map<String, InstanceContext> getSessionContext() throws CoreRuntimeException {
        Object key = getEventContext().getIdentifier(EventContext.HTTP_SESSION);
        if (key == null) {
            throw new ScopeRuntimeException("Session key not set in request context");
        }
        Map m = contexts.get(key);
        if (m != null) {
            return m; // already created, return
        }
        Map<String, InstanceContext> sessionContext = new ConcurrentHashMap(runtimeConfigurations.size());
        for (RuntimeConfiguration<InstanceContext> config : runtimeConfigurations.values()) {
            InstanceContext context = null;
            context = config.createInstanceContext();
            context.addContextListener(this);
            context.start();
            sessionContext.put(context.getName(), context);
        }

        Queue shutdownQueue = new ConcurrentLinkedQueue();
        contexts.put(key, sessionContext);
        destroyableContexts.put(key, shutdownQueue);
        // initialize eager components. Note this cannot be done when we initially create each context since a component may
        // contain a forward reference to a component which has not been instantiated
        for (InstanceContext context : sessionContext.values()) {
            if (context instanceof SimpleComponentContext) {
                SimpleComponentContext simpleCtx = (SimpleComponentContext) context;
                if (simpleCtx.isEagerInit()) {
                    // Get the instance and perform manual shutdown registration to avoid a map lookup
                    context.getInstance(null, false);
                    if (simpleCtx.isDestroyable()) {
                        shutdownQueue.add(context);
                    }
                }
            }
        }
        return sessionContext;
    }

    /**
     * Removes the components associated with an expiring context
     */
    private void destroyComponentContext(Object key) {
        contexts.remove(key);
        destroyableContexts.remove(key);
    }

}