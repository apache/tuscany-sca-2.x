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

package org.apache.tuscany.sca.plugin.core.launch;

import static org.apache.tuscany.sca.plugin.core.launch.DomainManagerLauncherUtil.domainProject;
import static org.apache.tuscany.sca.plugin.core.launch.DomainManagerLauncherUtil.launchDomainManager;
import static org.apache.tuscany.sca.plugin.core.launch.TuscanyLaunchConfigurationDelegate.TUSCANY_LAUNCH_CONFIGURATIONTYPE;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;

import org.apache.tuscany.sca.plugin.core.classpath.RuntimeClasspathContainerInitializerExtensionPoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * Provides utility methods to launch SCA nodes.
 *
 * @version $Rev: $ $Date: $
 */
public class NodeLauncherUtil {

    private static final String START_HEADER1 =
        "GET /quickstart?";
    private static final String START_HEADER2 =
        " HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String START_CONTENT = "";

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
    static void launchNode(String mode,
                            IFile file,
                            IProgressMonitor progressMonitor) throws CoreException, JavaModelException, IOException, InterruptedException {
        progressMonitor.subTask("Starting SCA node");
        if (progressMonitor.isCanceled()) {
            return;
        }
        
        // First start the SCA domain manager
        launchDomainManager(new SubProgressMonitor(progressMonitor, 20));
        
        // Get the Java project
        IJavaProject javaProject = JavaCore.create(file.getProject());
        
        // Get the contribution location and URI
        String contributionLocation = contributionLocation(javaProject);
        String contributionURI = contributionURI(javaProject);
    
        // Determine the composite file URI
        String compositeURI = compositeURI(javaProject, file);
        
        // Configure the node
        String nodeName = configureNode(contributionURI, contributionLocation, compositeURI, progressMonitor);
    
        // Get the node launch configuration
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType launchConfigurationType =launchManager.getLaunchConfigurationType(TUSCANY_LAUNCH_CONFIGURATIONTYPE);
        ILaunchConfiguration configuration = null;
        for (ILaunchConfiguration c : launchManager.getLaunchConfigurations(launchConfigurationType)) {
            if (file.getFullPath().toString().equals(c.getAttribute("COMPOSITE_PATH", ""))) {
                configuration = c;
                break;
            }
        }
        
        if (configuration == null) {
            progressMonitor.worked(10);
            if (progressMonitor.isCanceled()) {
                return;
            }

            // Create a new launch configuration
            ILaunchConfigurationWorkingCopy newConfiguration = launchConfigurationType.newInstance(null,
                                        launchManager.generateUniqueLaunchConfigurationNameFrom(file.getName()));

            // Set the project and type to launch
            newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
            newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.node.launcher.NodeLauncher");

            // Pass the URL of the node install image to the launcher
            newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                                       "http://localhost:9990/node-config/" + nodeName);
            
            // Save the composite path in the launch configuration
            newConfiguration.setAttribute("COMPOSITE_PATH", file.getFullPath().toString());

            // Pass the runtime classpath as a system property
            newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "\"-DTUSCANY_PATH=" + RuntimeClasspathContainerInitializerExtensionPoint.installedRuntimeClasspath() + "\"");

            // Save the configuration
            newConfiguration.doSave();

            configuration = newConfiguration;
        }

        // Launch
        configuration.launch(mode, null);
    }

    /**
     * Returns the location of the SCA contribution representing a Java project. 
     * @param javaProject
     * @return
     * @throws MalformedURLException
     * @throws JavaModelException
     */
    private static String contributionLocation(IJavaProject javaProject) throws MalformedURLException, JavaModelException {
        IPath location = javaProject.getOutputLocation();
        IResource resource;
        if (location.segmentCount() == 1) {
            resource = javaProject.getProject();
        } else {
            resource = javaProject.getProject().getWorkspace().getRoot().getFolder(location);
        }
        location = resource.getLocation();
        String url = location.toFile().toURI().toURL().toString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * Returns the URI of the SCA contribution representing a Java project. 
     * @param javaProject
     * @return
     */
    private static String contributionURI(IJavaProject javaProject) {
        return javaProject.getProject().getName();
    }

    /**
     * Returns the SCA artifact URI of a composite file inside a Java project.
     * 
     * @param javaProject
     * @param file
     * @return
     * @throws JavaModelException
     */
    private static String compositeURI(IJavaProject javaProject, IFile file) throws JavaModelException {
    
        // Find the Java source container containing the specified file
        IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
        int sourceFolderSegments = 0;
        for (IClasspathEntry entry : classpathEntries) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                sourceFolderSegments = entry.getPath().matchingFirstSegments(file.getFullPath());
                if (sourceFolderSegments > 0)
                    break;
            }
        }
    
        // Determine the composite URI
        String compositeURI = file.getFullPath().removeFirstSegments(sourceFolderSegments).toString();
        return compositeURI;
    }

    /**
     * Send a request to the SCA domain manager to configure an SCA node for
     * the specified composite.
     *  
     * @param contributionURI
     * @param contributionLocation
     * @param compositeURI
     * @return
     * @throws IOException
     */
    private static String configureNode(String contributionURI, String contributionLocation, String compositeURI,
                                        IProgressMonitor progressMonitor) throws IOException, CoreException {
        progressMonitor.subTask("Configuring node");
        
        // Send the request to configure the node
        Socket client = new Socket("localhost", 9990);
        OutputStream os = client.getOutputStream();
        String request = START_HEADER1 +
            "contribution=" + contributionURI + "&location=" + contributionLocation + "&composite=" + compositeURI +
            START_HEADER2 + START_CONTENT.getBytes().length + "\n\n" + START_CONTENT;
        os.write(request.getBytes());
        os.flush();
        String response = DomainManagerLauncherUtil.read(client);
        
        // Refresh the domain project
        domainProject(progressMonitor).refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        
        int i = response.indexOf("<span id=\"node\">");
        if (i != -1) {
            
            // Extract the node name
            response = response.substring(i + 16);
            i = response.indexOf("</span>");
            String nodeName = response.substring(0, i);
            return nodeName;
            
        } else {
            throw new RuntimeException("Node could not be configured: " + response);
        }
    }

}
