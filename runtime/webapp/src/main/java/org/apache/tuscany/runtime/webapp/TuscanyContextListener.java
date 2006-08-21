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

/**
 * Launcher for runtime environment that loads info from servlet context params. This listener manages one top-level
 * Launcher (and hence one Tuscany runtime context) per servlet context; the lifecycle of that runtime corresponds to
 * the the lifecycle of the associated servlet context.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements ServletContextListener {
    /**
     * Name of the context parameter that defines the directory containing bootstrap jars.
     */
    public static final String BOOTDIR_PARAM = "tuscany.bootDir";

    /**
     * Name of the class to load to launch the runtime.
     */
    public static final String LAUNCHER_PARAM = "tuscany.launcherClass";

    private ServletContextListener runtime;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ClassLoader bootClassLoader = getBootClassLoader(servletContext);

        runtime = getLauncher(servletContext, bootClassLoader);

        runtime.contextInitialized(servletContextEvent);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (runtime != null) {
            runtime.contextDestroyed(servletContextEvent);
        }
    }

    protected ClassLoader getBootClassLoader(ServletContext servletContext) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String bootDirName = servletContext.getInitParameter(BOOTDIR_PARAM);
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

    protected ServletContextListener getLauncher(ServletContext servletContext, ClassLoader bootClassLoader) {
        String launcherClass = servletContext.getInitParameter(LAUNCHER_PARAM);
        if (launcherClass == null) {
            launcherClass = "org.apache.tuscany.core.launcher.ServletLauncherListener";
        }

        try {
            return (ServletContextListener) Beans.instantiate(bootClassLoader, launcherClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
