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
package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitor;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoader;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.ContextRuntimeException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.model.assembly.ExtensibleModelObject;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * Serves as the runtime bootstrap
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeContextImpl extends AbstractContext implements RuntimeContext {

    private List<RuntimeConfigurationBuilder> builders = new ArrayList();

    private List<RuntimeEventListener> listeners = new ArrayList();

    private AggregateContext rootContext;

    // the cached system context
    private AutowireContext systemContext;

    private ConfigurationLoader loader;

    private MonitorFactory monitorFactory;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public RuntimeContextImpl() {
        super(RUNTIME);
        monitorFactory = new NullMonitorFactory();
    }

    public RuntimeContextImpl(MonitorFactory monitorFactory, List<RuntimeConfigurationBuilder> builders,
            ConfigurationLoader loader) {
        super(RUNTIME);
        this.monitorFactory = monitorFactory;
        if (builders != null) {
            this.builders.addAll(builders);
        }
        this.loader = loader;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void start() throws CoreRuntimeException {
        rootContext = new AggregateContextImpl(ROOT, this, this, new RuntimeScopeStrategy(), new EventContextImpl(), this,
                monitorFactory);
        rootContext.start();
        lifecycleState = RUNNING;
    }

    public void stop() throws CoreRuntimeException {
        checkRunning();
        rootContext.stop();
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
            return getSystemContext();
        }
        return (AggregateContext) rootContext.getContext(ctxName);
    }

    public AggregateContext getRootContext() {
        checkRunning();
        return rootContext;
    }

    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    public void registerModelObject(ExtensibleModelObject model) throws ConfigurationException {
        assert (model != null) : "Model was null";
        // note do not configure or build model object since the root context will perform a call back
        rootContext.registerModelObject(model);
    }

    public void registerModelObjects(List<ExtensibleModelObject> models) throws ConfigurationException {
        for (ExtensibleModelObject model : models) {
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

    public AutowireContext getSystemContext() {
        checkRunning();
        if (systemContext == null) {
            InstanceContext ctx = rootContext.getContext(SYSTEM);
            if (ctx == null) {
                throw new ContextRuntimeException("System context not found");
            } else if (!(ctx instanceof AutowireContext)) {
                ContextRuntimeException e = new ContextRuntimeException("Invalid type for system context: it must implement "
                        + AutowireContext.class.getName());
                e.setIdentifier(ctx.getClass().getName());
                throw e;
            }
            systemContext = (AutowireContext) ctx;
        }
        return systemContext;
    }

    // ----------------------------------
    // ConfigurationContext methods
    // ----------------------------------

    public ModuleComponent loadModuleComponent(String qualifiedName, String moduleUri) throws ConfigurationException {
        return loader.loadModuleComponent(qualifiedName, moduleUri);
    }

    public synchronized void build(AggregateContext parent, ExtensibleModelObject model) throws BuilderConfigException {
        AssemblyVisitor visitor = new AssemblyVisitor(parent, builders);
        visitor.start(model);
    }

    public void configure(ExtensibleModelObject model) throws ConfigurationException {
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

    // ----------------------------------
    // Private methods
    // ----------------------------------

    private void checkRunning() {
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Context must be in RUNNING state");
        }
    }

}
