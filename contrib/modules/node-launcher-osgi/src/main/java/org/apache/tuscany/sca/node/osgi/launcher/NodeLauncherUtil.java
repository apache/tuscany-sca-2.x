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

import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
final class NodeLauncherUtil {
    // private static final Logger logger = Logger.getLogger(NodeLauncherUtil.class.getName());

    private static final String DOMAIN_MANAGER_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.domain.manager.launcher.DomainManagerLauncherBootstrap";

    private static final String NODE_IMPLEMENTATION_DAEMON_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationDaemonBootstrap";

    private static final String NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationLauncherBootstrap";

    /**
     * Collect JAR files under the given directory.
     * 
     * @p @param contributions
     * @throws LauncherException
     */
    static Object node(String configurationURI,
                       String compositeURI,
                       String compositeContent,
                       Contribution[] contributions,
                       ClassLoader contributionClassLoader) throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {

            // Use Java reflection to create the node as only the runtime class
            // loader knows the runtime classes required by the node
            String className = NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP;
            Class<?> bootstrapClass;
            bootstrapClass = Class.forName(className, false, tccl);

            Object bootstrap;
            if (configurationURI != null) {

                // Construct the node with a configuration URI
                bootstrap = bootstrapClass.getConstructor(String.class).newInstance(configurationURI);

            } else if (contributionClassLoader != null) {

                // Construct the node with a compositeURI and a classloader
                Constructor<?> constructor = bootstrapClass.getConstructor(String.class, ClassLoader.class);
                bootstrap = constructor.newInstance(compositeURI, contributionClassLoader);

            } else if (compositeContent != null) {

                // Construct the node with a composite URI, the composite content and
                // the URIs and locations of a list of contributions
                Constructor<?> constructor =
                    bootstrapClass.getConstructor(String.class, String.class, String[].class, String[].class);
                String[] uris = new String[contributions.length];
                String[] locations = new String[contributions.length];
                for (int i = 0; i < contributions.length; i++) {
                    uris[i] = contributions[i].getURI();
                    locations[i] = contributions[i].getLocation();
                }
                bootstrap = constructor.newInstance(compositeURI, compositeContent, uris, locations);

            } else {

                // Construct the node with a composite URI and the URIs and
                // locations of a list of contributions
                Constructor<?> constructor =
                    bootstrapClass.getConstructor(String.class, String[].class, String[].class);
                String[] uris = new String[contributions.length];
                String[] locations = new String[contributions.length];
                for (int i = 0; i < contributions.length; i++) {
                    uris[i] = contributions[i].getURI();
                    locations[i] = contributions[i].getLocation();
                }
                bootstrap = constructor.newInstance(compositeURI, uris, locations);
            }

            Object node = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            try {
                Class<?> type = Class.forName("org.apache.tuscany.sca.node.SCANodeFactory");
                type = type.getDeclaredClasses()[0];
                return type.getMethod("createProxy", Class.class, Object.class).invoke(null, type, node);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
            return node;

        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Creates a new node daemon.
     * 
     * @throws LauncherException
     */
    static Object nodeDaemon() throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {

            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = NODE_IMPLEMENTATION_DAEMON_BOOTSTRAP;
            Class<?> bootstrapClass;
            bootstrapClass = Class.forName(className, false, tccl);
            Object bootstrap = bootstrapClass.getConstructor().newInstance();

            Object nodeDaemon = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            return nodeDaemon;

        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node Daemon could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Creates a new domain manager.
     * 
     * @throws LauncherException
     */
    static Object domainManager(String rootDirectory) throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {

            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = DOMAIN_MANAGER_LAUNCHER_BOOTSTRAP;
            Class<?> bootstrapClass;
            bootstrapClass = Class.forName(className, false, tccl);
            Constructor<?> constructor = bootstrapClass.getConstructor(String.class);
            Object bootstrap = constructor.newInstance(rootDirectory);

            Object domainManager = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            return domainManager;

        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Domain Manager could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    static OSGiHost startOSGi() {
        OSGiHost host = new FelixOSGiHost();
        host.start();
        return host;
    }

    static void stopOSGi(OSGiHost host) {
        host.stop();
    }
    
    /*
    static OSGiHost getOSGiHost() throws Exception {
        ServiceDiscovery discovery = ServiceDiscovery.getInstance(Thread.currentThread().getContextClassLoader());
        Class<?> hostClass = discovery.loadFirstServiceClass(OSGiHost.class);
        if (hostClass != null) {
            return (OSGiHost) hostClass.newInstance();
        } else {
            return null;
        }
    }
    */

}
