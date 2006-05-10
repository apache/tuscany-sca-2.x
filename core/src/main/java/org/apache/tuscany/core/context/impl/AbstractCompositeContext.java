/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.common.TuscanyRuntimeException;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.core.context.MissingContextFactoryException;
import org.apache.tuscany.core.context.MissingImplementationException;
import org.apache.tuscany.core.context.MissingScopeException;
import org.apache.tuscany.core.context.ProxyConfigurationException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeAwareContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.event.Event;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.SessionBound;
import org.apache.tuscany.core.context.event.SessionEvent;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.wire.InvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.WireConfiguration;
import org.apache.tuscany.core.wire.WireFactory;
import org.apache.tuscany.core.wire.WireFactoryInitException;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;

/**
 * The base implementation of a composite context
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized", "RawUseOfParameterizedType", "NonPrivateFieldAccessedInSynchronizedContext"})
public abstract class AbstractCompositeContext extends AbstractContext implements AutowireContext, ScopeAwareContext, ConfigurationContext {

    public static final int DEFAULT_WAIT = 1000 * 60;

    protected AssemblyContext assemblyContext;

    protected CompositeContext parentContext;

    // The parent configuration context, if one exists
    @Autowire
    protected ConfigurationContext configurationContext;

    // The logical model representing the module assembly
    // protected ModuleComponent moduleComponent;
    protected Module module;

    protected Map<String, ContextFactory<Context>> configurations = new HashMap<String, ContextFactory<Context>>();

    // Factory for scope contexts
    protected ScopeStrategy scopeStrategy;

    // The event context for associating context events to threads
    protected EventContext eventContext;

    // The scopes for this context
    protected Map<Scope, ScopeContext> scopeContexts;

    protected Map<Scope, ScopeContext> immutableScopeContexts;

    // A component context name to scope context index
    protected Map<String, ScopeContext> scopeIndex;

    // Blocking latch to ensure the module is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);

    protected final Object lock = new Object();

    // Indicates whether the module context has been initialized
    protected boolean initialized;

    // a mapping of service type to component name
    private final Map<Class, NameToScope> autowireInternal = new ConcurrentHashMap<Class, NameToScope>();
    private final Map<Class, NameToScope> autowireExternal = new ConcurrentHashMap<Class, NameToScope>();

    private AutowireContext autowireContext;

    public AbstractCompositeContext() {
        scopeIndex = new ConcurrentHashMap<String, ScopeContext>();
        // FIXME the factory should be injected
        module = new AssemblyFactoryImpl().createModule();
        scopeStrategy = new DefaultScopeStrategy();
    }

    public AbstractCompositeContext(String name, CompositeContext parent, ScopeStrategy strategy, EventContext ctx, ConfigurationContext configCtx) {
        super(name);
        if (strategy == null) {
            strategy = new DefaultScopeStrategy();
        }
        this.scopeStrategy = strategy;
        this.eventContext = ctx;
        this.configurationContext = configCtx;
        scopeIndex = new ConcurrentHashMap<String, ScopeContext>();
        parentContext = parent;
        // FIXME the factory should be injected
        module = new AssemblyFactoryImpl().createModule();
    }

    public void setAssemblyContext(AssemblyContext assemblyContext) {
        this.assemblyContext = assemblyContext;
    }

    private String uri;

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }


    public void start() {
        synchronized (lock) {
            try {
                if (lifecycleState == STOPPED) {
                    throw new IllegalStateException("Context cannot be restarted - create a new one");
                } else if (lifecycleState != UNINITIALIZED) {
                    throw new IllegalStateException("Context not in UNINITIALIZED state");
                }

                lifecycleState = INITIALIZING;
                initializeScopes();

                Map<Scope, List<ContextFactory<Context>>> configurationsByScope = new EnumMap<Scope, List<ContextFactory<Context>>>(Scope.class);
                if (configurations != null) {
                    for (ContextFactory<Context> contextFactory : configurations.values()) {
                        // FIXME scopes are defined at the interface level
                        Scope sourceScope = contextFactory.getScope();
                        wireSource(contextFactory);
                        buildTarget(contextFactory);
                        scopeIndex.put(contextFactory.getName(), scopeContexts.get(sourceScope));
                        List<ContextFactory<Context>> list = configurationsByScope.get(sourceScope);
                        if (list == null) {
                            list = new ArrayList<ContextFactory<Context>>();
                            configurationsByScope.put(sourceScope, list);
                        }
                        list.add(contextFactory);
                    }
                }
                for (EntryPoint ep : module.getEntryPoints()) {
                    registerAutowire(ep);
                }
                for (Component component : module.getComponents()) {
                    if (component instanceof ModuleComponent) {
                        registerAutowire((ModuleComponent) component);
                    } else {
                        registerAutowire(component);
                    }
                }
                for (ExternalService es : module.getExternalServices()) {
                    registerAutowire(es);
                }
                for (Map.Entry<Scope, List<ContextFactory<Context>>> entries : configurationsByScope.entrySet())
                {
                    // register configurations with scope contexts
                    ScopeContext scope = scopeContexts.get(entries.getKey());
                    scope.registerFactories(entries.getValue());
                }
                initializeWireFactories();
                for (ScopeContext scope : scopeContexts.values()) {
                    // register scope contexts as a listeners for events in the composite context
                    addListener(scope);
                    scope.start();
                }
                lifecycleState = RUNNING;
            } catch (WireFactoryInitException e) {
                lifecycleState = ERROR;
                ContextInitException cie = new ContextInitException(e);
                cie.addContextName(getName());
                throw cie;
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
        initializeLatch = new CountDownLatch(1); //xcv
        lifecycleState = STOPPING;
        initialized = false;
        if (scopeContexts != null) {
            for (ScopeContext scopeContext : scopeContexts.values()) {
                if (scopeContext.getLifecycleState() == Lifecycle.RUNNING) {
                    scopeContext.stop();
                }
            }
        }
        scopeContexts = null;
        scopeIndex.clear();
        // allow initialized to be called
        initializeLatch.countDown();
        lifecycleState = STOPPED;

    }

    public void setModule(Module module) {
        assert (module != null) : "Module cannot be null";
        name = module.getName();
        this.module = module;
    }

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void setConfigurationContext(ConfigurationContext context) {
        this.configurationContext = context;
    }

    public CompositeContext getParent() {
        return parentContext;
    }

    @ParentContext
    public void setParent(CompositeContext parent) {
        parentContext = parent;
    }

    public void registerModelObjects(List<? extends Extensible> models) throws ConfigurationException {
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
                configurationContext.build(model);
            } catch (BuilderConfigException e) {
                e.addContextName(getName());
                throw e;
            }
        }
        ContextFactory<Context> configuration;
        if (model instanceof Module) {
            // merge new module definition with the existing one
            Module oldModule = module;
            Module newModule = (Module) model;
            module = newModule;
            for (Component component : newModule.getComponents()) {
                Implementation componentImplementation = component.getImplementation();
                if (componentImplementation == null) {
                    ConfigurationException e = new MissingImplementationException("Component implementation not set");
                    e.addContextName(component.getName());
                    e.addContextName(getName());
                    throw e;
                }
                configuration = (ContextFactory<Context>) component.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.addContextName(component.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                if (component instanceof ModuleComponent) {
                    registerAutowire((ModuleComponent) component);
                } else {
                    registerAutowire(component);
                }
            }
            for (EntryPoint ep : newModule.getEntryPoints()) {
                configuration = (ContextFactory<Context>) ep.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.setIdentifier(ep.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(ep);
            }
            for (ExternalService service : newModule.getExternalServices()) {
                configuration = (ContextFactory<Context>) service.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.setIdentifier(service.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(service);
            }
            if (lifecycleState == RUNNING) {
                for (Component component : newModule.getComponents()) {
                    ContextFactory<Context> contextFactory = (ContextFactory<Context>) component.getContextFactory();
                    wireSource(contextFactory);
                    buildTarget(contextFactory);
                    contextFactory.prepare(this);
                    try {
                        if (contextFactory.getSourceWireFactories() != null) {
                            for (SourceWireFactory sourceWireFactory : contextFactory.getSourceWireFactories())
                            {
                                sourceWireFactory.initialize();
                            }
                        }
                        if (contextFactory.getTargetWireFactories() != null) {
                            for (TargetWireFactory targetWireFactory : contextFactory.getTargetWireFactories()
                                    .values()) {
                                targetWireFactory.initialize();
                            }
                        }
                    } catch (WireFactoryInitException e) {
                        ProxyConfigurationException ce = new ProxyConfigurationException(e);
                        ce.addContextName(getName());
                        throw ce;
                    }

                }
                for (EntryPoint ep : newModule.getEntryPoints()) {
                    ContextFactory<Context> contextFactory = (ContextFactory<Context>) ep.getContextFactory();
                    wireSource(contextFactory);
                    buildTarget(contextFactory);
                    contextFactory.prepare(this);
                    try {
                        if (contextFactory.getSourceWireFactories() != null) {
                            for (SourceWireFactory sourceWireFactory : contextFactory.getSourceWireFactories())
                            {
                                sourceWireFactory.initialize();
                            }
                        }
                        if (contextFactory.getTargetWireFactories() != null) {
                            for (TargetWireFactory targetWireFactory : contextFactory.getTargetWireFactories()
                                    .values()) {
                                targetWireFactory.initialize();
                            }
                        }
                    } catch (WireFactoryInitException e) {
                        ProxyConfigurationException ce = new ProxyConfigurationException(e);
                        ce.addContextName(getName());
                        throw ce;
                    }

                }
                for (ExternalService es : newModule.getExternalServices()) {
                    ContextFactory<Context> contextFactory = (ContextFactory<Context>) es.getContextFactory();
                    wireSource(contextFactory);
                    buildTarget(contextFactory);
                    contextFactory.prepare(this);
                    try {
                        if (contextFactory.getSourceWireFactories() != null) {
                            for (SourceWireFactory sourceWireFactory : contextFactory.getSourceWireFactories())
                            {
                                sourceWireFactory.initialize();
                            }
                        }
                        if (contextFactory.getTargetWireFactories() != null) {
                            for (WireFactory targetWireFactory : contextFactory.getTargetWireFactories()
                                    .values()) {
                                targetWireFactory.initialize();
                            }
                        }
                    } catch (WireFactoryInitException e) {
                        ProxyConfigurationException ce = new ProxyConfigurationException(e);
                        ce.addContextName(getName());
                        throw ce;
                    }

                }

            }
            // merge existing module component assets
            module.getComponents().addAll(oldModule.getComponents());
            module.getEntryPoints().addAll(oldModule.getEntryPoints());
            module.getExternalServices().addAll(oldModule.getExternalServices());
        } else {
            if (model instanceof Component) {
                Component component = (Component) model;
                module.getComponents().add(component);
                configuration = (ContextFactory<Context>) component.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.setIdentifier(component.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                if (component instanceof ModuleComponent) {
                    registerAutowire((ModuleComponent) component);
                } else {
                    registerAutowire(component);
                }
            } else if (model instanceof EntryPoint) {
                EntryPoint ep = (EntryPoint) model;
                module.getEntryPoints().add(ep);
                configuration = (ContextFactory<Context>) ep.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.setIdentifier(ep.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(ep);
            } else if (model instanceof ExternalService) {
                ExternalService service = (ExternalService) model;
                module.getExternalServices().add(service);
                configuration = (ContextFactory<Context>) service.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.setIdentifier(service.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(service);
            } else {
                BuilderConfigException e = new BuilderConfigException("Unknown model type");
                e.setIdentifier(model.getClass().getName());
                e.addContextName(getName());
                throw e;
            }
        }
    }

    protected void registerConfiguration(ContextFactory<Context> factory) throws ConfigurationException {
        factory.prepare(this);
        if (lifecycleState == RUNNING) {
            if (scopeIndex.get(factory.getName()) != null) {
                throw new DuplicateNameException(factory.getName());
            }
            try {
                ScopeContext scope = scopeContexts.get(factory.getScope());
                if (scope == null) {
                    ConfigurationException e = new MissingScopeException("Component has an unknown scope");
                    e.addContextName(factory.getName());
                    e.addContextName(getName());
                    throw e;
                }
                scope.registerFactory(factory);
                scopeIndex.put(factory.getName(), scope);
            } catch (TuscanyRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
            configurations.put(factory.getName(), factory); // xcv
        } else {
            if (configurations.get(factory.getName()) != null) {
                throw new DuplicateNameException(factory.getName());
            }
            configurations.put(factory.getName(), factory);
        }

    }

    public void fireEvent(int eventType, Object message) throws EventException {
        throw new UnsupportedOperationException();
    }

    public void publish(Event event) {
        checkInit();
        if (event instanceof SessionBound) {
            SessionEvent sessionEvent = ((SessionBound) event);
            // update context
            eventContext.setIdentifier(sessionEvent.getSessionTypeIdentifier(), sessionEvent.getId());
        } else if (event instanceof RequestEnd) {
            // be very careful with pooled threads, ensuring threadlocals are cleaned up
            eventContext.clearIdentifiers();
        }
        super.publish(event);
    }

    public Context getContext(String componentName) {
        checkInit();
        assert (componentName != null) : "Name was null";
        ScopeContext scope = scopeIndex.get(componentName);
        if (scope == null) {
            return null;
        }
        return scope.getContext(componentName);

    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        assert (qName != null) : "Name was null ";
        // use the port name to get the context since entry points ports
        ScopeContext scope = scopeIndex.get(qName.getPortName());
        if (scope == null) {
            return null;
        }
        Context ctx = scope.getContext(qName.getPortName());
        if (!(ctx instanceof EntryPointContext)) {
            TargetException e = new TargetException("Target not an entry point");
            e.setIdentifier(qName.getQualifiedName());
            e.addContextName(name);
            throw e;
        }
        return ctx.getInstance(null);
    }

    public Map<Scope, ScopeContext> getScopeContexts() {
        initializeScopes();
        return immutableScopeContexts;
    }

    /**
     * Blocks until the module context has been initialized
     */
    protected void checkInit() {
        if (lifecycleState == STOPPED) {
            throw new IllegalStateException("Context cannot be restarted - create a new one");
        }
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
            scopeContexts = scopeStrategy.getScopeContexts(eventContext);
            immutableScopeContexts = Collections.unmodifiableMap(scopeContexts);
        }
    }

    /**
     * Iterates through references and delegates to the configuration context to wire them to their targets
     */
    protected void wireSource(ContextFactory<Context> source) {
        Scope sourceScope = source.getScope();
        if (source.getSourceWireFactories() != null) {
            for (SourceWireFactory<?> sourceFactory : source.getSourceWireFactories()) {
                WireConfiguration wireConfiguration = sourceFactory.getConfiguration();
                QualifiedName targetName = wireConfiguration.getTargetName();
                ContextFactory<?> target = configurations.get(targetName.getPartName());
                if (target == null) {
                    ContextInitException e = new ContextInitException("Target not found");
                    e.setIdentifier(targetName.getPartName());
                    e.addContextName(source.getName());
                    e.addContextName(name);
                    throw e;
                }
                // get the proxy chain for the target
                TargetWireFactory targetFactory = target.getTargetWireFactory(targetName.getPortName());
                if (targetFactory == null) {
                    ContextInitException e = new ContextInitException("No proxy factory found for service");
                    e.setIdentifier(wireConfiguration.getTargetName().getPortName());
                    e.addContextName(target.getName());
                    e.addContextName(source.getName());
                    e.addContextName(name);
                    throw e;
                }
                try {
                    boolean downScope = scopeStrategy.downScopeReference(sourceScope, target.getScope());
                    configurationContext.connect(sourceFactory, targetFactory, target.getClass(), downScope, scopeContexts
                            .get(target.getScope()));
                } catch (BuilderConfigException e) {
                    e.addContextName(target.getName());
                    e.addContextName(source.getName());
                    e.addContextName(name);
                    throw e;
                }

            }
        }
        // wire invokers when the proxy only contains the target chain
        if (source.getTargetWireFactories() != null) {
            for (TargetWireFactory targetFactory : source.getTargetWireFactories().values()) {
                try {
                    configurationContext.completeTargetChain(targetFactory, source.getClass(), scopeContexts.get(sourceScope));
                } catch (BuilderConfigException e) {
                    e.addContextName(source.getName());
                    e.addContextName(name);
                    throw e;
                }
            }
        }
    }

    /**
     * Signals to target side of reference configurations to initialize
     */
    protected void buildTarget(ContextFactory<?> target) {
        Map<String, TargetWireFactory>  targetProxyFactories = target.getTargetWireFactories();
        if (targetProxyFactories != null) {
            for (TargetWireFactory<?> targetFactory : targetProxyFactories.values()) {
                for (InvocationConfiguration iConfig : targetFactory
                        .getConfiguration().getInvocationConfigurations().values()) {
                    iConfig.build();
                }
            }
        }
    }

    protected void initializeWireFactories() throws WireFactoryInitException {
        for (ContextFactory<?> config : configurations.values()) {
            List<SourceWireFactory> sourceProxyFactories = config.getSourceWireFactories();
            if (sourceProxyFactories != null) {
                for (WireFactory<?> sourceWireFactory : sourceProxyFactories) {
                    sourceWireFactory.initialize();
                }
            }
            if (sourceProxyFactories != null) {
                Map<String, TargetWireFactory> targetWireFactories = config.getTargetWireFactories();
                for (TargetWireFactory<?> targetWireFactory : targetWireFactories.values()) {
                    targetWireFactory.initialize();
                }
            }
        }
    }

    public Composite getComposite() {
        return module;
    }

    @Autowire
    public void setAutowireContext(AutowireContext context) {
        autowireContext = context;
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (ConfigurationContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AutowireContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AssemblyContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(assemblyContext);
        }

        NameToScope nts = autowireInternal.get(instanceInterface);
        if (nts != null) {
            try {
                return instanceInterface.cast(nts.getScopeContext().getInstance(nts.getName()));
            } catch (TargetException e) {
                AutowireResolutionException ae = new AutowireResolutionException("Autowire instance not found", e);
                ae.addContextName(getName());
                throw ae;
            }
        } else if (autowireContext != null) {
            try {
                // resolve to parent
                return autowireContext.resolveInstance(instanceInterface);
            } catch (AutowireResolutionException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        NameToScope nts = autowireExternal.get(instanceInterface);
        if (nts != null) {
            try {
                return instanceInterface.cast(nts.getScopeContext().getInstance(nts.getName()));
            } catch (TargetException e) {
                AutowireResolutionException ae = new AutowireResolutionException("Autowire instance not found", e);
                ae.addContextName(getName());
                throw ae;
            }
        } else {
            return null;
        }
    }

    private void registerAutowire(ExternalService service) {
    }

    private void registerAutowire(ModuleComponent component) {
        for (EntryPoint ep : component.getImplementation().getEntryPoints()) {
            for (Binding binding : ep.getBindings()) {
                if (binding instanceof SystemBinding) {
                    Class interfaze = ep.getConfiguredService().getPort().getServiceContract().getInterface();
                    ScopeContext scope = scopeContexts.get(Scope.AGGREGATE);
                    String qname = component.getName() + QualifiedName.NAME_SEPARATOR + ep.getName();
                    registerAutowireInternal(interfaze, qname, scope);
                }
            }
        }
    }

    private void registerAutowire(Component component) {
        for (Service service : component.getImplementation().getComponentType().getServices()) {
            Class interfaze = service.getServiceContract().getInterface();
            ScopeContext scopeCtx = scopeContexts.get(service.getServiceContract().getScope());
            registerAutowireInternal(interfaze, component.getName(), scopeCtx);
        }
    }

    protected void registerAutowireInternal(Class<?> interfaze, String name, ScopeContext scopeContext) {
        assert interfaze != null;
        if (autowireInternal.containsKey(interfaze)) {
            return;
        }
        QualifiedName qname = new QualifiedName(name);
        NameToScope nts = new NameToScope(qname, scopeContext);
        autowireInternal.put(interfaze, nts);
    }

    private void registerAutowire(EntryPoint ep) {
        for (Binding binding : ep.getBindings()) {
            if (binding instanceof SystemBinding) {
                Class interfaze = ep.getConfiguredService().getPort().getServiceContract().getInterface();
                ScopeContext scope = scopeContexts.get(((ContextFactory) ep.getContextFactory()).getScope());
                registerAutowireExternal(interfaze, ep.getName(), scope);
            }
        }
    }

    protected void registerAutowireExternal(Class<?> interfaze, String name, ScopeContext scopeContext) {
        assert interfaze != null;
        if (autowireExternal.containsKey(interfaze)) {
            return;
        }
        QualifiedName qname = new QualifiedName(name);
        NameToScope nts = new NameToScope(qname, scopeContext);
        autowireExternal.put(interfaze, nts);
    }

    protected static class NameToScope {

        private final QualifiedName qName;

        private final ScopeContext scope;

        public NameToScope(QualifiedName name, ScopeContext scope) {
            this.qName = name;
            this.scope = scope;
        }

        public QualifiedName getName() {
            return qName;
        }

        public ScopeContext getScopeContext() {
            return scope;
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

    public void connect(SourceWireFactory sourceFactory, TargetWireFactory targetFactory, Class targetType, boolean downScope,
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

    public void completeTargetChain(TargetWireFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
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
}
