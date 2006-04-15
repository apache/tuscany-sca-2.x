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

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ServiceNotFoundException;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.MissingScopeException;
import org.apache.tuscany.core.invocation.ProxyFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceUnavailableException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The standard implementation of an composite context. Autowiring is performed by delegating to the parent context.
 *
 * @version $Rev$ $Date$
 */
public class CompositeContextImpl extends AbstractCompositeContext implements ConfigurationContext, ModuleContext {

    // a mapping of service type to component name
    private Map<Class, NameToScope> autowireIndex = new ConcurrentHashMap<Class, NameToScope>();

    @Autowire(required = false)
    private AutowireContext autowireContext;

    @Autowire(required = false)
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
        Context ctx = scope.getContext(qName.getPartName());
        try {
            Object o = ctx.getInstance(qName);
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

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        NameToScope nts = autowireIndex.get(instanceInterface);
        if (nts != null && nts.isVisible()) {
            return instanceInterface.cast(nts.getScopeContext().getInstance(nts.getName()));
            // } else if (autowireContext != null) {
            // return instanceInterface.cast(autowireContext.resolveExternalInstance(instanceInterface));
        } else {
            return null;
        }
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (ConfigurationContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AutowireContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        NameToScope nts = autowireIndex.get(instanceInterface);
        if (nts != null) {
            return instanceInterface.cast(nts.getScopeContext().getInstance(nts.getName()));
        }
        if (autowireContext != null) {
            try {
                // resolve to parent
                return instanceInterface.cast(autowireContext.resolveInstance(instanceInterface));
            } catch (AutowireResolutionException e) {
                e.addContextName(getName());
                throw e;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void registerAutowire(Extensible model) throws ConfigurationException {
        if (lifecycleState == INITIALIZING || lifecycleState == INITIALIZED || lifecycleState == RUNNING) {
            if (model instanceof EntryPoint) {
                EntryPoint ep = (EntryPoint) model;
                for (Binding binding : ep.getBindings()) {
                    if (binding instanceof SystemBinding) {
                        Class interfaze = ep.getConfiguredService().getPort().getServiceContract().getInterface();
                        NameToScope nts = autowireIndex.get(interfaze);
                        if (nts == null || !nts.isEntryPoint()) { // handle special case where two entry points with
                            // same interface register: first wins
                            ScopeContext scope = scopeContexts.get(((ContextFactory) ep.getContextFactory()).getScope());
                            if (scope == null) {
                                ConfigurationException ce = new MissingScopeException("Scope not found for entry point");
                                ce.setIdentifier(ep.getName());
                                ce.addContextName(getName());
                                throw ce;
                            }
                            // only register if an impl has not already been registered
                            NameToScope mapping = new NameToScope(new QualifiedName(ep.getName()), scope, true, true);
                            autowireIndex.put(interfaze, mapping);
                        }
                    }
                }
            } else if (model instanceof ModuleComponent) {
                ModuleComponent component = (ModuleComponent) model;
                for (EntryPoint ep : component.getImplementation().getEntryPoints()) {
                    for (Binding binding : ep.getBindings()) {
                        if (binding instanceof SystemBinding) {
                            Class interfaze = ep.getConfiguredService().getPort().getServiceContract().getInterface();
                            if (autowireIndex.get(interfaze) == null) {
                                ScopeContext scope = scopeContexts.get(Scope.AGGREGATE);
                                // only register if an impl has not already been registered, ensuring it is not visible outside the containment
                                NameToScope mapping = new NameToScope(new QualifiedName(component.getName()
                                        + QualifiedName.NAME_SEPARATOR + ep.getName()), scope, false, false);
                                autowireIndex.put(interfaze, mapping);
                            }
                        }
                    }
                }
            } else if (model instanceof Component) {
                Component component = (Component) model;
                for (Service service : component.getImplementation().getComponentInfo().getServices()) {
                    Class interfaze = service.getServiceContract().getInterface();
                    if (autowireIndex.get(interfaze) == null) {
                        // only register if an impl has not already been registered
                        ScopeContext scopeCtx = scopeContexts.get(service.getServiceContract().getScope());
                        NameToScope mapping = new NameToScope(new QualifiedName(component.getName()), scopeCtx, false, false);
                        autowireIndex.put(interfaze, mapping);
                    }
                }
            }
        }
    }

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

    public void build(AssemblyObject model) throws BuilderConfigException {
        if (configurationContext != null) {
            try {
                configurationContext.build(model);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        if (configurationContext != null) {
            try {
                configurationContext.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        if (configurationContext != null) {
            try {
                configurationContext.completeTargetChain(targetFactory, targetType, targetScopeContext);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
    }

    private class NameToScope {

        private QualifiedName qName;

        private ScopeContext scope;

        private boolean visible;

        private boolean entryPoint;

        public NameToScope(QualifiedName name, ScopeContext scope, boolean visible, boolean entryPoint) {
            this.qName = name;
            this.scope = scope;
            this.visible = visible;
            this.entryPoint = entryPoint;
        }

        public QualifiedName getName() {
            return qName;
        }

        public ScopeContext getScopeContext() {
            return scope;
        }

        public boolean isVisible() {
            return visible;
        }

        public boolean isEntryPoint() {
            return entryPoint;
        }

    }

}
