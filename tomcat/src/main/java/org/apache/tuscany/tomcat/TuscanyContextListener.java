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

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.binding.axis2.handler.WebServiceEntryPointServlet;
import org.apache.tuscany.binding.jsonrpc.handler.JSONRPCEntryPointServlet;
import org.apache.tuscany.binding.jsonrpc.handler.ScriptGetterServlet;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog(TuscanyContextListener.class);
    private static final String TUSCANY_RUNTIME_NAME = RuntimeContext.class.getName();
    public static final String MODULE_COMPONENT_NAME = "org.apache.tuscany.core.webapp.ModuleComponentContext";

    private final AssemblyFactory modelFactory;
    private final AssemblyModelLoader modelLoader;
    private final RuntimeContext runtime;
    private final ResourceLoader systemLoader;
    private AggregateContext moduleContext;

    public TuscanyContextListener(RuntimeContext runtimeContext, AssemblyFactory modelFactory, AssemblyModelLoader modelLoader, ResourceLoader systemLoader) {
        this.runtime = runtimeContext;
        this.modelFactory = modelFactory;
        this.modelLoader = modelLoader;
        this.systemLoader = systemLoader;
    }

    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        if (Lifecycle.START_EVENT.equals(type)) {
            startContext((Context) event.getLifecycle());
        } else if (Lifecycle.STOP_EVENT.equals(type)) {
            stopContext((Context) event.getLifecycle());
        }
    }

    private void startContext(Context ctx) {
        ClassLoader appLoader = ctx.getLoader().getClassLoader();
        if (appLoader.getResource("sca.module") == null) {
            return;
        }

        try {
            loadContext(ctx);
        } catch (ConfigurationException e) {
            log.error("context.configError", e);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
            }
            return;
        }
        moduleContext.fireEvent(EventContext.MODULE_START, null);

        // add a valve to this context's pipeline that will associate the request with the runtime
        Valve valve = new TuscanyValve(moduleContext);
        ctx.getPipeline().addValve(valve);

        // add the web service servlet wrapper
        addWebServiceWrapper(ctx);
        addJSONRPCServiceWrapper(ctx);

        // add the RuntimeContext in as a servlet context parameter
        ServletContext servletContext = ctx.getServletContext();
        servletContext.setAttribute(TUSCANY_RUNTIME_NAME, runtime);
        servletContext.setAttribute(MODULE_COMPONENT_NAME, moduleContext);
    }

    private void loadContext(Context ctx) throws ConfigurationException {
        ResourceLoader resourceLoader = new ResourceLoaderImpl(ctx.getLoader().getClassLoader());
        ClassLoader oldCl  = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
            AssemblyModelContext modelContext = new AssemblyModelContextImpl(modelFactory, modelLoader, systemLoader, resourceLoader);

            ModuleComponentConfigurationLoader loader = BootstrapHelper.getConfigurationLoader(runtime.getSystemContext(), modelContext);

            // Load the SCDL configuration of the application module
            ModuleComponent moduleComponent = loader.loadModuleComponent(ctx.getName(), ctx.getPath());

            // Register it under the root application context
            AggregateContext rootContext = runtime.getRootContext();
            rootContext.registerModelObject(moduleComponent);
            moduleContext = (AggregateContext)rootContext.getContext(moduleComponent.getName());
            //moduleContext.registerModelObject(moduleComponent.getComponentImplementation());
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    private void stopContext(Context ctx) {
        if (moduleContext!=null) {
            moduleContext.fireEvent(EventContext.MODULE_START, null);
        }
        // todo unload module component from runtime
    }

    private static void addWebServiceWrapper(Context ctx) {
        // todo this should not depend on axis2, we need an API in the model for embedders
        // todo should only add this servlet if we need it
        // todo servlet implementation should be determined by the binding implementation
        // todo should get path from entry point definition and not hard code to /services

        Class<WebServiceEntryPointServlet> servletClass = WebServiceEntryPointServlet.class;
        StandardWrapper wrapper = new StandardWrapper();
        wrapper.setName("TuscanyAxis2EntryPointServlet");
        wrapper.setLoader(new ContainerLoader(servletClass.getClassLoader()));
        wrapper.setServletClass(servletClass.getName());
        ctx.addChild(wrapper);
        ctx.addServletMapping("/services/*", wrapper.getName());
    }

    private static void addJSONRPCServiceWrapper(Context ctx) {
        // todo this should not depend on jsonrpc, we need an API in the model for embedders
        // todo should only add this servlet if we need it
        // todo servlet implementation should be determined by the binding implementation
        // todo should get path from entry point definition and not hard code to /services

    	{
            Class<JSONRPCEntryPointServlet> servletClass = JSONRPCEntryPointServlet.class;
            StandardWrapper wrapper = new StandardWrapper();
            wrapper.setName("TuscanyJSONRPCServlet");
            wrapper.setLoader(new ContainerLoader(servletClass.getClassLoader()));
            wrapper.setServletClass(servletClass.getName());
            ctx.addChild(wrapper);
            ctx.addServletMapping("/SCA/jsonrpc/*", wrapper.getName());
    	}
    	{
            Class<ScriptGetterServlet> servletClass = ScriptGetterServlet.class;
            StandardWrapper wrapper = new StandardWrapper();
            wrapper.setName("TuscanyJSONRPCScriptServlet");
            wrapper.setLoader(new ContainerLoader(servletClass.getClassLoader()));
            wrapper.setServletClass(servletClass.getName());
            ctx.addChild(wrapper);
            ctx.addServletMapping("/SCA/scripts/*", wrapper.getName());
    	}
    }
}
