/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.tomcat;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.config.impl.ModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.loader.SystemSCDLModelLoader;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

/**
 * A Tomcat listener to be attached to a Host container to add SCA runtime functionality.
 * The listener wraps a Tuscany runtime and listens for container events to detect the
 * addition and removal of Context children.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyHost extends StandardHost {
    private static final String SYSTEM_MODULE_COMPONENT = "org.apache.tuscany.core.system";
    private static final Log log = LogFactory.getLog(TuscanyHost.class);
    private static final StringManager sm = StringManager.getManager("org.apache.tuscany.tomcat");

    private RuntimeContext runtime;
    private AssemblyModelLoader modelLoader;
    private AssemblyFactory modelFactory;
    private ResourceLoader systemLoader;

    public synchronized void start() throws LifecycleException {
        startRuntime();
        super.start();
    }

    public synchronized void stop() throws LifecycleException {
        super.stop();
        stopRuntime();
    }

    private void startRuntime() {
        systemLoader = new ResourceLoaderImpl(getClass().getClassLoader());

        // Create an assembly model factory
        modelFactory = new AssemblyFactoryImpl();

        // Create an assembly model loader
        List<SCDLModelLoader> scdlLoaders=new ArrayList<SCDLModelLoader>();
        scdlLoaders.add(new SystemSCDLModelLoader());
        modelLoader = new SCDLAssemblyModelLoaderImpl(scdlLoaders);

        // Create an assembly model context
        AssemblyModelContext modelContext = new AssemblyModelContextImpl(modelFactory, modelLoader, systemLoader);

        // Load the system module component
        ModuleComponentConfigurationLoader loader = new ModuleComponentConfigurationLoaderImpl(modelContext);
        ModuleComponent systemModuleComponent;
        try {
            systemModuleComponent = loader.loadSystemModuleComponent(SYSTEM_MODULE_COMPONENT, SYSTEM_MODULE_COMPONENT);
        } catch (ConfigurationLoadException e) {
            log.warn(sm.getString("runtime.loadSystemFailed"), e);
            return;
        }

        List<RuntimeConfigurationBuilder> configBuilders = new ArrayList();
        configBuilders.add((new SystemComponentContextBuilder()));
        configBuilders.add(new SystemEntryPointBuilder());
        configBuilders.add(new SystemExternalServiceBuilder());

        runtime = new RuntimeContextImpl(new NullMonitorFactory(), scdlLoaders, configBuilders, new DefaultWireBuilder());
        runtime.start();

        try {
            SystemAggregateContext systemContext = runtime.getSystemContext();
            systemContext.registerModelObject(systemModuleComponent);

            // Get the aggregate context representing the system module component
            AggregateContext systemModuleComponentContext = (AggregateContext) systemContext.getContext(SYSTEM_MODULE_COMPONENT);
            systemModuleComponentContext.registerModelObject(systemModuleComponent.getComponentImplementation());
            systemModuleComponentContext.fireEvent(EventContext.MODULE_START, null);
        } catch (Exception e) {
            log.warn(sm.getString("runtime.registerSystemFailed"), e);
            runtime.stop();
            runtime = null;
            return;
        }

        log.info(sm.getString("runtime.started"));
    }

    private void stopRuntime() {
        if (runtime == null) {
            return;
        }

        runtime.stop();
        runtime = null;
        log.info(sm.getString("runtime.stopped"));
    }

    public synchronized void addChild(Container child) {
        if (!(child instanceof StandardContext)) {
            throw new IllegalArgumentException(sm.getString("tuscanyHost.notContext"));
        }
        StandardContext ctx = (StandardContext) child;
        ctx.addLifecycleListener(new TuscanyContextListener(runtime.getRootContext(), modelFactory, modelLoader, systemLoader));
        super.addChild(child);
    }

    public String toString() {

        StringBuffer sb = new StringBuffer(132);
        if (getParent() != null) {
            sb.append(getParent().toString()).append('.');
        }
        sb.append("TuscanyHost[").append(getName()).append(']');
        return (sb.toString());

    }
}
