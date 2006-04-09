package org.apache.tuscany.core.context.impl;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.EventContext;
import static org.apache.tuscany.core.context.EventContext.*;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.ScopeAwareContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.MissingImplementationException;
import org.apache.tuscany.core.context.MissingContextFactoryException;
import org.apache.tuscany.core.context.ProxyConfigurationException;
import org.apache.tuscany.core.context.MissingScopeException;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyInitializationException;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;

import javax.wsdl.Part;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * The base implementation of an composite context
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeContext extends AbstractContext implements AutowireContext, ScopeAwareContext {

    public static final int DEFAULT_WAIT = 1000 * 60;

    // The parent context, if one exists
    @ParentContext
    protected CompositeContext parentContext;

    // The parent configuration context, if one exists
    @Autowire(required = false)
    protected ConfigurationContext configurationContext;

    // The system monitor factory
    @Autowire(required = false)
    protected MonitorFactory monitorFactory;

    // The logical model representing the module assembly
    // protected ModuleComponent moduleComponent;
    protected Module module;

    protected Map<String, ContextFactory<Context>> configurations = new HashMap<String, ContextFactory<Context>>();

    // Factory for scope contexts
    @Autowire(required = false)
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

    public AbstractCompositeContext() {
        scopeIndex = new ConcurrentHashMap<String, ScopeContext>();
        // FIXME the factory should be injected
        module = new AssemblyFactoryImpl().createModule();
    }

    public AbstractCompositeContext(String name, CompositeContext parent, ScopeStrategy strategy, EventContext ctx,
                                    ConfigurationContext configCtx, MonitorFactory factory) {
        super(name);
        this.scopeStrategy = strategy;
        this.eventContext = ctx;
        this.configurationContext = configCtx;
        this.monitorFactory = factory;
        scopeIndex = new ConcurrentHashMap<String, ScopeContext>();
        parentContext = parent;
        // FIXME the factory should be injected
        module = new AssemblyFactoryImpl().createModule();
    }

    public void start() {
        synchronized (lock) {
            try {
                if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                    throw new IllegalStateException("Context not in UNINITIALIZED state");
                }
                lifecycleState = INITIALIZING;
                initializeScopes();

                Map<Scope, List<ContextFactory<Context>>> configurationsByScope = new HashMap<Scope, List<ContextFactory<Context>>>();
                if (configurations != null) {
                    for (ContextFactory<Context> source : configurations.values()) {
                        // FIXME scopes are defined at the interface level
                        Scope sourceScope = source.getScope();
                        wireSource(source);
                        buildTarget(source);
                        scopeIndex.put(source.getName(), scopeContexts.get(sourceScope));
                        List<ContextFactory<Context>> list = configurationsByScope.get(sourceScope);
                        if (list == null) {
                            list = new ArrayList<ContextFactory<Context>>();
                            configurationsByScope.put(sourceScope, list);
                        }
                        list.add(source);
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
                for (Map.Entry<Scope, List<ContextFactory<Context>>> entries : configurationsByScope.entrySet()) {
                    // register configurations with scope contexts
                    ScopeContext scope = scopeContexts.get(entries.getKey());
                    scope.registerFactories(entries.getValue());
                }
                initializeProxies();
                for (ScopeContext scope : scopeContexts.values()) {
                    // register scope contexts as a listeners for events in the composite context
                    addListener(scope);
                    scope.start();
                }
                lifecycleState = RUNNING;
            } catch (ProxyInitializationException e) {
                lifecycleState = ERROR;
                ContextInitException cie = new ContextInitException(e);
                cie.addContextName(getName());
                throw cie;
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
            for (ScopeContext scopeContext : scopeContexts.values()) {
                if (scopeContext.getLifecycleState() == ScopeContext.RUNNING) {
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

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void setModule(Module module) {
        assert (module != null) : "Module cannot be null";
        name = module.getName();
        this.module = module;
    }

    public void setScopeStrategy(ScopeStrategy scopeStrategy) {
        this.scopeStrategy = scopeStrategy;
    }

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void setMonitorFactory(MonitorFactory factory) {
        this.monitorFactory = factory;
    }

    public void setConfigurationContext(ConfigurationContext context) {
        this.configurationContext = context;
    }

    public CompositeContext getParent() {
        return parentContext;
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
                configurationContext.configure(model);
                configurationContext.build(model);
            } catch (ConfigurationException e) {
                e.addContextName(getName());
                throw e;
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
                ComponentImplementation componentImplementation = component.getComponentImplementation();
                if (componentImplementation == null) {
                    ConfigurationException e = new MissingImplementationException("Component implementation not set");
                    e.addContextName(component.getName());
                    e.addContextName(getName());
                    throw e;
                }
                configuration = (ContextFactory) componentImplementation.getContextFactory();
                if (configuration == null) {
                    ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                    e.addContextName(component.getName());
                    e.addContextName(getName());
                    throw e;
                }
                registerConfiguration(configuration);
                registerAutowire(component);
            }
            for (EntryPoint ep : newModule.getEntryPoints()) {
                configuration = (ContextFactory) ep.getConfiguredReference().getContextFactory();
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
                configuration = (ContextFactory) service.getConfiguredService().getContextFactory();
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
                    ContextFactory<Context> contextFactory = (ContextFactory<Context>) component
                            .getComponentImplementation().getContextFactory();
                    wireSource(contextFactory);
                    buildTarget(contextFactory);
                    contextFactory.prepare(this);
                    try {
                        if (contextFactory.getSourceProxyFactories() != null) {
                            for (ProxyFactory sourceProxyFactory : contextFactory.getSourceProxyFactories()) {
                                sourceProxyFactory.initialize();
                            }
                        }
                        if (contextFactory.getTargetProxyFactories() != null) {
                            for (ProxyFactory targetProxyFactory : contextFactory.getTargetProxyFactories()
                                    .values()) {
                                targetProxyFactory.initialize();
                            }
                        }
                    } catch (ProxyInitializationException e) {
                        ProxyConfigurationException ce = new ProxyConfigurationException(e);
                        ce.addContextName(getName());
                        throw ce;
                    }

                }
                for (EntryPoint ep : newModule.getEntryPoints()) {
                    ContextFactory<Context> contextFactory = (ContextFactory<Context>) ep
                            .getConfiguredReference().getContextFactory();
                    wireSource(contextFactory);
                    buildTarget(contextFactory);
                    contextFactory.prepare(this);
                    try {
                        if (contextFactory.getSourceProxyFactories() != null) {
                            for (ProxyFactory sourceProxyFactory : contextFactory.getSourceProxyFactories()) {
                                sourceProxyFactory.initialize();
                            }
                        }
                        if (contextFactory.getTargetProxyFactories() != null) {
                            for (ProxyFactory targetProxyFactory : contextFactory.getTargetProxyFactories()
                                    .values()) {
                                targetProxyFactory.initialize();
                            }
                        }
                    } catch (ProxyInitializationException e) {
                        ProxyConfigurationException ce = new ProxyConfigurationException(e);
                        ce.addContextName(getName());
                        throw ce;
                    }

                }
                for (ExternalService es : newModule.getExternalServices()) {
                    ContextFactory<Context> contextFactory = (ContextFactory<Context>) es
                            .getConfiguredService().getContextFactory();
                    buildTarget(contextFactory);
                    contextFactory.prepare(this);
                    try {
                        if (contextFactory.getSourceProxyFactories() != null) {
                            for (ProxyFactory sourceProxyFactory : contextFactory.getSourceProxyFactories()) {
                                sourceProxyFactory.initialize();
                            }
                        }
                        if (contextFactory.getTargetProxyFactories() != null) {
                            for (ProxyFactory targetProxyFactory : contextFactory.getTargetProxyFactories()
                                    .values()) {
                                targetProxyFactory.initialize();
                            }
                        }
                    } catch (ProxyInitializationException e) {
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
                configuration = (ContextFactory<Context>) component.getComponentImplementation()
                        .getContextFactory();
            } else if (model instanceof EntryPoint) {
                EntryPoint ep = (EntryPoint) model;
                module.getEntryPoints().add(ep);
                configuration = (ContextFactory<Context>) ep.getConfiguredReference().getContextFactory();
            } else if (model instanceof ExternalService) {
                ExternalService service = (ExternalService) model;
                module.getExternalServices().add(service);
                configuration = (ContextFactory<Context>) service.getConfiguredService().getContextFactory();
            } else {
                BuilderConfigException e = new BuilderConfigException("Unknown model type");
                e.setIdentifier(model.getClass().getName());
                e.addContextName(getName());
                throw e;
            }
            if (configuration == null) {
                ConfigurationException e = new MissingContextFactoryException("Context factory not set");
                if (model instanceof Part) {
                    e.setIdentifier(((Part) model).getName());
                }
                e.addContextName(getName());
                throw e;
            }
            registerConfiguration(configuration);
            registerAutowire(model);
        }
    }

    protected void registerConfiguration(ContextFactory<Context> configuration) throws ConfigurationException {
        configuration.prepare(this);
        if (lifecycleState == RUNNING) {
            if (scopeIndex.get(configuration.getName()) != null) {
                throw new DuplicateNameException(configuration.getName());
            }
            // configuration.prepare(this);
            ScopeContext scope = scopeContexts.get(configuration.getScope());
            if (scope == null) {
                ConfigurationException e = new MissingScopeException("Component has an unknown scope");
                e.addContextName(configuration.getName());
                e.addContextName(getName());
                throw e;
            }
            scope.registerFactory(configuration);
            scopeIndex.put(configuration.getName(), scope);
            configurations.put(configuration.getName(), configuration); // xcv
        } else {
            if (configurations.get(configuration.getName()) != null) {
                throw new DuplicateNameException(configuration.getName());
            }
            configurations.put(configuration.getName(), configuration);
        }

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
     * Registers a model object as autowirable
     *
     * @throws org.apache.tuscany.core.context.ContextInitException
     */
    protected abstract void registerAutowire(Extensible model) throws ConfigurationException;

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
            if (scopeStrategy == null) {
                scopeStrategy = new DefaultScopeStrategy();
            }
            scopeContexts = scopeStrategy.getScopeContexts(eventContext);
            immutableScopeContexts = Collections.unmodifiableMap(scopeContexts);
        }
    }

    /**
     * Iterates through references and delegates to the configuration context to wire them to their targets
     */
    protected void wireSource(ContextFactory<Context> source) {
        Scope sourceScope = source.getScope();
        if (source.getSourceProxyFactories() != null) {
            for (ProxyFactory<?> sourceFactory : source.getSourceProxyFactories()) {
                ProxyConfiguration proxyConfiguration = sourceFactory.getProxyConfiguration();
                QualifiedName targetName = proxyConfiguration.getTargetName();
                ContextFactory<?> target = configurations.get(targetName.getPartName());
                if (target == null) {
                    ContextInitException e = new ContextInitException("Target not found");
                    e.setIdentifier(targetName.getPartName());
                    e.addContextName(source.getName());
                    e.addContextName(name);
                    throw e;
                }
                // get the proxy chain for the target
                ProxyFactory targetFactory = target.getTargetProxyFactory(proxyConfiguration.getTargetName()
                        .getPortName());
                if (targetFactory == null) {
                    ContextInitException e = new ContextInitException("No proxy factory found for service");
                    e.setIdentifier(proxyConfiguration.getTargetName().getPortName());
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
        if (source.getTargetProxyFactories() != null) {
            for (ProxyFactory targetFactory : source.getTargetProxyFactories().values()) {
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
        Map<String, ProxyFactory>  targetProxyFactories = target.getTargetProxyFactories();
        if (targetProxyFactories != null) {
            for (ProxyFactory<?> targetFactory : targetProxyFactories.values()) {
                for (InvocationConfiguration iConfig : targetFactory
                        .getProxyConfiguration().getInvocationConfigurations().values()) {
                    iConfig.build();
                }
            }
        }
    }

    protected void initializeProxies() throws ProxyInitializationException {
        for (ContextFactory<?> config : configurations.values()) {
            List<ProxyFactory> sourceProxyFactories = config.getSourceProxyFactories();
            if (sourceProxyFactories != null) {
                for (ProxyFactory<?> sourceProxyFactory : sourceProxyFactories) {
                    sourceProxyFactory.initialize();
                }
            }
            if (sourceProxyFactories != null) {
                Map<String, ProxyFactory> targetProxyFactories = config.getTargetProxyFactories();
                for (ProxyFactory<?> targetProxyFactory : targetProxyFactories.values()) {
                    targetProxyFactory.initialize();
                }
            }
        }
    }

    public Aggregate getAggregate() {
        return module;
    }
}