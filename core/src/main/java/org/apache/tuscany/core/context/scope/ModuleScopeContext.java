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
 * Manages component contexts whose implementations are module scoped
 * 
 * @version $Rev$ $Date$
 */
public class ModuleScopeContext extends AbstractScopeContext implements RuntimeEventListener {

    // Component contexts in this scope keyed by name
    private Map<String, Context> componentContexts;

    private Queue<AtomicContext> destroyableContexts;

     public ModuleScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Module Scope");
    }

     public void onEvent(int type, Object key) {
         switch(type){
             case EventContext.MODULE_START:
                lifecycleState = RUNNING;
                initComponentContexts();
                break;
            case EventContext.MODULE_STOP:
                notifyInstanceShutdown(key);
                break;
            case EventContext.CONTEXT_CREATED:
                checkInit();
                if (key instanceof AtomicContext) {
                    AtomicContext serviceContext = (AtomicContext) key;
                    // Queue the context to have its implementation instance released if destroyable
                    if (serviceContext.isDestroyable()) {
                        destroyableContexts.add(serviceContext);
                    }
                }
                break;
        }
    }

   public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        componentContexts = null;
        destroyableContexts = null;
        lifecycleState = STOPPED;
    }

   public boolean isCacheable() {
        return true;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactorys.put(configuration.getName(), configuration);
        if (lifecycleState == RUNNING) {
            componentContexts.put(configuration.getName(), configuration.createContext());
        }
    }

    public Context getContext(String ctxName) {
        checkInit();
        return componentContexts.get(ctxName);
    }

    public Context getContextByKey(String ctxName, Object key) {
        checkInit();
        return componentContexts.get(ctxName);
    }

    public void removeContext(String ctxName) {
        checkInit();
        Context context = componentContexts.remove(ctxName);
        if (context != null) {
            destroyableContexts.remove(context);
        }
    }

    public void removeContextByKey(String ctxName, Object key) {
        checkInit();
        removeContext(ctxName);
    }

    /**
     * Returns an array of {@link AtomicContext}s representing components that need to be notified of scope shutdown.
     */
    protected Context[] getShutdownContexts(Object key) {
        if (destroyableContexts != null) {
            // create 0-length array since Queue.size() has O(n) traversal
            return destroyableContexts.toArray(new Context[0]);
        } else {
            return null;
        }
    }

    private synchronized void initComponentContexts() throws CoreRuntimeException {
        if (componentContexts == null) {
            componentContexts = new ConcurrentHashMap<String, Context> ();
            destroyableContexts = new ConcurrentLinkedQueue<AtomicContext>();
            for (ContextFactory<Context> config : contextFactorys.values()) {
                Context context = config.createContext();
                context.addListener(this);
                context.start();
                componentContexts.put(context.getName(), context);
            }
            // Initialize eager contexts. Note this cannot be done when we initially create each context since a component may
            // contain a forward reference to a component which has not been instantiated
            for (Context context : componentContexts.values()) {
                if (context instanceof AtomicContext) {
                    AtomicContext simpleCtx = (AtomicContext) context;
                    if (simpleCtx.isEagerInit()) {
                        // perform silent creation and manual shutdown registration
                        simpleCtx.init();
                        if (simpleCtx.isDestroyable()) {
                            destroyableContexts.add(simpleCtx);
                        }
                    }
                }
            }
        }
    }
}