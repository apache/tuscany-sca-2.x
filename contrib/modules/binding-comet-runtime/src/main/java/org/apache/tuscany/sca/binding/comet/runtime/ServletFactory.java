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

package org.apache.tuscany.sca.binding.comet.runtime;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.atmosphere.cpr.AtmosphereServlet;

/**
 * This class is used to create two servlets: one exposing all the comet
 * services, the other one exposing the javascript toolkit. Exposing all comet
 * services through a single servlet is needed as the browsers are undergone by
 * the two http connection limit so all comet services should send their
 * responses via the same http connection to the same client. Dispatching to the
 * corresponding endpoint and operation is done internally using Jersey RESTful
 * Web Services integration with the AtmosphereServlet. The Javascript toolkit
 * servlet is unique as it is not tied to any of the services - it offers a
 * global API.
 */
public final class ServletFactory {

    /**
     * Init-param key for the AtmosphereServlet defining where to look for
     * Jersey classes.
     */
    private static final String PACKAGE_KEY = "com.sun.jersey.config.property.packages";

    /**
     * Package of the class handling dispatching to endpoints.
     */
    private static final String PACKAGE_VALUE = "org.apache.tuscany.sca.binding.comet.runtime.handler";

    /**
     * Package of the class handling Javascript toolkit retrieval.
     */
    private static final String JS_PACKAGE_VALUE = "org.apache.tuscany.sca.binding.comet.runtime.javascript";

    /**
     * Property in the ServletContext where endpoints are added incrementally as
     * the Tuscany runtime calls the CometServiceBindingProvider for each comet
     * service.
     */
    public static final String ENDPOINTS_KEY = "org.apache.tuscany.sca.binding.comet.endpoints";

    /**
     * Property in the ServletContext where operations are added incrementally
     * as the CometServiceBindingProvider is calling the registerServlet method
     * for each comet service method.
     */
    public static final String OPERATIONS_KEY = "org.apache.tuscany.sca.binding.comet.operations";

    /**
     * Path where services will be exposed.
     */
    public static final String PATH = "/tuscany-comet/*";

    /**
     * Path where Javascript toolkit will be exposed.
     */
    public static final String JS_PATH = "/org.apache.tuscany.sca.cometComponentContext.js/*";

    /**
     * The servlet that is exposing the comet services.
     */
    private static AtmosphereServlet cometServlet = null;

    /**
     * The servlet that is exposing the Javascript toolkit.
     */
    private static AtmosphereServlet javascriptServlet = null;

    /**
     * Private constructor for the singleton class.
     */
    private ServletFactory() {
    }

    /**
     * Method called by CometServiceBindingProvider for each endpoint in order
     * to create the two singleton servlets.
     * 
     * @param servletHost the underlying servlet host
     */
    public static synchronized void registerServlet(final ServletHost servletHost) {
        if (ServletFactory.cometServlet == null) {
            ServletFactory.cometServlet = new AtmosphereServlet();
            ServletFactory.cometServlet.addInitParameter(ServletFactory.PACKAGE_KEY, ServletFactory.PACKAGE_VALUE);
            servletHost.addServletMapping(ServletFactory.PATH, ServletFactory.cometServlet);
            // store operations and corresponding endpoint in the ServletContext
            // so that they can be retrieved from inside the web service methods
            final Map<String, RuntimeEndpoint> endpoints = new HashMap<String, RuntimeEndpoint>();
            ServletFactory.cometServlet.getServletContext().setAttribute(ServletFactory.ENDPOINTS_KEY, endpoints);
            final Map<String, Operation> operations = new HashMap<String, Operation>();
            ServletFactory.cometServlet.getServletContext().setAttribute(ServletFactory.OPERATIONS_KEY, operations);
        }
        if (ServletFactory.javascriptServlet == null) {
            ServletFactory.javascriptServlet = new AtmosphereServlet();
            ServletFactory.javascriptServlet.addInitParameter(ServletFactory.PACKAGE_KEY,
                                                              ServletFactory.JS_PACKAGE_VALUE);
            servletHost.addServletMapping(ServletFactory.JS_PATH, ServletFactory.javascriptServlet);
        }
    }

    /**
     * Method called by CometServiceBindingProvider for each endpoint operation
     * in order to store all the operations the servlet will serve.
     * 
     * @param endpoint the endpoint
     * @param operation the operation
     */
    public static synchronized void addOperation(final RuntimeEndpoint endpoint, final Operation operation) {
        final String url = "/" + endpoint.getService().getName() + "/" + operation.getName();
        final Map<String, RuntimeEndpoint> endpoints =
            (Map<String, RuntimeEndpoint>)ServletFactory.cometServlet.getServletContext()
                .getAttribute(ServletFactory.ENDPOINTS_KEY);
        endpoints.put(url, endpoint);
        final Map<String, Operation> operations =
            (Map<String, Operation>)ServletFactory.cometServlet.getServletContext()
                .getAttribute(ServletFactory.OPERATIONS_KEY);
        operations.put(url, operation);
    }

    /**
     * Method called by CometServiceBindingProvider for each endpoint operation
     * in order to remove the two servlets.
     * 
     * @param servletHost the underlying servlet host
     */
    public static synchronized void unregisterServlet(final ServletHost servletHost) {
        servletHost.removeServletMapping(ServletFactory.PATH);
        servletHost.removeServletMapping(ServletFactory.JS_PATH);
    }

}
