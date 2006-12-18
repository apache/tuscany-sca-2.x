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
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

/**
 * A scope context which manages stateless atomic component instances in a non-pooled fashion
 *
 * @version $Rev$ $Date$
 */
public class StatelessScopeContainer extends AbstractScopeContainer {

    public StatelessScopeContainer(WorkContext workContext, ScopeContainerMonitor monitor) {
        super(workContext, monitor);
    }

    public Scope getScope() {
        return Scope.STATELESS;
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        lifecycleState = STOPPED;
    }

    public void onEvent(Event event) {
    }

    public void register(AtomicComponent component) {
        checkInit();
    }

    protected InstanceWrapper getInstanceWrapper(AtomicComponent component, boolean create)
        throws TargetResolutionException {
        // there never is a previously associated instance, return null
        if (!create) {
            return null;
        }
        InstanceWrapper ctx = new InstanceWrapperImpl(component, component.createInstance());
        ctx.start();
        return ctx;
    }

}
