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

import static org.apache.tuscany.sca.node.osgi.launcher.NodeLauncherUtil.domainManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple launcher for the SCA domain manager. 
 *
 * @version $Rev$ $Date$
 */
public class DomainManagerLauncher {

    private static final Logger logger = Logger.getLogger(DomainManagerLauncher.class.getName());

    /**
     * Constructs a new DomainManagerLauncher.
     */
    private DomainManagerLauncher() {
    }

    /**
     * Returns a new launcher instance.
     *  
     * @return a new launcher instance
     */
    public static DomainManagerLauncher newInstance() {
        return new DomainManagerLauncher();
    }

    /**
     * Creates a new DomainManager.
     * 
     * @return a new DomainManager
     * @throws LauncherException
     */
    public <T> T createDomainManager() throws LauncherException {
        return (T)domainManager(".");
    }

    /**
     * Creates a new DomainManager.
     * 
     * @param rootDirectory the domain's root configuration directory 
     * 
     * @return a new DomainManager
     * @throws LauncherException
     */
    public <T> T createDomainManager(String rootDirectory) throws LauncherException {
        return (T)domainManager(rootDirectory);
    }

    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Domain Manager starting...");

        // Create a domain manager
        DomainManagerLauncher launcher = newInstance();
        OSGiHost host = NodeLauncherUtil.startOSGi();
        try {

            Object domainManager = launcher.createDomainManager();

            // Start the domain manager
            try {
                domainManager.getClass().getMethod("start").invoke(domainManager);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA Domain Manager could not be started", e);
                throw e;
            }
            logger.info("SCA Domain Manager started.");

            logger.info("Press enter to shutdown.");
            try {
                System.in.read();
            } catch (IOException e) {
            }

            // Stop the domain manager
            try {
                domainManager.getClass().getMethod("stop").invoke(domainManager);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "SCA Domain Manager could not be stopped", e);
                throw e;
            }
        } finally {
            NodeLauncherUtil.stopOSGi(host);
        }
    }
}
