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
package org.apache.tuscany.implementation.java.context;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;

/**
 * @version $Rev$ $Date$
 */
public class ComponentObjectFactory<T, CONTEXT> implements ObjectFactory<T> {
    private final ScopeContainer<CONTEXT> scopeContainer;

    public ComponentObjectFactory(RuntimeComponent component) {
        this.scopeContainer = component.getScopeContainer();
    }

    public T getInstance() throws ObjectCreationException {
        try {
            WorkContext workContext = WorkContextTunnel.getThreadWorkContext();
            @SuppressWarnings("unchecked")
            CONTEXT contextId = (CONTEXT) workContext.getIdentifier(scopeContainer.getScope());
            return (T) scopeContainer.getWrapper(contextId).getInstance();
        } catch (TargetResolutionException e) {
            throw new ObjectCreationException(e);
        }
    }
}
