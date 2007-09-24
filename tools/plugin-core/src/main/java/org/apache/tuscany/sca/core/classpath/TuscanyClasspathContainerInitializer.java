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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * A classpath container initializer for the Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyClasspathContainerInitializer extends ClasspathContainerInitializer {

    @Override
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {

        TuscanyClasspathContainer classpathContainer = new TuscanyClasspathContainer();
        JavaCore.setClasspathContainer(containerPath,
                                       new IJavaProject[] {project},
                                       new IClasspathContainer[] {classpathContainer},
                                       null);
    }

    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return true;
    }
}
