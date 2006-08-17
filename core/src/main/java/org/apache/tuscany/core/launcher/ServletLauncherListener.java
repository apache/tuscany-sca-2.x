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
package org.apache.tuscany.core.launcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.core.monitor.MonitorFactoryUtil;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.host.MonitorFactory;

/**
 * Launcher for runtime environment that loads info from servlet context params.
 * This listener manages one top-level Launcher (and hence one Tuscany runtime context)
 * per servlet context; the lifecycle of that runtime corresponds to the the lifecycle of the
 * associated servlet context.
 *
 * Web application code may obtain the top-level CompositeContext via
 * {@link org.osoa.sca.CurrentCompositeContext#getContext()}.  If that returns null,
 * it is likely the runtime failed to boot: the context param {@link LAUNCHER_THROWABLE_ATTRIBUTE}
 * will contain a {@link Throwable} with diagnostic information.
 *
 * @version $$Rev$$ $$Date$$
 */

public class ServletLauncherListener implements ServletContextListener {
    /**
     * Servlet context-param name for user-specified system SCDL path.
     */
    public static final String SYSTEM_SCDL_PATH_PARAM = "systemScdlPath";
    /**
     * Servlet context-param name for user-specified application SCDL path.
     */
    public static final String APPLICATION_SCDL_PATH_PARAM = "applicationScdlPath";
    /**
     * Servlet context-param name for system monitoring level.
     * Supported values are the names of statics defined in java.util.logging.Level.
     * If absent, no monitoring will take place.
     */
    public static final String SYSTEM_MONITORING_PARAM = "tuscanyMonitoringLevel";

    /**
     * Default application SCDL path used if no "applicationScdlPath" param is specified
     */
    public static final String DEFAULT_APPLICATION_SCDL_PATH = "/WEB-INF/default.scdl";

    /**
     * Context attribute to which an Exception or Error object will be bound to if the
     * launcher fails to initialize.
     */
    public static final String LAUNCHER_THROWABLE_ATTRIBUTE = "Tuscany.Launcher.Throwable";

    /**
     * Context attribute to which the active {@link Launcher} managing the runtime for this
     * servlet context is stored.
     */
    private static final String LAUNCHER_ATTRIBUTE = "Tuscany.Launcher";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        // Read optional path to system SCDL from context-param
        String systemScdlPath = servletContext.getInitParameter(SYSTEM_SCDL_PATH_PARAM);
        if (systemScdlPath == null) {
            systemScdlPath = "/" + Launcher.METAINF_SYSTEM_SCDL_PATH;
        }

        // Read optional path to application SCDL from context-param
        String applicationScdlPath = servletContext.getInitParameter(APPLICATION_SCDL_PATH_PARAM);
        if (applicationScdlPath == null) {
            applicationScdlPath = DEFAULT_APPLICATION_SCDL_PATH;
        }

        // Read optional system monitor factory classname
        String systemLogging = servletContext.getInitParameter(SYSTEM_MONITORING_PARAM);
        MonitorFactory mf = getMonitorFactory(systemLogging);

        Launcher launcher = new Launcher();

        // Current thread context classloader should be the webapp classloader
        launcher.setApplicationLoader(Thread.currentThread().getContextClassLoader());

        CompositeComponent<?> component;
        CompositeContextImpl context;

        try {
            URL systemScdl = getClass().getResource(systemScdlPath);
            CompositeComponent<?> rt = launcher.bootRuntime(systemScdl, mf);
            servletContext.setAttribute("Tuscany.SystemComposite", rt);
            servletContext.setAttribute(LAUNCHER_ATTRIBUTE, launcher);

            URL appScdl;
            if (applicationScdlPath.startsWith("/")) {
                // Paths begining w/ "/" are treated as webapp resources
                try {
                    appScdl = servletContext.getResource(applicationScdlPath);
                } catch (MalformedURLException mue) {
                    throw new LoaderException("Unable to find application SCDL: " + applicationScdlPath);
                }
            } else {
                // Other paths are searched using the application classloader
                appScdl = launcher.getApplicationLoader().getResource(applicationScdlPath);
                if (appScdl == null) {
                    throw new LoaderException("Unable to find application SCDL: " + applicationScdlPath);
                }
            }

            String name = servletContext.getServletContextName();
            if (name == null) {
                name = "application";
            }
            component = launcher.bootApplication(name, appScdl);
            component.start();
            context = new CompositeContextImpl(component);
            context.start();

        } catch (Throwable t) {
            servletContext.setAttribute(LAUNCHER_THROWABLE_ATTRIBUTE, t);
            t.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        Launcher launcher = (Launcher) servletContext.getAttribute(LAUNCHER_ATTRIBUTE);

        if (launcher != null) {
            launcher.shutdownRuntime();
        }
    }

    private MonitorFactory getMonitorFactory(String loggingLevel) {
        String factoryName = "org.apache.tuscany.core.monitor.NullMonitorFactory";
        Map<String, Object> props = null;
        if (loggingLevel != null) {
            factoryName = "org.apache.tuscany.core.monitor.JavaLoggingMonitorFactory";
            props = new HashMap<String, Object>();
            Level level = Level.SEVERE;
            try {
                level = Level.parse(loggingLevel);
            } catch (IllegalArgumentException e) {
                // ignore bad loggingLevel
            }
            props.put("bundleName", "SystemMessages");
            props.put("defaultLevel", level);
        }

        return MonitorFactoryUtil.createMonitorFactory(factoryName, props);
    }
}
