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

package org.apache.tuscany.sca.core.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

/**
 * A launch shortcut for SCA .composite files.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyLaunchShortcut implements ILaunchShortcut {

    public void launch(ISelection selection, String mode) {

        try {
            
            // Make sure we have a .composite file selected
            if (!(selection instanceof IStructuredSelection)) {
                return;
            }
            Object[] selections = ((IStructuredSelection)selection).toArray();
            if (selections.length == 0) {
                return;
            }
            IFile file = (IFile)selections[0];
            if (!file.getFileExtension().equals("composite")) {
                return;
            }

            // Get the Java project
            IJavaProject javaProject = JavaCore.create(file.getProject());

            // Find the Java source container containing the selected
            // .composite file
            IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
            int segments = 0;
            for (IClasspathEntry entry : classpathEntries)
                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    segments = entry.getPath().matchingFirstSegments(file.getFullPath());
                    if (segments > 0)
                        break;
                }

            // Get our launch configuration type
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType launchConfigurationType =
                launchManager.getLaunchConfigurationType("org.apache.tuscany.sca.core.launch.configurationtype");

            // Create a launch configuration
            ILaunchConfigurationWorkingCopy configuration =
                launchConfigurationType.newInstance(null,
                                                    launchManager.generateUniqueLaunchConfigurationNameFrom(file.getFullPath().removeFileExtension().lastSegment()));
            configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.host.embedded.impl.DefaultLauncher");
            configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());

            // Pass the path of the .composite relative to the classpath root to the launcher
            configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, file.getFullPath()
                .removeFirstSegments(segments).toString());

            // Save the configuration
            configuration.doSave();

            // Launch!!
            configuration.launch(mode, null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launch(IEditorPart editor, String mode) {
        //TODO later...
    }
}
