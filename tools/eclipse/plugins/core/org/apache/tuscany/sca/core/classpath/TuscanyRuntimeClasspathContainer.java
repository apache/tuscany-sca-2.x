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

package org.apache.tuscany.sca.core.classpath;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

/**
 * A classpath container for the Tuscany runtime.
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyRuntimeClasspathContainer implements IClasspathContainer {
    
    public static final IPath TUSCANY_LIBRARY_CONTAINER = new Path("org.apache.tuscany.sca.runtime.library");  
    
    private static final String TUSCANY_HOME = "TUSCANY_HOME";
    
    private static final String TUSCANY_FEATURE = "features/org.apache.tuscany.sca.feature_1.2.0";
    
    private static final String TUSCANY_FEATURE_RUNTIME = TUSCANY_FEATURE + "/runtime"; 

    public TuscanyRuntimeClasspathContainer() {
    }

    public IClasspathEntry[] getClasspathEntries() {
        
        // Get the runtime location from the installed Tuscany feature
        IPath runtimePath = null;
        try {
            
            // Find the Tuscany distribution under the feature's runtime directory
            // Typically runtime/distro-archive-name/un-archived-distro-dir
            URL url = FileLocator.toFileURL(Platform.getInstallLocation().getURL());
            File file = new File(url.toURI());
            file = new File(file, TUSCANY_FEATURE_RUNTIME);
            if (file.exists()) {
                File distro = null;
                for (File f: file.listFiles()) {
                    if (f.getName().contains("tuscany-sca")) {
                        distro = f;
                        break;
                    }
                }
                if (distro != null) {
                    for (File f: distro.listFiles()) {
                        if (f.getName().contains("tuscany-sca")) {
                            runtimePath = new Path(f.getPath());
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        if (runtimePath == null) {

            // Try to get the location of the Tuscany binary distribution from
            // the TUSCANY_HOME property or environment variable
            String home = System.getProperty(TUSCANY_HOME);
            if (home == null || home.length() == 0) {
                home = System.getenv(TUSCANY_HOME);
            }
            if (home != null && home.length() != 0) {
                if (new File(home).exists()) {
                    runtimePath = new Path(home);
                }
            }
        }
        
        if (runtimePath != null) {
            return new IClasspathEntry[] {JavaCore.newLibraryEntry(runtimePath, null, null)};
        } else {
            return new IClasspathEntry[0];
        }
    }

    public String getDescription() {
        return "Tuscany Library";
    }

    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    public IPath getPath() {
        return TUSCANY_LIBRARY_CONTAINER;
    }

}
