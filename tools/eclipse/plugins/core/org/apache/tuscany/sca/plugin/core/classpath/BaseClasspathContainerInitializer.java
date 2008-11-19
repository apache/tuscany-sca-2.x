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

import org.apache.tuscany.sca.plugin.core.classpath.RuntimeClasspathContainer;
import org.apache.tuscany.sca.plugin.core.classpath.RuntimeClasspathContainerInitializer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * A base classpath container implementation.
 *
 * @version $Rev$ $Date$
 */
public class BaseClasspathContainerInitializer extends ClasspathContainerInitializer implements RuntimeClasspathContainerInitializer {
    
    private RuntimeClasspathContainer container;
    
    public BaseClasspathContainerInitializer(RuntimeClasspathContainer container) {
        this.container = container;
    }

    @Override
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        JavaCore.setClasspathContainer(containerPath,
                                       new IJavaProject[] {project},
                                       new IClasspathContainer[] {container},
                                       null);
    }

    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return true;
    }
    
    public IClasspathContainer getRuntimeClasspathContainer() {
        return new IClasspathContainer() {
            public IClasspathEntry[] getClasspathEntries() {
                return container.getRuntimeClasspathEntries();
            }
            
            public String getDescription() {
                return container.getDescription();
            }
            
            public int getKind() {
                return container.getKind();
            }
            
            public IPath getPath() {
                return container.getPath();
            }
        };
    }
}
