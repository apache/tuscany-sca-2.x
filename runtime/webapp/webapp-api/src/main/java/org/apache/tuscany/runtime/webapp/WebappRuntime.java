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

import java.net.URI;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionListener;

import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
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
public interface WebappRuntime extends HttpSessionListener, TuscanyRuntime<WebappRuntimeInfo> {
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
     * Returns the request injector for the runtime
     * @return the runtime's request injector
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

    /**
     * Request has been started for the given request.
     * @param request the servlet request
     */
    void httpRequestStarted(HttpServletRequest request);

    /**
     * Request has been ended with the given session id.
     * @param id the session id
     */
    void httpRequestEnded(Object id);

    /**
     * Temporary method for deploying SCDL supplied with an application to the runtime.
     *
     * @param compositeId the id of the component that the supplied SCDL should implement
     * @param applicationScdl the location of an application composite
     * @param componentId the id of the component that should be bound to the webapp
     * @throws InitializationException if there was a problem initializing the composite
     */
    @Deprecated
    void deploy(URI compositeId, URL applicationScdl, URI componentId) throws InitializationException;
}
