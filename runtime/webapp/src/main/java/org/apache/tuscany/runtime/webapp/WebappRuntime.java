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

import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionListener;

import org.osoa.sca.SCA;

import org.apache.tuscany.host.servlet.ServletRequestInjector;

/**
 * The contract for artifacts loaded in the web application classloader to comminicate with the Tuscany runtime loaded
 * in a child classloader. For example, filters and listeners may use this interface to notify the runtime of the web
 * container events.
 *
 * @version $Rev$ $Date$
 * @see TuscanyFilter
 * @see TuscanySessionListener
 */
public interface WebappRuntime extends HttpSessionListener {
    /**
     * Returns the ServletContext associated with this runtime.
     *
     * @return the ServletContext associated with this runtime
     */
    ServletContext getServletContext();

    /**
     * Sets the ServletContext associated with this runtime.
     *
     * @param servletContext the ServletContext associated with this runtime
     */
    void setServletContext(ServletContext servletContext);

    /**
     * Returns the location of the SCDL used to boot this runtime.
     *
     * @return the location of the SCDL used to boot this runtime
     */
    URL getSystemScdl();

    /**
     * Sets the location of the SCDL used to boot this runtime.
     *
     * @param systemScdl the location of the SCDL used to boot this runtime
     */
    void setSystemScdl(URL systemScdl);

    /**
     * Returns the location of the default application's SCDL.
     *
     * @return the location of the default application's SCDL
     */
    URL getApplicationScdl();

    /**
     * Sets the location of the default application's SCDL
     *
     * @param applicationScdl the location of the default application's SCDL
     */
    void setApplicationScdl(URL applicationScdl);

    /**
     * Returns the web application's ClassLoader.
     *
     * @return the web application's ClassLoader
     */
    ClassLoader getWebappClassLoader();

    /**
     * Sets the web application's ClassLoader.
     *
     * @param webappClassLoader the web application's ClassLoader
     */
    void setWebappClassLoader(ClassLoader webappClassLoader);

    /**
     * Initialize a runtime for the supplied servlet context.
     *
     */
    void initialize();

    /**
     * Destroy the runtime.
     * Any further invocations should result in an error.
     */
    void destroy();

    /**
     * Returns the current SCA context
     */
    SCA getContext();

    /**
     * Returns the request injector for the runtime
     */
    ServletRequestInjector getRequestInjector();

    /**
     * Notification that the web application has begun servicing a request
     */
    void startRequest();

    /**
     * Notification that the web application has stopped servicing a request
     */
    void stopRequest();
}
