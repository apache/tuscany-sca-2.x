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

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * A ServletContextListener to create and close the SCADomain
 * when the webapp is initialized or destroyed.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        final ServletContext servletContext = event.getServletContext();
        try {
            WebAppServletHost.getInstance().init(new ServletConfig() {
                public String getInitParameter(String name) {
                    return servletContext.getInitParameter(name);
                }

                public Enumeration getInitParameterNames() {
                    return servletContext.getInitParameterNames();
                }

                public ServletContext getServletContext() {
                    return servletContext;
                }

                public String getServletName() {
                    return null;
                }
            });
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        SCADomain scaDomain = (SCADomain) servletContext.getAttribute(WebAppServletHost.SCA_DOMAIN_ATTRIBUTE);
        if (scaDomain != null) {
            scaDomain.close();
        }
    }

}
