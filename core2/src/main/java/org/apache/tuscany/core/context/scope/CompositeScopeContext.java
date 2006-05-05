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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.core.AbstractLifecycle;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeInitializationException;
import org.apache.tuscany.spi.context.ScopeRuntimeException;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.event.Event;

/**
 * Manages the lifecycle of composite component contexts, i.e. contexts which contain child contexts
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 * @see org.apache.tuscany.spi.context.CompositeContext
 */
public class CompositeScopeContext extends AbstractLifecycle implements ScopeContext<CompositeContext> {

    // Composite component contexts in this scope keyed by parent
    private final List<CompositeContext> contexts;

    public CompositeScopeContext() {
        setName("Composite Scope");
        contexts = new CopyOnWriteArrayList<CompositeContext>();
    }

    public Scope getScope() {
        return Scope.AGGREGATE;
    }

    public void start() throws ScopeInitializationException {
        lifecycleState = Lifecycle.RUNNING;
    }

    public void stop() throws ScopeRuntimeException {
        lifecycleState = STOPPED;
    }

    public void onEvent(Event event) {
    }

    public void register(CompositeContext context) {
        checkInit();
        contexts.add(context);
    }

    public Object getInstance(CompositeContext context) throws TargetException {
        checkInit();
        return context; // return the context since it is the instance
    }

    public InstanceContext getInstanceContext(CompositeContext context) throws TargetException {
        throw new UnsupportedOperationException();
    }

    private void checkInit() {
        if (getLifecycleState() != Lifecycle.RUNNING) {
            throw new IllegalStateException("Scope not running [" + getLifecycleState() + "]");
        }
    }


}
