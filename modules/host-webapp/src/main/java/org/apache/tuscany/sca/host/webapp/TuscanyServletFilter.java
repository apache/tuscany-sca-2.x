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
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * A servlet filter that forwards service requests to the servlets registered with
 * the Tuscany ServletHost.
 */
public class TuscanyServletFilter implements Filter {
    private static final long serialVersionUID = 1L;

    private WebAppServletHost servletHost;
    private ServletContext servletContext;

    public void init(final FilterConfig config) throws ServletException {
        servletContext = config.getServletContext();
        SCADomainHelper.initSCADomain(servletContext);

        // TODO: must be a better way to get this than using a static
        servletHost = WebAppServletHost.getInstance();
        servletHost.init(new ServletConfig() {
            public String getInitParameter(String name) {
                return config.getInitParameter(name);
            }
            public Enumeration getInitParameterNames() {
                return config.getInitParameterNames();
            }
            public ServletContext getServletContext() {
                return config.getServletContext();
            }
            public String getServletName() {
                return config.getFilterName();
            }
        });
    }
    
    public void destroy() {
        SCADomain scaDomain = (SCADomain) servletContext.getAttribute(SCADomainHelper.SCA_DOMAIN_ATTRIBUTE);
        if (scaDomain != null) {
            scaDomain.close();
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, javax.servlet.FilterChain chain) throws IOException ,ServletException {

        // Get the servlet path
        String path = ((HttpServletRequest)request).getServletPath();
        if (path == null) {
            path = "/";
        }
        
        // Get a request dispatcher for the servlet mapped to that path
        RequestDispatcher dispatcher = servletHost.getRequestDispatcher(path);

        if (dispatcher != null) {

            // Let the dispatcher forward the request to the servlet 
            dispatcher.forward(request, response);
            
        } else {
            
            // Proceed
            chain.doFilter(request, response);
            
        }
    }
}
