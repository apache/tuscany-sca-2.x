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
package org.apache.tuscany.core.scope;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.TargetResolutionException;

/**
 * A scope context which manages atomic component instances keyed by composite
 *
 * @version $Rev$ $Date$
 */
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
    private final Map<RuntimeComponent, InstanceWrapper> instanceWrappers =
        new ConcurrentHashMap<RuntimeComponent, InstanceWrapper>();

    public CompositeScopeContainer() {
        super(Scope.COMPOSITE);
    }

    public  void register(RuntimeComponent component, URI groupId) {
        super.register(component, groupId);
        instanceWrappers.put(component, EMPTY);
    }

    public  void unregister(RuntimeComponent component) {
        // FIXME should this component be destroyed already or do we need to stop it?
        instanceWrappers.remove(component);
        super.unregister(component);
    }

    public synchronized void stop() {
        super.stop();
        instanceWrappers.clear();
    }

    public  InstanceWrapper getWrapper(RuntimeComponent component, KEY contextId)
        throws TargetResolutionException {
        assert instanceWrappers.containsKey(component);
        @SuppressWarnings("unchecked")
        InstanceWrapper wrapper = (InstanceWrapper) instanceWrappers.get(component);
        if (wrapper == EMPTY) {
            // FIXME is there a potential race condition here that may result in two instances being created
            wrapper = createInstance(component);
            instanceWrappers.put(component, wrapper);
            wrapper.start();
            // FIXME: [rfeng]
            if (contextId != null) {
                destroyQueues.get(contextId).add(wrapper);
            }
        }
        return wrapper;
    }

    public  InstanceWrapper getAssociatedWrapper(RuntimeComponent component, KEY contextId)
        throws TargetResolutionException {
        assert instanceWrappers.containsKey(component);
        @SuppressWarnings("unchecked")
        InstanceWrapper wrapper = (InstanceWrapper) instanceWrappers.get(component);
        if (wrapper == EMPTY) {
            throw new TargetNotFoundException(component.getURI());
        }
        return wrapper;
    }
}
