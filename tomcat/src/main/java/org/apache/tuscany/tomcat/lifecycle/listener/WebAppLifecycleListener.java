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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.tuscany.core.TuscanyRuntimeException;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.context.webapp.HTTPSessionExpirationListener;
import org.apache.tuscany.core.context.webapp.TuscanyRequestFilter;
import org.apache.tuscany.core.context.webapp.TuscanyWebAppRuntime;
import org.apache.tuscany.core.system.context.RuntimeContext;
import org.apache.tuscany.core.system.context.RuntimeMonitor;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * Responsible for initializing web applications as module components in a Tomcat instance. When a web app is being
 * loaded in Tomcat, this listener receives a callback to register a corresponding module component and set up the
 * appropriate Servlet filters in the web context.
 * <p>
 * Note that Tomcat loads this class in the standard (common/lib) classloader even though the context classloader is
 * that of the web app being loaded.
 */
public class WebAppLifecycleListener implements LifecycleListener {

    private RuntimeContext runtime;

    private RuntimeMonitor monitor;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public WebAppLifecycleListener() {
        // locate runtime in the global naming context
        javax.naming.Context context = null;
        try {
            context = (javax.naming.Context) (new InitialContext()).lookup("java:/");
            runtime = (RuntimeContext) context.lookup(RuntimeBootstrap.RUNTIME_NAME);
            monitor = runtime.getMonitorFactory().getMonitor(RuntimeMonitor.class);
        } catch (NamingException e) {
            // FIXME need bootstrap logging;
            return;
        }
    }

    /**
     * Callback for receiving web application events. When a web app containing an SCA configuration is initialized, a
     * module component is created and appropriate SCA filters are set for in the web context. If a web app stop event
     * is received, the corresponding module component is deregistered from the system.
     */
    public void lifecycleEvent(LifecycleEvent event) {
        String lifecycleEventType = event.getType();
        Object lifecycleEventSource = event.getSource();

        if (Lifecycle.START_EVENT.equals(lifecycleEventType) && lifecycleEventSource instanceof Context) {
            // The web app is starting
            Context context = (Context) lifecycleEventSource;
            String name = context.getPath().substring(1);
            try {
                ModuleComponent moduleComponent = null;//FIXME runtime.loadModuleComponent(name, "sca.module");
                if (moduleComponent == null) {
                    return; // not an SCA module component
                }
                // create the module component
                runtime.registerModelObject(moduleComponent);
                TuscanyModuleComponentContext moduleComponentContext = (TuscanyModuleComponentContext) runtime
                        .getContext(moduleComponent.getName());
                TuscanyWebAppRuntime tuscanyRuntime = new TuscanyWebAppRuntime(moduleComponentContext);
                context.getServletContext().setAttribute(TuscanyWebAppRuntime.class.getName(), tuscanyRuntime);
                // Start the runtime and the module component context
                tuscanyRuntime.start();
                try {
                    moduleComponentContext.start();
                    moduleComponentContext.fireEvent(EventContext.MODULE_START, null);
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
            } catch (ConfigurationException e) {
                e.addContextName(name);
                monitor.log(e);
                return;
            } catch (TuscanyRuntimeException e) {
                e.addContextName(name);
                monitor.log(e);
                return;
            }
        } else if (Lifecycle.STOP_EVENT.equals(lifecycleEventType) && lifecycleEventSource instanceof Context) {
            // The web app is stopping
            Context context = (Context) lifecycleEventSource;
            // Get the tuscany runtime from the servlet context
            TuscanyWebAppRuntime tuscanyRuntime = (TuscanyWebAppRuntime) context.getServletContext().getAttribute(
                    TuscanyWebAppRuntime.class.getName());
            if (tuscanyRuntime == null) {
                return; // not an SCA module
            }
            try {
                // Stop the module context
                tuscanyRuntime.start();
                TuscanyModuleComponentContext moduleComponentContext = tuscanyRuntime.getModuleComponentContext();
                moduleComponentContext.fireEvent(EventContext.MODULE_STOP, null);
                moduleComponentContext.stop();
            } catch (TuscanyRuntimeException e) {
                monitor.log(e);
            } finally {
                try {
                    tuscanyRuntime.stop();
                } catch (TuscanyRuntimeException e) {
                    monitor.log(e);
                } finally {
                    // Cleanup the servlet context
                    context.getServletContext().removeAttribute(TuscanyWebAppRuntime.class.getName());
                }
            }

        }
    }
}
