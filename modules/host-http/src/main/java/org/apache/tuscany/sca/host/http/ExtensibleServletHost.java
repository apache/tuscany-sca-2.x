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

package org.apache.tuscany.sca.host.http;

import java.net.URL;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;

/**
 * Default implementation of an extensible Servlet host.
 * 
 * @version $Rev$ $Date$
 */
public class ExtensibleServletHost implements ServletHost {
    
    private ServletHostExtensionPoint servletHosts;

    public ExtensibleServletHost(ExtensionPointRegistry registry) {
        this.servletHosts = registry.getExtensionPoint(ServletHostExtensionPoint.class);
    }
    
    public static ExtensibleServletHost getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilityExtensionPoint.getUtility(ExtensibleServletHost.class);
    }

    public ExtensibleServletHost(ServletHostExtensionPoint servletHosts) {
        this.servletHosts = servletHosts;
    }
    
    public void setDefaultPort(int port) {
        for (ServletHost servletHost: servletHosts.getServletHosts()) {
            servletHost.setDefaultPort(port);
        }
    }
    
    public ServletHost getDefaultServletHost() {
        List<ServletHost> hosts = servletHosts.getServletHosts();
        if (hosts.isEmpty()) {
            throw new ServletMappingException("No servlet host is available.");
        }
        if (servletHosts.isWebApp()) {
            for (ServletHost servletHost : hosts) {
                if (!"webapp".equals(servletHost.getName())) {
                    continue;
                }
                if (servletHost instanceof DefaultServletHostExtensionPoint.LazyServletHost) {
                    return ((DefaultServletHostExtensionPoint.LazyServletHost)servletHost).getServletHost();
                } else {
                    return servletHost;
                }
            }
        }
        return hosts.get(0);
    }
    
    public int getDefaultPort() {
        return getDefaultServletHost().getDefaultPort();
    }

    public String addServletMapping(String uri, Servlet servlet) throws ServletMappingException {
        // TODO implement selection of the correct Servlet host based on the mapping
        // For now just select the first one
        return getDefaultServletHost().addServletMapping(uri, servlet);
    }
    
    public String addServletMapping(String uri, Servlet servlet, SecurityContext securityContext) throws ServletMappingException {
        // TODO implement selection of the correct Servlet host based on the mapping
        // For now just select the first one
        return getDefaultServletHost().addServletMapping(uri, servlet, securityContext);
    }    

    public Servlet getServletMapping(String uri) throws ServletMappingException {
        // TODO implement selection of the correct Servlet host based on the mapping
        // For now just select the first one
        return getDefaultServletHost().getServletMapping(uri);
    }
    
    public Servlet removeServletMapping(String uri) throws ServletMappingException {
        // TODO implement selection of the correct Servlet host based on the mapping
        // For now just select the first one
        return getDefaultServletHost().removeServletMapping(uri);
    }
    
    public RequestDispatcher getRequestDispatcher(String uri) throws ServletMappingException {
        // TODO implement selection of the correct Servlet host based on the mapping
        // For now just select the first one
        return getDefaultServletHost().getRequestDispatcher(uri);
    }

    public String getContextPath() {
        // TODO implement selection of the correct Servlet host based on the mapping
        // For now just select the first one
        return getDefaultServletHost().getContextPath();
    }
    
    public URL getURLMapping(String uri, SecurityContext securityContext) {
        return getDefaultServletHost().getURLMapping(uri, securityContext);
    }

    public void setContextPath(String path) {
        getDefaultServletHost().setContextPath(path);
    }

    public void setAttribute(String name, Object value) {
        getDefaultServletHost().setAttribute(name, value);
    }
    
    public String getName() {
        return getDefaultServletHost().getName();
    }
}
