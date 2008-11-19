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

import static org.apache.tuscany.sca.node.osgi.launcher.NodeLauncherUtil.node;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A launcher for SCA nodes.
 *  
 * @version $Rev$ $Date$
 */
public class NodeLauncher {

    static final Logger logger = Logger.getLogger(NodeLauncher.class.getName());

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
     * Creates a new SCA node from the configuration URL
     * 
     * @param configurationURL the URL of the node configuration which is the ATOM feed
     * that contains the URI of the composite and a collection of URLs for the contributions
     *  
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNodeFromURL(String configurationURL) throws LauncherException {
        return (T)node(configurationURL, null, null, null, null);
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
        return (T)node(null, compositeURI, null, contributions, null);
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
        return (T)node(null, compositeURI, compositeContent, contributions, null);
    }

    /**
     * Create a SCA node based on the discovery of the contribution on the classpath for the 
     * given classloader. This method should be treated a convenient shortcut with the following
     * assumptions:
     * <ul>
     * <li>This is a standalone application and there is a deployable composite file on the classpath.
     * <li>There is only one contribution which contains the deployable composite file physically in its packaging hierarchy.
     * </ul> 
     * 
     * @param compositeURI The URI of the composite file relative to the root of the enclosing contribution
     * @param classLoader The ClassLoader used to load the composite file as a resource. If the value is null,
     * then thread context classloader will be used
     * @return A newly created SCA node
     */
    public <T> T createNodeFromClassLoader(String compositeURI, ClassLoader classLoader) throws LauncherException {
        return (T)node(null, compositeURI, null, null, classLoader);
    }

    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA OSGi Node is starting...");

        // Create a node
        NodeLauncher launcher = newInstance();

        OSGiHost host = NodeLauncherUtil.startOSGi();
        try {

            Object node;
            if (args.length == 1) {

                // Create from a configuration URI
                String configurationURI = args[0];
                logger.info("SCA OSGi Node configuration: " + configurationURI);
                node = launcher.createNodeFromURL(configurationURI);
            } else {

                // Create from a composite URI and a contribution location
                String compositeURI = args[0];
                String contributionLocation = args[1];
                logger.info("SCA composite: " + compositeURI);
                logger.info("SCA contribution: " + contributionLocation);
                node = launcher.createNode(compositeURI, new Contribution("default", contributionLocation));
            }

            // Start the node
            try {
                node.getClass().getMethod("start").invoke(node);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA OSGi Node could not be started", e);
                throw e;
            }
            logger.info("SCA OSGi Node is now started.");

            logger.info("Press Enter to shutdown...");
            try {
                System.in.read();
            } catch (IOException e) {
            }

            // Stop the node
            try {
                node.getClass().getMethod("stop").invoke(node);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA OSGi Node could not be stopped", e);
                throw e;
            }
        } finally {
            NodeLauncherUtil.stopOSGi(host);
        }
    }

}
