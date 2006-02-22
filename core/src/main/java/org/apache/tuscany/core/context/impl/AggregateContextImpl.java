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

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.ServiceNotFoundException;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.Extensible;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceUnavailableException;

/**
 * The standard implementation of an aggregate context. Autowiring is performed by delegating to the parent context.
 * 
 * @version $Rev$ $Date$
 */
public class AggregateContextImpl extends AbstractAggregateContext implements ConfigurationContext, ModuleContext {

    // ----------------------------------
    // Fields
    // ----------------------------------

    @Autowire(required = false)
    private AutowireContext autowireContext;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public AggregateContextImpl() {
        super();
        eventContext = new EventContextImpl();
    }

    public AggregateContextImpl(String name, AggregateContext parent, ScopeStrategy strategy, EventContext ctx,
            ConfigurationContext configCtx, MonitorFactory factory) {
        super(name, parent, strategy, ctx, configCtx, factory);
    }

    public AggregateContextImpl(String name, AggregateContext parent, AutowireContext autowireContext, ScopeStrategy strategy,
            EventContext ctx, ConfigurationContext configCtx, MonitorFactory factory) {
        super(name, parent, strategy, ctx, configCtx, factory);
        this.autowireContext = autowireContext;
    }

    // ----------------------------------
    // ModuleContext methods
    // ----------------------------------

    private String uri;

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public Object locateService(String qualifiedName) throws ServiceUnavailableException {
        checkInit();
        QualifiedName qName = new QualifiedName(qualifiedName);
        ScopeContext scope = scopeIndex.get(qName.getPartName());
        if (scope == null) {
            throw new ServiceNotFoundException(qualifiedName);
        }
        InstanceContext ctx = scope.getContext(qName.getPartName());
        try {
            Object o = ctx.getInstance(qName, true);
            if (o == null) {
                throw new ServiceUnavailableException(qualifiedName);
            }
            return o;
        } catch (TargetException e) {
            e.addContextName(getName());
            throw new ServiceUnavailableException(e);
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

    // ----------------------------------
    // AutowireContext methods
    // ----------------------------------

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (MonitorFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(monitorFactory);
        } else if (ConfigurationContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AutowireContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        if (autowireContext != null) {
            try {
                return autowireContext.resolveInstance(instanceInterface);
            } catch (AutowireResolutionException e) {
                e.addContextName(getName());
                throw e;
            }
        }
        return null;
    }

    @Override
    protected void registerAutowire(Extensible model) {
        // this context only delegates autowiring
    }

    // ----------------------------------
    // ConfigurationContext methods
    // ----------------------------------

    public void configure(Extensible model) throws ConfigurationException {
        if (configurationContext != null) {
            try {
                configurationContext.configure(model);
            } catch (ConfigurationException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    public void build(AggregateContext parent, Extensible model) throws BuilderConfigException {
        if (configurationContext != null) {
            try {
                configurationContext.build(parent, model);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        if (configurationContext != null) {
            try {
                configurationContext.wire(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    public void wire(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext) throws BuilderConfigException {
        if (configurationContext != null) {
            try {
                configurationContext.wire(targetFactory, targetType, targetScopeContext);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    // ----------------------------------
    // InstanceContext methods
    // ----------------------------------

    public Object getImplementationInstance() throws TargetException {
        return this;
    }

    public Object getImplementationInstance(boolean notify) throws TargetException {
        return this;
    }

}
