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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.tuscany.host.servlet.ServletRequestInjector;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

/**
 * A servlet that forwards requests intended for SCA services into the Tuscany runtime via a ServletRequestInjector.
 * This servlet is typically mapped to relative paths beginning with <code>/services</code> in the <code>web.xml</code>
 * <p/>
 * TODO a better URL mapping scheme out to be implemented that corresponds to the SCA specification
 */
public class TuscanyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ServletRequestInjector requestInjector;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        WebappRuntime runtime = (WebappRuntime) servletContext.getAttribute(RUNTIME_ATTRIBUTE);
        if (runtime == null) {
            throw new ServletException("Tuscany runtime not configured for web application");
        }
        requestInjector = runtime.getRequestInjector();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        requestInjector.service(req, res);
    }

}
