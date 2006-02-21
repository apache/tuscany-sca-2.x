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

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.common.resource.loader.ResourceLoaderFactory;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoader;
import org.apache.tuscany.core.config.impl.EMFConfigurationLoader;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyLoader;
import org.apache.tuscany.model.assembly.loader.impl.AssemblyLoaderImpl;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Create and initialize a Tuscany SCA runtime environment.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyRuntime extends SCA {
    private final Monitor monitor;
    private final TuscanyModuleComponentContext ctx;
    private final Object sessionKey = new Object();

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
        ResourceLoader resourceLoader = ResourceLoaderFactory.getResourceLoader(classLoader);
        AssemblyLoader assemblyLoader = new AssemblyLoaderImpl();
        AssemblyModelContext modelContext = new AssemblyModelContextImpl(assemblyLoader, resourceLoader);

        // load the configuration files using EMF
        ConfigurationLoader loader = new EMFConfigurationLoader(modelContext);
        ModuleComponent moduleComponent = loader.loadModuleComponent(name, uri);

        // create the module component context
        EventContext context = new EventContextImpl();
        ScopeStrategy scopeStrategy = new DefaultScopeStrategy();
        
        //FIXME This is going away and will be replaced by Jim's AggregateContext
        //ctx = new TuscanyModuleComponentContextImpl(moduleComponent, context, scopeStrategy, modelContext);
        ctx = null;
    }

    /**
     * Start the runtime and associate the module context with the calling thread.
     */
    @Override
    public void start() {
        setModuleContext(ctx);
        try {
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
        void started(TuscanyModuleComponentContext ctx);

        /**
         * Event emitted when an attempt to start the runtime failed.
         *
         * @param ctx the runtime's module component context
         * @param e   the exception that caused the failure
         */
        void startFailed(TuscanyModuleComponentContext ctx, CoreRuntimeException e);

        /**
         * Event emitted after the runtime has been stopped.
         *
         * @param ctx the runtime's module component context
         */
        void stopped(TuscanyModuleComponentContext ctx);
    }
}
