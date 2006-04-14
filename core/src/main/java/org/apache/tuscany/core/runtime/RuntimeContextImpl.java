/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.runtime;

import java.util.List;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.HierarchicalWireBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitorImpl;
import org.apache.tuscany.core.builder.impl.ContextFactoryBuilderRegistryImpl;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.impl.AbstractContext;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.context.SystemScopeStrategy;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Extensible;

/**
 * Implementation of a RuntimeContext that forms the foundation for a Tuscany environment.
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeContextImpl extends AbstractContext implements RuntimeContext {

    // the top-level wire builder in the runtime
    private final HierarchicalWireBuilder wireBuilder;

    //private final List<RuntimeEventListener> listeners = new ArrayList<RuntimeEventListener>(1);

    private final CompositeContext rootContext;

    private final SystemCompositeContext systemContext;

    private final MonitorFactory monitorFactory;

    private final ContextFactoryBuilderRegistryImpl builderRegistry;

    /**
     * Default constructor that creates a runtime with a NullMonitorFactory and no builders.
     */
    public RuntimeContextImpl() {
        this(new NullMonitorFactory(), null, null);
    }

    /**
     * Constructor for creating a runtime with a specified MonitorFactory and pre-defined builders.
     *
     * @param monitorFactory the default {@link org.apache.tuscany.common.monitor.MonitorFactory} for this runtime
     * @param builders a list of builders automatically made available; may be null
     * @param wireBuilder the top-level hierarchical wire builder for the runtime; if not specified, a default
     */
    public RuntimeContextImpl(MonitorFactory monitorFactory, List<ContextFactoryBuilder> builders, HierarchicalWireBuilder wireBuilder) {
        super(RUNTIME);
        this.monitorFactory = monitorFactory;
        this.wireBuilder = (wireBuilder == null) ? new DefaultWireBuilder() : wireBuilder;

        rootContext = new CompositeContextImpl(ROOT, this, this, new RuntimeScopeStrategy(), new EventContextImpl(), this);
        systemContext = new SystemCompositeContextImpl(SYSTEM, this, this, new SystemScopeStrategy(), new EventContextImpl(), this);

        // bootstrap the builder regsitry
        builderRegistry = new ContextFactoryBuilderRegistryImpl();
        if (builders != null) {
            for (ContextFactoryBuilder builder: builders) {
                builderRegistry.register(builder);
            }
        }
    }

    /**
     * Specialized constructor that allows the default implementations of the root and system contexts to be overridden.
     *
     * @param monitorFactory the default {@link org.apache.tuscany.common.monitor.MonitorFactory} for this runtime
     * @param rootContext the context to use for the root of the user context tree
     * @param systemContext the context to use for the root of the system context tree
     * @param wireBuilder the top-level hierarchical wire builder for the runtime; if not specified, a default
     */
    public RuntimeContextImpl(MonitorFactory monitorFactory,
                              CompositeContext rootContext,
                              SystemCompositeContext systemContext,
                              HierarchicalWireBuilder wireBuilder) {
        super(RUNTIME);
        this.rootContext = rootContext;
        this.systemContext = systemContext;
        this.monitorFactory = monitorFactory;
        this.wireBuilder = (wireBuilder == null) ? new DefaultWireBuilder() : wireBuilder;
        builderRegistry = null;
    }

    public void start() throws CoreRuntimeException {
        if (lifecycleState == RUNNING) {
            return;
        }
        systemContext.start();
        try {
            systemContext.registerJavaObject(ContextFactoryBuilderRegistry.class.getName(), ContextFactoryBuilderRegistry.class, builderRegistry);
        } catch (ConfigurationException e) {
            throw new AssertionError();
        }

        rootContext.start();
        lifecycleState = RUNNING;
    }

    public void stop() throws CoreRuntimeException {
        if (lifecycleState == STOPPED) {
            return;
        }
        rootContext.stop();
        systemContext.stop();
        lifecycleState = STOPPED;
    }

    public void addBuilder(ContextFactoryBuilder builder) {
        assert (builder != null) : "Builder was null";
        builderRegistry.register(builder);
    }

    public void addBuilder(WireBuilder builder) {
        assert (builder != null) : "Builder was null";
        wireBuilder.addWireBuilder(builder);
    }

    public CompositeContext getContext(String ctxName) {
        checkRunning();
        if (ROOT.equals(ctxName)) {
            return rootContext;
        } else if (SYSTEM.equals(ctxName)) {
            return systemContext;
        }
        return (CompositeContext) rootContext.getContext(ctxName);
    }

    public CompositeContext getRootContext() {
        checkRunning();
        return rootContext;
    }

    public SystemCompositeContext getSystemContext() {
        checkRunning();
        return systemContext;
    }

    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    public void registerModelObject(Extensible model) throws ConfigurationException {
        assert (model != null) : "Model was null";
        // note do not configure or build model object since the root context will perform a call back
        rootContext.registerModelObject(model);
    }

    public void registerModelObjects(List<? extends Extensible> models) throws ConfigurationException {
        for (Extensible model : models) {
            registerModelObject(model);
        }
    }

    public CompositeContext getParent() {
        return null; // there is no parent
    }

    public void setParent(CompositeContext parent) {
        throw new UnsupportedOperationException();
    }

    //TODO remove
    public void fireEvent(int pEventType, Object pMessage) throws EventException {
        throw new UnsupportedOperationException();
    }

    public Object locateService(QualifiedName serviceName) {
        return null;
    }

    public Object locateInstance(QualifiedName serviceName) {
        return null;
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getSystemContext().getInstance(qName);
    }

    public synchronized void build(AssemblyObject model) throws BuilderConfigException {
        AssemblyVisitorImpl visitor = new AssemblyVisitorImpl(builderRegistry.getBuilders());
        visitor.start(model);
    }

    public void configure(Extensible model) throws ConfigurationException {
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        wireBuilder.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        wireBuilder.completeTargetChain(targetFactory, targetType, targetScopeContext);
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (MonitorFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(monitorFactory);
        } else if (ConfigurationContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (AutowireContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (RuntimeContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else {
            // autowire to system components
            return instanceInterface.cast(getSystemContext().resolveExternalInstance(instanceInterface));
        }
    }

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        return systemContext.resolveExternalInstance(instanceInterface);
    }

    public Composite getComposite() {
        return systemContext.getComposite();
    }

    private void checkRunning() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Context must be in RUNNING state");
        }
    }

}
