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
package org.apache.tuscany.maven.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tuscany.sca.shell.Shell;

/**
 * Maven Mojo to run the Tuscany Shell and install the project as an SCA contribution.
 * 
 * Invoked with "mvn tuscany:run"
 * 
 * @goal run
 * @requiresDependencyResolution runtime
 * @execute phase="test-compile"
 * @description Runs Tuscany directly from a SCA conribution maven project
 */
public class TuscanyRunMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The project artifactId.
     * 
     * @parameter expression="${project.artifactId}"
     * @required
     */
    protected String artifactId;

    /**
     * The project packaging.
     * 
     * @parameter expression=".${project.packaging}"
     * @required
     */
    protected String packaging;

    /**
     * The project build output directory
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    protected File buildDirectory;

    /**
     * The project build output directory
     * 
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    protected File finalName;

    /**
     * @parameter expression="${domainURI}" default-value="default"
     */
    private String domainURI;

    /**
     * @parameter expression="${nodeConfig}"
     */
    private String nodeConfig;

    /**
     * @parameter expression="${contributions}" 
     */
    private String[] contributions;

    /**
     * @parameter expression="${mainClass}"
     */
    private String mainClass;

    /**
     * @parameter expression="${arguments}"
     */
    private String[] arguments;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (mainClass != null) {
            executeMainMethod();
        } else if (".war".equals(packaging)) {
            try {
                executeTomcat();
            } catch (Exception e) {
                throw new MojoExecutionException("Exception running Tuscany/Tomcat", e);
            }
        } else {
            executeShell();
        }
    }

    private void executeTomcat() throws ServletException, LifecycleException, MalformedURLException {
        getLog().info("Starting Tuscany/Tomcat...");
        
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.setBaseDir("target");
        String appBase = "../src/main/webapp";
        tomcat.getHost().setAppBase(".");
        String contextPath = "/" + artifactId;

        StandardServer server = (StandardServer)tomcat.getServer();
        server.setParentClassLoader(getMainClassLoader());
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);

        tomcat.addWebapp(contextPath, appBase);
        tomcat.start();
        tomcat.getServer().await();
    }

    private void executeShell() throws MojoExecutionException {
        getLog().info("Starting Tuscany Shell...");

        List<String> contributionList = new ArrayList<String>();

        addProjectContribution(contributionList);

        addAdditionalContributions(contributionList);

        contributionList.add(0, "-help");
        contributionList.add(0, domainURI);
        
        try {
            Shell.main(contributionList.toArray(new String[contributionList.size()]));
        } catch (Exception e) {
            throw new MojoExecutionException("Exception in Shell", e);
        }
        
        getLog().info("Tuscany Shell stopped.");
    }

    private void addAdditionalContributions(List<String> contributionList) throws MojoExecutionException {
        if (contributions != null) {
            for (String s : contributions) {
                if (new File(s).exists()) {
                    contributionList.add(s);
                } else {
                    boolean found = false;
                    for (Object o : project.getDependencyArtifacts()) {
                        Artifact a = (Artifact) o;
                        if (a.getId().startsWith(s)) {
                            try {
                                contributionList.add(a.getFile().toURI().toURL().toString());
                            } catch (MalformedURLException e) {
                                throw new MojoExecutionException("", e);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException("Contribution not found as file or dependency: " + s);
                    }
                }
            }
        }
    }

    protected void addProjectContribution(List<String> cs) throws MojoExecutionException {
        try {

            File contributionFile = new File(buildDirectory.getParent(), finalName.getName());
            if (!contributionFile.exists()) {
                contributionFile = new File(buildDirectory.getParent(), "classes");
            }
            if (!contributionFile.exists()) {
                contributionFile = new File(buildDirectory.getParent(), finalName.getName() + packaging);
            }
            
            String contribution = contributionFile.toURI().toURL().toString();
            getLog().info("Project contribution: " + contribution);
            cs.add(contribution);
            
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("", e);
        }
    }

    public void executeMainMethod() throws MojoExecutionException, MojoFailureException {
        getLog().info("Invoking " + mainClass + " class main method...");
        
        if (arguments == null) {
            arguments = new String[0];
        }

        try {
            Method main = getMainClassLoader().loadClass(mainClass).getMethod("main", new Class[] {String[].class});
            main.invoke(main, new Object[] {arguments});
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("The specified mainClass doesn't contain a main method with appropriate signature", e);
        } catch (Exception e) {
            throw new MojoExecutionException("exception invoking main method", e);
        }
    }

    private ClassLoader getMainClassLoader() throws MalformedURLException {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        List<URL> classPathEntries = new ArrayList<URL>();
        classPathEntries.add(new File(project.getBuild().getOutputDirectory()).toURI().toURL());
        for (Object o : project.getRuntimeArtifacts()) {
            Artifact a = (Artifact) o;
            classPathEntries.add( a.getFile().toURI().toURL() );
        }
        return new URLClassLoader((URL[])classPathEntries.toArray(new URL[]{}), parent );
    }

}
