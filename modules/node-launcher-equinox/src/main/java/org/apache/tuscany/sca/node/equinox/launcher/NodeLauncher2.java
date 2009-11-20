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

package org.apache.tuscany.sca.node.equinox.launcher;

import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.node;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;

/**
 * A launcher for SCA nodes.
 *  
 * @version $Rev$ $Date$
 */
public class NodeLauncher2 {

    static final Logger logger = Logger.getLogger(NodeLauncher2.class.getName());
    private EquinoxHost equinoxHost;
    private BundleContext bundleContext;

    /**
     * Constructs a new node launcher.
     */
    private NodeLauncher2() {
        equinoxHost = new EquinoxHost();
        bundleContext = equinoxHost.start();
    }

    /**
     * Returns a new launcher instance.
     *  
     * @return a new launcher instance
     */
    public static NodeLauncher2 newInstance() {
        return new NodeLauncher2();
    }

    /**
     * Creates a new SCA node from the configuration URL
     * 
     * @param configurationURL the URL of the node configuration which is the ATOM feed
     * that contains the URI of the composite and a collection of URLs for the contributions
     *  
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String configurationURL) throws LauncherException {
        return (T)node(configurationURL, null, null, null, bundleContext);
    }

    /**
     * Creates a new SCA OSGi Node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related 
     * artifacts. If the list is empty, then we will use the thread context classloader to discover
     * the contribution on the classpath
     *   
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, Contribution... contributions) throws LauncherException {
        return (T)node(null, compositeURI, null, contributions, bundleContext);
    }

    /**
     * Creates a new SCA OSGi Node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param compositeContent the XML content of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related artifacts 
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, String compositeContent, Contribution... contributions)
        throws LauncherException {
        return (T)node(null, compositeURI, compositeContent, contributions, bundleContext);
    }

    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Node is starting...");

        // Create a node launcher
        NodeLauncher2 launcher = newInstance();

        EquinoxHost equinox = launcher.equinoxHost;
        Object node = null;
        ShutdownThread shutdown = null;
        try {

          Contribution[] contributions = new Contribution[args.length];
          for (int i=0; i<args.length; i++) {
              File f = new File(args[i]);
              if (!f.exists()) {
                  System.err.println("contribution not found: " + f);
                  System.exit(1);
              }
              contributions[i] = new Contribution(f.toURI().toString(), f.toURI().toString());
          }

          // Create a node from a composite URI and a contribution location
//          String contributionLocation = args[1];
//          logger.info("SCA composite: " + null);
//          logger.info("SCA contribution: " + contributionLocation);
          node = launcher.createNode(null, contributions);
            
            // Start the node
            try {
                node.getClass().getMethod("start").invoke(node);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA Node could not be started", e);
                throw e;
            }
            logger.info("SCA Node is now started.");
            
            // Install a shutdown hook
            shutdown = new ShutdownThread(node, equinox);
            Runtime.getRuntime().addShutdownHook(shutdown);
            
            logger.info("Press enter to shutdown.");
            try {
                System.in.read();
            } catch (IOException e) {
                
                // Wait forever
                Object lock = new Object();
                synchronized(lock) {
                    lock.wait();
                }
            }
        } finally {

            // Remove the shutdown hook
            if (shutdown != null) {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            }
            
            // Stop the node
            if (node != null) {
                destroyNode(node);
            }
            if (equinox != null) {
                equinox.stop();
            }
        }
    }
    
    public void destroy() {
        if (equinoxHost != null) {
            equinoxHost.stop();
            bundleContext = null;
        }
    }

    /**
     * Stop the given node.
     * 
     * @param node
     * @throws Exception
     */
    private static void destroyNode(Object node) throws Exception {
        try {
            node.getClass().getMethod("stop").invoke(node);
            node.getClass().getMethod("destroy").invoke(node);
            logger.info("SCA Node is now stopped.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node could not be stopped", e);
            throw e;
        }
    }
    
    private static class ShutdownThread extends Thread {
        private Object node;
        private EquinoxHost equinox;

        public ShutdownThread(Object node, EquinoxHost equinox) {
            super();
            this.node = node;
            this.equinox = equinox;
        }

        @Override
        public void run() {
            try {
                destroyNode(node);
            } catch (Exception e) {
                // Ignore
            }
            try {
                equinox.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
