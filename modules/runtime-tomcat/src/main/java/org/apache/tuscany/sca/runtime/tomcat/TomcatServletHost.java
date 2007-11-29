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

package org.apache.tuscany.sca.runtime.tomcat;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;

import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;

/**
 */
public class TomcatServletHost implements ServletHost {
    private static final Logger logger = Logger.getLogger(TomcatServletHost.class.getName());
    
    private static final TomcatServletHost instance = new TomcatServletHost();

    private String contextPath = "/tuscany";
    
    private int defaultPortNumber = 8080;
    
    protected TuscanyHost tuscanyHost;

    private TomcatServletHost() {
    }
    
    public void setDefaultPort(int port) {
        defaultPortNumber = port;
    }
    
    public int getDefaultPort() {
        return defaultPortNumber;
    }

    public void addServletMapping(String suri, Servlet servlet) throws ServletMappingException {
        suri = patchURI(suri);
        tuscanyHost.registerMapping(suri, servlet);
        logger.info("Added Servlet mapping: " + suri);
    }

    public Servlet removeServletMapping(String suri) throws ServletMappingException {
        suri = patchURI(suri);
        Servlet servlet = tuscanyHost.unregisterMapping(suri);
        logger.info("removed Servlet mapping: " + suri);
        return servlet;
    }

    private String patchURI(String suri) {
        URI pathURI = URI.create(suri);

        // Make sure that the path starts with a /
        suri = pathURI.getPath();
        if (!suri.startsWith("/")) {
            suri = '/' + suri;
        }

        if (!suri.startsWith("/tuscany")) {
            suri = "/tuscany" + suri;
        }
        return suri;
    }

    public Servlet getServletMapping(String suri) throws ServletMappingException {
//        if (!suri.startsWith("/")) {
//            suri = '/' + suri;
//        }
//        
//        // Get the servlet mapped to the given path
//        Servlet servlet = servlets.get(suri);
//        return servlet;
        return null;
    }

    public URL getURLMapping(String suri) throws ServletMappingException {
        URI uri = URI.create(suri);

        // Get the URI scheme and port
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        }
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = defaultPortNumber;
        }
        
        // Get the host
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            host = "localhost";
        }
        
        // Construct the URL
        String path = uri.getPath();
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        URL url;
        try {
            url = new URL(scheme, host, portNumber, path);
        } catch (MalformedURLException e) {
            throw new ServletMappingException(e);
        }
        return url;
    }
        
    public RequestDispatcher getRequestDispatcher(String suri) throws ServletMappingException {

//        // Make sure that the path starts with a /
//        if (!suri.startsWith("/")) {
//            suri = '/' + suri;
//        }
//        
//        // Get the servlet mapped to the given path
//        Servlet servlet = servlets.get(suri);
//        if (servlet != null) {
//            return new WebAppRequestDispatcher(suri, servlet);
//        }
//        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
//            String servletPath = entry.getKey();
//            if (servletPath.endsWith("*")) {
//                servletPath = servletPath.substring(0, servletPath.length() -1);
//                if (suri.startsWith(servletPath)) {
//                    return new WebAppRequestDispatcher(entry.getKey(), entry.getValue());
//                } else {
//                    if ((suri + "/").startsWith(servletPath)) {
//                        return new WebAppRequestDispatcher(entry.getKey(), entry.getValue());
//                    }
//                }
//            }
//        }
        
        // No servlet found
        return null;
    }
    
    static TomcatServletHost getInstance() {
        return instance;
    }


//    void destroy() {
//        
//        // Destroy the registered servlets
//        for (Servlet servlet : servlets.values()) {
//            servlet.destroy();
//        }
//
////        // Close the SCA domain
////        if (scaDomain != null) {
////            scaDomain.close();
////        }
//    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String path) {
//        if (!contextPath.equals(path)) {
//            throw new IllegalArgumentException("invalid context path for webapp, existing context path: " + contextPath + " new contextPath: " + path);
//        }
    }

    public void setTuscanyHost(TuscanyHost tuscanyHost) {
        this.tuscanyHost = tuscanyHost;
    }

}
