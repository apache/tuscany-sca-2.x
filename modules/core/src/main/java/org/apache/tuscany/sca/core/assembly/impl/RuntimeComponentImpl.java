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

package org.apache.tuscany.sca.core.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.impl.ComponentImpl;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeComponentImpl extends ComponentImpl implements RuntimeComponent,
                                              ScopedRuntimeComponent, ResolverExtension {
    protected RuntimeComponentContext componentContext;
    protected ImplementationProvider implementationProvider;
    protected List<PolicyProvider> policyProviders = new ArrayList<PolicyProvider>();
    protected ScopeContainer scopeContainer;
    protected boolean started;
    protected ModelResolver modelResolver;

    /**
     */
    public RuntimeComponentImpl() {
        super();
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
    public RuntimeComponentContext getComponentContext() {
        return componentContext;
    }

    /**
     * @param componentContext the componentContext to set
     */
    public void setComponentContext(RuntimeComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    public void addPolicyProvider(PolicyProvider policyProvider) {
        policyProviders.add(policyProvider);
    }

    public List<PolicyProvider> getPolicyProviders() {
        return policyProviders;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }
    
    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String serviceName) {
        RuntimeComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite component that provides the requested service
        if (getImplementation() instanceof Composite) {
            for (ComponentService componentService : getServices()) {
                String bindingName = null;
                if (serviceName != null) {
                    int index = serviceName.indexOf('/');
                    if (index != -1) {
                        bindingName = serviceName.substring(index + 1);
                        serviceName = serviceName.substring(0, index);
                    }
                }
                if (serviceName == null || serviceName.equals(componentService.getName())) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        componentContext =
                            ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                        serviceName = compositeService.getPromotedService().getName();
                        if (bindingName != null) {
                            serviceName = serviceName + "/" + bindingName;
                        }
                        return componentContext.createSelfReference(businessInterface, serviceName);
                    }
                    break;
                }
            }
            // No matching service found
            throw new ServiceRuntimeException("Composite service not found: " + serviceName);
        } else {
            componentContext = getComponentContext();
            if (serviceName != null) {
                return componentContext.createSelfReference(businessInterface, serviceName);
            } else {
                return componentContext.createSelfReference(businessInterface);
            }
        }
    }
}
