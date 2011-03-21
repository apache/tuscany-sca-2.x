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

package org.apache.tuscany.sca.implementation.spring.webapp;

import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.spring.context.SpringApplicationContextAccessor;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.FrameworkServlet;

public class SpringWebApplicationContextAccessor implements SpringApplicationContextAccessor {
    private static Logger log = Logger.getLogger(SpringWebApplicationContextAccessor.class.getName());

    public SpringWebApplicationContextAccessor(ExtensionPointRegistry registry) {
        super();
    }

    public ApplicationContext getParentApplicationContext(RuntimeComponent component) {
        ApplicationContext context = null;
        Servlet servlet = component.getComponentContext().getCompositeContext().getAttribute(Servlet.class.getName());
        if (servlet instanceof FrameworkServlet) {
            FrameworkServlet frameworkServlet = (FrameworkServlet)servlet;
            context = frameworkServlet.getWebApplicationContext();
            if (context != null) {
                log.info("Spring application context is found for servlet: " + frameworkServlet.getServletName());
                return context;
            }
        }
        ServletContext servletContext =
            component.getComponentContext().getCompositeContext().getAttribute(ServletContext.class.getName());
        if (servletContext != null) {
            context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            if (context == null) {
                context = ApplicationContextAccessorBean.getInstance().getApplicationContext();
            }
        }

        return context;
    }

}
