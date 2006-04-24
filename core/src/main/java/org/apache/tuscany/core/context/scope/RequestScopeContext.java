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
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.core.context.event.Event;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of a request-scoped component container.  This scope contexts eagerly starts contexts when
 * a {@link org.apache.tuscany.core.context.event.RequestStart} event is received. If a contained context has an implementation marked to eagerly initialized,
 * the an instance will be created at that time as well. Contained contexts are shutdown when a {@link org.apache.tuscany.core.context.event.RequestEnd}
 * event is received in reverse order to which their implementation instances were created.
 * 
 * @version $Rev$ $Date$
 */
public class RequestScopeContext extends AbstractScopeContext {

    // A collection of service component contexts keyed by thread. Note this could have been implemented with a ThreadLocal but
    // using a Map allows finer-grained concurrency.
    private Map<Object, Map<String, Context>> contexts;

    // stores ordered lists of contexts to shutdown for each thread.
    private Map<Object, List<Context>> destroyQueues;

    public RequestScopeContext(EventContext eventContext) {
        super(eventContext);
        setName("Request Scope");
    }

    public void onEvent(Event event){
        if (event instanceof RequestStart){
            getContexts(); // eager load
        }else if (event instanceof RequestEnd){
            checkInit();
            getEventContext().clearIdentifiers(); // clean up current context for pooled threads
            shutdownContexts();
            cleanupRequestContexts();
        }else if (event instanceof InstanceCreated){
            checkInit();
            assert(event.getSource() instanceof Context): "Context must be passed on created event";
            Context context = (Context)event.getSource();
            List<Context> collection = destroyQueues.get(Thread.currentThread());
            collection.add(context);
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
        contexts = new ConcurrentHashMap<Object, Map<String, Context>>();
        destroyQueues = new ConcurrentHashMap<Object, List<Context>>();
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        contexts = null;
        destroyQueues = null;
        lifecycleState = STOPPED;
    }

    public boolean isCacheable() {
        return true;
    }

    public void registerFactory(ContextFactory<Context> configuration) {
        contextFactories.put(configuration.getName(), configuration);
    }

    public Context getContext(String ctxName) {
        checkInit();
        Map<String, Context> contexts = getContexts();
        Context ctx = contexts.get(ctxName);
        if (ctx == null){
            // check to see if the configuration was added after the request was started
            ContextFactory<Context> configuration = contextFactories.get(ctxName);
            if (configuration != null) {
                ctx = configuration.createContext();
                //ctx.addListener(this);
                ctx.start();
                contexts.put(ctx.getName(), ctx);
            }
        }
        return ctx;
    }

    public Context getContextByKey(String ctxName, Object key) {
        checkInit();
        if (key == null) {
            return null;
        }
        Map<String, Context> components = contexts.get(key);
        if (components == null) {
            return null;
        }
        return components.get(ctxName);
    }

    public void removeContext(String ctxName) {
        removeContextByKey(ctxName, Thread.currentThread());
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
    }



    private void cleanupRequestContexts() {
        // TODO uninitialize all request-scoped components
        contexts.remove(Thread.currentThread());
        destroyQueues.remove(Thread.currentThread());
    }

    /**
     * Initializes ServiceComponentContexts for the current request.
     * <p>
     * TODO This eagerly creates all component contexts, even if the component is never accessed during the request. This method
     * should be profiled to determine if lazy initialization is more performant
     * <p>
     * TODO Eager initialization is not performed for request-scoped components
     */

    private Map<String, Context> getContexts() throws CoreRuntimeException {
        Map<String, Context>  requestContexts = this.contexts.get(Thread.currentThread());
        if (requestContexts == null) {
            requestContexts = new ConcurrentHashMap<String, Context>();
            List<Context> shutdownQueue = new ArrayList<Context>();
            for (ContextFactory<Context> config : contextFactories.values()) {
                Context context = config.createContext();
                context.start();
                requestContexts.put(context.getName(), context);
            }
            // initialize eager components. Note this cannot be done when we initially create each context since a component may
            // contain a forward reference to a component which has not been instantiated
            for (Context context : requestContexts.values()) {
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
            contexts.put(Thread.currentThread(), requestContexts);
            destroyQueues.put(Thread.currentThread(), shutdownQueue);
        }
        return requestContexts;
    }

    private void shutdownContexts() {
          List<Context> destroyQueue = destroyQueues.remove(Thread.currentThread());
          if (destroyQueue == null || destroyQueue.size() == 0) {
             return;
          }
          synchronized(destroyQueue){
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
          }
          // shutdown contexts
          Map<String,Context> currentContexts = contexts.remove(Thread.currentThread());
          if (currentContexts == null){
             return;
          }
          for (Lifecycle context: currentContexts.values()){
              if (context.getLifecycleState() == RUNNING) {
                  context.stop();
              }
          }

      }

}