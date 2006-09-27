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
package org.apache.tuscany.core.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tuscany.host.RuntimeInfo;

/**
 * @version $Rev$ $Date$
 */
public class LauncherRuntimeInfo implements RuntimeInfo {

    /** Install directory */
    private final File installDirectory;

    /** Application root directory */
    private final File applicationRootDirectory;

    /**
     * Initializes the installation and application root directories.
     * 
     * @param installDirectory Installation directory.
     * @param applicationRootDirectory Application root directory.
     */
    public LauncherRuntimeInfo(File installDirectory, File applicationRootDirectory) {
        this.installDirectory = installDirectory;
        this.applicationRootDirectory = applicationRootDirectory;
    }

    /**
     * Return the directory where the running runtime was installed.
     *
     * @return the directory where the runtime was installed
     */
    public File getInstallDirectory() {
        return installDirectory;
    }

    /**
     * Return the root directory used to resolve application file paths.
     *
     * @return the directory used to resolve application file paths.
     */
    public File getApplicationRootDirectory() {
        return applicationRootDirectory;
    }

    /**
     * Gets the base URL for the runtime.
     * 
     * @return The base URL for the runtime.
     */
    public URL getBaseURL() {
        try {
            return getInstallDirectory().toURL();
        } catch (MalformedURLException e) {
            // TODO Decide on how to handle the exception
            throw new RuntimeException(e);
        }
    }

}
