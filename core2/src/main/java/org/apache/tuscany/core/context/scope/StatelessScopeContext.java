/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context.scope;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.event.Event;

/**
 * A container that manages stateless components.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class StatelessScopeContext extends AbstractScopeContext<AtomicContext> {

    private Map<CompositeContext, List<AtomicContext>> contexts;
    private WorkContext workContext;

    public StatelessScopeContext(WorkContext workContext) {
        super("Stateless scope", workContext);
        assert(workContext != null): "Work context was null";
        this.workContext = workContext;
    }

    public Scope getScope() {
        return Scope.INSTANCE;
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
        //TODO stop all contexts
    }

    public void onEvent(Event event) {
        List<AtomicContext> ctxs = contexts.get(workContext.getCurrentModule());
        if(ctxs != null){
            for (AtomicContext atomicContext : ctxs) {
                atomicContext.stop();
            }
        }
    }

    public void register(AtomicContext context) {
        checkInit();
        CompositeContext module = workContext.getCurrentModule();
        List<AtomicContext> ctxs = contexts.get(module);
        if (ctxs == null) {
            ctxs = new ArrayList<AtomicContext>();
        }
        synchronized (ctxs) {
            ctxs.add(context);
        }
        contexts.put(module, ctxs);
    }

    public Object getInstance(AtomicContext context) throws TargetException {
        return context.createInstance();
    }

    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
       throw new UnsupportedOperationException();
    }

}
