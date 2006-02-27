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
package org.apache.tuscany.tomcat.lifecycle.listener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.config.impl.ModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.webapp.HTTPSessionExpirationListener;
import org.apache.tuscany.core.context.webapp.TuscanyRequestFilter;
import org.apache.tuscany.core.context.webapp.TuscanyWebAppRuntime;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.core.system.loader.SystemSCDLModelLoader;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;
import org.osoa.sca.ServiceRuntimeException;

//FIXME This is a temporary hack to bootstrap the runtime in a Tomcat environment and do some bringup testing, the real bootstrap code is
// still under construction

/**
 * Responsible for initializing web applications as module components in a
 * Tomcat instance. When a web app is being loaded in Tomcat, this listener
 * receives a callback to register a corresponding module component and set up
 * the appropriate Servlet filters in the web context.
 * <p/>
 * Note that Tomcat loads this class in the standard (common/lib) classloader
 * even though the context classloader is that of the web app being loaded.
 * Consequently, the transitive closure of class references must not include
 * <tt>ModuleContext</tt> or <tt>ModuleManager</tt>
 * <p/>
 * TODO decide if we want to set this up as a Tomcat listener or as a web app
 * listener Setting this up as a web app listener allows users to set the
 * context path, avoiding a performance hit for SCA event processing in contexts
 * where it is not used. The downside is the potential for user error. For
 * example, SCA event filters must be manually configured. We probably should
 * provide both options
 * FIXME fix the exception handling
 */
public class TomcatWebAppLifecycleListener implements LifecycleListener {

    private final static String SYSTEM_MODULE_COMPONENT = "org.apache.tuscany.core.system";
    
    /**
     * Constructor
     */
    public TomcatWebAppLifecycleListener() {
    }

    /**
     * Callback for receiving web application events. When a web app containing
     * an SCA configuration is initialized, a module component is created and
     * appropriate SCA filters are set for in the web context. If a web app stop
     * event is received, the corresponding module component is deregistered from
     * the system.
     */
    public void lifecycleEvent(LifecycleEvent event) {
        String lifecycleEventType = event.getType();
        Object lifecycleEventSource = event.getSource();

        if (Lifecycle.START_EVENT.equals(lifecycleEventType) && lifecycleEventSource instanceof Context) {

            // The web app is starting
            Context context = (Context) lifecycleEventSource;
            try {

                // Get the application classloader
                ClassLoader applicationClassLoader = Thread.currentThread().getContextClassLoader();
                ResourceLoader resourceLoader = new ResourceLoaderImpl(applicationClassLoader);

                // Check if the web app contains an sca.module file
                URL url;
                try {
                    url = resourceLoader.getResource("sca.module");
                } catch (IOException e) {
                    url = null;
                }
                if (url != null) {
                    // The Web app has an sca.module file
                    // Get the module component name from the context
                    String moduleComponentName = context.getPath().substring(1);
                    try {

                        // Create an assembly model factory
                        AssemblyFactory modelFactory=new AssemblyFactoryImpl();
                        
                        // Create an assembly model loader
                        List<SCDLModelLoader> scdlLoaders=new ArrayList<SCDLModelLoader>();
                        scdlLoaders.add(new SystemSCDLModelLoader());
                        AssemblyModelLoader modelLoader=new SCDLAssemblyModelLoaderImpl(scdlLoaders);
                        
                        // Create an assembly model context
                        AssemblyModelContext modelContext = new AssemblyModelContextImpl(modelFactory, modelLoader, resourceLoader);

                        // Create system configuration builders
                        List<RuntimeConfigurationBuilder> configBuilders = new ArrayList();
                        configBuilders.add((new SystemComponentContextBuilder()));
                        configBuilders.add(new SystemEntryPointBuilder());
                        configBuilders.add(new SystemExternalServiceBuilder());

                        // Create a runtime context and start it
                        RuntimeContext runtimeContext = new RuntimeContextImpl(new NullMonitorFactory(), scdlLoaders, configBuilders,new DefaultWireBuilder());
                        runtimeContext.start();

                        // Get the system context
                        AggregateContext systemContext = runtimeContext.getSystemContext();
                        
                        // Load the system module component
                        ModuleComponentConfigurationLoader loader = new ModuleComponentConfigurationLoaderImpl(modelContext);
                        ModuleComponent systemModuleComponent = loader.loadSystemModuleComponent(SYSTEM_MODULE_COMPONENT, SYSTEM_MODULE_COMPONENT);
                        
                        // Register it with the system context
                        systemContext.registerModelObject(systemModuleComponent);

                        // Get the aggregate context representing the system module component
                        AggregateContext systemModuleComponentContext = (AggregateContext) systemContext.getContext(SYSTEM_MODULE_COMPONENT);
                        systemModuleComponentContext.registerModelObject(systemModuleComponent.getComponentImplementation());
                        systemModuleComponentContext.fireEvent(EventContext.MODULE_START, null);
                        
                        // Load the SCDL configuration of the application module
                        String uri = context.getPath().substring(1);
                        ModuleComponent moduleComponent = loader.loadModuleComponent(moduleComponentName, uri);
                        
                        // Register it under the root application context
                        runtimeContext.getRootContext().registerModelObject(moduleComponent);
                        AggregateContext moduleContext=(AggregateContext)runtimeContext.getContext(moduleComponent.getName());
                        moduleContext.registerModelObject(moduleComponent.getComponentImplementation());

                        // Create a Tuscany runtime and store it in the servlet
                        // context
                        TuscanyWebAppRuntime tuscanyRuntime = new TuscanyWebAppRuntime(moduleContext);
                        context.getServletContext().setAttribute(TuscanyWebAppRuntime.class.getName(), tuscanyRuntime);

                        // Start the runtime and the module component context
                        tuscanyRuntime.start();
                        try {
                            //moduleContext.start();

                            moduleContext.fireEvent(EventContext.MODULE_START, null);

                        } finally {
                            tuscanyRuntime.stop();
                        }

                        // Set up the SCA web app filter and listener
                        FilterDef def = new FilterDef();
                        def.setDescription("SCA Filter");
                        def.setFilterClass(TuscanyRequestFilter.class.getName());
                        def.setFilterName("__modContext");
                        def.setDisplayName("SCA Filter");
                        FilterMap map = new FilterMap();
                        map.setFilterName("__modContext");
                        map.setURLPattern("/*");
                        context.addFilterDef(def);
                        context.addFilterMap(map);
                        context.addApplicationListener(HTTPSessionExpirationListener.class.getName());

                    } catch (CoreRuntimeException e) {
                        throw new ServiceRuntimeException(e);
                    } catch (Exception e) {
                        throw new ServiceRuntimeException(e);
                    }
                }
            } catch (Exception e) {
            }
        } else if (Lifecycle.STOP_EVENT.equals(lifecycleEventType) && lifecycleEventSource instanceof Context) {

            // The web app is stopping
            Context context = (Context) lifecycleEventSource;
            try {

                // Get the tuscany runtime from the servlet context
                // If the servlet context contains a tuscany runtime then this is a
                // Tuscany app
                TuscanyWebAppRuntime tuscanyRuntime = (TuscanyWebAppRuntime) context.getServletContext().getAttribute(
                        TuscanyWebAppRuntime.class.getName());
                if (tuscanyRuntime != null) {

                    // Get the module component name from the servlet context
                    try {
                        // Start the runtime
                        tuscanyRuntime.start();
                        try {
                            // Stop the module context
                            AggregateContext aggregateContext = tuscanyRuntime.getModuleComponentContext();
                            aggregateContext.fireEvent(EventContext.MODULE_STOP, null);
                            aggregateContext.stop();
                        } finally {

                            // Stop the runtime
                            tuscanyRuntime.stop();

                            // Cleanup the servlet context
                            context.getServletContext().removeAttribute(TuscanyWebAppRuntime.class.getName());
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }

}
