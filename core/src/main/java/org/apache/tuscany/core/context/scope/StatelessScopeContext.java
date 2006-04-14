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
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A container that manages stateless components.
 * 
 * @version $Rev$ $Date$
 */
public class StatelessScopeContext extends AbstractScopeContext {

   // Component contexts keyed by name
    private Map<String, Context> contexts;

    public StatelessScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Stateless Scope");
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        contexts = null;
        lifecycleState = STOPPED;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactories.put(configuration.getName(), configuration);
        if (contexts != null) {
            contexts.put(configuration.getName(), configuration.createContext());
        }
    }

    public void onEvent(Event event){
        // do nothing
    }

    public boolean isCacheable() {
        return true;
    }

    public Context getContext(String ctxName) {
        prepare();
        return contexts.get(ctxName);
    }

    public Context getContextByKey(String ctxName, Object key) {
        return getContext(ctxName);
    }

    public void removeContext(String ctxName) {
        if (contexts == null){
            return;
        }
        contexts.remove(ctxName);
    }

    public void removeContextByKey(String ctxName, Object key) {
        removeContext(ctxName);
    }

    private void prepare() throws CoreRuntimeException {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope not in INITIALIZED state [" + lifecycleState + "]");
        }
        if (contexts == null) {
            contexts = new ConcurrentHashMap<String, Context> ();
            for (ContextFactory<Context> config : contextFactories.values()) {
                for (int i = 0; i < contextFactories.size(); i++) {
                    Context context = config.createContext();
                    context.start();
                    contexts.put(context.getName(), context);
                }

            }
        }
    }

}