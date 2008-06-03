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

import static org.apache.tuscany.sca.core.log.LogUtil.error;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class TuscanyClasspathContainer implements IClasspathContainer {
    
    public static final IPath TUSCANY_LIBRARY_CONTAINER = new Path("org.apache.tuscany.sca.runtime.library");  
    
    private static final String TUSCANY_HOME = "TUSCANY_HOME";
    private static final String TUSCANY_SRC = "TUSCANY_SRC";
    
    private static final String TUSCANY_FEATURE = "org.apache.tuscany.sca.feature";
    
    public TuscanyClasspathContainer() {
    }

    public IClasspathEntry[] getClasspathEntries() {
        List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
        
        // Get the runtime location from the installed Tuscany feature
        IPath runtimePath = null;
        try {
            
            // Find the Tuscany distribution under the feature's runtime directory
            // Typically runtime/distro-archive-name/un-archived-distro-dir
            URL url = Platform.getBundle(TUSCANY_FEATURE).getResource("runtime");
            File file = new File(url.getPath());
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
                    if (runtimePath == null) {
                        error("Tuscany runtime distribution directory not found", new FileNotFoundException(distro.getAbsolutePath()));
                    }
                } else {
                    error("Tuscany runtime distribution archive not found", new FileNotFoundException(file.getAbsolutePath()));
                }
            } else {
                error("Tuscany runtime feature not found", new FileNotFoundException(file.getAbsolutePath()));
            }
        } catch (Exception e) {
            error("Tuscany runtime feature not found", e);
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
        
        // Get the source location from the installed Tuscany feature
        IPath sourcePath = null;
        try {

            // Find the Tuscany source distribution under the feature's src directory
            // Typically src/distro-archive-src.zip
            URL url = Platform.getBundle(TUSCANY_FEATURE).getResource("src");
            File file = new File(url.getPath());
            if (file.exists()) {
                File distro = null;
                for (File f: file.listFiles()) {
                    if (f.getName().contains("tuscany-sca") && f.getName().endsWith("-src.zip")) {
                        distro = f;
                        break;
                    }
                }
                if (distro != null) {
                    sourcePath = new Path(distro.getPath());
                }
            }
        } catch (Exception e) {
        }

        if (sourcePath == null) {
            
            // Try to get the location of the Tuscany source distribution from
            // the TUSCANY_SRC property or environment variable
            String source = System.getProperty(TUSCANY_SRC);
            if (source == null || source.length() == 0) {
                source = System.getenv(TUSCANY_SRC);
            }
            if (source != null && source.length() != 0) {
                if (new File(source).exists()) {
                    sourcePath = new Path(source);
                }
            }
        }
        
        // Add the JARs from runtime/lib and runtime/modules as classpath entries
        if (runtimePath != null) {
            
            // Add a selection of the jars from runtime/modules
            File modulesDirectory = runtimePath.append("modules").toFile();
            if (modulesDirectory != null && modulesDirectory.exists()) {
                for (File file : modulesDirectory.listFiles()) {
                    IPath path = new Path(file.getPath());
                    String name = path.lastSegment();
                    String extension = path.getFileExtension();
                    
                    // Only include API and launcher JARs
                    if (!"jar".equals(extension)) {
                        continue;
                    }
                    if (name.indexOf("-api-") == -1 && name.indexOf("-launcher-") == -1) {
                        continue;
                    }
                    if (name.startsWith("tuscany-node-api-") || name.startsWith("tuscany-domain-api-")) {
                        continue;
                    }

                    list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
                }
            }
        }
        
        return (IClasspathEntry[])list.toArray(new IClasspathEntry[list.size()]);
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
