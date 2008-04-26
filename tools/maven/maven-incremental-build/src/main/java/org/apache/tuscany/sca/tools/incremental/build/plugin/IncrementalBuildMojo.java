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
package org.apache.tuscany.sca.tools.incremental.build.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * @version $Rev$ $Date$
 * @goal build
 * @phase validate
 * @requiresDependencyResolution test
 * @description Incrementally build project modules that depend on modified modules.
 */
public class IncrementalBuildMojo extends AbstractMojo {

    /**
     * Keep track of modified projects.
     */
    private static Set<String> modifiedProjectIDs = new HashSet<String>();

    /**
     * The current user system settings for use in Maven.
     *
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;
    
    /**
     * Used to invoke Maven builds.
     *
     * @component
     */
    private Invoker invoker;

    /**
     * The target directory of the compiler if fork is true.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File buildDirectory;

    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private File outputFile;

    public void execute() throws MojoExecutionException {
        String projectID = id(project);
        outputFile = getOutputFile();

        File testMarkerFile = new File(project.getBasedir().getPath() + "/.test");
        
        List<String> goals = new ArrayList<String>();
        String type = project.getArtifact().getType();
        if ("pom".equals(type)) {
            
            // Always install pom modules
            goals.add("install");

        } else {

            // Check if anything has changed in the project
            boolean changed = false;
            boolean testChanged = false;
            boolean testFailed = false;
            if (new File(project.getBasedir().getPath() + "/.modified").exists()) {
                getLog().info("Found .modified marker file.");
                changed = true;
            } else {
                changed = areSourcesStale() || areResourcesStale() || isPOMStale();
            }
            if (changed) {
                modifiedProjectIDs.add(projectID);
            } else {
                testChanged = areTestSourcesStale() || areTestResourcesStale();
            }
            
            // Check if a project has compile dependencies on the modified projects
            // and will need to be recompiled, or has runtime or test dependencies
            // on the modified projects and needs to be retested
            if (changed) {
                goals.add("clean");
                goals.add("install");
                getLog().info("Project " + projectID + " has changed and will be recompiled.");
                
            } else {
                for (Artifact artifact : (List<Artifact>)project.getCompileArtifacts()) {
                    String artifactID = id(artifact);
                    if (modifiedProjectIDs.contains(artifactID)) {
                        getLog().info("Project " + projectID + " depends on modified project " + artifactID + " and will be recompiled.");
                        goals.add("clean");
                        goals.add("install");
                        break;
                    }
                }

                if (goals.isEmpty()) {
                    List<Artifact> artifacts = new ArrayList<Artifact>();
                    artifacts.addAll(project.getRuntimeArtifacts());
                    artifacts.addAll(project.getTestArtifacts());
                    for (Artifact artifact : artifacts) {
                        String artifactID = id(artifact);
                        if (modifiedProjectIDs.contains(artifactID)) {
                            getLog().info("Project " + projectID + " depends on modified project " + artifactID + " and will be retested.");
                            goals.add("test");
                            break;
                        }
                    }
                }
            }

            if (testChanged && goals.isEmpty()) {
                getLog().info("Project " + projectID + " has changed and will be retested.");
                goals.add("test");
            }

            if (goals.isEmpty()) {
                if (testMarkerFile.exists()) {
                    testFailed = true;
                    getLog().info("Project " + projectID + " contains failed tests and will be retested.");
                    goals.add("test");
                }
            }
        }

        // Invoke Maven with the necessary goals
        if (!goals.isEmpty()) {
            DefaultInvocationRequest request = new DefaultInvocationRequest();
            request.setGoals(goals);
            // FIXME: The maven invoker doesn't handle the directory names with spaces
            // request.setLocalRepositoryDirectory(new File(localRepository.getBasedir()));
            request.setInteractive(false);
            request.setShowErrors(false);
            request.setRecursive(false);
            // request.setDebug(true);
            request.setOffline(settings.isOffline());
            request.setBaseDirectory(project.getBasedir());
            request.setPomFile(project.getFile());

            boolean success = false;
            try {
                try {
                    InvocationResult result = invoker.execute(request);
    
                    CommandLineException cle = result.getExecutionException();
                    if (cle != null) {
                        throw new MojoExecutionException(cle.getMessage(), cle);
                    }
    
                    int ec = result.getExitCode();
                    if (ec != 0) {
                        throw new MojoExecutionException("Maven invocation exit code: " + ec);
                    }
                    
                    success = true;
                    
                    
                    
                } catch (MavenInvocationException e) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }
            } finally {
                
                // Create or delete a .test marker file to keep track of the latest
                // test result status and trigger testing again next time the build
                // is run
                if (!success) {
                    try {
                        if (!testMarkerFile.exists()) {
                            testMarkerFile.createNewFile();
                        }
                    } catch (IOException e) {
                        throw new MojoExecutionException(e.getMessage(), e);
                    }
                } else {
                    if (testMarkerFile.exists()) {
                        testMarkerFile.delete();
                    }
                }
            }
        } else {
            getLog().info("The project is up-to-date. No build is required.");
        }
    }

    private File getOutputFile() {
        File basedir = buildDirectory;
        String finalName = project.getBuild().getFinalName();
        String classifier = project.getArtifact().getClassifier();
        if (classifier == null) {
            classifier = "";
        } else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }
    
        String pkg = project.getPackaging();
        if ("maven-plugin".equals(pkg)) {
            pkg = "jar";
        }
        return new File(basedir, finalName + classifier + "." + pkg);
    }

    /**
     * Test if any of the resources are stale
     * @param resources
     * @param outputDirectory
     * @return
     * @throws MojoExecutionException
     */
    private boolean areStale(List<Resource> resources, String outputDirectory) throws MojoExecutionException {
        
        for (Resource resource: resources) {
            
            File resourceDirectory = new File(resource.getDirectory());
            if (!resourceDirectory.exists()) {
                continue;
            }
    
            DirectoryScanner scanner = new DirectoryScanner();
    
            scanner.setBasedir(resource.getDirectory());
            if (resource.getIncludes() != null && !resource.getIncludes().isEmpty()) {
                scanner.setIncludes((String[])resource.getIncludes().toArray(new String[]{}));
            } else {
                scanner.setIncludes(new String[]{"**/**"});
            }
            if (resource.getExcludes() != null && !resource.getExcludes().isEmpty()) {
                scanner.setExcludes((String[])resource.getExcludes().toArray(new String[]{}));
            }

            scanner.addDefaultExcludes();
            scanner.scan();
    
            List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());
            String targetPath = resource.getTargetPath();
            for (String source: includedFiles) {
                String target;
                if (source.endsWith(".java")) {
                    target = source.substring(0, source.length() - 5) + ".class";
                } else {
                    target = source;
                }
                
                String destination;
                if (targetPath != null) {
                    destination = targetPath + "/" + target;
                } else {
                    destination = target;
                }
    
                File sourceFile = new File(resource.getDirectory(), source);
                File destinationFile = new File(outputDirectory, destination);
                
                if (!destinationFile.exists()) {
                    getLog().info("Source file " + sourceFile + ".");
                    getLog().info("Target file " + destinationFile + " could not be found.");
                    return true;
                } else {
                    if (sourceFile.lastModified() > destinationFile.lastModified()) {
                        getLog().info("Source file " + sourceFile + " has changed.");
                        getLog().info("Target file " + destinationFile + " is stale.");
                        return true;
                    } else if (sourceFile.lastModified() > outputFile.lastModified()) {
                        getLog().info("Source file " + sourceFile + " has changed.");
                        getLog().info("Target build output file " + outputFile + " is stale.");
                        return true;
                    } else if (outputFile.lastModified() == 0) {
                        getLog().info("Target build output file " + outputFile + " could not be found.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Test if the POM resource is stale.
     * 
     * @return
     */
    private boolean isPOMStale() {
        File pom = project.getFile();
        if (pom.lastModified() > outputFile.lastModified()) {
            getLog().info("File " + pom + " has changed.");
            getLog().info("Target build output file " + pom + " is stale.");
            return true;
        } else if (outputFile.lastModified() == 0) {
            getLog().info("Target build output file " + outputFile + " could not be found.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Test if the project resources are stale.
     * 
     * @return
     * @throws MojoExecutionException
     */
    private boolean areResourcesStale() throws MojoExecutionException {
        return areStale(project.getResources(), project.getBuild().getOutputDirectory());
    }

    /**
     * Test if the project sources are stale.
     * 
     * @return
     * @throws MojoExecutionException
     */
    private boolean areSourcesStale() throws MojoExecutionException {
        List<Resource> resources = new ArrayList<Resource>();
        for (String root: (List<String>)project.getCompileSourceRoots()) {
            if (new File(root).exists()) {
                Resource resource = new Resource();
                resource.setDirectory(root);
                resource.addInclude("*.java");
                resources.add(resource);
            }
        }
        return areStale(resources, project.getBuild().getOutputDirectory());
    }

    /**
     * Tests if the project test resources are stale.
     * 
     * @return
     * @throws MojoExecutionException
     */
    private boolean areTestResourcesStale() throws MojoExecutionException {
        return areStale(project.getTestResources(), project.getBuild().getTestOutputDirectory());
    }

    /**
     * Tests if the project test sources are stale.
     * 
     * @return
     * @throws MojoExecutionException
     */
    private boolean areTestSourcesStale() throws MojoExecutionException {
        List<Resource> resources = new ArrayList<Resource>();
        for (String root: (List<String>)project.getTestCompileSourceRoots()) {
            if (new File(root).exists()) { 
                Resource resource = new Resource();
                resource.setDirectory(root);
                resources.add(resource);
            }
        }
        return areStale(resources, project.getBuild().getTestOutputDirectory());
    }

    /**
     * Returns the qualified id of a Maven artifact .
     * @param p a Maven artifact
     * @return a qualified id
     */
    private static String id(Artifact a) {
        return a.getGroupId() + ':' + a.getArtifactId();
    }

    /**
     * Returns the qualified id of a Maven project.
     * @param p a Maven project
     * @return a qualified id
     */
    private static String id(MavenProject p) {
        return p.getGroupId() + ':' + p.getArtifactId();
    }
}
