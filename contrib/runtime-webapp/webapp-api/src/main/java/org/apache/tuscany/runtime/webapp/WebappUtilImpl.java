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

import static org.apache.tuscany.runtime.webapp.Constants.APPLICATION_SCDL_PATH_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.APPLICATION_SCDL_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.BOOTDIR_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.BOOTDIR_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_SCDL_PATH_DEFAULT;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_SCDL_PATH_PARAM;

/**
 * @version $Rev$ $Date$
 */
public class WebappUtilImpl implements WebappUtil {
    private final ServletContext servletContext;

    public WebappUtilImpl(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public WebappRuntime getRuntime(ClassLoader bootClassLoader) throws TuscanyInitException {
        try {
            String className = getInitParameter(RUNTIME_PARAM, RUNTIME_DEFAULT);
            return (WebappRuntime) bootClassLoader.loadClass(className).newInstance();
        } catch (InstantiationException e) {
            throw new TuscanyInitException(e);
        } catch (IllegalAccessException e) {
            throw new TuscanyInitException(e);
        } catch (ClassNotFoundException e) {
            throw new TuscanyInitException("Runtime Implementation not found", e);
        }
    }

    public ClassLoader getBootClassLoader(ClassLoader webappClassLoader) throws InvalidResourcePath {
        String bootDirName = getInitParameter(BOOTDIR_PARAM, BOOTDIR_DEFAULT);
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
                throw new InvalidResourcePath(APPLICATION_SCDL_PATH_PARAM, path.toString(),  e);
            }
        }
        return new URLClassLoader(urls, webappClassLoader);
    }

    public URL getSystemScdl(ClassLoader bootClassLoader) throws InvalidResourcePath {
        String path = getInitParameter(SYSTEM_SCDL_PATH_PARAM, SYSTEM_SCDL_PATH_DEFAULT);
        try {
            return getScdlURL(path, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new InvalidResourcePath(SYSTEM_SCDL_PATH_PARAM, path, e);
        }
    }

    public String getApplicationName() {
        String name = servletContext.getServletContextName();
        if (name == null) {
            name = "application";
        }
        return name;
    }

    public URL getApplicationScdl(ClassLoader bootClassLoader) throws InvalidResourcePath {
        String path = getInitParameter(APPLICATION_SCDL_PATH_PARAM, APPLICATION_SCDL_PATH_DEFAULT);
        try {
            return getScdlURL(path, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new InvalidResourcePath(APPLICATION_SCDL_PATH_PARAM, path, e);
        }
    }

    public URL getScdlURL(String path, ClassLoader classLoader) throws MalformedURLException {
        URL ret = null;
        if (path.charAt(0) == '/') {
            // user supplied an absolute path - look up as a webapp resource
            ret = servletContext.getResource(path);
        }
        if (ret == null) {
            // user supplied a relative path - look up as a boot classpath resource
            ret = classLoader.getResource(path);
        }
        return ret;
    }

    public String getInitParameter(String name, String value) {
        String result = servletContext.getInitParameter(name);
        if (result != null && result.length() != 0) {
            return result;
        }
        return value;
    }
}
