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
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A container that manages stateless components.
 * 
 * @version $Rev$ $Date$
 */
public class StatelessScopeContext extends AbstractScopeContext implements RuntimeEventListener {

   // Component contexts keyed by name
    private Map<String, Context> contextMap;

    public StatelessScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Stateless Scope");
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
        prepare();
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        contextMap = null;
        lifecycleState = STOPPED;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactorys.put(configuration.getName(), configuration);
        if (lifecycleState == RUNNING) {
            contextMap.put(configuration.getName(), configuration.createContext());
        }

    }

    public void onEvent(Event event){
        // do nothing
    }

    public boolean isCacheable() {
        return true;
    }

    public Context getContext(String ctxName) {
        return contextMap.get(ctxName);
    }

    public Context getContextByKey(String ctxName, Object key) {
        return getContext(ctxName);
    }

    public void removeContext(String ctxName) {
        removeContextByKey(ctxName, null);
    }

    public void removeContextByKey(String ctxName, Object key) {
        contextMap.remove(ctxName);
    }

    /**
     * Always returns null since stateless components cannot be shutdown
     */
    protected Context[] getShutdownContexts(Object key) {
        return null;
    }

    private void prepare() throws CoreRuntimeException {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope not in INITIALIZED state [" + lifecycleState + "]");
        }
        if (contextMap == null) {
            contextMap = new ConcurrentHashMap<String, Context> ();
            for (ContextFactory<Context> config : contextFactorys.values()) {
                for (int i = 0; i < contextFactorys.size(); i++) {
                    Context context = config.createContext();
                    context.addListener(this);
                    context.start();
                    contextMap.put(context.getName(), context);
                }

            }
        }
    }

}