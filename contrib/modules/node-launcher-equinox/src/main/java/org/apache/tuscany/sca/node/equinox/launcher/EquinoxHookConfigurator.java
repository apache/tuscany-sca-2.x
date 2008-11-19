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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;

/**
 * Hook Configurator for Equinox.
 *
 * @version $Rev: $ $Date: $
 */
public class EquinoxHookConfigurator implements HookConfigurator {
    private static Logger logger = Logger.getLogger(HookConfigurator.class.getName());
    
    private String[] jarFiles;
    private Manifest manifest;
    
    public EquinoxHookConfigurator() {

        // Get the list of JAR files to install
        String jarFilesProperty = System.getProperty("org.apache.tuscany.sca.node.launcher.equinox.jarFiles");
        jarFiles = jarFilesProperty.split(";");
        
        // Create a single 'library' bundle for them
        long libraryStart = System.currentTimeMillis();
        manifest = NodeLauncherUtil.libraryManifest(jarFiles);
        
        if (logger.isLoggable(Level.FINE)) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                manifest.write(bos);
                bos.close();
                logger.fine(new String(bos.toByteArray()));
            } catch (IOException e) {
            }
        }

        logger.info("Third-party library manifest generated in " + (System.currentTimeMillis() - libraryStart) + " ms");
        
    }
    
    public void addHooks(HookRegistry registry) {
        
        // Register our BundleFileFactory hook 
        registry.addBundleFileFactoryHook(new LibrariesBundleFileFactoryHook(manifest));
    }

}
