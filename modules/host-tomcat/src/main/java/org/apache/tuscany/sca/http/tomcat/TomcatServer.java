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
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;
import org.apache.coyote.http11.Http11Protocol;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.mapper.MappingData;
import org.apache.tomcat.util.net.JIoEndpoint;
import org.apache.tuscany.sca.host.http.DefaultResourceServlet;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletMappingException;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * A Tomcat based implementation of ServletHost.
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings("deprecation")
public class TomcatServer implements ServletHost {
    private final static Logger logger = Logger.getLogger(TomcatServer.class.getName());

    private int defaultPortNumber = 8080;

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
        private StandardEngine engine;
        private StandardHost host;
        private Connector connector;

        private Port(StandardEngine engine, StandardHost host, Connector connector) {
            this.engine = engine;
            this.host = host;
            this.connector = connector;
        }

        public StandardEngine getEngine() {
            return engine;
        }

        public StandardHost getHost() {
            return host;
        }

        public Connector getConnector() {
            return connector;
        }
    }

    private Map<Integer, Port> ports = new HashMap<Integer, Port>();

    private WorkScheduler workScheduler;

    private String contextPath = "/";

    /**
     * Constructs a new embedded Tomcat server.
     *
     * @param workScheduler the WorkScheduler to use to process requests.
     */
    public TomcatServer(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    public void setDefaultPort(int port) {
        defaultPortNumber = port;
    }

    public int getDefaultPort() {
        return defaultPortNumber;
    }

    /**
     * Stop all the started servers.
     */
    public void stop() throws ServletMappingException {
        if (!ports.isEmpty()) {
            try {
                Set<Entry<Integer, Port>> entries = new HashSet<Entry<Integer, Port>>(ports.entrySet());
                for (Entry<Integer, Port> entry : entries) {
                    Port port = entry.getValue();
                    port.getConnector().stop();
                    port.getEngine().stop();
                    ports.remove(entry.getKey());
                }
            } catch (Exception e) {
                throw new ServletMappingException(e);
            }
        }
    }

    public void addServletMapping(String suri, Servlet servlet) {
        URI uri = URI.create(suri);

        // Get the URI scheme and port
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        }
        final int portNumber = (uri.getPort() == -1 ? defaultPortNumber : uri.getPort() ); 

        // Get the port object associated with the given port number
        Port port = ports.get(portNumber);
        if (port == null) {

            // Create an engine
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            final StandardEngine engine = 
            AccessController.doPrivileged(new PrivilegedAction<StandardEngine>() {
                public StandardEngine run() {
                    return new StandardEngine();
                }
            });
            
            engine.setBaseDir("");
            engine.setDefaultHost("localhost");
            engine.setName("engine/" + portNumber);

            // Create a host
            final StandardHost host = new StandardHost();
            host.setAppBase("");
            host.setName("localhost");
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    engine.addChild(host);
                    return null;
                }
            });

            // Create the root context
            StandardContext context = new StandardContext();
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            context.setLoader(new TuscanyLoader(tccl));
            // context.setParentClassLoader(tccl.getParent());
            context.setDocBase("");
            context.setPath("");
            ContextConfig config = new ContextConfig();
            ((Lifecycle)context).addLifecycleListener(config);
            host.addChild(context);

            // Install an HTTP connector
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws LifecycleException {
                        engine.start();
                        return null;
                    }
                });
            } catch (PrivilegedActionException e) {
                // throw (LifecycleException)e.getException();
                throw new ServletMappingException(e);
            }                
            Connector connector;
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            try {
                connector = AccessController.doPrivileged(new PrivilegedExceptionAction<CustomConnector>() {
                    public CustomConnector run() throws Exception {
                       CustomConnector customConnector = new CustomConnector();
                       customConnector.setPort(portNumber);
                       customConnector.setContainer(engine);
                       customConnector.initialize();
                       customConnector.start();
                       return customConnector;
                   }
                });
            } catch (Exception e) {
                throw new ServletMappingException(e);
            }
            // Keep track of the running server
            port = new Port(engine, host, connector);
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
            portNumber = defaultPortNumber;
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

    public Servlet removeServletMapping(String suri) {
        URI uri = URI.create(suri);

        // Get the URI port
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = defaultPortNumber;
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
            
            //logger.info("Removed Servlet mapping: " + suri);
            
            // Stop the port if there's no servlets on it anymore
            String[] contextNames = port.getConnector().getMapper().getContextNames();
            if (contextNames == null || contextNames.length ==0) {
                try {
                    port.getConnector().stop();
                    port.getEngine().stop();
                    ports.remove(portNumber);
                } catch (LifecycleException e) {
                    throw new IllegalStateException(e);
                }
            }
            
            return servletWrapper.getServlet();
        } else {
            logger.info("Trying to Remove servlet mapping: " + mapping + " where mapping is not registered");
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

    /**
     * A custom connector that uses our WorkScheduler to schedule
     * worker threads.
     */
    private class CustomConnector extends Connector {

        private class CustomHttpProtocolHandler extends Http11Protocol {

            /**
             * An Executor wrapping our WorkScheduler
             */
            private class WorkSchedulerExecutor implements Executor {
                public void execute(Runnable command) {
                    workScheduler.scheduleWork(command);
                }
            }

            /**
             * A custom Endpoint that waits for its acceptor thread to
             * terminate before stopping.
             */
            private class CustomEndpoint extends JIoEndpoint {
                private Thread acceptorThread;

                private class CustomAcceptor extends Acceptor {
                    CustomAcceptor() {
                        super();
                    }
                }

                @Override
                public void start() throws Exception {
                    if (!initialized)
                        init();
                    if (!running) {
                        running = true;
                        paused = false;
                        acceptorThread = new Thread(new CustomAcceptor(), getName() + "-Acceptor-" + 0);
                        acceptorThread.setPriority(threadPriority);
                        acceptorThread.setDaemon(daemon);
                        acceptorThread.start();
                    }
                }

                @Override
                public void stop() {
                    super.stop();
                    try {
                        acceptorThread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public int getCurrentThreadsBusy() {
                    return 0;
                }
            }

            CustomHttpProtocolHandler() {
                endpoint = new CustomEndpoint();
                endpoint.setExecutor(new WorkSchedulerExecutor());
            }
        }

        CustomConnector() throws Exception {
            protocolHandler = new CustomHttpProtocolHandler();
        }
    }

    public void setContextPath(String path) {
        this.contextPath = path;
    }

}
