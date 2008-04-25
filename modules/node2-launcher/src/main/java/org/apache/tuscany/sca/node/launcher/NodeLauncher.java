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

import static org.apache.tuscany.sca.node.launcher.NodeLauncherUtil.node;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A launcher for SCA nodes.
 *  
 * @version $Rev$ $Date$
 */
public class NodeLauncher {

    final static Logger logger = Logger.getLogger(NodeLauncher.class.getName());

    /**
     * Constructs a new node launcher.
     */
    private NodeLauncher() {
    }
    
    /**
     * Returns a new launcher instance.
     *  
     * @return a new launcher instance
     */
    public static NodeLauncher newInstance() {
        return new NodeLauncher();
    }

    /**
     * Creates a new node.
     * 
     * @param configurationURI
     * @return a new node
     * @throws LauncherException
     */
    public <T> T createNode(String configurationURI) throws LauncherException {
        return (T)node(configurationURI, null, null, null);
    }
    
    /**
     * Represents an SCA contribution uri + location.
     */
    public final static class Contribution {
        private String uri;
        private String location;
        
        /**
         * Constructs a new SCA contribution.
         * 
         * @param uri
         * @param location
         */
        public Contribution(String uri, String location) {
            this.uri = uri;
            this.location = location;
        }
        
        public String getURI() {
            return uri;
        }
        
        public String getLocation() {
            return location;
        }
    }
    
    /**
     * Creates a new Node.
     * 
     * @param compositeURI
     * @param contributions
     * @return a new node
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, Contribution...contributions) throws LauncherException {
        return (T)node(null, compositeURI, null, contributions);
    }
    
    /**
     * Creates a new Node.
     * 
     * @param compositeURI
     * @param compositeContent
     * @param contributions
     * @return a new node
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, String compositeContent, Contribution...contributions) throws LauncherException {
        return (T)node(null, compositeURI, compositeContent, contributions);
    }
    
    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Node starting...");

        // Create a node
        NodeLauncher launcher = newInstance();
        String configurationURI = args[0];
        logger.info("SCA Node configuration: " + configurationURI);
        Object node = launcher.createNode(configurationURI);
        
        // Start the node
        try {
            node.getClass().getMethod("start").invoke(node);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node could not be started", e);
            throw e;
        }
        logger.info("SCA Node started.");
        
        logger.info("Press enter to shutdown.");
        try {
            System.in.read();
        } catch (IOException e) {}

        // Stop the node
        try {
            node.getClass().getMethod("stop").invoke(node);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node could not be stopped", e);
            throw e;
        }
    }

}
