/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.context.impl;

import org.osoa.sca.ModuleContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceUnavailableException;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.ServiceNotFoundException;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.system.annotation.Autowire;

/**
 * The standard implementation of an composite context. Autowiring is performed by delegating to the parent context.
 *
 * @version $Rev$ $Date$
 */
public class CompositeContextImpl extends AbstractCompositeContext implements ModuleContext {

    @Autowire
    public void setScopeStrategy(ScopeStrategy scopeStrategy) {
        if (scopeStrategy != null) {
            this.scopeStrategy = scopeStrategy;
        }
    }

    public CompositeContextImpl() {
        super();
        eventContext = new EventContextImpl();
    }

    public CompositeContextImpl(String name, CompositeContext parent, ScopeStrategy strategy, EventContext ctx,
                                ConfigurationContext configCtx) {
        super(name, parent, strategy, ctx, configCtx);
    }

    public CompositeContextImpl(String name, CompositeContext parent, AutowireContext autowireContext, ScopeStrategy strategy,
                                EventContext ctx, ConfigurationContext configCtx) {
        super(name, parent, strategy, ctx, configCtx);
        setAutowireContext(autowireContext);
    }

    // ----------------------------------
    // ModuleContext methods
    // ----------------------------------

    public Object locateService(String qualifiedName) throws ServiceUnavailableException {
        checkInit();
        QualifiedName qName = new QualifiedName(qualifiedName);
        ScopeContext scope = scopeIndex.get(qName.getPartName());
        if (scope == null) {
            throw new ServiceNotFoundException(qualifiedName);
        }
        Context ctx = scope.getContext(qName.getPartName());
        try {
            Object o = ctx.getInstance(qName);
            if (o == null) {
                throw new ServiceNotFoundException(qualifiedName);
            }
            return o;
        } catch (TargetException e) {
            e.addContextName(getName());
            throw new ServiceNotFoundException(e);
        }
    }

    public ServiceReference createServiceReference(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public RequestContext getRequestContext() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self, String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName, Object sessionId) {
        throw new UnsupportedOperationException();
    }
}
