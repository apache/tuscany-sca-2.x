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

package org.apache.tuscany.sca.plugin.core.classpath;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.plugin.core.classpath.RuntimeClasspathContainer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

/**
 * A base classpath container implementation.
 * 
 * @version $Rev$ $Date$
 */
public class BaseClasspathContainer implements RuntimeClasspathContainer {
    
    private String pluginID;
    private String libraryID;
    private String libraryName;
    private String distributionName;
    private String sourceDistributionName;
    private String distributionVersion;
    private String homeProperty;
    private String sourceProperty;
    
    /**
     * Constructs a new Classpath container.
     * 
     * @param pluginID
     * @param libraryID
     * @param libraryName
     * @param distributionName
     * @param distributionVersion
     * @param homeProperty
     * @param sourceProperty
     */
    public BaseClasspathContainer(String pluginID, String libraryID, String libraryName,
                                  String distributionName, String sourceDistributionName, String distributionVersion,
                                  String homeProperty, String sourceProperty) {
        this.pluginID = pluginID;
        this.libraryID = libraryID;
        this.libraryName = libraryName;
        this.distributionName = distributionName;
        this.sourceDistributionName = sourceDistributionName;
        this.distributionVersion = distributionVersion;
        this.homeProperty = homeProperty;
        this.sourceProperty = sourceProperty;
    }

    public IClasspathEntry[] getClasspathEntries() {
        List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
        
        // Get the runtime distribution location
        IPath runtimePath = runtimePath();
        
        // Get the source distribution location
        IPath sourcePath = sourcePath();
        
        // Add the JARs from runtime/lib and runtime/modules as classpath entries
        if (runtimePath != null) {
            
            // Add the jars from runtime/modules
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

                    list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
                }
            }

            // Add the jars from runtime/lib
            File libDirectory = runtimePath.append("lib").toFile();
            if (libDirectory != null && libDirectory.exists()) {
                for (File file : libDirectory.listFiles()) {
                    IPath path = new Path(file.getPath());
                    String name = path.lastSegment();
                    String extension = path.getFileExtension();
                    
                    // Only include jaxb, jaxws and jsr API JARs
                    if (!"jar".equals(extension)) {
                        continue;
                    }
                    if (name.indexOf("-api-") != -1) {
                        if (name.startsWith("jaxb") || name.startsWith("jaxws") || name.startsWith("jsr")) {
                            list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
                        }
                    }
                }
            }
        }
        
        return (IClasspathEntry[])list.toArray(new IClasspathEntry[list.size()]);
    }

    public IClasspathEntry[] getRuntimeClasspathEntries() {
        
        // Get the runtime distribution location
        IPath runtimePath = runtimePath();

        if (runtimePath != null) {
            return new IClasspathEntry[] {JavaCore.newLibraryEntry(runtimePath, null, null)};
        } else {
            return new IClasspathEntry[0];
        }
    }

    public String getDescription() {
        return libraryName;
    }

    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    public IPath getPath() {
        return new Path(libraryID);
    }

    /**
     * Returns the location of the runtime distribution.
     * 
     * @return
     */
    private IPath runtimePath() {
        IPath path = artifactLocation(pluginID, distributionName, distributionVersion, null, null);
        
        if (path == null) {
            
            // Try to get the location of the distribution from
            // the HOME property or environment variable
            String home = System.getProperty(homeProperty);
            if (home == null || home.length() == 0) {
                home = System.getenv(homeProperty);
            }
            if (home != null && home.length() != 0) {
                if (new File(home).exists()) {
                    path = new Path(home);
                }
            }
        }
        return path;
    }
    
    /**
     * Returns the location of the source distribution.
     * 
     * @return
     */
    private IPath sourcePath() {
        IPath path = artifactLocation(pluginID, sourceDistributionName, distributionVersion, "src", ".zip");

        if (path == null) {
            
            // Try to get the location of the source distribution from
            // the SRC property or environment variable
            String source = System.getProperty(sourceProperty);
            if (source == null || source.length() == 0) {
                source = System.getenv(sourceProperty);
            }
            if (source != null && source.length() != 0) {
                if (new File(source).exists()) {
                    path = new Path(source);
                }
            }
        }
        return path;
    }

    /**
     * Returns the location of the specified artifact.
     *  
     * @param pluginId
     * @param artifactId
     * @param version
     * @param classifier
     * @param extension
     * @return
     */
    private IPath artifactLocation(String pluginId, String artifactId, String version, String classifier, String extension) {
        String artifactName;
        if (classifier != null) {
            artifactName = artifactId + '-' + version + '-' + classifier; 
        } else {
            artifactName = artifactId + '-' + version; 
        }
        if (extension != null) {
            artifactName += extension;
        }
        try {
            Bundle bundle = Platform.getBundle(pluginId);
            URL location = FileLocator.find(bundle, new Path(artifactName), null);
            location = FileLocator.toFileURL(location);
            IPath path = new Path(new File(location.toURI()).getPath());
            return path;
        } catch (Exception e) {
            error("Artifact not found: " + artifactName, e);
            return null;
        }
    }

    /**
     * Log an error.
     * 
     * @param msg
     * @param e
     */
    private void error(String msg, Exception e) {
        Platform.getLog(
                        Platform.getBundle(pluginID)).log(
                        new Status(IStatus.ERROR, pluginID, IStatus.OK, msg, e));
    }

}
