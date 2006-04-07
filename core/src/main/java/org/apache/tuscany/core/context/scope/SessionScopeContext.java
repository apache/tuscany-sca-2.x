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
import org.apache.tuscany.core.context.*;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An implementation of an session-scoped component container where each HTTP session is mapped to a context in the scope
 * 
 * @version $Rev$ $Date$
 */
public class SessionScopeContext extends AbstractScopeContext implements RuntimeEventListener {

    // The collection of service component contexts keyed by session
    private Map<Object, Map<String, Context>> contexts;

    // Stores ordered lists of contexts to shutdown keyed by session
    private Map<Object, Queue<AtomicContext>> destroyableContexts;

    public SessionScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Session Scope");
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope container must be in UNINITIALIZED state");
        }
        contexts = new ConcurrentHashMap<Object, Map<String, Context>>();
        destroyableContexts = new ConcurrentHashMap<Object, Queue<AtomicContext>>();
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope container in wrong state");
        }
        contexts = null;
        contexts = null;
        destroyableContexts = null;
        lifecycleState = STOPPED;
    }

    public void onEvent(int type, Object key) {
        if (key == null) {
            return;
        }
        switch(type){
            case EventContext.SESSION_END:
                checkInit();
                notifyInstanceShutdown(key);
                destroyComponentContext(key);
                break;
            case EventContext.CONTEXT_CREATED:
                checkInit();
                if (key instanceof AtomicContext) {
                     AtomicContext simpleCtx = (AtomicContext)key;
                     // if destroyable, queue the context to have its component implementation instance released
                     if (simpleCtx.isDestroyable()) {
                         Object sessionKey = getEventContext().getIdentifier(EventContext.HTTP_SESSION);
                         Queue<AtomicContext> comps = destroyableContexts.get(sessionKey);
                         if (comps == null) {
                             ScopeRuntimeException e = new ScopeRuntimeException("Shutdown queue not found for key");
                             e.setIdentifier(sessionKey.toString());
                             throw e;
                         }
                         comps.add(simpleCtx);
                     }
                }
                break;
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactorys.put(configuration.getName(), configuration);
    }

    public Context getContext(String ctxName) {
        checkInit();
        if (ctxName == null) {
            return null;
        }
        // try{
        Map<String, Context> ctxs = getSessionContext();
        if (ctxs == null) {
            return null;
        }
        Context ctx = ctxs.get(ctxName);
        if (ctx == null) {
            // the configuration was added after the session had started, so create a context now and start it
            ContextFactory<Context> configuration = contextFactorys.get(ctxName);
            if (configuration != null) {
                ctx = configuration.createContext();
                ctx.addListener(this);
                ctx.start();
                ctxs.put(ctx.getName(), ctx);
            }
        }
        return ctx;
    }

    public Context getContextByKey(String ctxName, Object key) {
        checkInit();
        if (key == null && ctxName == null) {
            return null;
        }
        Map components = contexts.get(key);
        if (components == null) {
            return null;
        }
        return (Context) components.get(ctxName);
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
        Map components = contexts.get(key);
        if (components == null) {
            return;
        }
        components.remove(ctxName);
        Map<String, Context> definitions = contexts.get(key);
        Context ctx = definitions.get(ctxName);
        if (ctx != null){
            destroyableContexts.get(key).remove(ctx);
        }
        definitions.remove(ctxName);
    }


    /**
     * Returns an array of {@link AtomicContext}s representing components that need to be notified of scope shutdown or
     * null if none found.
     */
    protected Context[] getShutdownContexts(Object key) {
        /*
         * This method will be called from the Listener which is associated with a different thread than the request. So, just
         * grab the key directly
         */
        Queue<AtomicContext> queue = destroyableContexts.get(key);
        if (queue != null) {
            // create 0-length array since Queue.size() has O(n) traversal
            return queue.toArray(new AtomicContext[0]);
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
    private Map<String, Context> getSessionContext() throws CoreRuntimeException {
        Object key = getEventContext().getIdentifier(EventContext.HTTP_SESSION);
        if (key == null) {
            throw new ScopeRuntimeException("Session key not set in request context");
        }
        Map<String, Context> m = contexts.get(key);
        if (m != null) {
            return m; // already created, return
        }
        Map<String, Context> sessionContext = new ConcurrentHashMap<String, Context>(contextFactorys.size());
        for (ContextFactory<Context> config : contextFactorys.values()) {
            Context context = config.createContext();
            context.addListener(this);
            context.start();
            sessionContext.put(context.getName(), context);
        }

        Queue<AtomicContext> shutdownQueue = new ConcurrentLinkedQueue<AtomicContext>();
        contexts.put(key, sessionContext);
        destroyableContexts.put(key, shutdownQueue);
        // initialize eager components. Note this cannot be done when we initially create each context since a component may
        // contain a forward reference to a component which has not been instantiated
        for (Context context : sessionContext.values()) {
            if (context instanceof AtomicContext) {
                AtomicContext simpleCtx = (AtomicContext) context;
                if (simpleCtx.isEagerInit()) {
                    context.notify();  // Notify the instance
                    if (simpleCtx.isDestroyable()) {
                        shutdownQueue.add(simpleCtx);
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