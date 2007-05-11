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

package org.apache.tuscany.core.runtime;

import java.util.List;

import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.impl.ComponentImpl;
import org.apache.tuscany.core.component.ServiceReferenceImpl;
import org.apache.tuscany.core.invocation.WireObjectFactory;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.invocation.ProxyFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.scope.ScopeContainer;
import org.osoa.sca.CallableReference;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeComponentImpl extends ComponentImpl implements RuntimeComponent {
    public static final String SELF_REFERENCE_PREFIX = "$self$.";
    protected ImplementationProvider implementationProvider;
    protected ProxyFactory proxyService;
    protected ScopeContainer scopeContainer;

    /**
     * @param proxyService
     */
    public RuntimeComponentImpl(ProxyFactory proxyService) {
        super();
        this.proxyService = proxyService;
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface) {
        return getServiceReference(businessInterface, SELF_REFERENCE_PREFIX);
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, String serviceName) {
        return getServiceReference(businessInterface, SELF_REFERENCE_PREFIX + serviceName);
    }

    public <B> B getProperty(Class<B> type, String propertyName) {
        for (Property p : getProperties()) {
            if (p.getName().equals(propertyName)) {
                // FIXME: Need to use the property object factory to create the
                // instance
                return null;
            }
        }
        return null;
    }

    public RequestContext getRequestContext() {
        return null;
    }

    public <B> B getService(Class<B> businessInterface, String referenceName) {
        List<ComponentReference> refs = getReferences();
        for (ComponentReference ref : refs) {
            if (ref.getName().equals(referenceName)) {
                RuntimeComponentReference attachPoint = (RuntimeComponentReference)ref;
                RuntimeWire wire = attachPoint.getRuntimeWires().get(0);
                return proxyService.createProxy(businessInterface, wire);
            }
        }
        return null;
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
        List<ComponentReference> references = getReferences();
        for (ComponentReference reference : references) {
            if (reference.getName().equals(referenceName) || referenceName.equals("$self$.")
                && reference.getName().startsWith(referenceName)) {
                RuntimeComponentReference attachPoint = (RuntimeComponentReference)reference;
                RuntimeWire wire = attachPoint.getRuntimeWires().get(0);
                WireObjectFactory<B> factory = new WireObjectFactory<B>(businessInterface, wire, proxyService);
                return new ServiceReferenceImpl<B>(businessInterface, factory);
            }
        }
        return null;

    }

    @SuppressWarnings("unchecked")
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        Object ref = proxyService.cast(target);
        return (R)ref;
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
}
