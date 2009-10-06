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

package org.apache.tuscany.sca.http.osgi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class OSGiServletHost implements ServletHost, BundleActivator {
    static final String DUMMY_URI = "/_tuscany";

    /**
     * Default servlet
     */
    private static class DefaultServlet extends HttpServlet {

        @Override
        public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            PrintWriter pw = response.getWriter();
            pw.println("<html><body><h1>Apache Tuscany</h1></body></html>");
            pw.flush();
        }
    }

    private Map<String, Servlet> servlets = new ConcurrentHashMap<String, Servlet>();

    private static final Logger logger = Logger.getLogger(OSGiServletHost.class.getName());

    private String contextPath = "/";
    private int defaultPortNumber = 8080;

    private ServletContext servletContext;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private HttpServiceWrapper wrapper;

    private static BundleContext bundleContext;

    public void start(BundleContext context) {
        bundleContext = context;
        wrapper = new HttpServiceWrapper(bundleContext);
        wrapper.open();

        HttpServlet defaultServlet = new DefaultServlet();
        addServletMapping(DUMMY_URI, defaultServlet);
        // this.servletContext = defaultServlet.getServletContext();
    }

    public void stop(BundleContext bundleContext) {
        removeServletMapping(DUMMY_URI);
        wrapper.close();
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
        try {
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put(HttpServiceWrapper.SERVLET_URI, suri);
            ServiceRegistration reg = bundleContext.registerService(Servlet.class.getName(), servlet, props);
            servlets.put(suri, servlet);
        } catch (Exception e) {
            throw new ServletMappingException(e);
        }

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
        try {
            return servlets.remove(suri);
        } catch (Exception e) {
            throw new ServletMappingException(e);
        }

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
            return new ServletRequestDispatcher(suri, servlet);
        }

        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            String servletPath = entry.getKey();
            if (servletPath.endsWith("*")) {
                servletPath = servletPath.substring(0, servletPath.length() - 1);
                if (suri.startsWith(servletPath)) {
                    // entry key is contextPath/servletPath, WebAppRequestDispatcher only wants servletPath
                    return new ServletRequestDispatcher(entry.getKey().substring(contextPath.length()), entry
                        .getValue());
                } else {
                    if ((suri + "/").startsWith(servletPath)) {
                        return new ServletRequestDispatcher(entry.getKey().substring(contextPath.length()), entry
                            .getValue());
                    }
                }
            }
        }

        // No Servlet found
        return null;
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

    public void setAttribute(String name, Object value) {
        if (servletContext != null) {
            servletContext.setAttribute(name, value);
        } else {
            attributes.put(name, value);
        }
    }

    public String getName() {
        return "osgi";
    }

}
