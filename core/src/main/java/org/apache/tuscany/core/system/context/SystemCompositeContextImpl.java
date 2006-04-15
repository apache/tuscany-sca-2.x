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
package org.apache.tuscany.core.system.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.MissingScopeException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.impl.AbstractCompositeContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.config.SystemObjectContextFactory;
import org.apache.tuscany.core.wire.ProxyFactory;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;


/**
 * Implements an composite context for system components. By default a system context uses the scopes specified by
 * {@link org.apache.tuscany.core.system.context.SystemScopeStrategy}. In addition, it implements an autowire policy
 * where entry points configured with a {@link org.apache.tuscany.core.system.assembly.SystemBinding} are matched
 * according to their exposed interface. A system context may contain child composite contexts but an entry point in a
 * child context will only be outwardly accessible if there is an entry point that exposes it configured in the
 * top-level system context.
 *
 * @version $Rev$ $Date$
 */
public class SystemCompositeContextImpl extends AbstractCompositeContext implements SystemCompositeContext {
    // a mapping of service type to component name
    private Map<Class, NameToScope> autowireIndex = new ConcurrentHashMap<Class, NameToScope>();

    @Autowire(required = false)
    private AutowireContext autowireContext;

    public SystemCompositeContextImpl() {
        super();
        eventContext = new EventContextImpl();
        scopeStrategy = new SystemScopeStrategy();
    }

    public SystemCompositeContextImpl(String name,
                                      CompositeContext parent,
                                      AutowireContext autowire,
                                      ScopeStrategy strategy,
                                      EventContext ctx,
                                      ConfigurationContext configCtx
    ) {
        super(name, parent, strategy, ctx, configCtx);
        this.autowireContext = autowire;
        scopeIndex = new ConcurrentHashMap<String, ScopeContext>();
    }

    public void setAutowireContext(AutowireContext context) {
        autowireContext = context;
    }

    public void registerJavaObject(String componentName, Class<?> service, Object instance) throws ConfigurationException {
        SystemObjectContextFactory configuration = new SystemObjectContextFactory(componentName, instance);
        registerConfiguration(configuration);
        ScopeContext scope = scopeContexts.get(configuration.getScope());
        NameToScope mapping = new NameToScope(new QualifiedName(componentName), scope, false, false);
        autowireIndex.put(service, mapping);
    }

    // FIXME These should be removed and configured
    private static final MessageFactory messageFactory = new MessageFactoryImpl();

    private static final ProxyFactoryFactory proxyFactoryFactory = new JDKProxyFactoryFactory();

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (RuntimeContext.class.equals(instanceInterface)) {
            return autowireContext.resolveInstance(instanceInterface);
        } else if (ConfigurationContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (CompositeContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AutowireContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (MessageFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(messageFactory);
        } else if (ProxyFactoryFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(proxyFactoryFactory);
        }

        NameToScope mapping = autowireIndex.get(instanceInterface);
        if (mapping != null) {
            try {
                return instanceInterface.cast(mapping.getScopeContext().getInstance(mapping.getName()));
            } catch (TargetException e) {
                AutowireResolutionException ae = new AutowireResolutionException("Autowire instance not found", e);
                ae.addContextName(getName());
                throw ae;
            }
        }
        if (autowireContext != null) {
            return autowireContext.resolveInstance(instanceInterface);
        } else {
            return null;
        }
    }

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        NameToScope nts = autowireIndex.get(instanceInterface);
        if (nts != null && nts.isVisible()) {
            return instanceInterface.cast(nts.getScopeContext().getInstance(nts.getName()));
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
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
            configurationContext.configure(model);
        }
    }

    public void build(AssemblyObject model) throws BuilderConfigException {
        if (configurationContext != null) {
            configurationContext.build(model);
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

    // ----------------------------------
    // Inner classes
    // ----------------------------------

    /**
     * Maps a context name to a scope
     * <p/>
     * TODO this is a duplicate of composite context
     */
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
