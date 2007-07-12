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

package org.apache.tuscany.sca.webapp;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.tuscany.sca.host.embedded.impl.HotUpdatableSCADomain;

/**
 * A WebApp ContextListener that starts a Tuscany runtime supporting multiple
 * SCA contribution jars. All contribution jars found in the repository
 * directory named "sca-contributions" will be contributed to the SCA 
 * domain. Any changes to the contributions in the repository will be 
 * automatically detected and the sca domain updated accordingly.
 */
public class HotUpdateContextListener extends TuscanyContextListener {

    protected static final String REPOSITORY_FOLDER_NAME = "sca-contributions";
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        
        // TODO better domaiURI, maybe based on webapp name?
        String domainURI = "http://localhost/" + servletContext.getServletContextName().replace(' ', '.');

        File repository = new File(servletContext.getRealPath(REPOSITORY_FOLDER_NAME));
        HotUpdatableSCADomain scaDomain = new HotUpdatableSCADomain(domainURI, repository, 2000);        

        servletContext.setAttribute(SCADomainHelper.SCA_DOMAIN_ATTRIBUTE, scaDomain);

        super.contextInitialized(event);
    }
    
}
