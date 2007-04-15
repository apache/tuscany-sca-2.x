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
package org.apache.tuscany.binding.axis2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;

/**
 * This overrides the servlet init of the AxisServlet so Tuscany can use
 * a single Axis2 ConfigurationContext instance shared between AxisServlet 
 * instances for each SCA service with a ws binding. 
 * TODO: need to review if thats really what we want to be doing
 */
public class Axis2ServiceServlet extends AxisServlet {

    private static final long serialVersionUID = 1L;

    private static final ServletConfig DUMMY_CONFIG = createDummyServletConfig();
    
    private boolean inited;

    public void init(ConfigurationContext configContext) {
        this.configContext = configContext;
        try {
            super.init(DUMMY_CONFIG);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * We've setup the Servlet by passing in a ConfigurationContext on our init method 
     * override this method to just return that
     */
    @Override
    protected ConfigurationContext initConfigContext(ServletConfig config) throws ServletException {
        return this.configContext;
    }

    /**
     * The AxisServlet gets NPE during init without a ServletConfig so this is a mocked up one to prevent that.
     */
    private static ServletConfig createDummyServletConfig() {
        ServletConfig sc = new ServletConfig() {

            public String getServletName() {
                return "TuscanyAxis2DummyServlet";
            }

            public ServletContext getServletContext() {
                return new ServletContext() {

                    public ServletContext getContext(String uripath) {
                        return null;
                    }

                    public int getMajorVersion() {
                        return 0;
                    }

                    public int getMinorVersion() {
                        return 0;
                    }

                    public String getMimeType(String file) {
                        return null;
                    }

                    public Set getResourcePaths(String path) {
                        return Collections.emptySet();
                    }

                    public URL getResource(String path) throws MalformedURLException {
                        if("/".equals(path)) {
                            // HACK: To avoid NPE
                            return new URL("/axis2");
                        }
                        return null;
                    }

                    public InputStream getResourceAsStream(String path) {
                        return null;
                    }

                    public RequestDispatcher getRequestDispatcher(String path) {
                        return null;
                    }

                    public RequestDispatcher getNamedDispatcher(String arg0) {
                        return null;
                    }

                    public Servlet getServlet(String arg0) throws ServletException {
                        return null;
                    }

                    public Enumeration getServlets() {
                        return null;
                    }

                    public Enumeration getServletNames() {
                        return null;
                    }

                    public void log(String arg0) {
                    }

                    public void log(Exception arg0, String arg1) {
                    }

                    public void log(String arg0, Throwable arg1) {
                    }

                    public String getRealPath(String arg0) {
                        return null;
                    }

                    public String getServerInfo() {
                        return null;
                    }

                    public String getInitParameter(String arg0) {
                        return null;
                    }

                    public Enumeration getInitParameterNames() {
                        return null;
                    }

                    public Object getAttribute(String arg0) {
                        return null;
                    }

                    public Enumeration getAttributeNames() {
                        return null;
                    }

                    public void setAttribute(String arg0, Object arg1) {
                    }

                    public void removeAttribute(String arg0) {
                    }

                    public String getServletContextName() {
                        return null;
                    }
                };
            }

            public String getInitParameter(String arg0) {
                return null;
            }

            public Enumeration getInitParameterNames() {
                return new Vector().elements();
            }
        };
        return sc;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // HACK: Get the correct context root which is not available during init() call
        if (!inited) {
            synchronized (configContext) {
                configContext.setContextRoot(request.getContextPath());
                inited = true;
            }
        }

        // Create a work context TODO: where should this get done?
        WorkContext workContext = new SimpleWorkContext();
        workContext.setIdentifier(Scope.COMPOSITE, ComponentNames.TUSCANY_APPLICATION_ROOT.resolve("default"));
        WorkContextTunnel.setThreadWorkContext(workContext);
        
        super.service(request, response);
    }

}
