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
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.api.TuscanyRuntimeException;
import static org.apache.tuscany.runtime.webapp.Constants.APPLICATION_SCDL_PATH_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.APPLICATION_SCDL_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.BOOTDIR_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.BOOTDIR_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_SCDL_PATH_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_SCDL_PATH_PARAM;

/**
 * Launches a Tuscany runtime in a web application, loading information from servlet context parameters. This listener
 * manages one runtime per servlet context; the lifecycle of that runtime corresponds to the the lifecycle of the
 * associated servlet context.
 * <p/>
 * The runtime is launched in a child classloader of the web application, thereby providing isolation between
 * application and system artifacts. Application code only has access to the SCA API and may not reference Tuscany
 * system artifacts directly.
 * <p/>
 * The <code>web.xml</code> of a web application embedding Tuscany must have entries for this listener and {@link
 * TuscanySessionListener}. The latter notifies the runtime of session creation and expiration events through a
 * "bridging" contract, {@link WebappRuntime}. The <code>web.xml</code> may also optionally be configured with
 * entries for {@link TuscanyFilter} and {@link TuscanyServlet}. The former must be mapped to all urls that execute
 * "unmanaged" code which accesses the Tuscany runtime though the SCA API, for example, JSPs and Servlets. The latter
 * forwards service requests into the runtime, by default requests sent to URLs relative to the context path beginning
 * with <code>/services</code>.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements ServletContextListener {

    private WebappRuntime runtime;

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        try {
            ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader bootClassLoader = getBootClassLoader(servletContext, webappClassLoader);
            runtime = getRuntime(servletContext, bootClassLoader);
            URL systemScdl = getSystemScdl(servletContext, bootClassLoader);
            URL applicationScdl = getApplicationScdl(servletContext, webappClassLoader);

            runtime.setServletContext(servletContext);
            runtime.setHostClassLoader(webappClassLoader);
            runtime.setSystemScdl(systemScdl);
            runtime.setApplicationScdl(applicationScdl);
            runtime.initialize();
        } catch (TuscanyRuntimeException e) {
            servletContext.log(e.getMessage(), e);
            throw e;
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (runtime != null) {
            runtime.destroy();
        }
    }

    /**
     * Return the classloader that should be used to boot the Tuscany runtime.
     * This will be a child of the web application's ClassLoader.
     *
     * @param servletContext    the servlet context for the webapp containing the bootstrap classes
     * @param webappClassLoader the web application's classloader
     * @return a classloader that can be used to load the Tuscany runtime classes
     */
    protected ClassLoader getBootClassLoader(ServletContext servletContext, ClassLoader webappClassLoader) {
        String bootDirName = getInitParameter(servletContext, BOOTDIR_PARAM, BOOTDIR_DEFAULT);
        Set paths = servletContext.getResourcePaths(bootDirName);
        if (paths == null) {
            // nothing in boot directory, assume everything is in the webapp classloader
            return webappClassLoader;
        }
        URL[] urls = new URL[paths.size()];
        int i = 0;
        for (Object path : paths) {
            try {
                urls[i++] = servletContext.getResource((String) path);
            } catch (MalformedURLException e) {
                throw new AssertionError("getResourcePaths returned an invalid path: " + path);
            }
        }
        return new URLClassLoader(urls, webappClassLoader);
    }

    protected WebappRuntime getRuntime(ServletContext servletContext, ClassLoader bootClassLoader) {
        try {
            String className = getInitParameter(servletContext, RUNTIME_PARAM, RUNTIME_DEFAULT);
            Class<?> runtimeClass = bootClassLoader.loadClass(className);
            return (WebappRuntime) runtimeClass.newInstance();
        } catch (InstantiationException e) {
            throw new TuscanyInitException("Invalid runtime class", e);
        } catch (IllegalAccessException e) {
            throw new TuscanyInitException("Invalid runtime class", e);
        } catch (ClassNotFoundException e) {
            throw new TuscanyInitException("Runtime Implementation not found", e);
        }
    }

    protected URL getSystemScdl(ServletContext servletContext, ClassLoader bootClassLoader) {
        String path = getInitParameter(servletContext, SYSTEM_SCDL_PATH_PARAM, SYSTEM_SCDL_PATH_DEFAULT);
        try {
            return getScdlURL(path, servletContext, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new TuscanyInitException("Invalid resource path for " + SYSTEM_SCDL_PATH_PARAM + " : " + path, e);
        }
    }

    protected URL getApplicationScdl(ServletContext servletContext, ClassLoader bootClassLoader) {
        String path = getInitParameter(servletContext, APPLICATION_SCDL_PATH_PARAM, APPLICATION_SCDL_PATH_DEFAULT);
        try {
            return getScdlURL(path, servletContext, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new TuscanyInitException("Invalid resource path for " + APPLICATION_SCDL_PATH_PARAM + " : " + path,
                                           e);
        }
    }

    protected URL getScdlURL(String path, ServletContext servletContext, ClassLoader classLoader)
        throws MalformedURLException {
        if (path.charAt(0) == '/') {
            // user supplied an absolute path - look up as a webapp resource
            return servletContext.getResource(path);
        } else {
            // user supplied a relative path - look up as a boot classpath resource
            return classLoader.getResource(path);
        }
    }

    /**
     * Return a init parameter from the servlet context or provide a default.
     *
     * @param servletContext the servlet context for the application
     * @param name           the name of the parameter
     * @param value          the default value
     * @return the value of the specified parameter, or the default if not defined
     */
    protected String getInitParameter(ServletContext servletContext, String name, String value) {
        String result = servletContext.getInitParameter(name);
        if (result != null && result.length() != 0) {
            return result;
        }
        return value;
    }
}
