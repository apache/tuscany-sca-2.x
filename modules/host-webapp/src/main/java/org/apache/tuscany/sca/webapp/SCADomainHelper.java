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
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Utility class to initialize the SCADomian associated with a webapp
 */
public class SCADomainHelper {

    public static final String SCA_DOMAIN_ATTRIBUTE = "org.apache.tuscany.sca.SCADomain";

    /**
     * Initializes the SCADomian associated with a webapp context. If a SCADomain
     * doesn't exist already then one is create based on the webapp config.
     */
    public static SCADomain initSCADomain(ServletContext servletContext) {
        SCADomain scaDomain = (SCADomain)servletContext.getAttribute(SCA_DOMAIN_ATTRIBUTE);
        
        String domainURI = "http://localhost/" + servletContext.getServletContextName().replace(' ', '.');
        String contributionRoot = null;
        
        try {
            URL rootURL = servletContext.getResource("/");
            if (rootURL.getProtocol().equals("jndi")) {
                //this is tomcat case, we should use getRealPath
                File warRootFile = new File(servletContext.getRealPath("/"));
                contributionRoot = warRootFile.toURL().toString();
            } else {
                //this is jetty case
                contributionRoot  = rootURL.toString();
            }
        } catch(MalformedURLException mf) {
            //ignore, pass null
        }
                
        
        if (scaDomain == null) {
            scaDomain = SCADomain.newInstance(domainURI, contributionRoot);
            servletContext.setAttribute(SCA_DOMAIN_ATTRIBUTE, scaDomain);
        }
        return scaDomain;
    }
}
