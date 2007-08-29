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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * A ServletContextListener to create and close the SCADomain
 * when the webapp is initialized or destroyed.
 * 
 * @deprecated Not needed anymore, TuscanyServletFilter is sufficient
 */
@Deprecated
public class TuscanyContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        try {
            SCADomainHelper.initSCADomain(servletContext);
        } catch (Throwable e) {
            servletContext.log("exception initializing SCADomain", e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        SCADomain scaDomain = (SCADomain) servletContext.getAttribute(SCADomainHelper.SCA_DOMAIN_ATTRIBUTE);
        if (scaDomain != null) {
            scaDomain.close();
        }
    }

}
