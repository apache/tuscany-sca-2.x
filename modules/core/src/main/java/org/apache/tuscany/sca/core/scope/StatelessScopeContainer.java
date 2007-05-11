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

import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.spi.component.TargetDestructionException;
import org.apache.tuscany.sca.spi.component.TargetResolutionException;

/**
 * A scope context which manages stateless atomic component instances in a non-pooled fashion.
 *
 * @version $Rev$ $Date$
 */
public class StatelessScopeContainer<KEY> extends AbstractScopeContainer<KEY> {

    public StatelessScopeContainer(RuntimeComponent component) {
        super(Scope.STATELESS, component);
    }

    public  InstanceWrapper getWrapper(KEY contextId)
        throws TargetResolutionException {
        InstanceWrapper ctx = createInstanceWrapper();
        ctx.start();
        return ctx;
    }

    public  InstanceWrapper getAssociatedWrapper(KEY contextId)
        throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public  void returnWrapper(InstanceWrapper wrapper, KEY contextId)
        throws TargetDestructionException {
        wrapper.stop();
    }
}
