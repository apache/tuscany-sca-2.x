/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.servicemix.sca.tuscany;

import java.util.List;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

public class TuscanyRuntime extends SCA {
    private final TuscanyRuntime.Monitor monitor;
    private final Object sessionKey = new Object();

    private final RuntimeContext runtime;
    private final AggregateContext moduleContext;
    
    private final ModuleComponent moduleComponent;

    private static final String SYSTEM_MODULE_COMPONENT = "org.apache.tuscany.core.system";

    /**
     * Construct a runtime using a null MonitorFactory.
     *
     * @param name the name of the module component
     * @param uri  the URI to assign to the module component
     * @throws ConfigurationException if there was a problem loading the SCA configuration
     * @see TuscanyRuntime#TuscanyRuntime(String, String, org.apache.tuscany.common.monitor.MonitorFactory)
     */
    public TuscanyRuntime(String name, String uri) throws ConfigurationException {
        this(name, uri, 
             Thread.currentThread().getContextClassLoader(), 
             new NullMonitorFactory());
    }

    /**
     * Construct a runtime containing a single module component with the
     * specified name. The module definition is loaded from a "/sca.module"
     * resource found on the classpath of the current Thread context classloader.
     *
     * @param name           the name of the module component
     * @param uri            the URI to assign to the module component
     * @param classLoader    the class loader to use for the assembly
     * @param monitorFactory the MonitorFactory for this runtime
     * @throws ConfigurationException if there was a problem loading the SCA configuration
     */
    public TuscanyRuntime(String name, String uri, ClassLoader classLoader, MonitorFactory monitorFactory) throws ConfigurationException {
        this.monitor = monitorFactory.getMonitor(TuscanyRuntime.Monitor.class);

        // Create an assembly model context
        AssemblyModelContext modelContext = BootstrapHelper.getModelContext(classLoader);

        // Create a runtime context and start it
        List<SCDLModelLoader> loaders = modelContext.getAssemblyLoader().getLoaders();
        List<ContextFactoryBuilder> configBuilders = BootstrapHelper.getBuilders();
        runtime = new RuntimeContextImpl(monitorFactory, loaders, configBuilders, new DefaultWireBuilder());
        runtime.start();
        monitor.started(runtime);

        // Load and start the system configuration
        SystemAggregateContext systemContext = runtime.getSystemContext();
        ModuleComponentConfigurationLoader loader = BootstrapHelper.getConfigurationLoader(systemContext, modelContext);
        ModuleComponent systemModuleComponent = loader.loadSystemModuleComponent(SYSTEM_MODULE_COMPONENT, SYSTEM_MODULE_COMPONENT);
        AggregateContext context = BootstrapHelper.registerModule(systemContext, systemModuleComponent);
        context.fireEvent(EventContext.MODULE_START, null);

        // Load the SCDL configuration of the application module
        AggregateContext rootContext = runtime.getRootContext();
        moduleComponent = loader.loadModuleComponent(name, uri);
        moduleContext = BootstrapHelper.registerModule(rootContext, moduleComponent);
    }

    public ModuleComponent getModuleComponent() {
        return moduleComponent;
    }
    
    public AggregateContext getModuleContext() {
        return moduleContext;
    }
    
    /**
     * Start the runtime and associate the module context with the calling thread.
     */
    @Override
    public void start() {
        setModuleContext((ModuleContext) moduleContext);
        try {
            //moduleContext.start();
            moduleContext.fireEvent(EventContext.MODULE_START, null);
            moduleContext.fireEvent(EventContext.REQUEST_START, null);
            moduleContext.fireEvent(EventContext.SESSION_NOTIFY, sessionKey);
            monitor.started(moduleContext);
        } catch (CoreRuntimeException e) {
            setModuleContext(null);
            monitor.startFailed(moduleContext, e);
            //FIXME throw a better exception
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Disassociate the module context from the current thread and shut down the runtime.
     */
    @Override
    public void stop() {
        setModuleContext(null);
        moduleContext.fireEvent(EventContext.REQUEST_END, null);
        moduleContext.fireEvent(EventContext.SESSION_END, sessionKey);
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
        monitor.stopped(moduleContext);
        runtime.stop();
        monitor.stopped(runtime);
    }

    /**
     * Monitor interface for a TuscanyRuntime.
     */
    public static interface Monitor {
        /**
         * Event emitted after the runtime has been started.
         *
         * @param ctx the runtime's module component context
         */
        void started(AggregateContext ctx);

        /**
         * Event emitted when an attempt to start the runtime failed.
         *
         * @param ctx the runtime's module component context
         * @param e   the exception that caused the failure
         */
        void startFailed(AggregateContext ctx, CoreRuntimeException e);

        /**
         * Event emitted after the runtime has been stopped.
         *
         * @param ctx the runtime's module component context
         */
        void stopped(AggregateContext ctx);
    }

}
