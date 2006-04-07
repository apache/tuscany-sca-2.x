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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements functionality common to scope contexts.
 * <p>
 * <b>NB: </b>Minimal synchronization is performed, particularly for initializing and destroying scopes, and it is
 * assumed the scope container will block requests until these operations have completed.
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContext extends AbstractContext implements ScopeContext {

    // The collection of runtime configurations for the scope
    protected Map<String, ContextFactory<InstanceContext>> contextFactorys = new ConcurrentHashMap<String, ContextFactory<InstanceContext>>();

    // The event context the scope container is associated with
    protected EventContext eventContext;

    public AbstractScopeContext(EventContext eventContext) {
        assert (eventContext != null) : "Event context was null";
        this.eventContext = eventContext;
    }

    public synchronized void start() {
    }

    public synchronized void stop() {
    }

    public void registerFactories(List<ContextFactory<InstanceContext>> configurations) {
        for (ContextFactory<InstanceContext> configuration : configurations) {
            contextFactorys.put(configuration.getName(), configuration);
        }
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        InstanceContext context = getContext(qName.getPartName());
        if (context == null) {
            TargetException e = new TargetException("Target not found");
            e.setIdentifier(qName.getQualifiedName());
            throw e;
        }
        return context.getInstance(qName);
    }

    protected EventContext getEventContext() {
        return eventContext;
    }

    /**
     * Notfies instances that are associated with a context and configured to receive callbacks that the context is
     * being destroyed in reverse order
     * 
     * @param key the context key
     */
    protected void notifyInstanceShutdown(Object key) {
        InstanceContext[] contexts = getShutdownContexts(key);
        if ((contexts == null) || (contexts.length < 1)) {
            return;
        }
        // shutdown destroyable instances in reverse instantiation order
        for (int i = contexts.length - 1; i >= 0; i--) {
            InstanceContext context = contexts[i];

            if (context.getLifecycleState() == RUNNING) {
                synchronized (context) {
                    removeContextByKey(context.getName(), key);
                    try {
                        context.stop();
                    } catch (TargetException e) {
                        // TODO send a monitoring event
                        // log.error("Error releasing instance [" + context.getName() + "]",e);
                    }
                }
            }
        }
    }

    protected void checkInit() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope not running [" + lifecycleState + "]");
        }
    }

    /**
     * Returns an array of contexts that need to be notified of scope shutdown. The array must be in the order in which
     * component contexts were created
     */
    protected abstract InstanceContext[] getShutdownContexts(Object key);

}
