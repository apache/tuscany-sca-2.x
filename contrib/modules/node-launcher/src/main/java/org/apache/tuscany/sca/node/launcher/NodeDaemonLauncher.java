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

package org.apache.tuscany.sca.node.launcher;

import static org.apache.tuscany.sca.node.launcher.NodeLauncherUtil.nodeDaemon;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A launcher for the SCA Node daemon.
 *  
 * @version $Rev$ $Date$
 */
public class NodeDaemonLauncher {

    static final Logger logger = Logger.getLogger(NodeDaemonLauncher.class.getName());
    
    /**
     * Constructs a new node daemon launcher.
     */
    private NodeDaemonLauncher() {
    }
    
    /**
     * Returns a new launcher instance.
     *  
     * @return a new launcher instance
     */
    public static NodeDaemonLauncher newInstance() {
        return new NodeDaemonLauncher();
    }

    /**
     * Creates a new node daemon.
     * 
     * @param
     * @return a new node daemon
     * @throws LauncherException
     */
    public <T> T createNodeDaemon() throws LauncherException {
        return (T)nodeDaemon();
    }
    
    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Node Daemon is starting...");

        // Create a node launcher
        NodeDaemonLauncher launcher = newInstance();

        Object node = null;
        ShutdownThread shutdown = null;
        try {
            while (true) {
                
                // Start the node
                node = launcher.createNodeDaemon();
                try {
                    node.getClass().getMethod("start").invoke(node);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "SCA Node Daemon could not be started", e);
                    throw e;
                }
                logger.info("SCA Node Daemon is now started.");
                
                // Install a shutdown hook
                shutdown = new ShutdownThread(node);
                Runtime.getRuntime().addShutdownHook(shutdown);
                
                logger.info("Press 'q' to quit, 'r' to restart.");
                int k = 0;
                try {
                    while ((k != 'q') && (k != 'r')) {
                        k = System.in.read();
                    }
                } catch (IOException e) {
                    
                    // Wait forever
                    Object lock = new Object();
                    synchronized(lock) {
                        lock.wait();
                    }
                }

                // Stop the node
                if (node != null) {
                    Object n = node;
                    node = null;
                    stopNode(n);
                }
                
                // Quit
                if (k == 'q' ) {
                    break;
                }
            } 
        } catch (Exception e) {
            // Stop the node
            if (node != null) {
                try {
                    Object n = node;
                    node = null;
                    stopNode(n);
                } catch (Exception e2) {
                }
            }
            throw e;
            
        } finally {

            // Remove the shutdown hook
            if (shutdown != null) {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            }
        }
    }
    
    /**
     * Stop the given node.
     * 
     * @param node
     * @throws Exception
     */
    private static void stopNode(Object node) throws Exception {
        try {
            node.getClass().getMethod("stop").invoke(node);
            logger.info("SCA Node Daemon is now stopped.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node Daemon could not be stopped", e);
            throw e;
        }
    }
    
    private static class ShutdownThread extends Thread {
        private Object node;

        public ShutdownThread(Object node) {
            super();
            this.node = node;
        }

        public void run() {
            try {
                stopNode(node);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
