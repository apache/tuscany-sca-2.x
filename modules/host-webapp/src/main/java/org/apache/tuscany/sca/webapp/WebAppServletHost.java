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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.http.DefaultResourceServlet;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.http.ServletMappingException;

/**
 * ServletHost impl singleton thats shared between the SCADomain
 * instance and the TuscanyServlet instance.
 * TODO: using a static singleton seems a big hack but how 
 *       should it be shared? Need some way for TuscanyServlet
 *       to pull it out of the SCADomain instance.
 *       
 */
public class WebAppServletHost implements ServletHost {

    private static WebAppServletHost instance = new WebAppServletHost();

    private Map<String, Servlet> servlets;

    private WebAppServletHost() {
        servlets = new HashMap<String, Servlet>();
    }

    public void addServletMapping(String path, Servlet servlet) throws ServletMappingException {
        URI pathURI = URI.create(path);
        
        // Ignore registrations of our default resource servlet, as resources
        // are already served by the web container
        if (servlet instanceof DefaultResourceServlet) {
            //TODO maybe ignore registration of the servlet only if it's
            // mapped to "/" and still honor other registrations, to do this
            // we will need a way to determine what's the default servlet
            // in the web container that we are running in.
            return;
        }
            
        // For webapps just use the path and ignore the host and port
        servlets.put(pathURI.getPath(), servlet);
        System.out.println("addServletMapping: " + pathURI.getPath());
    }

    public Servlet removeServletMapping(String path) throws ServletMappingException {
        URI pathURI = URI.create(path);
        // for webapps just use the path and ignore the host and port
        return servlets.remove(pathURI.getPath());
    }

    public Servlet getServlet(String path) {
        Servlet servlet = servlets.get(path);
        if (servlet != null) {
            return servlet;
        }
        for (String servletPath : servlets.keySet()) {
            if (servletPath.endsWith("*")) {
                if (path.startsWith(servletPath.substring(0, servletPath.length()-1))) { 
                    return servlets.get(servletPath);
                }
            }
        }
        return null;
    }

    public static WebAppServletHost getInstance() {
        return instance;
    }
    
    public void init(ServletConfig config) throws ServletException {
        for (Servlet servlet : servlets.values()) {
            servlet.init(config);
        }
    }

}
