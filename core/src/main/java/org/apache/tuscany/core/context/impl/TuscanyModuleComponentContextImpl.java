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

import static org.apache.tuscany.core.context.EventContext.HTTP_SESSION;
import static org.apache.tuscany.core.context.EventContext.REQUEST_END;
import static org.apache.tuscany.core.context.EventContext.SESSION_NOTIFY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.core.addressing.AddressingFactory;
import org.apache.tuscany.core.addressing.impl.AddressingFactoryImpl;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.SimpleComponentRuntimeConfiguration;
import org.apache.tuscany.core.builder.impl.PortRuntimeConfigurationBuilderImpl;
import org.apache.tuscany.core.builder.impl.SystemRuntimeConfigurationBuilderImpl;
import org.apache.tuscany.core.builder.impl.TuscanyModuleContextBuilder;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.ServiceNotFoundException;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ExtensibleModelObject;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceUnavailableException;

/**
 * The Tuscany implementation of a module component context.
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyModuleComponentContextImpl extends AbstractContext implements TuscanyModuleComponentContext {

    // ----------------------------------
    // Constants
    // ----------------------------------

    // TODO make overridable
    public static final int DEFAULT_WAIT = 1000 * 60;

    // ----------------------------------
    // Fields
    // ----------------------------------

    // The logical model representing the module assembly
    private ModuleComponent moduleComponent;

    // Manages scope containers, which in turn manage service component contexts
    private ScopeStrategy scopeStrategy;

    // the event context for associating context events to threads
    private EventContext eventContext;

    // The scope containers for this context
    private Map<Integer, ScopeContext> scopeContainers;

    // A component context name to scope container index
    private Map<String, ScopeContext> scopeIndex;

    // lsistenrs for context events
    private CopyOnWriteArrayList<RuntimeEventListener> listeners = new CopyOnWriteArrayList();

    /*
     * Blocking latch to ensure the module is initialized exactly once prior to servicing requests <p>@FIXME Java 5
     */
    private CountDownLatch initializeLatch = new CountDownLatch(1);

    // Indicates whether the module context has been initialized
    private boolean initialized;

    private AssemblyModelContext modelContext;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public TuscanyModuleComponentContextImpl(ModuleComponent moduleComponent, EventContext eventContext,
            ScopeStrategy scopeStrategy, AssemblyModelContext modelContext) {
        super(moduleComponent.getName());
        assert (eventContext != null) : "Event context cannot be null";
        assert (scopeStrategy != null) : "Scope strategy cannot be null";
        this.moduleComponent = moduleComponent;
        this.eventContext = eventContext;
        this.scopeStrategy = scopeStrategy;
        scopeIndex = new ConcurrentHashMap();
        this.modelContext = modelContext;
    }

    // ----------------------------------
    // ComponentContext methods
    // ----------------------------------

    public void start() {
        synchronized (initializeLatch) {
            try {
                lifecycleState = INITIALIZING;
                // create the scope containers, which are extensible
                scopeContainers = scopeStrategy.createScopes(eventContext);

                // Build the creation pipeline
                Module module = moduleComponent.getModuleImplementation();
                AssemblyFactory assemblyFactory = modelContext.getAssemblyFactory();
                AddressingFactory addressingFactory = new AddressingFactoryImpl();
                ResourceLoader resourceLoader = modelContext.getResourceLoader();
                SystemRuntimeConfigurationBuilderImpl systemConfigurationBuilder = new SystemRuntimeConfigurationBuilderImpl(
                        assemblyFactory, addressingFactory, resourceLoader);
                systemConfigurationBuilder.build(module);
                Component coreCreationPipelineComponent = module
                        .getComponent("org.apache.tuscany.core.pipeline.TuscanyCorePipeline");
                SimpleComponentRuntimeConfiguration runtimeConfiguration = (SimpleComponentRuntimeConfiguration) coreCreationPipelineComponent
                        .getComponentImplementation().getRuntimeConfiguration();
                MessageHandler tuscanyCoreCreationPipelineHandler = (MessageHandler) runtimeConfiguration
                        .createComponentContext().getInstance(null);

                // Build the port runtime configurations
                MessageFactory messageFactory = new MessageFactoryImpl();
                PortRuntimeConfigurationBuilderImpl portBuilder = new PortRuntimeConfigurationBuilderImpl(
                        tuscanyCoreCreationPipelineHandler, addressingFactory, messageFactory, scopeContainers);
                portBuilder.build(module);

                // Build the runtime config model by walking and decorating the
                // logical model
                List<RuntimeConfigurationBuilder<TuscanyModuleComponentContext>> componentBuilders = new ArrayList();
                // FIXME come up with a better configuration for this
                RuntimeConfigurationBuilder<TuscanyModuleComponentContext> builder = (RuntimeConfigurationBuilder<TuscanyModuleComponentContext>) Class
                        .forName("org.apache.tuscany.container.java.builder.JavaComponentContextBuilder").newInstance();
                componentBuilders.add(builder);
                TuscanyModuleContextBuilder moduleContextBuilder = new TuscanyModuleContextBuilder(componentBuilders);
                moduleContextBuilder.build(moduleComponent, this);

                // sort the components by scope
                Collection serviceComponents = moduleComponent.getModuleImplementation().getComponents();
                Map<Integer, List<SimpleComponent>> componentsByScope = new HashMap<Integer, List<SimpleComponent>>();
                for (Iterator iter = serviceComponents.iterator(); iter.hasNext();) {
                    SimpleComponent sc = (SimpleComponent) iter.next();
                    // FIXME scopes are defined at the interface level
                    ScopeEnum scope = sc.getComponentImplementation().getServices().get(0).getInterfaceContract().getScope();
                    if (scope == null) {
                        scope = ScopeEnum.INSTANCE_LITERAL;
                    }
                    List<SimpleComponent> list = componentsByScope.get(scope.getValue());
                    if (list == null) {
                        list = new ArrayList();
                        componentsByScope.put(scope.getValue(), list);
                    }
                    list.add(sc);
                }

                // iterate over the sorted components and start the scope containers
                for (Map.Entry entries : componentsByScope.entrySet()) {
                    List<Component> components = (List<Component>) entries.getValue();
                    List<RuntimeConfiguration<InstanceContext>> runtimeConfigurations = new ArrayList();
                    ScopeContext container = scopeContainers.get(entries.getKey());
                    for (Component component : components) {
                        // create component name to scope container index
                        scopeIndex.put(component.getName(), container);
                        // assemble the runtime configuration for components by scope
                        // FIXME avoid cast
                        Object config = component.getComponentImplementation().getRuntimeConfiguration();
                        if (!(config instanceof RuntimeConfiguration)) {
                            throw new ContextInitException("Invalid runtime configuration for component [" + component.getName()
                                    + "]");
                        }
                        runtimeConfigurations.add(((RuntimeConfiguration<InstanceContext>) config));
                    }
                    container.registerConfigurations(runtimeConfigurations);
                }
                // register scopes as listeners for events in the module context
                for (ScopeContext scopeContainer : scopeContainers.values()) {
                    registerListener(scopeContainer);
                    scopeContainer.start();
                }
                lifecycleState = RUNNING;
            } catch (InstantiationException e) {
                lifecycleState = ERROR;
                ContextInitException cie = new ContextInitException(e);
                cie.addContextName(getName());
                throw cie;
            } catch (ClassNotFoundException e) {
                lifecycleState = ERROR;
                ContextInitException cie = new ContextInitException(e);
                cie.addContextName(getName());
                throw cie;
            } catch (IllegalAccessException e) {
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
        // need to block a start until reset is complete
        initializeLatch = new CountDownLatch(2);
        lifecycleState = STOPPING;
        initialized = false;
        if (scopeContainers != null) {
            for (ScopeContext scopeContainer : scopeContainers.values()) {
                scopeContainer.stop();
            }
        }
        scopeContainers = null;
        scopeIndex.clear();
        // allow initialized to be called
        lifecycleState = RUNNING;
        initializeLatch.countDown();
    }

    /**
     * Registers a listeners for the module context
     * 
     * @see RuntimeEventListener
     */
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

    public Object getInstance(QualifiedName componentName) throws ServiceUnavailableException {
        return getInstance(componentName, true);
    }

    public Object getInstance(QualifiedName componentName, boolean notify) throws ServiceUnavailableException {
        checkInit();
        assert (componentName != null) : "Name was null";
        ScopeContext scopeContainer = (ScopeContext) scopeIndex.get(componentName.getPartName());
        if (scopeContainer == null) {
            throw new ServiceNotFoundException(componentName.getQualifiedName());
        }
        return scopeContainer.getInstance(componentName);
    }

    // ----------------------------------
    // ModuleContext methods
    // ----------------------------------

    public String getURI() {
        return moduleComponent.getURI();
    }

    public Object locateService(String serviceComponentName) throws ServiceUnavailableException {
        return getInstance(new QualifiedName(serviceComponentName), true);
    }

    public ServiceReference createServiceReference(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public org.osoa.sca.model.Module getMetaData() {
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

    public AssemblyModelContext getAssemblyModelContext() {
        return modelContext;
    }

    public ModuleComponent getModuleComponent() {
        return moduleComponent;
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    /**
     * Blocks until the module context has been initialized
     */
    private void checkInit() {
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

    public void registerConfigurations(List<RuntimeConfiguration<InstanceContext>> configurations) {
        throw new UnsupportedOperationException();
    }

    public void registerConfiguration(RuntimeConfiguration<InstanceContext> configuration) throws DuplicateNameException {
        throw new UnsupportedOperationException();
    }

    public InstanceContext getContext(String componentName) {
        throw new UnsupportedOperationException();
    }

    public void registerModelObjects(List<ExtensibleModelObject> models) {
        throw new UnsupportedOperationException();
    }

    public void registerModelObject(ExtensibleModelObject model) {
        throw new UnsupportedOperationException();
    }

    public AggregateContext getParent() {
        return null;
    }

    public Object locateInstance(String serviceName) throws TargetException {
        return locateService(serviceName);
    }

}
