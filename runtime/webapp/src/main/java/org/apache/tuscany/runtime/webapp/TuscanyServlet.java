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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;

/**
 * A servlet that locates the ServletHost and forwards requests into the Tuscany runtime.
 * Needs to be added to the webapp web.xml
 */
public class TuscanyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ServletHostImpl servletHost;

    @Override
    public void init(ServletConfig config) {

        ServletContext servletContext = config.getServletContext();
        CompositeComponent systemComposite = (CompositeComponent) servletContext.getAttribute("Tuscany.SystemComposite");
        if (systemComposite == null) {
            Throwable e = (Throwable) servletContext.getAttribute("Tuscany.Launcher.Throwable");
            throw new RuntimeException("SystemComposite not found", e);
        }

        SCAObject o = systemComposite.getChild("ServletHost");
        if (o == null) {
            throw new RuntimeException("ServletHost not found");
        }
        servletHost = (ServletHostImpl) o.getServiceInstance();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        servletHost.handleService(req, res);
    }

}
