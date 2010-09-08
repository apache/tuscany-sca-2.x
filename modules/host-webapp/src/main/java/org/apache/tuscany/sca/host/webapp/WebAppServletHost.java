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

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.host.http.SecurityContext;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;
import org.apache.tuscany.sca.node.Node;

/**
 * ServletHost implementation for use in a webapp environment.
 *
 * @version $Rev$ $Date$
 */
public class WebAppServletHost implements ServletHost {
    private static final Logger logger = Logger.getLogger(WebAppServletHost.class.getName());

    private static final String INETADDRESS = "java.net.InetAddress";

    public static final String SCA_NODE_ATTRIBUTE = Node.class.getName();

    private Map<String, Servlet> servlets;
    private String contextPath = "/";
    private int defaultPortNumber = 8080;
    private String contributionRoot;

    private ServletConfig servletConfig;
    private ServletContext servletContext;
    private Map<String, Object> tempAttributes = new HashMap<String, Object>();

    public WebAppServletHost() {
        servlets = new HashMap<String, Servlet>();
    }

    public void setDefaultPort(int port) {
        defaultPortNumber = port;
    }

    public int getDefaultPort() {
        return defaultPortNumber;
    }

    public String getName() {
        return "webapp";
    }

    public String addServletMapping(String suri, Servlet servlet) throws ServletMappingException {
        return addServletMapping(suri, servlet, null);
    }

    public String addServletMapping(String suri, Servlet servlet, SecurityContext securityContext) throws ServletMappingException {
        URI pathURI = URI.create(suri);

        // Make sure that the path starts with a /
        suri = pathURI.getPath();
        if (!suri.startsWith("/")) {
            suri = '/' + suri;
        }

        // String relativeURI = suri;
        if (!suri.startsWith(contextPath + "/")) {
            suri = contextPath + suri;
        }

        if (!servlets.values().contains(servlet)) {
            // The same servlet can be registred more than once
            try {
                servlet.init(servletConfig);
            } catch (ServletException e) {
                throw new ServletMappingException(e);
            }
        }

        // In a webapp just use the given path and ignore the host and port
        // as they are fixed by the Web container
        servlets.put(suri, servlet);

        URL url = getURLMapping(pathURI.toString(), securityContext);
        logger.info("Added Servlet mapping: " + url);
        return url.toString();
    }

    public Servlet removeServletMapping(String suri) throws ServletMappingException {
        URI pathURI = URI.create(suri);

        // Make sure that the path starts with a /
        suri = pathURI.getPath();
        if (!suri.startsWith("/")) {
            suri = '/' + suri;
        }

        if (!suri.startsWith(contextPath)) {
            suri = contextPath + suri;
        }

        // In a webapp just use the given path and ignore the host and port
        // as they are fixed by the Web container
        Servlet servlet = servlets.remove(suri);
        /*
        if (servlet != null) {
            servlet.destroy();
        }
        */
        return servlet;
    }

    public Servlet getServletMapping(String suri) throws ServletMappingException {
        if (!suri.startsWith("/")) {
            suri = '/' + suri;
        }

        if (!suri.startsWith(contextPath)) {
            suri = contextPath + suri;
        }

        // Get the Servlet mapped to the given path
        Servlet servlet = servlets.get(suri);
        return servlet;
    }

    public URL getURLMapping(String suri, SecurityContext securityContext) throws ServletMappingException {
        URI uri = URI.create(suri);

        // Get the URI scheme and port
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        }
        int portNumber = uri.getPort();
        if (portNumber == -1 && uri.getScheme() == null) {
            // Only set the default port number if the scheme is not present
            portNumber = defaultPortNumber;
        }

        // Get the host
        String host = uri.getHost();
        if (host == null) {
            try {
            	//TUSCANY-3667 - InetAddress is not allowed in GoogleAppEngine
            	//host = InetAddress.getLocalHost().getHostName();
            	Class<?> clazz = Class.forName(INETADDRESS);
            	Object inetAddress = clazz.getMethod("getLocalHost").invoke(null);
            	host = (String) clazz.getMethod("getHostName").invoke(inetAddress);
            } catch (Throwable t) {
            	logger.log(Level.WARNING, "Error retrieving host information : " + t.getMessage());
                host = "localhost";
            }
        }

        // Construct the URL
        String path = uri.getPath();
        if (!path.startsWith("/")) {
            path = '/' + path;
        }

        if (contextPath != null && !path.startsWith(contextPath)) {
            path = contextPath + path;
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

        // Make sure that the path starts with a /
        if (!suri.startsWith("/")) {
            suri = '/' + suri;
        }

        if (contextPath != null && contextPath.length() > 0 && !"/".equals(contextPath)) {
            suri = contextPath + suri;
        }

        // Get the Servlet mapped to the given path
        Servlet servlet = servlets.get(suri);
        if (servlet != null) {
            return new WebAppRequestDispatcher(suri, servlet);
        }

        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            String servletPath = entry.getKey();
            if (servletPath.endsWith("*")) {
                servletPath = servletPath.substring(0, servletPath.length() - 1);
                if (suri.startsWith(servletPath)) {
                    // entry key is contextPath/servletPath, WebAppRequestDispatcher only wants servletPath
                    return new WebAppRequestDispatcher(entry.getKey().substring(contextPath.length()), entry.getValue());
                } else {
                    if ((suri + "/").startsWith(servletPath)) {
                        return new WebAppRequestDispatcher(entry.getKey().substring(contextPath.length()), entry.getValue());
                    }
                }
            }
        }

        // No Servlet found
        return null;
    }

    public void init(ServletConfig config) throws ServletException {
        this.servletConfig = config;
        servletContext = config.getServletContext();

        for (String name : tempAttributes.keySet()) {
            servletContext.setAttribute(name, tempAttributes.get(name));
        }

        // WebAppHelper.init(servletContext);

        initContextPath(config);

        // Initialize the registered Servlets
        for (Servlet servlet : servlets.values()) {
            servlet.init(config);
        }

    }

    /**
     * Initializes the contextPath
     * The 2.5 Servlet API has a getter for this, for pre 2.5 Servlet
     * containers use an init parameter.
     */
    @SuppressWarnings("unchecked")
    public void initContextPath(ServletConfig config) {

        String oldContextPath = contextPath;

        if (Collections.list(config.getInitParameterNames()).contains("contextPath")) {
            contextPath = config.getInitParameter("contextPath");
        } else {
            // The getContextPath() is introduced since Servlet 2.5
            ServletContext context = config.getServletContext();
            try {
                // Try to get the method anyway since some ServletContext impl has this method even before 2.5
                Method m = context.getClass().getMethod("getContextPath", new Class[] {});
                contextPath = (String)m.invoke(context, new Object[] {});
            } catch (Exception e) {
                logger.warning("Servlet level is: " + context.getMajorVersion() + "." + context.getMinorVersion());
                throw new IllegalStateException("'contextPath' init parameter must be set for pre-2.5 servlet container");
            }
        }

        logger.info("ContextPath: " + contextPath);

        // if the context path changes after some servlets have been registered then
        // need to reregister them (this can happen if extensions start before webapp init)
        if (!oldContextPath.endsWith(contextPath)) {
            List<String> oldServletURIs = new ArrayList<String>();
            for (String oldServletURI : servlets.keySet()) {
                if (oldServletURI.startsWith(oldContextPath)) {
                    if (!oldServletURI.startsWith(contextPath)) {
                        oldServletURIs.add(oldServletURI);
                    }
                }
            }
            for (String oldURI : oldServletURIs) {
                String ns = contextPath + "/" + oldURI.substring(oldContextPath.length());
                servlets.put(ns, servlets.remove(oldURI));
            }
        }

    }

    void destroy() {

        // Destroy the registered Servlets
        for (Servlet servlet : servlets.values()) {
            servlet.destroy();
        }

        // Close the SCA domain
        WebAppHelper.stop(servletContext);
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String path) {
    }

    /**
     * TODO: How context paths work is still up in the air so for now
     *    this hacks in a path that gets some samples working
     *    can't use setContextPath as NodeImpl calls that later
     */
    public void setContextPath2(String path) {
        if (path != null && path.length() > 0) {
            this.contextPath = path;
        }
    }

    public String getContributionRoot() {
        return contributionRoot;
    }

    public void setAttribute(String name, Object value) {
        if (servletContext != null) {
            servletContext.setAttribute(name, value);
        } else {
            tempAttributes.put(name, value);
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
