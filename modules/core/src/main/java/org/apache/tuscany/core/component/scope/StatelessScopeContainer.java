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

import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Service;

/**
 * A scope context which manages stateless atomic component instances in a non-pooled fashion.
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(ScopeContainer.class)
public class StatelessScopeContainer<KEY> extends AbstractScopeContainer<KEY> {

    public StatelessScopeContainer(@Monitor ScopeContainerMonitor monitor) {
        super(Scope.STATELESS, monitor);
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        InstanceWrapper<T> ctx = createInstance(component);
        ctx.start();
        return ctx;
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent<T> component, KEY contextId)
        throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public <T> void returnWrapper(AtomicComponent<T> component, InstanceWrapper<T> wrapper, KEY contextId)
        throws TargetDestructionException {
        wrapper.stop();
    }
}
