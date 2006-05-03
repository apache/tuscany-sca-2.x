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
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages contexts whose implementations are module scoped. This scope contexts eagerly starts contexts when
 * a {@link ModuleStart} event is received. If a contained context has an implementation marked to eagerly initialized,
 * the an instance will be created at that time as well. Contained contexts are shutdown when a {@link ModuleStop}
 * event is received in reverse order to which their implementation instances were created.
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeContext extends AbstractScopeContext {

    // Component contexts in this scope keyed by name
    private Map<String, Context> contexts;

    // the queue of contexts to destroy, in the order that their instances were created
    private List<Context> destroyQueue;

    public ModuleScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Module Scope");
    }

    public void onEvent(Event event) {
        if (event instanceof ModuleStart) {
            lifecycleState = RUNNING;
            initComponentContexts();
        } else if (event instanceof ModuleStop) {
            shutdownContexts();
        } else if (event instanceof InstanceCreated) {
            checkInit();
            if (event.getSource() instanceof Context) {
                Context context = (Context) event.getSource();
                // Queue the context to have its implementation instance released if destroyable
                destroyQueue.add(context);
            }
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
        contexts = null;
        destroyQueue = null;
        lifecycleState = STOPPED;
    }

    public boolean isCacheable() {
        return true;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactories.put(configuration.getName(), configuration);
        if (lifecycleState == RUNNING) {
            contexts.put(configuration.getName(), configuration.createContext());
        }
    }

    public Context getContext(String ctxName) {
        checkInit();
        initComponentContexts();
        return contexts.get(ctxName);
    }

    public Context getContextByKey(String ctxName, Object key) {
        checkInit();
        initComponentContexts();
        return contexts.get(ctxName);
    }

    public void removeContext(String ctxName) {
        checkInit();
        if (contexts == null){
            return;
        }
        Context context = contexts.remove(ctxName);
        if (context != null) {
            destroyQueue.remove(context);
        }
    }

    public void removeContextByKey(String ctxName, Object key){
        removeContext(ctxName);
    }

    /**
     * Notifies contexts of a shutdown in reverse order to which they were started
     */
    private synchronized void shutdownContexts() {
        if (destroyQueue == null || destroyQueue.size() == 0) {
            return;
        }
        // shutdown destroyable instances in reverse instantiation order
        ListIterator<Context> iter = destroyQueue.listIterator(destroyQueue.size());
        while(iter.hasPrevious()){
            Lifecycle context = iter.previous();
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
        if (contexts == null){
            return;
        }
        for(Lifecycle context: contexts.values()) {
            try {
                if (context.getLifecycleState() == RUNNING) {
                    context.stop();
                }
            } catch (CoreRuntimeException e){
                // TODO send monitoring event
            }
        }
        contexts = null;
        destroyQueue = null;
     }

    /**
     * Creates and starts components contexts in the module scope. Implementations marked to eagerly initialize will
     * also be notified to do so.
     *
     * @throws CoreRuntimeException
     */
    private synchronized void initComponentContexts() throws CoreRuntimeException {
        if (contexts == null) {
            contexts = new ConcurrentHashMap<String, Context>();
            destroyQueue = new ArrayList<Context>();
            for (ContextFactory<Context> config : contextFactories.values()) {
                Context context = config.createContext();
                context.start();
                contexts.put(context.getName(), context);
            }
            // Initialize eager contexts. Note this cannot be done when we initially create each context since a component may
            // contain a forward reference to a component which has not been instantiated
            for (Context context : contexts.values()) {
                if (context instanceof AtomicContext) {
                    AtomicContext atomic = (AtomicContext) context;
                    if (atomic.isEagerInit()) {
                        // perform silent creation and manual shutdown registration
                        atomic.init();
                        destroyQueue.add(context);
                    }
                }
                context.addListener(this);
            }
        }
    }
}