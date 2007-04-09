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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;

import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

/**
 * Maps an incoming request and the current composite context to the composite component for the web application. This
 * filter must be applied to all web application urls that execute unmanaged code (e.g. JSPs and Servlets) which
 * accesses the Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyFilter implements Filter {
    private WebappRuntime runtime;

    public void init(FilterConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        runtime = (WebappRuntime) servletContext.getAttribute(RUNTIME_ATTRIBUTE);
        if (runtime == null) {
            throw new ServletException("Tuscany is not configured for the web application");
        }
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
        throws IOException, ServletException {
        try {
            runtime.startRequest();
            filterChain.doFilter(req, resp);
        } finally {
            runtime.stopRequest();
        }
    }

    public void destroy() {
        runtime = null;
    }
}
