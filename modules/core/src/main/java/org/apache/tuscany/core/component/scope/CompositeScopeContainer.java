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

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Service;

/**
 * A scope context which manages atomic component instances keyed by composite
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(ScopeContainer.class)
public class CompositeScopeContainer<KEY> extends AbstractScopeContainer<KEY> {
    private static final InstanceWrapper<Object> EMPTY = new InstanceWrapper<Object>() {
        public Object getInstance() {
            return null;
        }

        public boolean isStarted() {
            return true;
        }

        public void start() throws TargetInitializationException {

        }

        public void stop() throws TargetDestructionException {

        }
    };

    // there is one instance per component so we can index directly
    private final Map<AtomicComponent<?>, InstanceWrapper<?>> instanceWrappers =
        new ConcurrentHashMap<AtomicComponent<?>, InstanceWrapper<?>>();

    public CompositeScopeContainer(@Monitor ScopeContainerMonitor monitor) {
        super(Scope.COMPOSITE, monitor);
    }

    public <T> void register(AtomicComponent<T> component, URI groupId) {
        super.register(component, groupId);
        instanceWrappers.put(component, EMPTY);
    }

    public <T> void unregister(AtomicComponent<T> component) {
        // FIXME should this component be destroyed already or do we need to stop it?
        instanceWrappers.remove(component);
        super.unregister(component);
    }

    public synchronized void stop() {
        super.stop();
        instanceWrappers.clear();
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        assert instanceWrappers.containsKey(component);
        @SuppressWarnings("unchecked")
        InstanceWrapper<T> wrapper = (InstanceWrapper<T>) instanceWrappers.get(component);
        if (wrapper == EMPTY) {
            // FIXME is there a potential race condition here that may result in two instances being created
            wrapper = createInstance(component);
            instanceWrappers.put(component, wrapper);
            wrapper.start();
            destroyQueues.get(contextId).add(wrapper);
        }
        return wrapper;
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        assert instanceWrappers.containsKey(component);
        @SuppressWarnings("unchecked")
        InstanceWrapper<T> wrapper = (InstanceWrapper<T>) instanceWrappers.get(component);
        if (wrapper == EMPTY) {
            throw new TargetNotFoundException(component.getUri().toString());
        }
        return wrapper;
    }
}
