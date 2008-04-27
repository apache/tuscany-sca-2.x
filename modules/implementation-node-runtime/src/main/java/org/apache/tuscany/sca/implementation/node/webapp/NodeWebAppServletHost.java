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

package org.apache.tuscany.sca.implementation.node.webapp;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletMappingException;
import org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationLauncherUtil;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.SCANode2Factory;

/**
 * ServletHost implementation for use in a Webapp Node environment.
 * 
 * @version $Rev$ $Date$
 */
public class NodeWebAppServletHost implements ServletHost, Filter {
    private static final Logger logger = Logger.getLogger(NodeWebAppServletHost.class.getName());

    private static final NodeWebAppServletHost servletHost = new NodeWebAppServletHost();

    private Map<String, Servlet> servlets = new HashMap<String, Servlet>();
    private SCANode2 node;
    
    private String contextPath = "/";
    private int defaultPort = 8080;

    /**
     * Constructs a new NodeWebAppServletHost.
     */
    private NodeWebAppServletHost() {
    }

    /**
     * Returns the Servlet host for the current Web app.
     * 
     * @return
     */
    static public NodeWebAppServletHost servletHost() {
        return servletHost;
    }

    /**
     * Initialize the Servlet host.
     * 
     * @param filterConfig
     * @throws ServletException
     */
    public void init(final FilterConfig filterConfig) throws ServletException {
        
        // Create a Servlet config wrapping the given filter config
        ServletConfig servletConfig = servletConfig(filterConfig);

        // Get the Servlet context
        ServletContext servletContext = servletConfig.getServletContext();

        // Initialize the context path
        contextPath = contextPath(servletContext);

        // Derive the node name from the Webapp context path
        String nodeName = contextPath;
        if (nodeName.startsWith("/")) {
            nodeName = nodeName.substring(1); 
        }
        if (nodeName.endsWith("/")) {
            nodeName = nodeName.substring(0, nodeName.length() - 1); 
        }
        
        // Determine the node configuration URI
        String nodeConfiguration = NodeImplementationLauncherUtil.nodeConfigurationURI(nodeName);
        
        // Create the SCA node
        SCANode2Factory nodeFactory = SCANode2Factory.newInstance();
        node = nodeFactory.createSCANode(nodeConfiguration);
        
        // Register the Servlet host
        ServletHostExtensionPoint servletHosts = servletHosts(node);
        servletHosts.getServletHosts().clear();
        servletHosts.addServletHost(servletHost);

        // Save the node in the Servlet context 
        servletContext.setAttribute(SCAClient.class.getName(), node);
        
        // Start the node
        node.start();

        // Initialize the registered Servlets
        for (Servlet servlet : servlets.values()) {
            servlet.init(servletConfig);
        }
    }

    public void addServletMapping(String suri, Servlet servlet) throws ServletMappingException {
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
        servlets.put(suri, servlet);

        logger.info("Added Servlet mapping: " + suri);
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
        return servlets.remove(suri);
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

    public URL getURLMapping(String suri) throws ServletMappingException {
        URI uri = URI.create(suri);

        // Get the URI scheme and port
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        }
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = defaultPort;
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

        suri = contextPath + suri;

        // Get the Servlet mapped to the given path
        Servlet servlet = servlets.get(suri);
        if (servlet != null) {
            return new NodeWebAppRequestDispatcher(suri, servlet);
        }

        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            String servletPath = entry.getKey();
            if (servletPath.endsWith("*")) {
                servletPath = servletPath.substring(0, servletPath.length() - 1);
                if (suri.startsWith(servletPath)) {
                    return new NodeWebAppRequestDispatcher(entry.getKey(), entry.getValue());
                } else {
                    if ((suri + "/").startsWith(servletPath)) {
                        return new NodeWebAppRequestDispatcher(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        // No Servlet found
        return null;
    }

    /**
     * Destroy the Servlet host.
     * 
     * @throws ServletException
     */
    public void destroy() {

        // Destroy the registered Servlets
        for (Servlet servlet : servlets.values()) {
            servlet.destroy();
        }

        // Stop the node
        if (node != null) {
            node.stop();
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, javax.servlet.FilterChain chain)
        throws IOException, ServletException {

        // Get the Servlet path
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String path = httpRequest.getPathInfo();
        if (path == null) {
            path = httpRequest.getServletPath();
        }
        if (path == null) {
            path = "/";
        }

        // Get a request dispatcher for the Servlet mapped to that path
        RequestDispatcher dispatcher = getRequestDispatcher(path);
        if (dispatcher != null) {

            // Let the dispatcher forward the request to the Servlet
            dispatcher.forward(request, response);

        } else {

            // Proceed down the filter chain
            chain.doFilter(request, response);
        }
    }

    public void setDefaultPort(int port) {
        defaultPort = port;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String path) {
        //this.contextPath = path;
    }

    /**
     * Initializes the contextPath
     * The 2.5 Servlet API has a getter for this, for pre 2.5 Servlet
     * containers use an init parameter.
     */
    private static String contextPath(ServletContext context) {
        String contextPath = "/";

        // The getContextPath() is introduced since Servlet 2.5
        Method m;
        try {
            // Try to get the method anyway since some ServletContext impl has this method even before 2.5
            m = context.getClass().getMethod("getContextPath", new Class[] {});
            contextPath = (String)m.invoke(context, new Object[] {});
        } catch (Exception e) {
            contextPath = context.getInitParameter("contextPath");
            if (contextPath == null) {
                logger.warning("Servlet level is: " + context.getMajorVersion() + "." + context.getMinorVersion());
                throw new IllegalStateException(
                                                "'contextPath' init parameter must be set for pre-2.5 servlet container");
            }
        }

        logger.info("ContextPath: " + contextPath);
        return contextPath;
    }

    /**
     * Returns the Servlet host extension point used by the given node.
     * 
     * @return
     */
    private static ServletHostExtensionPoint servletHosts(SCANode2 node) {
        //FIXME Need a clean way to get the extension point registry
        // from the node
        ExtensionPointRegistry registry;
        try {
            registry = (ExtensionPointRegistry)node.getClass().getMethod("getExtensionPointRegistry").invoke(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ServletHostExtensionPoint servletHosts = registry.getExtensionPoint(ServletHostExtensionPoint.class);
        return servletHosts;
    }

    /**
     * Returns a Servlet config wrapping a filter config.
     * 
     * @param filterConfig
     * @return
     */
    private static ServletConfig servletConfig(final FilterConfig filterConfig) {
        ServletConfig servletConfig = new ServletConfig() {
            public String getInitParameter(String name) {
                return filterConfig.getInitParameter(name);
            }

            public Enumeration getInitParameterNames() {
                return filterConfig.getInitParameterNames();
            }

            public ServletContext getServletContext() {
                return filterConfig.getServletContext();
            }

            public String getServletName() {
                return filterConfig.getFilterName();
            }
        };
        return servletConfig;
    }
}
