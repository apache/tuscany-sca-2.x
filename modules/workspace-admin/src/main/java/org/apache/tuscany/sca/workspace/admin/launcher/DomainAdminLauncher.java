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

package org.apache.tuscany.sca.workspace.admin.launcher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple launcher for the SCA domain administration application. 
 *
 * @version $Rev$ $Date$
 */
public class DomainAdminLauncher {
    
    private final static Logger logger = Logger.getLogger(DomainAdminLauncher.class.getName());    

    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Domain Administration starting...");

        Class<?> adminClass;
        Object admin;
        try {
            // Set up runtime classloader
            ClassLoader runtimeClassLoader = DomainAdminLauncherUtil.runtimeClassLoader();
            Thread.currentThread().setContextClassLoader(runtimeClassLoader);

            // Create the daemon
            
            // We use Java reflection here as only the runtime class
            // loader knows the runtime classes required by the daemon
            adminClass = runtimeClassLoader.loadClass("org.apache.tuscany.sca.workspace.admin.launcher.DomainAdminLauncherBootstrap"); 
            admin = adminClass.getConstructor().newInstance();
            
            // Start the daemon
            adminClass.getMethod("start").invoke(admin);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Domain Administration could not be started", e);
            throw e;
        }
        
        logger.info("SCA Domain Administration started.");
        logger.info("Press enter to shutdown.");
        try {
            System.in.read();
        } catch (IOException e) {}

        // Stop the daemon
        try {
            adminClass.getMethod("stop").invoke(admin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Domain Administration could not be stopped", e);
            throw e;
        }
    }

}
