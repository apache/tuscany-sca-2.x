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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class TuscanyClasspathContainer implements IClasspathContainer {

    public TuscanyClasspathContainer() {
    }

    public IClasspathEntry[] getClasspathEntries() {
        
        // Get the runtime plugin location
        IPath runtimePath;
        try {
            URL url = FileLocator.toFileURL(Platform.getBundle("org.apache.tuscany.sca.runtime").getEntry("/"));
            runtimePath = new Path(url.getFile());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        // Add Jars from runtime/lib as classpath entries
        List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
        for (File file : runtimePath.append("lib").toFile().listFiles()) {
            IPath path = new Path(file.getPath());
            if (!"jar".equals(path.getFileExtension())) {
                continue;
            }
            if (path.lastSegment().equals("tuscany-sca-all-1.0-incubating.jar")) {
                IPath sourcePath =
                    runtimePath.append("src/apache-tuscany-sca-1.0-incubating-src.zip");
                list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
            } else if (path.lastSegment().equals("tuscany-sca-all-1.1-incubating-SNAPSHOT.jar")) {
                IPath sourcePath =
                    runtimePath.append("src/apache-tuscany-sca-1.1-incubating-SNAPSHOT-src.zip");
                list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
            } else if (path.lastSegment().equals("tuscany-sca-all-1.2-incubating-SNAPSHOT.jar")) {
                IPath sourcePath =
                    runtimePath.append("src/apache-tuscany-sca-1.2-incubating-SNAPSHOT-src.zip");
                list.add(JavaCore.newLibraryEntry(path, sourcePath, null));
            } else
                list.add(JavaCore.newLibraryEntry(path, null, null));
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
