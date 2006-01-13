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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.RuntimeEventListener;

/**
 * A container that manages stateless components.
 * 
 * @version $Rev$ $Date$
 */
public class StatelessScopeContext extends AbstractScopeContext implements RuntimeEventListener, LifecycleEventListener {

    // ----------------------------------
    // Fields
    // ----------------------------------

    // Component contexts keyed by name
    private Map<String, InstanceContext> contextMap;

    // ----------------------------------
    // Constructor
    // ----------------------------------

    public StatelessScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Stateless Scope");
    }

    // ----------------------------------
    // Lifecycle methods
    // ----------------------------------

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
        super.start();
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        super.stop();
        contextMap = null;
        lifecycleState = STOPPED;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void registerConfiguration(RuntimeConfiguration<InstanceContext> configuration) {
        runtimeConfigurations.put(configuration.getName(), configuration);
        if (lifecycleState == RUNNING) {
            contextMap.put(configuration.getName(), configuration.createInstanceContext());
        }

    }

    public void onEvent(int type, Object key) {
        // do nothing
    }

    public boolean isCacheable() {
        return true;
    }

    public InstanceContext getContext(String ctxName) {
        prepare();
        return contextMap.get(ctxName);
    }

    public InstanceContext getContextByKey(String ctxName, Object key) {
        prepare();
        return getContext(ctxName);
    }

    public void removeContext(String ctxName) {
        prepare();
        removeContextByKey(ctxName, null);
    }

    public void removeContextByKey(String ctxName, Object key) {
        prepare();
        contextMap.remove(ctxName);
    }

    /**
     * Always returns null since stateless components cannot be shutdown
     */
    protected InstanceContext[] getShutdownContexts(Object key) {
        return null;
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    public void onInstanceCreate(Context component) {
        // do nothing
    }

    private void prepare() throws CoreRuntimeException {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope not in INITIALIZED state [" + lifecycleState + "]");
        }
        if (contextMap == null) {
            contextMap = new ConcurrentHashMap();
            for (RuntimeConfiguration<InstanceContext> config : runtimeConfigurations.values()) {
                for (int i = 0; i < runtimeConfigurations.size(); i++) {
                    InstanceContext context = null;
                    context = config.createInstanceContext();
                    context.addContextListener(this);
                    context.start();
                    contextMap.put(context.getName(), context);
                }

            }
        }
    }

}