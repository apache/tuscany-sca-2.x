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

package org.apache.tuscany.sca.host.webapp;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.tuscany.sca.host.http.DefaultResourceServlet;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;

/**
 * ServletHost implementation for use in a webapp environment.
 * 
 * FIXME: using a static singleton seems a big hack but how should it be shared?
 * Need some way for TuscanyServlet to pull it out.
 */
public class WebAppServletHost implements ServletHost {
    private static final Logger logger = Logger.getLogger(WebAppServletHost.class.getName());
    private static WebAppServletHost instance = new WebAppServletHost();

    private Map<String, Servlet> servlets;

    private WebAppServletHost() {
        servlets = new HashMap<String, Servlet>();
    }

    public void addServletMapping(String path, Servlet servlet) throws ServletMappingException {
        URI pathURI = URI.create(path);

        // Ignore registrations of the Tuscany default resource servlet, as resources
        // are already served by the web container
        if (servlet instanceof DefaultResourceServlet) {
            return;
        }

        // Make sure that the path starts with a /
        path = pathURI.getPath();
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        
        // In a webapp just use the given path and ignore the host and port
        // as they are fixed by the Web container
        servlets.put(path, servlet);
        
        logger.info("addServletMapping: " + path);
    }

    public Servlet removeServletMapping(String path) throws ServletMappingException {
        URI pathURI = URI.create(path);

        // Make sure that the path starts with a /
        path = pathURI.getPath();
        if (!path.startsWith("/")) {
            path = '/' + path;
        }

        // In a webapp just use the given path and ignore the host and port
        // as they are fixed by the Web container
        return servlets.remove(path);
    }

    /**
     * A servlet request dispatcher that can be used to dispath requests to a
     * serlvet registered with this host.
     */
    private class MappedRequestDispatcher implements RequestDispatcher {
        private String servletPath;
        private Servlet servlet;
        
        public MappedRequestDispatcher(String mapping, Servlet servlet) {
            if (mapping.endsWith("*")) {
                mapping = mapping.substring(0, mapping.length()-1);
            }
            if (mapping.endsWith("/")) {
                mapping = mapping.substring(0, mapping.length()-1);
            }
            this.servletPath = mapping;
            this.servlet = servlet;
        }

        /**
         * Returns a request wrapper which will return the correct servlet path
         * and path info.
         * 
         * @param request
         * @return
         */
        private HttpServletRequest createRequestWrapper(ServletRequest request) {
            HttpServletRequest requestWrapper = new HttpServletRequestWrapper((HttpServletRequest)request) {
                
                @Override
                public String getServletPath() {
                    return servletPath;
                }
                
                @Override
                public String getPathInfo() {
                    String path = super.getServletPath();
                    path = path.substring(servletPath.length());
                    return path;
                }
            };
            return requestWrapper;
        }
        
        public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            servlet.service(createRequestWrapper(request), response);
        }
        
        public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            servlet.service(createRequestWrapper(request), response);
        }
        
    }

    public RequestDispatcher getRequestDispatcher(String path) throws ServletMappingException {

        // Make sure that the path starts with a /
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        
        // Get the servlet mapped to the given path
        Servlet servlet = servlets.get(path);
        if (servlet != null) {
            return new MappedRequestDispatcher(path, servlet);
        }
        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            String servletPath = entry.getKey();
            if (servletPath.endsWith("*")) {
                servletPath = servletPath.substring(0, servletPath.length() -1);
                if (path.startsWith(servletPath)) {
                    return new MappedRequestDispatcher(entry.getKey(), entry.getValue());
                } else {
                    if ((path + "/").startsWith(servletPath)) {
                        return new MappedRequestDispatcher(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        
        // No servlet found
        return null;
    }
    
    public static WebAppServletHost getInstance() {
        return instance;
    }

    public void init(ServletConfig config) throws ServletException {
        for (Servlet servlet : servlets.values()) {
            servlet.init(config);
        }
    }

}
