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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
    
    private final static String TUSCANY_SCA_DOMAIN_PROJECT = "tuscany-sca-domain"; 

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
            if (!isDomainManagerRunning()) {
                launchDomainManager(mode, file, launchManager, launchConfigurationType);
                if (!waitForDomainManager()) {
                    throw new RuntimeException("SCA Domain Manager could not be started.");
                }
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
                            ILaunchConfigurationType launchConfigurationType) throws CoreException, JavaModelException, IOException {
        
        // Get the Java project
        IJavaProject javaProject = JavaCore.create(file.getProject());
        
        // Get the contribution location and URI
        String contributionLocation = contributionLocation(javaProject);
        String contributionURI = contributionURI(javaProject);

        // Determine the composite file URI
        String compositeURI = compositeURI(javaProject, file);
        
        // Configure the node
        String nodeName = configureNode(contributionURI, contributionLocation, compositeURI);

        // Create a launch configuration
        ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null,
                                    launchManager.generateUniqueLaunchConfigurationNameFrom(file.getFullPath().removeFileExtension().lastSegment()));

        // Set the project and type to launch
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.node.launcher.NodeLauncher");
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());

        // Pass the URL of the node install image to the launcher
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                                   "http://localhost:9990/node-image/" + nodeName);

        // Save the configuration
        configuration.doSave();

        // Launch
        configuration.launch(mode, null);
    }
    
    /**
     * Launch the SCA domain manager.
     * 
     * @param mode
     * @param launchManager
     * @param launchConfigurationType
     * @throws CoreException
     * @throws JavaModelException
     */
    private void launchDomainManager(String mode,
                            IFile file,
                            ILaunchManager launchManager,
                            ILaunchConfigurationType launchConfigurationType) throws CoreException, JavaModelException {
        
        // Get the SCA domain project
        IProject domainProject = domainProject();

        // Create a launch configuration
        ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null,
                                    launchManager.generateUniqueLaunchConfigurationNameFrom("Apache Tuscany SCA Domain Manager"));

        // Set the project and type to launch
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.node.launcher.DomainNodeLauncher");
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, domainProject.getLocation().toString());

        // Save the configuration
        configuration.doSave();

        // Launch
        configuration.launch(mode, null);
    }

    private static final String PING_HEADER =
        "GET /ping HTTP/1.0\n" + "Host: localhost\n"
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
    private static boolean isDomainManagerRunning() {
        try {
            Socket client = new Socket("localhost", 9990);
            OutputStream os = client.getOutputStream();
            os.write(PING.getBytes());
            os.flush();
            String response = read(client);
            if (response.indexOf("<span id=\"ping\">") != -1) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Wait for domain to be running.
     * 
     * @return
     */
    private static boolean waitForDomainManager() throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            if (isDomainManagerRunning()) {
                return true;
            }
            Thread.sleep(500);
        }
        return false;
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
    
    private static final String START_HEADER1 =
        "GET /quickstart?";
    private static final String START_HEADER2 =
        " HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String START_CONTENT = "";

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
    private static String configureNode(String contributionURI, String contributionLocation, String compositeURI) throws IOException, CoreException {
        
        // Send the request to configure the node
        Socket client = new Socket("localhost", 9990);
        OutputStream os = client.getOutputStream();
        String request = START_HEADER1 +
            "contribution=" + contributionURI + "&location=" + contributionLocation + "&composite=" + compositeURI +
            START_HEADER2 + START_CONTENT.getBytes().length + "\n\n" + START_CONTENT;
        os.write(request.getBytes());
        os.flush();
        String response = read(client);
        
        // Refresh the domain project
        domainProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        
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
    
    /**
     * Returns the SCA domain project.
     * 
     * @return
     * @throws CoreException
     */
    private static IProject domainProject() throws CoreException {
        IProject domainProject = ResourcesPlugin.getWorkspace().getRoot().getProject(TUSCANY_SCA_DOMAIN_PROJECT);
        if (!domainProject.exists()) {
            domainProject.create(new NullProgressMonitor());
            domainProject.open(new NullProgressMonitor());
            
            String html = "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"refresh\" content=\"0;url=http://localhost:9990/ui/home\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<a href=\"http://localhost:9990/ui/home\">SCA Domain</a>\n" +
                "</body>\n" +
                "</html>"; 
            
            IFile file = domainProject.getFile(new Path("domain.html"));
            file.create(new ByteArrayInputStream(html.getBytes()), true, new NullProgressMonitor());
        }
        return domainProject;
    }
}
