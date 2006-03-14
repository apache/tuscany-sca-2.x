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

import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.SimpleComponentContext;

/**
 * Manages component contexts whose implementations are module scoped
 * 
 * @version $Rev$ $Date$
 */
public class ModuleScopeContext extends AbstractScopeContext implements RuntimeEventListener, LifecycleEventListener {

    // ----------------------------------
    // Fields
    // ----------------------------------

    // Component contexts in this scope keyed by name
    private Map<String, InstanceContext> componentContexts;

    private Queue<SimpleComponentContext> destroyableContexts;

    // ----------------------------------
    // Constructor
    // ----------------------------------

    public ModuleScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Module Scope");
    }

    // ----------------------------------
    // Listener methods
    // ----------------------------------

    public void onEvent(int type, Object key) {
        if (type == EventContext.MODULE_START) {
            lifecycleState = RUNNING;
            initComponentContexts();
        } else if (type == EventContext.MODULE_STOP) {
            notifyInstanceShutdown(key);
        }
    }

    // ----------------------------------
    // Lifecycle methods
    // ----------------------------------

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        super.stop();
        componentContexts = null;
        destroyableContexts = null;
        lifecycleState = STOPPED;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public boolean isCacheable() {
        return true;
    }

    public void registerFactory(ContextFactory<InstanceContext> configuration) {
        contextFactorys.put(configuration.getName(), configuration);
        if (lifecycleState == RUNNING) {
            componentContexts.put(configuration.getName(), configuration.createContext());
        }
    }

    public InstanceContext getContext(String ctxName) {
        checkInit();
        return componentContexts.get(ctxName);
    }

    public InstanceContext getContextByKey(String ctxName, Object key) {
        checkInit();
        return componentContexts.get(ctxName);
    }

    public void removeContext(String ctxName) {
        checkInit();
        Object component = componentContexts.remove(ctxName);
        if (component != null) {
            destroyableContexts.remove(component);
        }
    }

    public void removeContextByKey(String ctxName, Object key) {
        checkInit();
        removeContext(ctxName);
    }

    public void onInstanceCreate(Context context) {
        checkInit();
        if (context instanceof SimpleComponentContext) {
            SimpleComponentContext serviceContext = (SimpleComponentContext) context;
            // Queue the context to have its implementation instance released if destroyable
            if (serviceContext.isDestroyable()) {
                destroyableContexts.add(serviceContext);
            }
        }
    }

    /**
     * Returns an array of {@link SimpleComponentContext}s representing components that need to be notified of scope shutdown.
     */
    protected InstanceContext[] getShutdownContexts(Object key) {
        if (destroyableContexts != null) {
            // create 0-length array since Queue.size() has O(n) traversal
            return (InstanceContext[]) destroyableContexts.toArray(new InstanceContext[0]);
        } else {
            return null;
        }
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    private synchronized void initComponentContexts() throws CoreRuntimeException {
        if (componentContexts == null) {
            componentContexts = new ConcurrentHashMap();
            destroyableContexts = new ConcurrentLinkedQueue();
            for (ContextFactory<InstanceContext> config : contextFactorys.values()) {
                InstanceContext context = config.createContext();
                context.addContextListener(this);
                context.start();
                componentContexts.put(context.getName(), context);
            }
            // Initialize eager contexts. Note this cannot be done when we initially create each context since a component may
            // contain a forward reference to a component which has not been instantiated
            for (InstanceContext context : componentContexts.values()) {
                if (context instanceof SimpleComponentContext) {
                    SimpleComponentContext simpleCtx = (SimpleComponentContext) context;
                    if (simpleCtx.isEagerInit()) {
                        // perform silent creation and manual shutdown registration
                        simpleCtx.getInstance(null, false);
                        if (simpleCtx.isDestroyable()) {
                            destroyableContexts.add(simpleCtx);
                        }
                    }
                }
            }
        }
    }
}