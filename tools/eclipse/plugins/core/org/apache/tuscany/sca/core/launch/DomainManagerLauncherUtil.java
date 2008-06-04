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

import static org.apache.tuscany.sca.core.launch.TuscanyLaunchConfigurationDelegate.TUSCANY_LAUNCH_CONFIGURATIONTYPE;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.tuscany.sca.core.classpath.ClasspathUtil;
import org.apache.tuscany.sca.core.classpath.TuscanyClasspathContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Provides utility methods to launch the SCA Domain Manager.
 *
 * @version $Rev: $ $Date: $
 */
public class DomainManagerLauncherUtil {
    
    private static final String TUSCANY_DOMAIN_LAUNCH_CONFIGURATION = "SCA Domain Manager";
    private static final String TUSCANY_SCA_DOMAIN_PROJECT = "tuscany-sca-domain";

    private static final String PING_HEADER =
        "GET /ping HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String PING_CONTENT = "";
    private static final String PING =
        PING_HEADER + PING_CONTENT.getBytes().length + "\n\n" + PING_CONTENT;
    
    /**
     * Returns the SCA domain project.
     * 
     * @return
     * @throws CoreException
     */
    static IProject domainProject(IProgressMonitor progressMonitor) throws CoreException {
        
        IProject domainProject = ResourcesPlugin.getWorkspace().getRoot().getProject(TUSCANY_SCA_DOMAIN_PROJECT);
        if (progressMonitor.isCanceled()) {
            return domainProject;
        }
        if (!domainProject.exists()) {
            progressMonitor.subTask("Creating SCA domain resources");
            
            // Create SCA domain project if necessary 
            domainProject.create(new SubProgressMonitor(progressMonitor, 5));
            domainProject.open(new SubProgressMonitor(progressMonitor, 5));
            
            String html = "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"refresh\" content=\"0;url=http://localhost:9990/ui/home\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<a href=\"http://localhost:9990/ui/home\">SCA Domain</a>\n" +
                "</body>\n" +
                "</html>"; 
            
            IFile file = domainProject.getFile(new Path("domain.html"));
            file.create(new ByteArrayInputStream(html.getBytes()), true, new SubProgressMonitor(progressMonitor, 5));
            
            IProjectDescription description = domainProject.getDescription();
            String[] prevNatures= description.getNatureIds();
            String[] newNatures= new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length]= JavaCore.NATURE_ID;
            description.setNatureIds(newNatures);
            domainProject.setDescription(description, new SubProgressMonitor(progressMonitor, 5));
            
            IJavaProject javaProject = JavaCore.create(domainProject);
            javaProject.setOutputLocation(domainProject.getFullPath(), new SubProgressMonitor(progressMonitor, 5));
            
            IClasspathEntry classPath = JavaCore.newContainerEntry(TuscanyClasspathContainer.TUSCANY_LIBRARY_CONTAINER);
            IClasspathEntry jrePath = JavaRuntime.getDefaultJREContainerEntry();
            javaProject.setRawClasspath(new IClasspathEntry[] {jrePath, classPath}, new SubProgressMonitor(progressMonitor, 5));
        
        } else {
            domainProject.open(new SubProgressMonitor(progressMonitor, 5));
        }
        
        return domainProject;
    }

    /**
     * Returns true if the SCA domain controller is running.
     * 
     * @return
     */
    private static boolean isDomainManagerRunning() {
        try {
            Socket client = new Socket("localhost", 9990);
            OutputStream os = client.getOutputStream();
            os.write(DomainManagerLauncherUtil.PING.getBytes());
            os.flush();
            String response = DomainManagerLauncherUtil.read(client);
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
    private static boolean waitForDomainManager(IProgressMonitor progressMonitor) throws InterruptedException {
        progressMonitor.subTask("Contacting SCA domain manager");
        for (int i = 0; i < 40; i++) {
            if (progressMonitor.isCanceled()) {
                return false;
            }
            if (isDomainManagerRunning()) {
                return true;
            }
            Thread.sleep(500);
        }
        return false;
    }

    static void launchDomainManager(IProgressMonitor progressMonitor) throws JavaModelException, CoreException, InterruptedException {
        
        progressMonitor.beginTask("Starting SCA Domain Manager", 100);
        
        // If the SCA domain controller is not running yet, launch it
        if (!isDomainManagerRunning()) {

            progressMonitor.subTask("Starting SCA domain manager");
            if (progressMonitor.isCanceled()) {
                return;
            }
            
            // Get the SCA domain project
            IProject domainProject = domainProject(progressMonitor);
            
            // Get the domain manager launch configuration
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType launchConfigurationType =launchManager.getLaunchConfigurationType(TUSCANY_LAUNCH_CONFIGURATIONTYPE);
            ILaunchConfiguration configuration = null;
            for (ILaunchConfiguration c : launchManager.getLaunchConfigurations(launchConfigurationType)) {
                if (TUSCANY_DOMAIN_LAUNCH_CONFIGURATION.equals(c.getName())) {
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
                ILaunchConfigurationWorkingCopy newConfiguration = launchConfigurationType.newInstance(null, TUSCANY_DOMAIN_LAUNCH_CONFIGURATION);

                // Set the project and type to launch
                newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, domainProject.getProject().getName());
                newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.tuscany.sca.node.launcher.DomainManagerLauncher");
                newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, domainProject.getLocation().toString());
                
                // Pass the runtime classpath as a system property
                newConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "\"-DTUSCANY_PATH=" + ClasspathUtil.installedRuntimeClasspath() + "\"");

                // Save the configuration
                newConfiguration.doSave();

                configuration = newConfiguration;
            }

            // Launch
            configuration.launch(ILaunchManager.RUN_MODE, new SubProgressMonitor(progressMonitor, 20));
            
            if (progressMonitor.isCanceled()) {
                return;
            }
            if (!waitForDomainManager(progressMonitor)) {
                throw new RuntimeException("SCA Domain Manager could not be started.");
            }
        }
        if (progressMonitor.isCanceled()) {
            return;
        }
        progressMonitor.done();
            
    }

    /**
     * Read a String from a socket.
     * 
     * @param socket
     * @return
     * @throws IOException
     */
    static String read(Socket socket) throws IOException {
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
