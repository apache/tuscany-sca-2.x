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

package org.apache.tuscany.sca.node.launcher;

import static org.apache.tuscany.sca.node.launcher.NodeLauncherUtil.webAppRuntimeClassLoader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A Servlet filter that forwards service requests to the Servlets registered with
 * the Tuscany ServletHost.
 * 
 * @version $Rev$ $Date$
 */
public class NodeServletFilter implements Filter {
    private static final long serialVersionUID = 1L;

    private static final String NODE_WEB_APP_SERVLET_HOST = "org.apache.tuscany.sca.implementation.node.webapp.NodeWebAppServletHost";

    private static final Logger logger = Logger.getLogger(NodeServletFilter.class.getName());

    private ClassLoader runtimeClassLoader;
    private Class<?> servletHostClass;
    private Object servletHost;
    private Filter filter;

    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Apache Tuscany SCA WebApp Node is starting...");

        try {
            // Get the Tuscany runtime ClassLoader
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            runtimeClassLoader = webAppRuntimeClassLoader(getClass().getClassLoader());
            
            try {
                if (runtimeClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                }
        
                // Load the Tuscany WebApp Servlet host and get the host instance
                // for the current webapp
                String className = NODE_WEB_APP_SERVLET_HOST; 
                if (runtimeClassLoader != null) {
                    servletHostClass = Class.forName(className, true, runtimeClassLoader);
                } else {
                    servletHostClass = Class.forName(className);
                }
                servletHost = servletHostClass.getMethod("servletHost").invoke(null);
        
                // Initialize the Servlet host
                servletHostClass.getMethod("init", FilterConfig.class).invoke(servletHost, filterConfig);
    
                // The Servlet host also implements the filter interface 
                filter = (Filter)servletHost;
                
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error Starting SCA WebApp Node", e);
            throw new ServletException(e);
        }

        logger.info("SCA WebApp Node started.");
    }

    public void destroy() {
        logger.info("Apache Tuscany WebApp Node stopping...");
        if (servletHost != null) {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                if (runtimeClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                }
                
                servletHostClass.getMethod("destroy").invoke(servletHost);
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error Stopping SCA WebApp Node", e);
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
        logger.info("SCA WebApp Node stopped.");
    }

    public void doFilter(ServletRequest request, ServletResponse response, javax.servlet.FilterChain chain)
        throws IOException, ServletException {

        // Delegate to the Servlet host filter
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            if (runtimeClassLoader != null) {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
            }
            
            filter.doFilter(request, response, chain);
            
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

}
