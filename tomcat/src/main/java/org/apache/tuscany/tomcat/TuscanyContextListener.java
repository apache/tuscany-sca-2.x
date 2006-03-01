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

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.impl.ModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements LifecycleListener {
    private final AssemblyFactory modelFactory;
    private final AssemblyModelLoader modelLoader;
    private final AggregateContext rootContext;
    private final ResourceLoader systemLoader;

    public TuscanyContextListener(AggregateContext rootContext, AssemblyFactory modelFactory, AssemblyModelLoader modelLoader, ResourceLoader systemLoader) {
        this.rootContext = rootContext;
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
        AggregateContext moduleContext;

        ResourceLoader resourceLoader = new ResourceLoaderImpl(ctx.getLoader().getClassLoader());
        ClassLoader oldCl  = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
            AssemblyModelContext modelContext = new AssemblyModelContextImpl(modelFactory, modelLoader, systemLoader, resourceLoader);
            ModuleComponentConfigurationLoader loader = new ModuleComponentConfigurationLoaderImpl(modelContext);

            try {
                // Load the SCDL configuration of the application module
                ModuleComponent moduleComponent = loader.loadModuleComponent(ctx.getName(), ctx.getPath());

                // Register it under the root application context
                rootContext.registerModelObject(moduleComponent);
                moduleContext = (AggregateContext)rootContext.getContext(moduleComponent.getName());
                moduleContext.registerModelObject(moduleComponent.getComponentImplementation());
            } catch (ConfigurationLoadException e) {
                throw new UnsupportedOperationException();
            } catch (ConfigurationException e) {
                throw new UnsupportedOperationException();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

        // add a valve to this context's pipeline that will associate the request with the runtime
        Valve valve = new TuscanyValve(moduleContext);
        ctx.getPipeline().addValve(valve);
    }

    private void stopContext(Context ctx) {
        // todo unload module component from runtime
    }
}
