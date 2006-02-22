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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitor;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemAggregateContextImpl;
import org.apache.tuscany.core.system.context.SystemScopeStrategy;
import org.apache.tuscany.model.assembly.Extensible;

/**
 * Implementation of a RuntimeContext that forms the foundation for a Tuscany environment.
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeContextImpl extends AbstractContext implements RuntimeContext {

    private final List<RuntimeConfigurationBuilder> builders;

    private final List<WireBuilder> wireBuilders;

    private final List<RuntimeEventListener> listeners = new ArrayList(1);

    private final AggregateContext rootContext;

    private final SystemAggregateContext systemContext;

    private final MonitorFactory monitorFactory;

    /**
     * Default constructor that creates a runtime with a NullMonitorFactory and no builders.
     */
    public RuntimeContextImpl() {
        this(new NullMonitorFactory(), null, null);
    }

    /**
     * Constructor for creating a runtime with a specified MonitorFactory and pre-defined builders.
     * 
     * @param monitorFactory the default {@link MonitorFactory} for this runtime
     * @param builders a list of builders automatically made available; may be null
     * @param wireBuilders a list of wire builders automatically made available; may be null
     */
    public RuntimeContextImpl(MonitorFactory monitorFactory, List<RuntimeConfigurationBuilder> builders,
            List<WireBuilder> wireBuilders) {
        super(RUNTIME);
        this.monitorFactory = monitorFactory;
        this.builders = (builders == null) ? new ArrayList(1) : new ArrayList(builders);
        this.wireBuilders = (wireBuilders == null) ? new ArrayList(1) : new ArrayList(wireBuilders);

        rootContext = new AggregateContextImpl(ROOT, this, this, new RuntimeScopeStrategy(), new EventContextImpl(), this,
                monitorFactory);
        systemContext = new SystemAggregateContextImpl(SYSTEM, this, this, new SystemScopeStrategy(), new EventContextImpl(),
                this, monitorFactory);
    }

    /**
     * Specicalized constructor that allows the default implementations of the root and system contexts to be
     * overridden.
     * 
     * @param monitorFactory the default {@link MonitorFactory} for this runtime
     * @param rootContext the context to use for the root of the user context tree
     * @param systemContext the context to use for the root of the system context tree
     * @param builders a list of builders automatically made available; may be null
     * @param wireBuilders a list of wire builders automatically made available; may be null
     */
    public RuntimeContextImpl(MonitorFactory monitorFactory, AggregateContext rootContext, SystemAggregateContext systemContext,
            List<RuntimeConfigurationBuilder> builders, List<WireBuilder> wireBuilders) {
        super(RUNTIME);
        this.rootContext = rootContext;
        this.systemContext = systemContext;
        this.monitorFactory = monitorFactory;
        this.builders = (builders == null) ? new ArrayList(1) : new ArrayList(builders);
        this.wireBuilders = (wireBuilders == null) ? new ArrayList(1) : new ArrayList(wireBuilders);
    }

    public void start() throws CoreRuntimeException {
        if (lifecycleState == RUNNING) {
            return;
        }
        systemContext.start();
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

    public void addBuilder(RuntimeConfigurationBuilder builder) {
        assert (builder != null) : "Builder was null";
        builders.add(builder);
    }

    public AggregateContext getContext(String ctxName) {
        checkRunning();
        if (ROOT.equals(ctxName)) {
            return rootContext;
        } else if (SYSTEM.equals(ctxName)) {
            return systemContext;
        }
        return (AggregateContext) rootContext.getContext(ctxName);
    }

    public AggregateContext getRootContext() {
        checkRunning();
        return rootContext;
    }

    public SystemAggregateContext getSystemContext() {
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

    public void registerModelObjects(List<Extensible> models) throws ConfigurationException {
        for (Extensible model : models) {
            registerModelObject(model);
        }
    }

    public void registerListener(RuntimeEventListener listener) {
        assert (listener != null) : "Listener cannot be null";
        listeners.add(listener);
    }

    public void fireEvent(int eventType, Object message) throws EventException {
        checkRunning();
        for (RuntimeEventListener listener : listeners) {
            listener.onEvent(eventType, message);
        }
    }

    public AggregateContext getParent() {
        return null; // there is no parent
    }

    public Object locateService(String serviceName) {
        return null;
    }

    public Object locateInstance(String serviceName) {
        return null;
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getSystemContext().getInstance(qName);
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        return getInstance(qName);
    }

    // ----------------------------------
    // ConfigurationContext methods
    // ----------------------------------

    public synchronized void build(AggregateContext parent, Extensible model) throws BuilderConfigException {
        AssemblyVisitor visitor = new AssemblyVisitor(parent, builders);
        visitor.start(model);
    }

    public void configure(Extensible model) throws ConfigurationException {
    }

    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        for (WireBuilder wireBuilder : wireBuilders) {
            wireBuilder.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
        }
    }

    public void wire(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext) throws BuilderConfigException {
        for (WireBuilder wireBuilder : wireBuilders) {
            wireBuilder.completeTargetChain(targetFactory, targetType, targetScopeContext);
        }
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
        } else if (RuntimeContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else {
            // autowire to system components
            return instanceInterface.cast(getSystemContext().resolveInstance(instanceInterface));
        }
    }

    //----------------------------------
    // InstanceContext methods
    //----------------------------------

    public Object getImplementationInstance() throws TargetException{
        return this;
    }

    public Object getImplementationInstance(boolean notify) throws TargetException{
        return this;
    }
    
    // ----------------------------------
    // Private methods
    // ----------------------------------

    private void checkRunning() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Context must be in RUNNING state");
        }
    }
}
