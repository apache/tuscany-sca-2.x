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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

/**
 * A classpath container for the Tuscany runtime.
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyClasspathContainer implements IClasspathContainer {
    
    private static final String TUSCANY_HOME = "TUSCANY_HOME";
    private static final String TUSCANY_SRC = "TUSCANY_SRC";

    public TuscanyClasspathContainer() {
    }

    public IClasspathEntry[] getClasspathEntries() {
        List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
        
        // Get the runtime plugin location
//        IPath runtimePath;
//        try {
//            URL url = FileLocator.toFileURL(Platform.getBundle("org.apache.tuscany.sca.runtime").getEntry("/"));
//            runtimePath = new Path(url.getFile());
//        } catch (IOException e) {
//            throw new IllegalArgumentException(e);
//        }
        
        // Get the location of the Tuscany binary distribution from
        // the TUSCANY_SOURCE property or environment variable
        String home = System.getProperty(TUSCANY_HOME);
        if (home == null || home.length() == 0) {
            home = System.getenv(TUSCANY_HOME);
        }
        if (home != null && home.length() != 0) {
            IPath runtimePath = new Path(home);

            // Get the location of the Tuscany source distribution from
            // the TUSCANY_SOURCE property or environment variable
            String source = System.getProperty(TUSCANY_SRC);
            if (source == null || source.length() == 0) {
                source = System.getenv(TUSCANY_SRC);
            }
            IPath sourcePath;
            if (source != null && source.length() != 0) {
                sourcePath = new Path(source);
            } else {
                sourcePath = null;
            }
            
            // Add JARs from runtime/lib and runtime/modules as classpath entries
            for (String directory: new String[]{"modules", "lib"}) {
                File parent = runtimePath.append(directory).toFile();
                if (parent != null && parent.exists()) {
                    for (File file : parent.listFiles()) {
                        IPath path = new Path(file.getPath());
                        String extension = path.getFileExtension();
                        if (!"jar".equals(extension)) {
                            continue;
                        }
                        list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
                    }
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
        return new Path("org.apache.tuscany.sca.runtime.library");
    }

}
