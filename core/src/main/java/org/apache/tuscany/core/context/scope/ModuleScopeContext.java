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
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.event.ContextCreatedEvent;
import org.apache.tuscany.core.context.event.Event;
import org.apache.tuscany.core.context.event.ModuleStartEvent;
import org.apache.tuscany.core.context.event.ModuleStopEvent;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages component contexts whose implementations are module scoped
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeContext extends AbstractScopeContext {

    // Component contexts in this scope keyed by name
    private Map<String, Context> componentContexts;

    private List<Context> destroyableContexts;

    public ModuleScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Module Scope");
    }

    public void onEvent(Event event) {
        if (event instanceof ModuleStartEvent) {
            lifecycleState = RUNNING;
            initComponentContexts();
        } else if (event instanceof ModuleStopEvent) {
            notifyInstanceShutdown();
        } else if (event instanceof ContextCreatedEvent) {
            checkInit();
            if (event.getSource() instanceof AtomicContext) {
                AtomicContext serviceContext = (AtomicContext) event.getSource();
                // Queue the context to have its implementation instance released if destroyable
                if (serviceContext.isDestroyable()) {
                    destroyableContexts.add(serviceContext);
                }
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
        componentContexts = null;
        destroyableContexts = null;
        lifecycleState = STOPPED;
    }

    public boolean isCacheable() {
        return true;
    }

    public void registerFactory
            (ContextFactory<Context> configuration) {
        contextFactories.put(configuration.getName(), configuration);
        if (lifecycleState == RUNNING) {
            componentContexts.put(configuration.getName(), configuration.createContext());
        }
    }

    public Context getContext(String ctxName) {
        checkInit();
        initComponentContexts();
        return componentContexts.get(ctxName);
    }

    public Context getContextByKey(String ctxName, Object key) {
        checkInit();
        initComponentContexts();
        return componentContexts.get(ctxName);
    }

    public void removeContext(String ctxName) {
        checkInit();
        if (componentContexts == null){
            return;
        }
        Context context = componentContexts.remove(ctxName);
        if (context != null) {
            destroyableContexts.remove(context);
        }
    }

    public void removeContextByKey(String ctxName, Object key){
        removeContext(ctxName);
    }


    /**
     * Notfies instances that are associated with a context and configured to receive callbacks that the context is
     * being destroyed in reverse order
     */
    private synchronized void notifyInstanceShutdown() {
        if (destroyableContexts == null || destroyableContexts.size() == 0) {
            return;
        }
        // shutdown destroyable instances in reverse instantiation order
        ListIterator<Context> iter = destroyableContexts.listIterator(destroyableContexts.size());
        while(iter.hasPrevious()){
            Context context = iter.previous();
            if (context.getLifecycleState() == RUNNING) {
                synchronized (context) {
                    //removeContextByKey(context.getName(), key);
                    try {
                        if (context instanceof AtomicContext){
                            ((AtomicContext)context).destroy();
                        }
                        context.stop();
                    } catch (TargetException e) {
                        // TODO send a monitoring event
                        // log.error("Error releasing instance [" + context.getName() + "]",e);
                    }
                }
            }
        }
        componentContexts = null;
        destroyableContexts = null;
     }

    private synchronized void initComponentContexts() throws CoreRuntimeException {
        if (componentContexts == null) {
            componentContexts = new ConcurrentHashMap<String, Context>();
            destroyableContexts = new ArrayList<Context>();
            for (ContextFactory<Context> config : contextFactories.values()) {
                Context context = config.createContext();
                context.start();
                componentContexts.put(context.getName(), context);
            }
            // Initialize eager contexts. Note this cannot be done when we initially create each context since a component may
            // contain a forward reference to a component which has not been instantiated
            for (Context context : componentContexts.values()) {
                if (context instanceof AtomicContext) {
                    AtomicContext atomic = (AtomicContext) context;
                    if (atomic.isEagerInit()) {
                        // perform silent creation and manual shutdown registration
                        atomic.init();
                        //if (atomic.isDestroyable()) {
                        //}
                    }
                }
                context.addListener(this);
                destroyableContexts.add(context);
            }
        }
    }
}