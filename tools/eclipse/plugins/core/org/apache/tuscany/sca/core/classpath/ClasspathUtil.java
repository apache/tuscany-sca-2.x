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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * Utility functions to help determine the runtime classpath.
 *
 * @version $Rev: $ $Date: $
 */
public class ClasspathUtil {

    private static final String TUSCANY_RUNTIME_LIBRARIES = "org.apache.tuscany.sca.core.runtimeLibraries";

    private static final String TUSCANY_FEATURE = "org.apache.tuscany.sca.feature";
    private static final String TUSCANY_VERSION = "1.2.0";
    
    /**
     * Return the installed runtime classpath entries.
     * 
     * @return
     * @throws CoreException
     */
    public static String installedRuntimeClasspath() throws CoreException {
        
        List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>(); 
        for (IExtension extension: Platform.getExtensionRegistry().getExtensionPoint(TUSCANY_RUNTIME_LIBRARIES).getExtensions()) {
            for (IConfigurationElement configuration: extension.getConfigurationElements()) {
                IClasspathContainer container = (IClasspathContainer)configuration.createExecutableExtension("class");
                classpathEntries.addAll(Arrays.asList(container.getClasspathEntries()));
            }
        }
        
        String separator = System.getProperty("path.separator");
        StringBuffer classpath = new StringBuffer();
        for (int i = 0, n = classpathEntries.size(); i < n; i++) {
            IClasspathEntry entry = classpathEntries.get(i);
            if (i >0) {
                classpath.append(separator);
            }
            classpath.append(entry.getPath().toFile().toURI().getPath());
        }
        
        return classpath.toString();
    }

    /**
     * Returns the Tuscany feature location.
     *  
     * @return
     */
    static IPath feature() {
        try {
            URL location = Platform.getInstallLocation().getURL();
            File feature = new File(location.getPath() + "/features/" + TUSCANY_FEATURE + "_" + TUSCANY_VERSION);
            return new Path(feature.getPath());
        } catch (Exception e) {
            error("Tuscany runtime feature not found", e);
            return null;
        }
    }

    /**
     * Returns the location of the runtime distribution under the Tuscany feature.
     * 
     * @param feature
     * @return
     */
    static IPath runtime(IPath feature) {
        IPath runtimePath = null;
        try {
            
            // Find the Tuscany distribution under the feature's runtime directory
            // Typically runtime/distro-archive-name/un-archived-distro-dir
            File file = new File(feature.toFile(), "runtime");
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
        return runtimePath;
    }

    /**
     * Returns the location of the src distribution under the Tuscany feature.
     * 
     * @param feature
     * @return
     */
    static IPath src(IPath feature) {
        IPath sourcePath = null;
        try {

            // Find the Tuscany source distribution under the feature's src directory
            // Typically src/distro-archive-src.zip
            File file = new File(feature.toFile(), "src");
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
        return sourcePath;
    }
    
}
