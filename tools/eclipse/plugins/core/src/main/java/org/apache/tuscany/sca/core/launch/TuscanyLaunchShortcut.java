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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
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

            // Get our launch configuration type
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType launchConfigurationType =launchManager.getLaunchConfigurationType(
                                                                                           "org.apache.tuscany.sca.core.launch.configurationtype");

            // If the SCA domain controller is not running yet, launch it
            if (!isDomainControllerRunning()) {
                launchDomainController(mode, file, launchManager, launchConfigurationType);
            }

            // Launch an SCA node 
            launchNode(mode, file, launchManager, launchConfigurationType);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launch(IEditorPart editor, String mode) {
        //TODO later...
    }

    /**
     * Launch an SCA node.
     * 
     * @param mode
     * @param file
     * @param launchManager
     * @param launchConfigurationType
     * @throws CoreException
     * @throws JavaModelException
     */
    private void launchNode(String mode,
                            IFile file,
                            ILaunchManager launchManager,
                            ILaunchConfigurationType launchConfigurationType) throws CoreException, JavaModelException {
        
        // Create a launch configuration
        ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null,
                                    launchManager.generateUniqueLaunchConfigurationNameFrom(file.getFullPath().removeFileExtension().lastSegment()));

        // Get the Java project
        IJavaProject javaProject = JavaCore.create(file.getProject());

        // Set the project and type to launch
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.node.launch.SCANodeLauncher");
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());

        // Find the Java source container containing the selected .composite file
        IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
        int sourceFolderSegments = 0;
        for (IClasspathEntry entry : classpathEntries)
            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                sourceFolderSegments = entry.getPath().matchingFirstSegments(file.getFullPath());
                if (sourceFolderSegments > 0)
                    break;
            }

        // Pass the path of the .composite relative to the source folder to the launcher
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                                   file.getFullPath().removeFirstSegments(sourceFolderSegments).toString());

        // Save the configuration
        configuration.doSave();

        // Launch
        configuration.launch(mode, null);
    }
    
    /**
     * Launch the SCA domain controller.
     * 
     * @param mode
     * @param launchManager
     * @param launchConfigurationType
     * @throws CoreException
     * @throws JavaModelException
     */
    private void launchDomainController(String mode,
                            IFile file,
                            ILaunchManager launchManager,
                            ILaunchConfigurationType launchConfigurationType) throws CoreException, JavaModelException {

        // Create a launch configuration
        ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null,
                                    launchManager.generateUniqueLaunchConfigurationNameFrom("Tuscany Domain Controller"));

        // Set the project and type to launch
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.domain.launch.SCADomainControllerLauncher");

        // Save the configuration
        configuration.doSave();

        // Launch
        configuration.launch(mode, null);
    }

    private static final String PING_HEADER =
        "GET /domain/index.html HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String PING_CONTENT = "";
    private static final String PING =
        PING_HEADER + PING_CONTENT.getBytes().length + "\n\n" + PING_CONTENT;

    /**
     * Returns true if the SCA domain controller is running.
     * 
     * @return
     */
    private boolean isDomainControllerRunning() {
        try {
            Socket client = new Socket("localhost", 9999);
            OutputStream os = client.getOutputStream();
            os.write(PING.getBytes());
            os.flush();
            String response = read(client);
            if (response.indexOf("Tuscany") != -1) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Read a String from a socket.
     * 
     * @param socket
     * @return
     * @throws IOException
     */
    private static String read(Socket socket) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
