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

import java.beans.Beans;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.api.TuscanyRuntimeException;
import static org.apache.tuscany.runtime.webapp.Constants.LAUNCHER_PARAM;

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
 * "bridging" contract, {@link TuscanyWebappRuntime}. The <code>web.xml</code> may also optionally be configured with
 * entries for {@link TuscanyFilter} and {@link TuscanyServlet}. The former must be mapped to all urls that execute
 * "unmanaged" code which accesses the Tuscany runtime though the SCA API, for example, JSPs and Servlets. The latter
 * forwards service requests into the runtime, by default requests sent to URLs relative to the context path beginning
 * with <code>/services</code>.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements ServletContextListener {

    private TuscanyWebappRuntime runtime;

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        try {
            ClassLoader bootClassLoader = getBootClassLoader(servletContext);
            runtime = getRuntime(servletContext, bootClassLoader);
            runtime.initialize(servletContext);
        } catch (IOException e) {
            servletContext.log("Error instantiating Tuscany bootstrap", e);
        } catch (ClassNotFoundException e) {
            servletContext.log("Tuscany bootstrap class not found ", e);
        } catch (TuscanyRuntimeException e) {
            servletContext.log("Error instantiating Tuscany bootstrap", e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (runtime != null) {
            runtime.destroy();
        }
    }

    protected ClassLoader getBootClassLoader(ServletContext servletContext) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String bootDirName = servletContext.getInitParameter(Constants.BOOTDIR_PARAM);
        if (bootDirName == null) {
            bootDirName = "/WEB-INF/tuscany/boot/";
        }
        Set paths = servletContext.getResourcePaths(bootDirName);
        if (paths == null) {
            // nothing in boot directory, assume everything is in the webapp classloader
            return contextClassLoader;
        }
        URL[] urls = new URL[paths.size()];
        int i = 0;
        for (Object path : paths) {
            try {
                urls[i++] = servletContext.getResource((String) path);
            } catch (MalformedURLException e) {
                throw new AssertionError("getResourcePaths returned an invalid path");
            }
        }
        return new URLClassLoader(urls, contextClassLoader);
    }

    protected TuscanyWebappRuntime getRuntime(ServletContext servletContext, ClassLoader bootClassLoader)
        throws IOException, ClassNotFoundException {
        String launcherClass = servletContext.getInitParameter(LAUNCHER_PARAM);
        if (launcherClass == null) {
            launcherClass = "org.apache.tuscany.runtime.webapp.ServletLauncherListener";
        }
        // launch the runtime in a separate classloader to preserve isolation of system artifacts
        return (TuscanyWebappRuntime) Beans.instantiate(bootClassLoader, launcherClass);
    }
}
