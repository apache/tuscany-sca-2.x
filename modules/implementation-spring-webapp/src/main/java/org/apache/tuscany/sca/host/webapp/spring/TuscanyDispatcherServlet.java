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

package org.apache.tuscany.sca.host.webapp.spring;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.host.webapp.WebAppHelper;
import org.apache.tuscany.sca.host.webapp.WebContextConfigurator;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * A Servlet that provides a hook to control the lifecycle of Tuscany node and extend Spring Web MVC's DispatcherServlet
 *
 * @version $Rev$ $Date$
 */
public class TuscanyDispatcherServlet extends DispatcherServlet {
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(TuscanyDispatcherServlet.class.getName());

    private transient WebContextConfigurator configurator;

    public TuscanyDispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            configurator = WebAppHelper.getConfigurator(this);
            WebAppHelper.init(configurator);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            configurator.getServletContext().log(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    public void destroy() {
        WebAppHelper.stop(configurator);
        super.destroy();
    }

}
