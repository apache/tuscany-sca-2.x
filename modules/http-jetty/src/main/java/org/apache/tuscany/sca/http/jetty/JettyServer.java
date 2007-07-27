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
package org.apache.tuscany.sca.http.jetty;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.http.DefaultResourceServlet;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.http.ServletMappingException;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.log.Log;
import org.mortbay.log.Logger;
import org.mortbay.thread.ThreadPool;

/**
 * Implements an HTTP transport service using Jetty.
 *
 * @version $$Rev$$ $$Date: 2007-02-21 13:28:30 +0000 (Wed, 21 Feb
 *          2007) $$
 */
public class JettyServer implements ServletHost {

    private static final String ROOT = "/";
    private static final int ERROR = 0;
    private static final int UNINITIALIZED = 0;
    private static final int STARTING = 1;
    private static final int STARTED = 2;
    private static final int STOPPING = 3;
    private static final int STOPPED = 4;
    private static final int DEFAULT_PORT = 8080;

    private final Object joinLock = new Object();
    private int state = UNINITIALIZED;
    private String keystore;
    private String certPassword;
    private String keyPassword;
    private boolean sendServerVersion;
    private boolean https;
    private int httpsPort = 8484;
    private boolean debug;
    private Server server;
    private Connector connector;
    private ServletHandler servletHandler;
    private WorkScheduler workScheduler;

    static {
        // hack to replace the static Jetty logger
        System.setProperty("org.mortbay.log.class", JettyLogger.class.getName());

    }

    public JettyServer(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;

        // Configure the Jetty logger
        Logger logger = Log.getLogger(null);
        if (logger instanceof JettyLogger) {
            JettyLogger jettyLogger = (JettyLogger)logger;
            if (debug) {
                jettyLogger.setDebugEnabled(true);
            }
        }
    }

    public void setSendServerVersion(boolean sendServerVersion) {
        this.sendServerVersion = sendServerVersion;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public void setDebug(boolean val) {
        debug = val;
    }

    public void init() {
        state = STARTING;
    }

    public void destroy() {
        if (state == STARTED) {
            state = STOPPING;
            synchronized (joinLock) {
                joinLock.notifyAll();
            }
            try {
                server.stop();
            } catch (Exception e) {
                throw new ServletMappingException(e);
            }
            state = STOPPED;
        }
    }

    public void addServletMapping(String uriStr, Servlet servlet) throws ServletMappingException {
        URI uri = URI.create(uriStr);
        int port = uri.getPort();
        if (state == STARTING) {

            if (port == -1) {
                port = DEFAULT_PORT;
            }

            try {
                server = new Server();
                server.setThreadPool(new WorkSchedulerThreadPool());
                if (connector == null) {
                    if (https) {
                        Connector httpConnector = new SelectChannelConnector();
                        httpConnector.setPort(port);
                        SslSocketConnector sslConnector = new SslSocketConnector();
                        sslConnector.setPort(httpsPort);
                        sslConnector.setKeystore(keystore);
                        sslConnector.setPassword(certPassword);
                        sslConnector.setKeyPassword(keyPassword);
                        server.setConnectors(new Connector[] {httpConnector, sslConnector});
                    } else {
                        SelectChannelConnector selectConnector = new SelectChannelConnector();
                        selectConnector.setPort(port);
                        server.setConnectors(new Connector[] {selectConnector});
                    }
                } else {
                    connector.setPort(port);
                    server.setConnectors(new Connector[] {connector});
                }

                ContextHandler contextHandler = new ContextHandler();
                contextHandler.setContextPath(ROOT);
                server.setHandler(contextHandler);

                SessionHandler sessionHandler = new SessionHandler();
                servletHandler = new ServletHandler();
                sessionHandler.addHandler(servletHandler);

                contextHandler.setHandler(sessionHandler);

                server.setStopAtShutdown(true);
                server.setSendServerVersion(sendServerVersion);
                // monitor.started();
                server.start();
                state = STARTED;
            } catch (Exception e) {
                state = ERROR;
                throw new ServletMappingException(e);
            }
        }

        // Register the servlet mapping
        ServletHolder holder;
        if (servlet instanceof DefaultResourceServlet) {
            
            // Optimize the handling of resource requests, use the Jetty default servlet
            // instead of our default resource servlet
            String servletPath = uri.getPath();
            if (servletPath.endsWith("*")) {
                servletPath = servletPath.substring(0, servletPath.length()-1);
            }
            if (servletPath.endsWith("/")) {
                servletPath = servletPath.substring(0, servletPath.length()-1);
            }
            DefaultResourceServlet resourceServlet = (DefaultResourceServlet)servlet;
            DefaultServlet defaultServlet = new JettyDefaultServlet(servletPath, resourceServlet.getDocumentRoot());
            holder = new ServletHolder(defaultServlet);
            
        } else {
            holder = new ServletHolder(servlet);
        }
        servletHandler.addServlet(holder);
        ServletMapping mapping = new ServletMapping();
        mapping.setServletName(holder.getName());
        String path = uri.getPath();
        mapping.setPathSpec(path);
        servletHandler.addServletMapping(mapping);
        System.out.println("addServletMapping port: " + port + " path: " + path);
    }

    public Servlet removeServletMapping(String uri) {
        Servlet removedServlet = null;
        List<ServletMapping> mappings =
            new ArrayList<ServletMapping>(Arrays.asList(servletHandler.getServletMappings()));
        String path = URI.create(uri).getPath();
        for (ServletMapping mapping : mappings) {
            if (Arrays.asList(mapping.getPathSpecs()).contains(path)) {
                try {
                    removedServlet = servletHandler.getServlet(mapping.getServletName()).getServlet();
                } catch (ServletException e) {
                    throw new IllegalStateException(e);
                }
                mappings.remove(mapping);
                break;
            }
        }
        if (removedServlet != null) {
            servletHandler.setServletMappings((ServletMapping[])mappings.toArray(new ServletMapping[mappings.size()]));
        }
        return removedServlet;
    }

    /**
     * An integration wrapper to enable use of a {@link WorkScheduler} with Jetty
     */
    private class WorkSchedulerThreadPool implements ThreadPool {

        public boolean dispatch(Runnable work) {
            workScheduler.scheduleWork(work);
            return true;
        }

        public void join() throws InterruptedException {
            synchronized (joinLock) {
                joinLock.wait();
            }
        }

        public int getThreads() {
            throw new UnsupportedOperationException();
        }

        public int getIdleThreads() {
            throw new UnsupportedOperationException();
        }

        public boolean isLowOnThreads() {
            // TODO FIXME
            return false;
        }

        public void start() throws Exception {
        }

        public void stop() throws Exception {
        }

        public boolean isRunning() {
            return state == STARTING || state == STARTED;
        }

        public boolean isStarted() {
            return state == STARTED;
        }

        public boolean isStarting() {
            return state == STARTING;
        }

        public boolean isStopping() {
            return state == STOPPING;
        }

        public boolean isFailed() {
            return state == ERROR;
        }
    }

}
