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

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;
import org.apache.coyote.http11.Http11Protocol;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.mapper.MappingData;
import org.apache.tomcat.util.net.JIoEndpoint;
import org.apache.tuscany.sca.http.DefaultResourceServlet;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.http.ServletMappingException;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * A Tomcat based implementation of ServletHost.
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings("deprecation")
public class TomcatServer implements ServletHost {

    private static final int DEFAULT_PORT = 8080;
    
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

    /**
     * Constructs a new embedded Tomcat server.
     *
     * @param workScheduler the WorkScheduler to use to process requests.
     */
    public TomcatServer(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    /**
     * Stop all the started servers.
     */
    public void stop() throws ServletMappingException {
        if (!ports.isEmpty()) {
            try {
                Set<Entry<Integer, Port>> entries = new HashSet<Entry<Integer, Port>>(ports.entrySet());
                for (Entry<Integer, Port> entry: entries) {
                    entry.getValue().getConnector().stop();
                    entry.getValue().getEngine().stop();
                    ports.remove(entry.getKey());
                }
            } catch (Exception e) {
                throw new ServletMappingException(e);
            }
        }
    }

    public void addServletMapping(String strURI, Servlet servlet) {
        URI uri = URI.create(strURI);
        
        // Get the URI scheme and port
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        }
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = DEFAULT_PORT;
        }

        // Get the port object associated with the given port number
        Port port = ports.get(portNumber);
        if (port == null) {

            // Create an engine
            StandardEngine engine = new StandardEngine();
            engine.setBaseDir("");
            engine.setDefaultHost("localhost");
            engine.setName("engine/" + portNumber);

            // Create a host
            StandardHost host = new StandardHost();
            host.setAppBase("");
            host.setName("localhost");
            engine.addChild(host);

            // Create the root context
            StandardContext context = new StandardContext();
            context.setDocBase("");
            context.setPath("");
            ContextConfig config = new ContextConfig();
            ((Lifecycle)context).addLifecycleListener(config);
            host.addChild(context);
            
            // Install an HTTP connector
            Connector connector;
            try {
                engine.start();
                connector = new CustomConnector();
                connector.setPort(portNumber);
                connector.setContainer(engine);
                connector.initialize();
                connector.start();
            } catch (Exception e) {
                throw new ServletMappingException(e);
            }
            
            // Keep track of the running server
            port = new Port(engine, host, connector);
            ports.put(portNumber, port);
        }

        // Register the servlet mapping
        String path = uri.getPath();
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        
        ServletWrapper wrapper;
        if (servlet instanceof DefaultResourceServlet) {
            
            // Optimize the handling of resource requests, use the Tomcat default servlet
            // instead of our default resource servlet
            if (path.endsWith("*")) {
                path = path.substring(0, path.length()-1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length()-1);
            }
            DefaultResourceServlet resourceServlet = (DefaultResourceServlet)servlet;
            TomcatDefaultServlet defaultServlet = new TomcatDefaultServlet(path, resourceServlet.getDocumentRoot());
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

        // Initialize the servlet
        try {
            wrapper.initServlet();
        } catch (ServletException e) {
            throw new ServletMappingException(e);
        }

        URI addedURI = URI.create(scheme + "://localhost:" + portNumber + path);
        System.out.println("Added Servlet mapping: " + addedURI);
    }

    public Servlet removeServletMapping(String uriStr) {
        URI uri = URI.create(uriStr);
        
        // Get the URI port
        int portNumber = uri.getPort();
        if (portNumber == -1) {
            portNumber = DEFAULT_PORT;
        }

        // Get the port object associated with the given port number
        Port port = ports.get(portNumber);
        if (port == null) {
            throw new IllegalStateException("No servlet registered at this URI: " + uriStr);
        }
        
        String mapping = uri.getPath();
        Context context = port.getHost().map(mapping);
        MappingData md = new MappingData();
        MessageBytes mb = MessageBytes.newInstance();
        mb.setString(mapping);
        try {
            context.getMapper().map(mb, md);
        } catch (Exception e) {
            return null;
        }
        if (md.wrapper instanceof ServletWrapper) {
            ServletWrapper servletWrapper = (ServletWrapper)md.wrapper;
            context.removeServletMapping(mapping);
            context.removeChild(servletWrapper);
            servletWrapper.destroyServlet();
            return servletWrapper.getServlet();
        } else {
            return null;
        }
    }

    /**
     * A custom connector that uses our WorkScheduler to schedule
     * worker threads.
     */
    private class CustomConnector extends Connector {

        private class CustomHttpProtocolHandler extends Http11Protocol {

            /**
             * An Executor wrappering our WorkScheduler
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

                public void stop() {
                    super.stop();
                    try {
                        acceptorThread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

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

}
