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
package org.apache.tuscany.core.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoader;
import org.apache.tuscany.core.config.impl.EMFConfigurationLoader;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Create and initialize a Tuscany SCA runtime environment.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyRuntime extends SCA {
    private final Monitor monitor;
    private final Object sessionKey = new Object();
    
    private final RuntimeContext runtime;
    private AggregateContext ctx;
    

    /**
     * Construct a runtime using a null MonitorFactory.
     *
     * @param name the name of the module component
     * @param uri  the URI to assign to the module component
     * @throws ConfigurationException if there was a problem loading the SCA configuration
     * @see TuscanyRuntime#TuscanyRuntime(String, String, org.apache.tuscany.common.monitor.MonitorFactory)
     */
    public TuscanyRuntime(String name, String uri) throws ConfigurationException {
        this(name, uri, new NullMonitorFactory());
    }

    /**
     * Construct a runtime containing a single module component with the
     * specified name. The module definition is loaded from a "/sca.module"
     * resource found on the classpath of the current Thread context classloader.
     *
     * @param name           the name of the module component
     * @param uri            the URI to assign to the module component
     * @param monitorFactory the MonitorFactory for this runtime
     * @throws ConfigurationException if there was a problem loading the SCA configuration
     */
    public TuscanyRuntime(String name, String uri, MonitorFactory monitorFactory) throws ConfigurationException {
        this.monitor = monitorFactory.getMonitor(Monitor.class);

        // create a resource loader from the current classloader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ResourceLoader resourceLoader = new ResourceLoaderImpl(classLoader);
        AssemblyFactory modelFactory=new AssemblyFactoryImpl();
        AssemblyModelLoader modelLoader=new SCDLAssemblyModelLoaderImpl();
        AssemblyModelContext modelContext = new AssemblyModelContextImpl(modelFactory, modelLoader, resourceLoader);

        // load the configuration files using EMF
        ConfigurationLoader loader = new EMFConfigurationLoader(modelContext);
        ModuleComponent moduleComponent = loader.loadModuleComponent(name, uri);

        List<RuntimeConfigurationBuilder> configBuilders = new ArrayList();
        configBuilders.add((new SystemComponentContextBuilder()));
        configBuilders.add(new SystemEntryPointBuilder());
        configBuilders.add(new SystemExternalServiceBuilder());

        List<WireBuilder> wireBuilders = new ArrayList();
        
        runtime = new RuntimeContextImpl(monitorFactory,configBuilders,wireBuilders);
        //TODO Come up with a configuration mechanism for registering Java builders
        //TODO register an aggregate context under the root
        //runtime.getRootContext().registerModelObject()
    }

    /**
     * Start the runtime and associate the module context with the calling thread.
     */
    @Override
    public void start() {
        setModuleContext((ModuleContext)ctx);
        try {
            runtime.start();
            monitor.started(runtime);
            ctx = null;
            ctx.start();
            ctx.fireEvent(EventContext.MODULE_START, null);
            ctx.fireEvent(EventContext.REQUEST_START, null);
            ctx.fireEvent(EventContext.SESSION_NOTIFY, sessionKey);
            monitor.started(ctx);
        } catch (CoreRuntimeException e) {
            setModuleContext(null);
            monitor.startFailed(ctx, e);
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
        ctx.fireEvent(EventContext.REQUEST_END, null);
        ctx.fireEvent(EventContext.SESSION_END, sessionKey);
        ctx.fireEvent(EventContext.MODULE_STOP, null);
        ctx.stop();
        monitor.stopped(ctx);
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
