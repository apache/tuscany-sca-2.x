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

import javax.servlet.Servlet;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.util.StringManager;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.webapp.ServletHost;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 * A specialied Tomcat Host that extends the Standardhost implementation and adds SCA capabilities.
 * <p/>
 * As children are added, they are examined for the presence of SCA configuration
 * information and if any is found then the web application is treated as an
 * SCA Module defintion which is used to create a ModuleComponent. The name of the
 * context is used as the name of the ModuleComponent and its context path is used
 * as the URI.
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"serial"})
public class TuscanyHost extends StandardHost implements ServletHost {
    private static final String SYSTEM_MODULE_COMPONENT = "org.apache.tuscany.core.system";

    private static final StringManager sm = StringManager.getManager("org.apache.tuscany.tomcat");

    private RuntimeContext runtime;
    private AssemblyModelLoader modelLoader;
    private AssemblyFactory modelFactory;

    public synchronized void start() throws LifecycleException {
        startRuntime();
        super.start();
    }

    public synchronized void stop() throws LifecycleException {
        super.stop();
        stopRuntime();
    }

    private void startRuntime() {
        // Create an assembly model context
        AssemblyContext modelContext = BootstrapHelper.getModelContext(getClass().getClassLoader());
        modelFactory = modelContext.getAssemblyFactory();
        modelLoader = modelContext.getAssemblyLoader();

        // Create and start the runtime
        NullMonitorFactory monitorFactory = new NullMonitorFactory();
        ContextFactoryBuilderRegistry builderRegistry = BootstrapHelper.bootstrapContextFactoryBuilders(monitorFactory);
        runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, new DefaultWireBuilder());
        runtime.start();

        // Load and start the system configuration
        try {
            SystemCompositeContext systemContext = runtime.getSystemContext();
            BootstrapHelper.bootstrapStaxLoader(systemContext, modelContext);
            ModuleComponentConfigurationLoader loader = BootstrapHelper.getConfigurationLoader(systemContext, modelContext);
            ModuleComponent systemModuleComponent = loader.loadSystemModuleComponent(SYSTEM_MODULE_COMPONENT, SYSTEM_MODULE_COMPONENT);
            CompositeContext context = BootstrapHelper.registerModule(systemContext, systemModuleComponent);
            context.publish(new ModuleStart(this));

            TomcatHost host = systemContext.resolveInstance(TomcatHost.class);
            host.setHost(this);
        } catch (ConfigurationLoadException e) {
            getLogger().warn(sm.getString("runtime.loadSystemFailed", e.getResourceURI()), e);
            return;
        } catch (Exception e) {
            getLogger().warn(sm.getString("runtime.registerSystemFailed"), e);
            runtime.stop();
            runtime = null;
            return;
        }

        getLogger().info(sm.getString("runtime.started"));
    }

    private void stopRuntime() {
        if (runtime == null) {
            return;
        }

        runtime.stop();
        runtime = null;
        getLogger().info(sm.getString("runtime.stopped"));
    }

    public synchronized void addChild(Container child) {
        if (!(child instanceof StandardContext)) {
            throw new IllegalArgumentException(sm.getString("tuscanyHost.notContext"));
        }
        StandardContext ctx = (StandardContext) child;
        ctx.addLifecycleListener(new TuscanyContextListener(runtime, modelFactory, modelLoader));
        super.addChild(child);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(132);
        if (getParent() != null) {
            sb.append(getParent().toString()).append('.');
        }
        sb.append("TuscanyHost[").append(getName()).append(']');
        return (sb.toString());
    }

    public void registerMapping(String mapping, Servlet servlet) {
        Context ctx = map(mapping);
        if (ctx == null) {
            throw new UnsupportedOperationException("Cannot find context for mapping " + mapping);
        }
        String contextPath = ctx.getPath();
        assert mapping.startsWith(contextPath);
        mapping = mapping.substring(contextPath.length());
        Wrapper wrapper = new TuscanyWrapper(servlet);
        ctx.addChild(wrapper);
        wrapper.addMapping(mapping);
        ctx.getMapper().addWrapper(mapping, wrapper, false);
    }

    public void unregisterMapping(String mapping) {
    }
}
