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
package org.apache.tuscany.runtime.webapp;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;

import org.osoa.sca.SCA;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ComponentDefinition;

import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.launcher.LauncherImpl;
import org.apache.tuscany.core.monitor.MonitorFactoryUtil;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import static org.apache.tuscany.runtime.webapp.Constants.APPLICATION_SCDL_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.CURRENT_COMPOSITE_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.DEFAULT_EXTENSION_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.EXTENSION_SCDL_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_MONITORING_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_SCDL_PATH_PARAM;

/**
 * Bootstrapper for the Tuscany runtime in a web application host. This listener manages one runtime per servlet
 * context; the lifecycle of that runtime corresponds to the the lifecycle of the associated servlet context.
 * <p/>
 * The bootstrapper launches the runtime, booting system extensions and applications, according to the servlet
 * parameters defined in {@link Constants}. When the runtime is instantiated, it is placed in the servlet context with
 * the attribute {@link Constants.RUNTIME_ATTRIBUTE}. The runtime implements {@link TuscanyWebappRuntime} so that
 * filters and servlets loaded in the parent web app classloader may pass events and requests to it.
 * <p/>
 * By default, the top-most application composite component will be returned when "non-managed" web application code
 * such as JSPs call {@link org.osoa.sca.CurrentCompositeContext}. If a composite deeper in the hierarchy should be
 * returned instead, the <code>web.xml</code> must contain an entry for {@link Constants.CURRENT_COMPOSITE_PATH_PARAM}
 * whose value is a component path expression using '/' as a delimeter such as foo/bar/baz.
 *
 * @version $$Rev$$ $$Date$$
 */

public class ServletLauncherListener implements TuscanyWebappRuntime {

    private URL testSystemScdl;
    private CompositeComponent component;
    private ServletLauncherMonitor monitor;
    private LauncherImpl launcher;
    private CompositeContextImpl context;
    private ServletRequestInjector requestInjector;

    public void initialize(ServletContext servletContext) {
        // Read optional path to system SCDL from context-param
        String systemScdlPath = servletContext.getInitParameter(SYSTEM_SCDL_PATH_PARAM);
        if (systemScdlPath == null) {
            systemScdlPath = Constants.WEBAPP_SYSTEM_SCDL_PATH;
        }

        // Read optional path to application SCDL from context-param
        String applicationScdlPath = servletContext.getInitParameter(APPLICATION_SCDL_PATH_PARAM);
        if (applicationScdlPath == null) {
            applicationScdlPath = Constants.DEFAULT_APPLICATION_SCDL_PATH_PARAM;
        }

        // Read optional system monitor factory classname
        String systemLogging = servletContext.getInitParameter(SYSTEM_MONITORING_PARAM);
        MonitorFactory mf = getMonitorFactory(systemLogging);
        monitor = mf.getMonitor(ServletLauncherMonitor.class);

        launcher = new LauncherImpl();

        // Current thread context classloader should be the webapp classloader
        ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
        launcher.setApplicationLoader(webappClassLoader);

        try {
            URL systemScdl = getSystemSCDL(systemScdlPath);
            CompositeComponent rt = launcher.bootRuntime(systemScdl, mf);

            // Read optional path to extension SCDLs from context-param
            String extensionScdlPath = servletContext.getInitParameter(EXTENSION_SCDL_PATH_PARAM);
            if (extensionScdlPath == null) {
                extensionScdlPath = DEFAULT_EXTENSION_PATH_PARAM;
            }

            // load extensions
            Set<String> paths = servletContext.getResourcePaths(extensionScdlPath);
            if (paths != null) {
                for (String path : paths) {
                    monitor.deployExtension(path);
                    deployExtension(rt, path, servletContext.getResource(path));
                }
            }

            // FIXME this is too coupled to the configuration
            SCAObject host = rt.getChild("servletHost");
            if (host == null) {
                MissingResourceException e = new MissingResourceException("ServletHost service not found");
                e.setIdentifier("servletHost");
                throw e;
            }
            // fixme this case is problematic
            requestInjector = (ServletRequestInjector) host.getServiceInstance();

            URL appScdl = getApplicationSCDL(applicationScdlPath, servletContext);

            String name = servletContext.getServletContextName();
            if (name == null) {
                name = "application";
            }

            CompositeComponent root = launcher.bootApplication(name, appScdl);
            String compositePath = servletContext.getInitParameter(CURRENT_COMPOSITE_PATH_PARAM);
            root.start();
            // set the current composite
            setCurrentComposite(compositePath, root);
            context = new CompositeContextImpl(component);
            context.start();
            servletContext.setAttribute(RUNTIME_ATTRIBUTE, this);

        } catch (Exception e) {
            throw new ServletLauncherInitException(e);
        }
    }

    public void destroy() {
        if (launcher != null) {
            launcher.shutdownRuntime();
        }
    }

    public SCA getContext() {
        return context;
    }

    public ServletRequestInjector getRequestInjector() {
        return requestInjector;
    }

    public void sessionCreated(HttpSessionEvent event) {
        component.publish(new HttpSessionStart(this, event.getSession().getId()));
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        component.publish(new HttpSessionEnd(this, event.getSession().getId()));
    }

    public void startRequest() {
        component.publish(new RequestStart(this));
    }

    public void stopRequest() {
        component.publish(new RequestEnd(this));
    }


    /**
     * Sets the system SCDL url for testing
     */
    void setTestSystemScdl(URL testSystemScdl) {
        this.testSystemScdl = testSystemScdl;
    }

    /**
     * Deploys an system extension
     *
     * @param composite     the composite to deploy to
     * @param extensionName the extensionname
     * @param scdlURL       the location of the system SCDL
     * @throws LoaderException
     */
    private void deployExtension(CompositeComponent composite, String extensionName, URL scdlURL)
        throws LoaderException {
        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
        implementation.setScdlLocation(scdlURL);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{scdlURL}, getClass().getClassLoader());
        implementation.setClassLoader(classLoader);

        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(extensionName,
                implementation);

        Deployer deployer = (Deployer) composite.getChild("deployer").getServiceInstance();
        Component component = deployer.deploy(composite, definition);

        component.start();
    }

    /**
     * Returns a monitor factory for the funtime
     *
     * @param loggingLevel
     */
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

    /**
     * Sets the root to point to a composite in the hierarchy specified by the given path
     *
     * @throws InvalidCompositePath
     */
    private void setCurrentComposite(String compositePath, CompositeComponent root) throws InvalidCompositePath {
        if (compositePath != null) {
            StringTokenizer tokens = new StringTokenizer(compositePath, "/");
            CompositeComponent current = root;
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                SCAObject child = current.getChild(token);
                if (child == null) {
                    InvalidCompositePath e = new InvalidCompositePath("Composite not found");
                    e.setIdentifier(token);
                    throw e;
                } else if (!(child instanceof CompositeComponent)) {
                    InvalidCompositePath e = new InvalidCompositePath("Child not a composite");
                    e.setIdentifier(child.getName());
                    throw e;
                }
                current = (CompositeComponent) child;
            }
            component = current;
        } else {
            component = root;
        }
    }

    /**
     * Returns a url to the application SCDL based on the given path
     *
     * @throws MissingResourceException
     */
    private URL getApplicationSCDL(String path, ServletContext context) throws MissingResourceException {
        URL appScdl;
        if (path.startsWith("/")) {
            // Paths begining w/ "/" are treated as webapp resources
            try {
                appScdl = context.getResource(path);
            } catch (MalformedURLException mue) {
                MissingResourceException e = new MissingResourceException("Unable to find application SCDL");
                e.setIdentifier(path);
                throw e;
            }
        } else {
            // Other paths are searched using the application classloader
            appScdl = launcher.getApplicationLoader().getResource(path);
            if (appScdl == null) {
                MissingResourceException e = new MissingResourceException("Unable to find application SCDL");
                e.setIdentifier(path);
                throw e;
            }
        }
        return appScdl;
    }

    /**
     * Returns the system SCDL based on the given path
     *
     * @throws MissingResourceException
     */
    private URL getSystemSCDL(String path) throws MissingResourceException {
        URL systemScdl;
        if (testSystemScdl != null) {
            systemScdl = testSystemScdl;
        } else {
            systemScdl = getClass().getClassLoader().getResource(path);
            if (systemScdl == null) {
                MissingResourceException e = new MissingResourceException("System SCDL not found");
                e.setIdentifier(path);
                throw e;
            }
        }
        return systemScdl;
    }


}
