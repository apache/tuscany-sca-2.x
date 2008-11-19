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

package org.apache.tuscany.sca.node.osgi.launcher;

import static org.apache.tuscany.sca.node.osgi.launcher.NodeLauncherUtil.nodeDaemon;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A launcher for the SCA Node daemon.
 *  
 * @version $Rev$ $Date$
 */
public class NodeDaemonLauncher {

    private static final Logger logger = Logger.getLogger(NodeDaemonLauncher.class.getName());

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
        logger.info("Apache Tuscany SCA Node Daemon starting...");

        // Create a node daemon
        NodeDaemonLauncher launcher = newInstance();
        OSGiHost host = NodeLauncherUtil.startOSGi();

        try {
            Object daemon = launcher.createNodeDaemon();

            // Start the node daemon
            try {
                daemon.getClass().getMethod("start").invoke(daemon);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA Node Daemon could not be started", e);
                throw e;
            }
            logger.info("SCA Node Daemon started.");

            logger.info("Press enter to shutdown.");
            try {
                System.in.read();
            } catch (IOException e) {
            }

            // Stop the node daemon
            try {
                daemon.getClass().getMethod("stop").invoke(daemon);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA Node Daemon could not be stopped", e);
                throw e;
            }
        } finally {
            NodeLauncherUtil.stopOSGi(host);
        }
    }

}
