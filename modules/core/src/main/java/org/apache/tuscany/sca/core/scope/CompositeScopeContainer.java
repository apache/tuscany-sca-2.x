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

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * A scope context which manages atomic component instances keyed by composite
 * 
 * @version $Rev$ $Date$
 */
public class CompositeScopeContainer<KEY> extends AbstractScopeContainer<KEY> {
    private InstanceWrapper<?> wrapper;

    public CompositeScopeContainer(RuntimeComponent component) {
        super(Scope.COMPOSITE, component);
    }

    @Override
    public synchronized void stop() {
        super.stop();
        if (wrapper != null) {
            try {
                wrapper.stop();
            } catch (TargetDestructionException e) {
                throw new IllegalStateException(e);
            }
        }
        wrapper = null;
    }

    @Override
    public synchronized InstanceWrapper getWrapper(KEY contextId) throws TargetResolutionException {
        if (wrapper == null) {
            wrapper = createInstanceWrapper();
            wrapper.start();
        }
        return wrapper;
    }

    @Override
    public InstanceWrapper getAssociatedWrapper(KEY contextId) throws TargetResolutionException {
        if (wrapper == null) {
            throw new TargetNotFoundException(component.getURI());
        }
        return wrapper;
    }

    @Override
    public synchronized void start() {
        super.start();
        if (isEagerInit()) {
            try {
                getWrapper(null);
            } catch (TargetResolutionException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
