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

import static org.apache.tuscany.core.context.EventContext.HTTP_SESSION;
import static org.apache.tuscany.core.context.EventContext.REQUEST_END;
import static org.apache.tuscany.core.context.EventContext.SESSION_NOTIFY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.config.SystemObjectRuntimeConfiguration;
import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.AggregatePart;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;

/**
 * Implements an aggregate context for system components. By default a system context uses the scopes specified by
 * {@link org.apache.tuscany.core.system.context.SystemScopeStrategy}. In addition, it implements an autowire policy
 * where entry points configured with a {@link org.apache.tuscany.core.system.assembly.SystemBinding} are matched
 * according to their exposed interface. A system context may contain child aggregate contexts but an entry point in a
 * child context will only be outwardly accessible if there is an entry point that exposes it configured in the
 * top-level system context.
 * 
 * @version $Rev$ $Date$
 */
public class SystemAggregateContextImpl extends AbstractContext implements SystemAggregateContext {

    public static final int DEFAULT_WAIT = 1000 * 60;

    // ----------------------------------
    // Fields
    // ----------------------------------

    // The parent context, if one exists
    @ParentContext
    protected AggregateContext parentContext;

    // The parent configuration context, if one exists
    @Autowire(required = false)
    protected ConfigurationContext configurationContext;

    // The system monitor factory
    @Autowire(required = false)
    protected MonitorFactory monitorFactory;

    // The logical model representing the module assembly
    // protected ModuleComponent moduleComponent;
    protected Module module;

    protected List<RuntimeConfiguration<InstanceContext>> configurations = new ArrayList();

    protected ScopeStrategy scopeStrategy;

    // The event context for associating context events to threads
    protected EventContext eventContext;

    // The scopes for this context
    protected Map<Scope, ScopeContext> scopeContexts;

    protected Map<Scope, ScopeContext> immutableScopeContexts;

    // A component context name to scope context index
    protected Map<String, ScopeContext> scopeIndex;

    // Listeners for context events
    protected List<RuntimeEventListener> listeners = new CopyOnWriteArrayList();

    // Blocking latch to ensure the module is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);

    // Indicates whether the module context has been initialized
    protected boolean initialized;

    // a mapping of service type to component name
    private Map<Class, NameToScope> autowireIndex = new ConcurrentHashMap();

    @Autowire(required = false)
    private AutowireContext autowireContext;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemAggregateContextImpl() {
        super();
        scopeIndex = new ConcurrentHashMap();
        //FIXME the assembly factory should be injected here
        module = new AssemblyFactoryImpl().createModule();
        eventContext = new EventContextImpl();
        scopeStrategy = new SystemScopeStrategy();
    }

    public SystemAggregateContextImpl(String name, AggregateContext parent, AutowireContext autowire, ScopeStrategy strategy,
            EventContext ctx, ConfigurationContext configCtx, MonitorFactory factory) {
        super(name);
        this.parentContext = parent;
        this.autowireContext = autowire;
        this.scopeStrategy = strategy;
        this.eventContext = ctx;
        this.configurationContext = configCtx;
        this.monitorFactory = factory;
        scopeIndex = new ConcurrentHashMap();
        //FIXME the assembly factory should be injected here
        module = new AssemblyFactoryImpl().createModule();
    }

    // ----------------------------------
    // Lifecycle methods
    // ----------------------------------

    public void start() {
        synchronized (initializeLatch) {
            try {
                if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                    throw new IllegalStateException("Context not in UNINITIALIZED state");
                }

                lifecycleState = INITIALIZING;
                initializeScopes();

                Map<Scope, List<RuntimeConfiguration<SimpleComponentContext>>> configurationsByScope = new HashMap();
                if (configurations != null) {
                    for (RuntimeConfiguration config : configurations) {
                        // FIXME scopes are defined at the interface level
                        Scope scope = config.getScope();
                        // ensure duplicate names were not added before the context was started
                        if (scopeIndex.get(config.getName()) != null) {
                            throw new DuplicateNameException(config.getName());
                        }
                        scopeIndex.put(config.getName(), scopeContexts.get(scope));
                        List<RuntimeConfiguration<SimpleComponentContext>> list = configurationsByScope.get(scope);
                        if (list == null) {
                            list = new ArrayList();
                            configurationsByScope.put(scope, list);
                        }
                        list.add(config);
                    }
                }
                for (EntryPoint ep : module.getEntryPoints()) {
                    registerAutowire(ep);
                }
                for (Component component : module.getComponents()) {
                    registerAutowire(component);
                }
                for (ExternalService es : module.getExternalServices()) {
                    registerAutowire(es);
                }
                for (Map.Entry entries : configurationsByScope.entrySet()) {
                    // register configurations with scope contexts
                    ScopeContext scope = scopeContexts.get(entries.getKey());
                    scope.registerConfigurations((List<RuntimeConfiguration<InstanceContext>>) entries.getValue());
                }
                for (ScopeContext scope : scopeContexts.values()) {
                    // register scope contexts as a listeners for events in the aggregate context
                    registerListener(scope);
                    scope.start();
                }
                lifecycleState = RUNNING;
            } catch (ConfigurationException e) {
                lifecycleState = ERROR;
                throw new ContextInitException(e);
            } catch (CoreRuntimeException e) {
                lifecycleState = ERROR;
                e.addContextName(getName());
                throw e;
            } finally {
                initialized = true;
                // release the latch and allow requests to be processed
                initializeLatch.countDown();
            }
        }
    }

    public void stop() {
        if (lifecycleState == STOPPED) {
            return;
        }
        // need to block a start until reset is complete
        initializeLatch = new CountDownLatch(2);
        lifecycleState = STOPPING;
        initialized = false;
        if (scopeContexts != null) {
            for (ScopeContext scope : scopeContexts.values()) {
                try {
                    if (scope.getLifecycleState() == ScopeContext.RUNNING) {
                        scope.stop();
                    }
                } catch (ScopeRuntimeException e) {
                    // log.error("Error stopping scope container [" + scopeContainers[i].getName() + "]", e);
                }
            }
        }
        scopeContexts = null;
        scopeIndex.clear();
        // allow initialized to be called
        initializeLatch.countDown();
        lifecycleState = STOPPED;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void setModule(Module module) {
        assert (module != null) : "Module cannot be null";
        name = module.getName();
        this.module = module;
    }

    public void addContextListener(LifecycleEventListener listener) {
        super.addContextListener(listener);
    }

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void setMonitorFactory(MonitorFactory factory) {
        this.monitorFactory = factory;
    }

    public AggregateContext getParent() {
        return parentContext;
    }

    public void registerModelObjects(List<Extensible> models) throws ConfigurationException {
        assert (models != null) : "Model object collection was null";
        for (Extensible model : models) {
            registerModelObject(model);
        }
    }

    public void registerModelObject(Extensible model) throws ConfigurationException {
        assert (model != null) : "Model object was null";
        initializeScopes();
        if (configurationContext != null) {
            try {
                configurationContext.configure(model);
                configurationContext.build(this, model);
            } catch (ConfigurationException e) {
                e.addContextName(getName());
                throw e;
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
        RuntimeConfiguration<InstanceContext> configuration = null;
        if (model instanceof Module) {
            // merge new module definition with the existing one
            Module oldModule = module;
            Module newModule = (Module) model;
            module = newModule;
            for (Component component : newModule.getComponents()) {
                configuration = (RuntimeConfiguration<InstanceContext>) component.getComponentImplementation()
                        .getRuntimeConfiguration();
                if (configuration == null) {
                    ConfigurationException e = new ConfigurationException("Runtime configuration not set");
                    e.addContextName(component.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(component);
            }
            for (EntryPoint ep : newModule.getEntryPoints()) {
                configuration = (RuntimeConfiguration<InstanceContext>) ep.getConfiguredReference().getRuntimeConfiguration();
                if (configuration == null) {
                    ConfigurationException e = new ConfigurationException("Runtime configuration not set");
                    e.setIdentifier(ep.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(ep);
            }
            for (ExternalService service : newModule.getExternalServices()) {
                configuration = (RuntimeConfiguration<InstanceContext>) service.getConfiguredService().getRuntimeConfiguration();
                if (configuration == null) {
                    ConfigurationException e = new ConfigurationException("Runtime configuration not set");
                    e.setIdentifier(service.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(service);
            }
            // merge existing module component assets
            module.getComponents().addAll(oldModule.getComponents());
            module.getEntryPoints().addAll(oldModule.getEntryPoints());
            module.getExternalServices().addAll(oldModule.getExternalServices());
        } else {
            if (model instanceof Component) {
                Component component = (Component) model;
                module.getComponents().add(component);
                configuration = (RuntimeConfiguration<InstanceContext>) component.getComponentImplementation()
                        .getRuntimeConfiguration();
            } else if (model instanceof EntryPoint) {
                EntryPoint ep = (EntryPoint) model;
                module.getEntryPoints().add(ep);
                configuration = (RuntimeConfiguration<InstanceContext>) ep.getConfiguredReference().getRuntimeConfiguration();
            } else if (model instanceof ExternalService) {
                ExternalService service = (ExternalService) model;
                module.getExternalServices().add(service);
                configuration = (RuntimeConfiguration<InstanceContext>) service.getConfiguredService().getRuntimeConfiguration();
            } else {
                BuilderConfigException e = new BuilderConfigException("Unknown model type");
                e.setIdentifier(model.getClass().getName());
                e.addContextName(getName());
                throw e;
            }
            if (configuration == null) {
                ConfigurationException e = new ConfigurationException("Runtime configuration not set");
                if (model instanceof AggregatePart) {
                    e.setIdentifier(((AggregatePart) model).getName());
                }
                e.addContextName(getName());
                throw e;
            }
            registerConfiguration(configuration);
            registerAutowire(model);
        }
    }

    public void registerJavaObject(String componentName, Object instance) throws ConfigurationException {
        registerConfiguration(new SystemObjectRuntimeConfiguration(componentName, instance));
    }

    protected void registerConfiguration(RuntimeConfiguration<InstanceContext> configuration) throws ConfigurationException {
        if (lifecycleState == RUNNING) {
            if (scopeIndex.get(configuration.getName()) != null) {
                throw new DuplicateNameException(configuration.getName());
            }
            ScopeContext scope = scopeContexts.get(configuration.getScope());
            if (scope == null) {
                ConfigurationException e = new ConfigurationException("Component has an unknown scope");
                e.addContextName(configuration.getName());
                e.addContextName(getName());
                throw e;
            }
            scope.registerConfiguration(configuration);
            scopeIndex.put(configuration.getName(), scope);
        } else {
            configurations.add(configuration);
        }

    }

    public void registerListener(RuntimeEventListener listener) {
        assert (listener != null) : "Listener cannot be null";
        listeners.add(listener);
    }

    public void fireEvent(int eventType, Object message) throws EventException {
        checkInit();
        if (eventType == SESSION_NOTIFY) {
            // update context
            eventContext.setIdentifier(HTTP_SESSION, message);
        } else if (eventType == REQUEST_END) {
            // be very careful with pooled threads, ensuring threadlocals are cleaned up
            eventContext.clearIdentifier(HTTP_SESSION);
        }
        for (RuntimeEventListener listener : listeners) {
            listener.onEvent(eventType, message);
        }
    }

    public InstanceContext getContext(String componentName) {
        checkInit();
        assert (componentName != null) : "Name was null";
        ScopeContext scope = scopeIndex.get(componentName);
        if (scope == null) {
            return null;
        }
        return scope.getContext(componentName);

    }
    
    /**
     * @see org.apache.tuscany.core.context.AggregateContext#getAggregate()
     */
    public Aggregate getAggregate() {
        return module;
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getInstance(qName, true);
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        assert (qName != null) : "Name was null ";
        // use the port name to get the context since entry points ports
        ScopeContext scope = scopeIndex.get(qName.getPortName());
        if (scope == null) {
            return null;
        }
        InstanceContext ctx = scope.getContext(qName.getPortName());
        if (!(ctx instanceof EntryPointContext)) {
            TargetException e = new TargetException("Target not an entry point");
            e.setIdentifier(qName.getQualifiedName());
            e.addContextName(name);
            throw e;
        }
        return ctx.getInstance(null, notify);
    }

    public Object locateInstance(String qualifiedName) throws TargetException {
        checkInit();
        QualifiedName qName = new QualifiedName(qualifiedName);
        ScopeContext scope = scopeIndex.get(qName.getPartName());
        if (scope == null) {
            TargetException e = new TargetException("Component not found");
            e.setIdentifier(qualifiedName);
            e.addContextName(getName());
            throw e;
        }
        InstanceContext ctx = scope.getContext(qName.getPartName());
        try {
            return ctx.getInstance(qName, true);
        } catch (TargetException e) {
            e.addContextName(getName());
            throw e;
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

    public Map<Scope, ScopeContext> getScopeContexts() {
        initializeScopes();
        return immutableScopeContexts;
    }

    // ----------------------------------
    // Protected methods
    // ----------------------------------

    /**
     * Blocks until the module context has been initialized
     */
    protected void checkInit() {
        if (!initialized) {
            try {
                /* block until the module has initialized */
                boolean success = initializeLatch.await(DEFAULT_WAIT, TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ContextInitException("Timeout waiting for module context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }

    protected void initializeScopes() {
        if (scopeContexts == null) {
            scopeContexts = scopeStrategy.createScopes(eventContext);
            immutableScopeContexts = Collections.unmodifiableMap(scopeContexts);
        }
    }

    // ////////////////////////////

    // ----------------------------------
    // AutowireContext methods
    // ----------------------------------
    
    //FIXME These should be removed and configured
    private static final MessageFactory messageFactory=new MessageFactoryImpl();
    private static final ProxyFactoryFactory proxyFactoryFactory=new JDKProxyFactoryFactory(); 

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (RuntimeContext.class.equals(instanceInterface)) {
            return autowireContext.resolveInstance(instanceInterface);
        } else if (MonitorFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(monitorFactory);
        } else if (ConfigurationContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AggregateContext.class.equals(instanceInterface)) {
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
        return null;
    }

    private void registerAutowire(Extensible model) throws ConfigurationException {
        if (lifecycleState == INITIALIZING || lifecycleState == INITIALIZED || lifecycleState == RUNNING) {
            // only autowire entry points with system bindings
            if (model instanceof EntryPoint) {
                EntryPoint ep = (EntryPoint) model;
                if (ep.getBindings() != null) {
                    if (ep.getBindings().get(0) instanceof SystemBinding) {
                        ScopeContext scope = scopeContexts.get(((RuntimeConfiguration) ep.getConfiguredReference()
                                .getRuntimeConfiguration()).getScope());
                        if (scope == null) {
                            ConfigurationException ce = new ConfigurationException("Scope not found for entry point");
                            ce.setIdentifier(ep.getName());
                            ce.addContextName(getName());
                            throw ce;
                        }
                        NameToScope mapping = new NameToScope(new QualifiedName(ep.getName()), scope);
                        autowireIndex.put(ep.getConfiguredService().getService().getServiceContract().getInterface(), mapping);
                    }
                }
            }
        }
    }

    // ----------------------------------
    // ConfigurationContext methods
    // ----------------------------------

    public void configure(Extensible model) throws ConfigurationException {
        if (configurationContext != null) {
            configurationContext.configure(model);
        }
    }

    public void build(AggregateContext parent, Extensible model) throws BuilderConfigException {
        if (configurationContext != null) {
            configurationContext.build(parent, model);
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
    // Inner classes
    // ----------------------------------

    /**
     * Maps a context name to a scope
     */
    private class NameToScope {

        private QualifiedName epName;

        private ScopeContext scope;

        public NameToScope(QualifiedName epName, ScopeContext scope) {
            this.epName = epName;
            this.scope = scope;
        }

        public QualifiedName getName() {
            return epName;
        }

        public ScopeContext getScopeContext() {
            return scope;
        }

    }

}
