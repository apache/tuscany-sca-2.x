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
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.core.context.event.Event;
import org.apache.tuscany.core.context.event.HttpSessionEvent;
import org.apache.tuscany.core.context.event.SessionEnd;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of an session-scoped component container
 * TODO this implementation needs to be made generic so that it supports a range of session types, i.e. not tied to HTTP
 * session scope 
 * 
 * @version $Rev$ $Date$
 */
public class SessionScopeContext extends AbstractScopeContext {

    // The collection of service component contexts keyed by session
    private Map<Object, Map<String, Context>> contexts;

    // Stores ordered lists of contexts to shutdown keyed by session
    private Map<Object, List<Context>> destroyQueues;

    public SessionScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Session Scope");
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope container must be in UNINITIALIZED state");
        }
        contexts = new ConcurrentHashMap<Object, Map<String, Context>>();
        destroyQueues = new ConcurrentHashMap<Object, List<Context>>();
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope container in wrong state");
        }
        contexts = null;
        contexts = null;
        destroyQueues = null;
        lifecycleState = STOPPED;
    }

    public void onEvent(Event event) {
        if (event instanceof SessionEnd){
            checkInit();
            Object key = ((SessionEnd)event).getId();
            shutdownContexts(key);
            destroyComponentContext(key);
        }else if(event instanceof InstanceCreated){
            checkInit();
            Object sessionKey = getEventContext().getIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);
            List<Context> shutdownQueue = destroyQueues.get(sessionKey);
            Context context = (Context)event.getSource();
            assert(shutdownQueue != null): "Shutdown queue not found for key";
            shutdownQueue.add(context);
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactories.put(configuration.getName(), configuration);
    }

    public Context getContext(String ctxName) {
        assert(ctxName != null): "No context name specified";
        checkInit();
        Map<String, Context> ctxs = getSessionContexts();
        Context context = ctxs.get(ctxName);
        if (context == null) {
            // the configuration was added after the session had started, so create a context now and start it
            ContextFactory<Context> configuration = contextFactories.get(ctxName);
            if (configuration != null) {
                context = configuration.createContext();
                context.start();
                if (context instanceof AtomicContext){
                    ((AtomicContext)context).init();
                }

                ctxs.put(context.getName(), context);
                List<Context> shutdownQueue = destroyQueues.get(getEventContext().getIdentifier(HttpSessionEvent.HTTP_IDENTIFIER));
                synchronized(shutdownQueue){
                    shutdownQueue.add(context);
                }
                context.addListener(this);
            }
        }
        return context;
    }

    public Context getContextByKey(String ctxName, Object key) {
        checkInit();
        assert(ctxName != null): "No context name specified";
        assert(key != null): "No key specified";
        Map ctxs = contexts.get(key);
        if (ctxs == null) {
            return null;
        }
        return (Context) ctxs.get(ctxName);
    }

    public void removeContext(String ctxName) {
        checkInit();
        Object key = getEventContext().getIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);
        removeContextByKey(ctxName, key);
    }

    public void removeContextByKey(String ctxName, Object key) {
        checkInit();
        assert(ctxName != null): "No context name specified";
        assert(key != null): "No key specified";
        Map components = contexts.get(key);
        if (components == null) {
            return;
        }
        components.remove(ctxName);
        Map<String, Context> definitions = contexts.get(key);
        Context ctx = definitions.get(ctxName);
        if (ctx != null){
            destroyQueues.get(key).remove(ctx);
        }
        definitions.remove(ctxName);
    }

    /**
     * Returns and, if necessary, creates a context for the current sesion
     */
    private Map<String, Context> getSessionContexts() throws CoreRuntimeException {
        Object key = getEventContext().getIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);
        if (key == null) {
            throw new ScopeRuntimeException("Session key not set in request context");
        }
        Map<String, Context> m = contexts.get(key);
        if (m != null) {
            return m; // already created, return
        }
        Map<String, Context> sessionContext = new ConcurrentHashMap<String, Context>(contextFactories.size());
        for (ContextFactory<Context> config : contextFactories.values()) {
            Context context = config.createContext();
            context.start();
            sessionContext.put(context.getName(), context);
        }

        List<Context> shutdownQueue = new ArrayList<Context>();
        contexts.put(key, sessionContext);
        destroyQueues.put(key, shutdownQueue);
        // initialize eager components. Note this cannot be done when we initially create each context since a component may
        // contain a forward reference to a component which has not been instantiated
        for (Context context : sessionContext.values()) {
            if (context instanceof AtomicContext) {
                AtomicContext atomic = (AtomicContext) context;
                if (atomic.isEagerInit()) {
                    atomic.init();  // Notify the instance
                    synchronized(shutdownQueue){
                        shutdownQueue.add(context);
                    }
                }
            }
            context.addListener(this);
        }
        return sessionContext;
    }

    /**
     * Removes the components associated with an expiring context
     */
    private void destroyComponentContext(Object key) {
        contexts.remove(key);
        destroyQueues.remove(key);
    }


    private synchronized void shutdownContexts(Object key) {
        List<Context> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue == null || destroyQueue.size() == 0) {
             return;
        }
        // shutdown destroyable instances in reverse instantiation order
        ListIterator<Context> iter = destroyQueue.listIterator(destroyQueue.size());
        synchronized(destroyQueue){
            while(iter.hasPrevious()){
                Context context = iter.previous();
                if (context.getLifecycleState() == RUNNING) {
                    try {
                        if (context instanceof AtomicContext){
                            ((AtomicContext)context).destroy();
                        }
                    } catch (TargetException e) {
                        // TODO send a monitoring event
                    }
                }
            }
        }
        // shutdown contexts
        Map<String,Context> currentContexts = contexts.remove(Thread.currentThread());
        if (currentContexts == null){
            return;
        }
        for (Context context: currentContexts.values()){
            if (context.getLifecycleState() == RUNNING) {
                context.stop();
            }
        }
    }

}