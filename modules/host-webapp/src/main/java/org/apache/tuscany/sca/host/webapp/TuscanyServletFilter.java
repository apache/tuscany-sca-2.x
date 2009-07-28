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

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.host.http.ServletHost;


/**
 * A Servlet filter that forwards service requests to the Servlets registered with
 * the Tuscany ServletHost.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyServletFilter implements Filter {
    private static final long serialVersionUID = 1L;
    
    private transient ServletContext context;
    private transient ServletHost servletHost;

    public void init(final FilterConfig config) throws ServletException {
        context = config.getServletContext();
        servletHost = ServletHostHelper.init(context);
    }

    public void destroy() {
        ServletHostHelper.stop(context);
        servletHost = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, javax.servlet.FilterChain chain)
        throws IOException, ServletException {

        // Get the Servlet path
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String path = httpRequest.getPathInfo();
        if (path == null) {
            path = httpRequest.getServletPath();
        }
        if (path == null) {
            path = "/";
        }

        // Get a request dispatcher for the Servlet mapped to that path
        RequestDispatcher dispatcher = servletHost.getRequestDispatcher(path);
        if (dispatcher != null) {

            // Let the dispatcher forward the request to the Servlet 
            dispatcher.forward(request, response);

        } else {

            // Proceed down the filter chain
            chain.doFilter(request, response);

        }
    }

}
