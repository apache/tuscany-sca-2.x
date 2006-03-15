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
package org.apache.tuscany.core.webapp;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.config.impl.ModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.builder.SystemContextFactoryBuilder;
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

/**
 * ServletContextListener that can be added to a standard web application to boot
 * a Tuscany runtime inside that application. All implementation classes should
 * be located in the web application itself.
 *
 * @version $Rev: 380792 $ $Date: 2006-02-24 11:25:11 -0800 (Fri, 24 Feb 2006) $
 */
public class TuscanyServletListener implements ServletContextListener, HttpSessionListener {
    public static final String SCA_COMPONENT_NAME = "org.apache.tuscany.core.webapp.ModuleComponentName";
    public static final String MODULE_COMPONENT_NAME = "org.apache.tuscany.core.webapp.ModuleComponentContext";
    public static final String TUSCANY_RUNTIME_NAME = RuntimeContext.class.getName();

    private final Object sessionKey = new Object();

    private RuntimeContext runtimeContext;
    private AggregateContext systemModuleComponentContext;
    private AggregateContext moduleContext;

    private static final String SYSTEM_MODULE_COMPONENT = "org.apache.tuscany.core.system";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        String name = servletContext.getInitParameter(SCA_COMPONENT_NAME);
        String uri = name; // todo get from context path
        MonitorFactory monitorFactory = new NullMonitorFactory(); // todo have one that writes to the servlet log

        try {
            bootRuntime(name, uri, monitorFactory);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        servletContext.setAttribute(TUSCANY_RUNTIME_NAME, runtimeContext);
        servletContext.setAttribute(MODULE_COMPONENT_NAME, moduleContext);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
        systemModuleComponentContext.fireEvent(EventContext.MODULE_STOP, null);
        systemModuleComponentContext.stop();
        runtimeContext.stop();
        servletContextEvent.getServletContext().removeAttribute(MODULE_COMPONENT_NAME);
        servletContextEvent.getServletContext().removeAttribute(TUSCANY_RUNTIME_NAME);
    }

    public void sessionCreated(HttpSessionEvent event) {
        // do nothing since sessions are lazily created in {@link TuscanyRequestFilter}
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        // todo do we actually need to bind the module context to the thread to fire this event?
        ModuleContext oldContext = CurrentModuleContext.getContext();
        try {
            ContextBinder.BINDER.setContext((ModuleContext) moduleContext);
            moduleContext.fireEvent(EventContext.SESSION_END, event.getSession());
        } finally{
            ContextBinder.BINDER.setContext(oldContext);
        }
    }

    private void bootRuntime(String name, String uri, MonitorFactory monitorFactory) throws ConfigurationException {
        // Create a resource loader from the current classloader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ResourceLoader resourceLoader = new ResourceLoaderImpl(classLoader);

        // Create an assembly model factory
        AssemblyFactory modelFactory=new AssemblyFactoryImpl();

        // Create an assembly model loader
        List<SCDLModelLoader> scdlLoaders=new ArrayList<SCDLModelLoader>();
        scdlLoaders.add(new SystemSCDLModelLoader());
        AssemblyModelLoader modelLoader=new SCDLAssemblyModelLoaderImpl(scdlLoaders);

        // Create an assembly model context
        AssemblyModelContext modelContext = new AssemblyModelContextImpl(modelFactory, modelLoader, resourceLoader);

        // Create system configuration builders
        List<ContextFactoryBuilder> configBuilders = new ArrayList();
        configBuilders.add((new SystemContextFactoryBuilder()));
        configBuilders.add(new SystemEntryPointBuilder());
        configBuilders.add(new SystemExternalServiceBuilder());

        // Create a runtime context and start it
        runtimeContext = new RuntimeContextImpl(monitorFactory, scdlLoaders, configBuilders,new DefaultWireBuilder());
        runtimeContext.start();

        // Get the system context
        AggregateContext systemContext = runtimeContext.getSystemContext();

        // Load the system module component
        ModuleComponentConfigurationLoader loader = new ModuleComponentConfigurationLoaderImpl(modelContext);
        ModuleComponent systemModuleComponent = loader.loadSystemModuleComponent(SYSTEM_MODULE_COMPONENT, SYSTEM_MODULE_COMPONENT);

        // Register it with the system context
        systemContext.registerModelObject(systemModuleComponent);

        // Get the aggregate context representing the system module component
        systemModuleComponentContext = (AggregateContext) systemContext.getContext(SYSTEM_MODULE_COMPONENT);
        //systemModuleComponentContext.registerModelObject(systemModuleComponent.getComponentImplementation());
        systemModuleComponentContext.fireEvent(EventContext.MODULE_START, null);

        // Load the SCDL configuration of the application module
        ModuleComponent moduleComponent = loader.loadModuleComponent(name, uri);

        // Register it under the root application context
        runtimeContext.getRootContext().registerModelObject(moduleComponent);
        moduleContext=(AggregateContext)runtimeContext.getContext(moduleComponent.getName());
        //moduleContext.registerModelObject(moduleComponent.getComponentImplementation());

        moduleContext.fireEvent(EventContext.MODULE_START, null);
    }
}
