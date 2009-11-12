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

package org.apache.tuscany.sca.web.javascript.dojo;

import java.net.URI;

import javax.servlet.Servlet;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;

public class DojoModuleActivator implements ModuleActivator {
    private static final String dojoBaseUri = URI.create("/dojo").toString();
    private static final String dojoUri = URI.create("/dojo/*").toString();

    private static final String tuscanyBaseUri = URI.create("/tuscany").toString();
    private static final String tuscanyUri = URI.create("/tuscany/*").toString();

    private ServletHost servletHost;

    public void start(ExtensionPointRegistry registry) {
        ServletHostExtensionPoint servletHosts = registry.getExtensionPoint(ServletHostExtensionPoint.class);
        this.servletHost = servletHosts.getServletHosts().get(0);
        
        if (servletHost == null) {
            throw new IllegalStateException("Can't find ServletHost reference !");
        }

        Servlet servlet = null;
        
        servlet = servletHost.getServletMapping(dojoBaseUri);
        if(servlet == null) {
            DojoResourceServlet baseResourceServlet = new DojoResourceServlet(); 
            servletHost.addServletMapping(dojoBaseUri, baseResourceServlet);

            DojoResourceServlet resourceServlet = new DojoResourceServlet(); 
            servletHost.addServletMapping(dojoUri, resourceServlet);
        }

        servlet = servletHost.getServletMapping(tuscanyBaseUri);
        if(servlet == null) {
            DojoResourceServlet baseResourceServlet = new DojoResourceServlet(); 
            servletHost.addServletMapping(tuscanyBaseUri, baseResourceServlet);

            DojoResourceServlet resourceServlet = new DojoResourceServlet(); 
            servletHost.addServletMapping(tuscanyUri, resourceServlet);
        }
        
    }

    public void stop(ExtensionPointRegistry registry) {
        Servlet servlet = servletHost.getServletMapping(dojoBaseUri);
        if(servlet != null) {
            servletHost.removeServletMapping(dojoBaseUri);
            servletHost.removeServletMapping(dojoUri);

            servletHost.removeServletMapping(tuscanyBaseUri);
            servletHost.removeServletMapping(tuscanyUri);
        }
        
        servletHost = null;

    }

}
