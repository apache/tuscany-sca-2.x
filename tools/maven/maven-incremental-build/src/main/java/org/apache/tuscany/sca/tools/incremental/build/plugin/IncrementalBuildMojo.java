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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
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
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The local repository where the artifacts are located
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

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
     * Keep track of modified projects.
     */
    private static Set<String> modifiedProjectIDs = new HashSet<String>();

    /**
     * Returns the qualified id of a project.
     * @param p a Maven project
     * @return a qualified id
     */
    private static String id(MavenProject p) {
        return p.getGroupId() + ':' + p.getArtifactId();
    }
    
    /**
     * Returns the qualified id of an artifact .
     * @param p a Maven artifact
     * @return a qualified id
     */
    private static String id(Artifact a) {
        return a.getGroupId() + ':' + a.getArtifactId();
    }
    
    public void execute() throws MojoExecutionException {
        String projectID = id(project);

        // Determine if the project has been modified
        // For now look for a .modified marker file but eventually we
        // should use "svn st" to detect modifications
        String marker = project.getBasedir().getPath()+ "/.modified";
        if (new File(marker).exists()) {
            getLog().info("Project: " + projectID + " has been modified.");
            modifiedProjectIDs.add(projectID);
        }

        // Check if a project has compile dependencies on the modified projects
        // and will need to be recompiled, or has runtime or test dependencies
        // on the modified projects and needs to be retested
        List<String> goals = new ArrayList<String>();

        for (Artifact artifact: (List<Artifact>)project.getCompileArtifacts()) {
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
            for (Artifact artifact: artifacts) {
                String artifactID = id(artifact);
                if (modifiedProjectIDs.contains(artifactID)) {
                    getLog().info("Project " + projectID + " depends on modified project " + artifactID + " and will be retested.");
                    goals.add("test");
                    break;
                }
            }
        }

        // Invoke Maven with the necessary goals
        if (!goals.isEmpty()) {
            InvocationRequest request = new DefaultInvocationRequest();
            request.setGoals(goals);
            request.setLocalRepositoryDirectory(new File(localRepository.getBasedir()));
            request.setInteractive(false);
            request.setShowErrors(false);
            request.setOffline(settings.isOffline());
            request.setBaseDirectory(project.getBasedir());
            request.setPomFile(new File(project.getBasedir().getPath() + "/pom.xml"));
            
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
            } catch (MavenInvocationException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

}