/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.core.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.event.RequestEnd;
import org.apache.tuscany.sca.event.Event;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * A scope context which manages atomic component instances keyed on the current
 * request context
 * 
 * @version $Rev$ $Date$
 */
public class RequestScopeContainer extends AbstractScopeContainer<Thread> {
    private final Map<Thread, InstanceWrapper> contexts;

    public RequestScopeContainer(RuntimeComponent component) {
        super(Scope.REQUEST, component);
        contexts = new ConcurrentHashMap<Thread, InstanceWrapper>();
    }

    @Override
    public void onEvent(Event event) {
        checkInit();
        if (event instanceof RequestEnd) {
            // shutdownInstances(Thread.currentThread());
        }
    }

    @Override
    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    @Override
    public synchronized void stop() {
        contexts.clear();
        // synchronized (destroyQueues) {
        // destroyQueues.clear();
        // }
        lifecycleState = STOPPED;
    }

    protected InstanceWrapper getInstanceWrapper(boolean create) throws TargetResolutionException {
        InstanceWrapper ctx = wrappers.get(Thread.currentThread());
        if (ctx == null && !create) {
            return null;
        }
        if (ctx == null) {
            ctx = super.createInstanceWrapper();
            ctx.start();
            wrappers.put(Thread.currentThread(), ctx);
        }
        return ctx;
    }

    @Override
    public InstanceWrapper getWrapper(Thread contextId) throws TargetResolutionException {
        return getInstanceWrapper(true);
    }

}
