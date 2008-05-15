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

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;

/**
 * ServletHost implementation for use in a webapp environment.
 * 
 * FIXME: using a static singleton seems a big hack but how should it be shared?
 * Need some way for TuscanyServlet to pull it out.
 *
 * @version $Rev$ $Date$
 */
public class WebAppServletHost implements ServletHost {
    private static final Logger logger = Logger.getLogger(WebAppServletHost.class.getName());

    public static final String SCA_DOMAIN_ATTRIBUTE = "org.apache.tuscany.sca.SCADomain";

    private static final WebAppServletHost instance = new WebAppServletHost();

    private Map<String, Servlet> servlets;
    private SCADomain scaDomain;
    private String contextPath = "/";
    private int defaultPortNumber = 8080;
    private String contributionRoot;

    private WebAppServletHost() {
        servlets = new HashMap<String, Servlet>();
    }

    public void setDefaultPort(int port) {
        defaultPortNumber = port;
    }

    public int getDefaultPort() {
        return defaultPortNumber;
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

    public static WebAppServletHost getInstance() {
        return instance;
    }

    public void init(ServletConfig config) throws ServletException {

        ServletContext servletContext = config.getServletContext();
        if (servletContext.getAttribute(SCA_DOMAIN_ATTRIBUTE) == null) {
            initContextPath(config);
            String domainURI = "http://localhost/" + contextPath;
            contributionRoot = getContributionRoot(servletContext);
            // logger.info("Contribution: " + contributionRoot);
            System.setProperty(SCADomain.class.getName(), WebSCADomain.class.getName());
            this.scaDomain = SCADomain.newInstance(domainURI, contributionRoot);
            servletContext.setAttribute(SCA_DOMAIN_ATTRIBUTE, scaDomain);
        }

        // Initialize the registered Servlets
        for (Servlet servlet : servlets.values()) {
            servlet.init(config);
        }
    }

    protected String getContributionRoot(ServletContext servletContext) {
        String contributionRoot = null;
        try {

            InitialContext ic = new InitialContext();
            URL repoURL = (URL)ic.lookup("java:comp/env/url/contributions");

            contributionRoot = repoURL.toString();

        } catch (NamingException e) {

            // ignore exception and use default location

            try {
                
                String root = servletContext.getInitParameter("contributionRoot");
                if (root == null || root.length() < 1) {
                    root = "/";
                }
                URL rootURL = servletContext.getResource(root);
                if (rootURL.getProtocol().equals("jndi")) {
                    //this is Tomcat case, we should use getRealPath
                    File warRootFile = new File(servletContext.getRealPath(root));
                    contributionRoot = warRootFile.toURL().toString();
                } else {
                    //this is Jetty case
                    contributionRoot = rootURL.toString();
                }

            } catch (MalformedURLException mf) {
                //ignore, pass null
            }
        }

        return contributionRoot;
    }

    /**
     * Initializes the contextPath
     * The 2.5 Servlet API has a getter for this, for pre 2.5 Servlet
     * containers use an init parameter.
     */
    @SuppressWarnings("unchecked")
    public void initContextPath(ServletConfig config) {
        
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
    }

    void destroy() {

        // Destroy the registered Servlets
        for (Servlet servlet : servlets.values()) {
            servlet.destroy();
        }

        // Close the SCA domain
        if (scaDomain != null) {
            scaDomain.close();
            if (scaDomain instanceof WebSCADomain) {
                ((WebSCADomain)scaDomain).destroy();
            }
        }
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String path) {
        //        if (!contextPath.equals(path)) {
        //            throw new IllegalArgumentException("invalid context path for webapp, existing context path: " + contextPath + " new contextPath: " + path);
        //        }
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
}
