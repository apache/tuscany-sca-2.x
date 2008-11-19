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

package org.apache.tuscany.sca.implementation.node.webapp;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A Servlet request dispatcher that can be used to delegate requests to a
 * Serlvet registered with the Webapp Servlet host.
 * 
 * @version $Rev$ $Date$
 */
class NodeWebAppRequestDispatcher implements RequestDispatcher {
    private String servletPath;
    private Servlet servlet;
    
    /**
     * Constructs a new request dispatcher.
     * 
     * @param mapping
     * @param servlet
     */
    public NodeWebAppRequestDispatcher(String mapping, Servlet servlet) {
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
     * Returns a request wrapper which will return the correct Servlet path
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
                if (path.length() == 0) {
                    path = super.getPathInfo();
                }

                // TODO: another context path hack, revisit when context path is sorted out
                path = fiddlePath(path, servletPath);

                return path;
            }
        };
        return requestWrapper;
    }

    /**
     * Remove any path suffix thats part of the Servlet context path.
     * 
     * @param path
     * @param servletPath
     */
    private static String fiddlePath(String path, String servletPath) {
        StringTokenizer st = new StringTokenizer(path, "/");
        String root = "";
        while (st.hasMoreTokens()){
                String s = st.nextToken();
                if (servletPath.endsWith((root + "/" + s))) {
                        root += "/" + s;
                } else {
                        break;
                }
        }
        String fiddlePath = path.substring(root.length());
        return fiddlePath;
    }

    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        servlet.service(createRequestWrapper(request), response);
    }
    
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        servlet.service(createRequestWrapper(request), response);
    }
}
