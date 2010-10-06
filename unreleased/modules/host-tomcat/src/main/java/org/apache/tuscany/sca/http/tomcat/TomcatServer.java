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
package org.apache.tuscany.sca.http.tomcat;

import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Embedded;
import org.apache.coyote.http11.Http11Protocol;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.mapper.MappingData;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.host.http.DefaultResourceServlet;
import org.apache.tuscany.sca.host.http.SecurityContext;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * A Tomcat based implementation of ServletHost.
 *
 * @version $Rev$ $Date$
 */
public class TomcatServer implements ServletHost, LifeCycleListener {
    private static final Logger logger = Logger.getLogger(TomcatServer.class.getName());

    private WorkScheduler workScheduler;
    private Embedded embedded;
    private Map<Integer, Port> ports = new HashMap<Integer, Port>();
    private String contextPath = "/";
    private int defaultPort = 8080;
    private int defaultSSLPort = 8443;

    private final class TuscanyLoader implements Loader {
        private final ClassLoader tccl;
        private boolean delegate;
        private boolean reloadable;
        private Container container;
        private List<String> repos = new ArrayList<String>();
        private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

        private TuscanyLoader(ClassLoader tccl) {
            this.tccl = tccl;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            listeners.add(listener);
        }

        public void addRepository(String repository) {
            repos.add(repository);
        }

        public void backgroundProcess() {
        }

        public String[] findRepositories() {
            return repos.toArray(new String[repos.size()]);
        }

        public Container getContainer() {
            return container;
        }

        public boolean getDelegate() {
            return delegate;
        }

        public String getInfo() {
            return "Tuscany Loader for Embedded Tomcat";
        }

        public boolean getReloadable() {
            return reloadable;
        }

        public boolean modified() {
            return false;
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            listeners.remove(listener);
        }

        public void setContainer(Container container) {
            this.container = container;
        }

        public void setDelegate(boolean delegate) {
            this.delegate = delegate;
        }

        public void setReloadable(boolean reloadable) {
            this.reloadable = reloadable;
        }

        public ClassLoader getClassLoader() {
            return tccl;
        }
    }

    /**
     * Represents a port and the server that serves it.
     */
    private class Port {
        private Engine engine;
        private Host host;
        private Connector connector;

        private Port(Engine engine, Host host, Connector connector) {
            this.engine = engine;
            this.host = host;
            this.connector = connector;
        }

        public Engine getEngine() {
            return engine;
        }

        public Host getHost() {
            return host;
        }

        public Connector getConnector() {
            return connector;
        }
    }

    /**
     * Constructs a new embedded Tomcat server.
     *
     */
    public TomcatServer(ExtensionPointRegistry registry, Map<String, String> attributes) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.workScheduler = utilities.getUtility(WorkScheduler.class);
        String port = attributes.get("defaultPort");
        if (port != null) {
            defaultPort = Integer.parseInt(port);
        }
        String sslPort = attributes.get("defaultSSLPort");
        if (sslPort != null) {
            defaultPort = Integer.parseInt(sslPort);
        }
        if (attributes.containsKey("contextPath")) {
            contextPath = attributes.get("contextPath");
        }
    }

    protected TomcatServer(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    public void setDefaultPort(int port) {
        defaultPort = port;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    /**
     * Stop all the started servers.
     */
    public void stop() {
        if (embedded != null) {
            try {
                // embedded.stop();
                embedded.destroy();
                for (Port port : ports.values()) {
                    port.connector.stop();
                }
            } catch (LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public synchronized String addServletMapping(String suri, Servlet servlet) {
        init();
        URI uri = URI.create(suri);

        // Get the URI scheme and port
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        }
        final int portNumber = (uri.getPort() == -1 ? defaultPort : uri.getPort());

        // Get the port object associated with the given port number
        Port port = ports.get(portNumber);
        if (port == null) {

            port = createInstance(scheme, portNumber);
            ports.put(portNumber, port);
        }

        // Register the Servlet mapping
        String path = uri.getPath();

        if (!path.startsWith("/")) {
            path = '/' + path;
        }

        if (!path.startsWith(contextPath)) {
            path = contextPath + path;
        }

        ServletWrapper wrapper;
        if (servlet instanceof DefaultResourceServlet) {
            String defaultServletPath = path;

            // Optimize the handling of resource requests, use the Tomcat default Servlet
            // instead of our default resource Servlet
            if (defaultServletPath.endsWith("*")) {
                defaultServletPath = defaultServletPath.substring(0, defaultServletPath.length() - 1);
            }
            if (defaultServletPath.endsWith("/")) {
                defaultServletPath = defaultServletPath.substring(0, defaultServletPath.length() - 1);
            }
            DefaultResourceServlet resourceServlet = (DefaultResourceServlet)servlet;
            TomcatDefaultServlet defaultServlet =
                new TomcatDefaultServlet(defaultServletPath, resourceServlet.getDocumentRoot());
            wrapper = new ServletWrapper(defaultServlet);

        } else {
            wrapper = new ServletWrapper(servlet);
        }
        Context context = port.getHost().map(path);
        wrapper.setName(path);
        wrapper.addMapping(path);
        context.addChild(wrapper);
        context.addServletMapping(path, path);
        port.getConnector().getMapper().addWrapper("localhost", "", path, wrapper);

        // Initialize the Servlet
        try {
            wrapper.initServlet();
        } catch (ServletException e) {
            throw new ServletMappingException(e);
        }

        // Compute the complete URL
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            host = "localhost";
        }
        URL addedURL;
        try {
            addedURL = new URL(scheme, host, portNumber, path);
        } catch (MalformedURLException e) {
            throw new ServletMappingException(e);
        }
        logger.info("Added Servlet mapping: " + addedURL);
        return addedURL.toString();
    }

    private Port createInstance(String scheme, final int portNumber) {
        Port port;
        // Create an engine
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        final Engine engine = AccessController.doPrivileged(new PrivilegedAction<Engine>() {
            public Engine run() {
                return embedded.createEngine();
            }
        });

        ((StandardEngine)engine).setBaseDir("");
        engine.setDefaultHost("localhost");
        engine.setName("engine/" + portNumber);

        // Create a host
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        final Host host = AccessController.doPrivileged(new PrivilegedAction<Host>() {
            public Host run() {
                Host host = embedded.createHost("localhost", "");
                engine.addChild(host);
                return host;
            }
        });

        // Create the root context
        Context context = embedded.createContext("", "");
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        context.setLoader(new TuscanyLoader(tccl));
        // context.setParentClassLoader(tccl.getParent());
        ContextConfig config = new ContextConfig();
        ((Lifecycle)context).addLifecycleListener(config);
        host.addChild(context);

        embedded.addEngine(engine);

        // Install an HTTP connector

        Connector connector;
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        try {
            final String protocol = scheme;
            connector = AccessController.doPrivileged(new PrivilegedExceptionAction<Connector>() {
                public Connector run() throws Exception {
                    Connector customConnector = new Connector();
                    customConnector.setPort(portNumber);

                    if ("https".equalsIgnoreCase(protocol)) {
                        configureSSL(customConnector);
                        ((Http11Protocol)customConnector.getProtocolHandler()).setSSLEnabled(true);
                    }
                    return customConnector;
                }

                private void configureSSL(Connector customConnector) {
                    String trustStore = System.getProperty("javax.net.ssl.trustStore");
                    String trustStorePass = System.getProperty("javax.net.ssl.trustStorePassword");
                    String keyStore = System.getProperty("javax.net.ssl.keyStore");
                    String keyStorePass = System.getProperty("javax.net.ssl.keyStorePassword");

                    customConnector.setProperty("protocol", "TLS");

                    customConnector.setProperty("keystore", keyStore);
                    customConnector.setProperty("keypass", keyStorePass);
                    String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType", KeyStore.getDefaultType());
                    String trustStoreType =
                        System.getProperty("javax.net.ssl.trustStoreType", KeyStore.getDefaultType());
                    customConnector.setProperty("keytype", keyStoreType);
                    customConnector.setProperty("trusttype", trustStoreType);
                    customConnector.setProperty("truststore", trustStore);
                    customConnector.setProperty("trustpass", trustStorePass);

                    customConnector.setProperty("clientauth", "false");
                    customConnector.setProtocol("HTTP/1.1");
                    customConnector.setScheme(protocol);
                    customConnector.setProperty("backlog", "10");
                    customConnector.setSecure(true);
                }
            });
        } catch (Exception e) {
            throw new ServletMappingException(e);
        }

        embedded.addConnector(connector);
        try {
            connector.start();
        } catch (LifecycleException e) {
            throw new ServletMappingException(e);
        }

        // Keep track of the running server
        port = new Port(engine, host, connector);
        return port;
    }

    private synchronized void init() {
        if (embedded != null) {
            return;
        }
        embedded = new Embedded();
        embedded.setAwait(true);
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws LifecycleException {
                    embedded.start();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            // throw (LifecycleException)e.getException();
            throw new ServletMappingException(e);
        }
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

        if (!path.startsWith(contextPath)) {
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

    public Servlet getServletMapping(String suri) throws ServletMappingException {

        if (suri == null) {
            return null;
        }

        URI uri = URI.create(suri);

        // Get the URI port
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = defaultPort;
        }

        // Get the port object associated with the given port number
        Port port = ports.get(portNumber);
        if (port == null) {
            return null;
        }

        String mapping = uri.getPath();

        if (!mapping.startsWith("/")) {
            mapping = '/' + mapping;
        }

        if (!mapping.startsWith(contextPath)) {
            mapping = contextPath + mapping;
        }

        final Context context = port.getHost().map(mapping);
        final MappingData md = new MappingData();
        final MessageBytes mb = MessageBytes.newInstance();
        mb.setString(mapping);
        try {
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    context.getMapper().map(mb, md);
                    return null;
                }
            });
        } catch (Exception e) {
            return null;
        }
        if (md.wrapper instanceof ServletWrapper) {
            ServletWrapper servletWrapper = (ServletWrapper)md.wrapper;
            return servletWrapper.getServlet();
        } else {
            return null;
        }
    }

    public synchronized Servlet removeServletMapping(String suri) {
        URI uri = URI.create(suri);

        // Get the URI port
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = defaultPort;
        }

        // Get the port object associated with the given port number
        Port port = ports.get(portNumber);
        if (port == null) {
            throw new IllegalStateException("No servlet registered at this URI: " + suri);
        }

        String mapping = uri.getPath();

        if (!mapping.startsWith("/")) {
            mapping = '/' + mapping;
        }

        if (!mapping.startsWith(contextPath)) {
            mapping = contextPath + mapping;
        }

        final Context context = port.getHost().map(mapping);
        final MappingData md = new MappingData();
        final MessageBytes mb = MessageBytes.newInstance();
        mb.setString(mapping);
        try {
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    context.getMapper().map(mb, md);
                    return null;
                }
            });
        } catch (Exception e) {
            return null;
        }
        if (md.wrapper instanceof ServletWrapper) {
            ServletWrapper servletWrapper = (ServletWrapper)md.wrapper;

            port.getConnector().getMapper().removeWrapper("localhost", "", mapping);

            try {
                context.removeServletMapping(mapping);
            } catch (NegativeArraySizeException e) {
                // JIRA TUSCANY-1599
                // FIXME Looks like a bug in Tomcat when removing the last
                // Servlet in the list, catch the exception for now as it doesn't
                // seem harmful, will find a better solution for the next release
            }
            context.removeChild(servletWrapper);
            try {
                servletWrapper.destroyServlet();
            } catch (Exception ex) {
                // Hack to handle destruction of Servlets without Servlet context 
            }

            logger.info("Removed Servlet mapping: " + suri);

            // Stop the port if there's no servlets on it anymore
            String[] contextNames = port.getConnector().getMapper().getContextNames();
            if (contextNames == null || contextNames.length == 0) {
                try {
                    port.getConnector().stop();
                    ((StandardEngine)port.getEngine()).stop();
                    embedded.removeEngine(port.getEngine());
                    embedded.removeConnector(port.getConnector());
                    ports.remove(portNumber);
                } catch (LifecycleException e) {
                    throw new IllegalStateException(e);
                }
            }

            return servletWrapper.getServlet();
        } else {
            logger.warning("Trying to Remove servlet mapping: " + mapping + " where mapping is not registered");
            return null;
        }
    }

    public RequestDispatcher getRequestDispatcher(String suri) throws ServletMappingException {
        //FIXME implement this later
        return null;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String path) {
        this.contextPath = path;
    }

    public void start() {

    }

    public String addServletMapping(String uri, Servlet servlet, SecurityContext securityContext)
        throws ServletMappingException {
        return addServletMapping(uri, servlet);
    }

    public String getName() {
        return "tomcat";
    }

    public URL getURLMapping(String arg0, SecurityContext arg1) {
        return null;
    }

    public void setAttribute(String arg0, Object arg1) {
    }

    private URL map(String suri, SecurityContext securityContext, boolean resolve) throws ServletMappingException {
        URI uri = URI.create(suri);

        // Get the URI scheme and port
        String scheme = null;
        if (securityContext != null && securityContext.isSSLEnabled()) {
            scheme = "https";
        } else {
            scheme = uri.getScheme();
            if (scheme == null) {
                scheme = "http";
            }
        }

        int portNumber = uri.getPort();
        if (portNumber == -1) {
            if ("http".equals(scheme)) {
                portNumber = defaultPort;
            } else {
                portNumber = defaultSSLPort;
            }
        }

        // Get the host
        String host = uri.getHost();
        if (host == null) {
            host = "0.0.0.0";
            if (resolve) {
                try {
                    host = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    host = "localhost";
                }
            }
        }

        // Construct the URL
        String path = uri.getPath();

        if (!path.startsWith("/")) {
            path = '/' + path;
        }

        if (!path.startsWith(contextPath)) {
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

    @Override
    public ServletContext getServletContext() {
        if (ports.isEmpty()) {
            return null;
        } else {
            Port port = ports.values().iterator().next();
            return port.getHost().map(getContextPath()).getServletContext();
        }
    }

}
