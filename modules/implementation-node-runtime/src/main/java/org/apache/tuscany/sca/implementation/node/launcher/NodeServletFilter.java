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

package org.apache.tuscany.sca.implementation.node.launcher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A servlet filter that forwards service requests to the servlets registered with
 * the Tuscany ServletHost.
 * 
 * @version $Rev$ $Date$
 */
public class NodeServletFilter implements Filter {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(NodeServletFilter.class.getName());

    private ClassLoader runtimeClassLoader;
    private Class<?> servletHostClass;
    private Object servletHost;
    private Filter filter;

    public void init(final FilterConfig config) throws ServletException {
        logger.info("Apache Tuscany SCA WebApp Node starting...");

        try {
            // Get the Tuscany runtime classloader
            runtimeClassLoader = NodeLauncherUtil.runtimeClassLoader();
    
            // Load the Tuscany WebApp servlet host and get the host instance
            // for the current webapp
            servletHostClass = Class.forName("org.apache.tuscany.sca.implementation.node.webapp.NodeWebAppServletHost", true, runtimeClassLoader);
            servletHost = servletHostClass.getMethod("servletHost").invoke(null);
    
            // Initialize the servlet host
            servletHostClass.getMethod("init", FilterConfig.class).invoke(config);

            // The servlet host also implements the filter interface 
            filter = (Filter)servletHost;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error Starting SCA WebApp Node", e);
            throw new ServletException(e);
        }

        logger.info("SCA WebApp Node started.");
    }

    public void destroy() {
        logger.info("Apache Tuscany WebApp Node stopping...");
        if (servletHost != null) {
            try {
                servletHostClass.getMethod("destroy").invoke(servletHost);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error Stopping SCA WebApp Node", e);
            }
        }
        logger.info("SCA WebApp Node stopped.");
    }

    public void doFilter(ServletRequest request, ServletResponse response, javax.servlet.FilterChain chain)
        throws IOException, ServletException {

        // Delegate to the servlet host filter
        filter.doFilter(request, response, chain);
    }

}
