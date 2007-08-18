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

package org.apache.tuscany.sca.core.runtime;

import org.apache.tuscany.sca.assembly.impl.ComponentImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.scope.ScopeContainer;
import org.apache.tuscany.sca.scope.ScopedRuntimeComponent;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeComponentImpl extends ComponentImpl implements RuntimeComponent, ScopedRuntimeComponent {
    protected ComponentContext componentContext;
    protected ImplementationProvider implementationProvider;
    protected ProxyFactory proxyService;
    protected ScopeContainer scopeContainer;
    protected boolean started;

    /**
     * @param proxyService
     */
    public RuntimeComponentImpl(ProxyFactory proxyService) {
        super();
        this.proxyService = proxyService;
    }

    public ImplementationProvider getImplementationProvider() {
        return implementationProvider;
    }

    public void setImplementationProvider(ImplementationProvider provider) {
        this.implementationProvider = provider;
    }

    public ScopeContainer getScopeContainer() {
        return scopeContainer;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
    }
    
    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * @return the componentContext
     */
    public ComponentContext getComponentContext() {
        return componentContext;
    }

    /**
     * @param componentContext the componentContext to set
     */
    public void setComponentContext(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }
}
