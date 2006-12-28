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
package org.apache.tuscany.standalone.server;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * Utility class for directory related operations.
 * 
 * @version $Revision$ $Date$
 *
 */
public abstract class DirectoryHelper {
    
    /** Installation directory system property. */
    private static final String INSTALL_DIRECTORY_PROPERTY = "tuscany.installDir";
    
    /**
     * Utility class constructor.
     *
     */
    private DirectoryHelper() {
    }
    
    /**
     * Gets the installation directory.
     * @return Directory where tuscany standalone server is installed.
     */
    static final File getInstallDirectory() {
        
        File installDirectory = null;
        String installDirectoryPath = System.getProperty(INSTALL_DIRECTORY_PROPERTY);
        
        if (installDirectoryPath != null) {
            installDirectory = new File(installDirectoryPath);
        } else {

            // use the parent of directory containing this command
            URL url = TuscanyServer.class.getResource("TuscanyServer.class");
            if (!"jar".equals(url.getProtocol())) {
                throw new IllegalStateException("Must be run from a jar: " + url);
            }
    
            String jarLocation = url.toString();
            jarLocation = jarLocation.substring(4, jarLocation.lastIndexOf("!/"));
            if (!jarLocation.startsWith("file:")) {
                throw new IllegalStateException("Must be run from a local filesystem: " + jarLocation);
            }
    
            File jarFile = new File(URI.create(jarLocation));
            installDirectory = jarFile.getParentFile().getParentFile();    
        }

        if(!installDirectory.exists()) {
            throw new IllegalStateException("Install directory doesn't exist: " + installDirectory);
        }
        
        return installDirectory;
        
    }
    
    /**
     * Gets the boot directory where all the boot libraries are stored. This 
     * is expected to be a directory named <code>boot</code> under the install 
     * directory.
     * 
     * @param installDirectory Tuscany install directory.
     * @param bootPath Boot path for the runtime.
     * @return Tuscany boot directory.
     */
    static final File getBootDirectory(File installDirectory, String bootPath) {
        
        File bootDirectory =  new File(installDirectory, bootPath);  
        if(!bootDirectory.exists()) {
            throw new IllegalStateException("Boot directory doesn't exist: " + bootDirectory.getAbsolutePath());
        }
        return bootDirectory;
        
    }

}
