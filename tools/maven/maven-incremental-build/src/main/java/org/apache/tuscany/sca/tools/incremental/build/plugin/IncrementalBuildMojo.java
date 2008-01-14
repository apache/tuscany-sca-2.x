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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * @version $Rev$ $Date$
 * @goal build
 * @phase validate
 * @requiresDependencyResolution test
 * @description Incrementally build project modules that depend on modified modules.
 */
public class IncrementalBuildMojo extends AbstractBuildMojo {
    /**
     * Keep track of modified projects.
     */
    private static Set<String> modifiedProjectIDs = new HashSet<String>();

    /**
     * Returns the qualified id of an artifact .
     * @param p a Maven artifact
     * @return a qualified id
     */
    private static String id(Artifact a) {
        return a.getGroupId() + ':' + a.getArtifactId();
    }

    /**
     * Returns the qualified id of a project.
     * @param p a Maven project
     * @return a qualified id
     */
    private static String id(MavenProject p) {
        return p.getGroupId() + ':' + p.getArtifactId();
    }

    /**
     * The current user system settings for use in Maven.
     *
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    // private Settings settings;
    /**
     * Used to invoke Maven builds.
     *
     * @component
     */
    private Invoker invoker;

    /**
     * The local repository where the artifacts are located
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;

    public void execute() throws MojoExecutionException {
        getLog().info("Building " + project.getName() + " [" + project.getId() + "]");
        List<String> goals = new ArrayList<String>();
        String type = project.getArtifact().getType();
        if ("pom".equals(type)) {
            goals.add("install");
        } else {
            String projectID = id(project);

            Compiler compiler = getCompiler();
            boolean changed = false;
            boolean testChanged = false;
            String marker = project.getBasedir().getPath() + "/.modified";
            if (new File(marker).exists()) {
                getLog().info("Project: " + projectID + " has been modified.");
                changed = true;
            } else {
                changed = isSourceChanged(compiler) || isResourceChanged() || isPOMChanged();
            }
            if (changed) {
                modifiedProjectIDs.add(projectID);
            } else {
                testChanged = isTestSourceChanged(compiler) || isTestResourceChanged();
            }

            // Check if a project has compile dependencies on the modified projects
            // and will need to be recompiled, or has runtime or test dependencies
            // on the modified projects and needs to be retested

            if (changed) {
                goals.add("clean");
                goals.add("install");
            } else {
                for (Artifact artifact : (List<Artifact>)project.getCompileArtifacts()) {
                    String artifactID = id(artifact);
                    if (modifiedProjectIDs.contains(artifactID)) {
                        getLog().info("Project " + projectID
                            + " depends on modified project "
                            + artifactID
                            + " and will be recompiled.");
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
                            getLog().info("Project " + projectID
                                + " depends on modified project "
                                + artifactID
                                + " and will be retested.");
                            goals.add("test");
                            break;
                        }
                    }
                }
            }

            if (testChanged && goals.isEmpty()) {
                goals.add("test");
            }
        }

        // Invoke Maven with the necessary goals
        if (!goals.isEmpty()) {
            DefaultInvocationRequest request = new DefaultInvocationRequest();
            request.setGoals(goals);
            // FIXME: The maven invoker doesn't handle the directory names with spaces
            // request.setLocalRepositoryDirectory(new File(localRepository.getBasedir()));
            request.setInteractive(false);
            request.setShowErrors(true);
            request.setRecursive(false);
            // request.setDebug(true);
            request.setOffline(settings.isOffline());
            request.setBaseDirectory(project.getBasedir());
            request.setPomFile(project.getFile());

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
                e.printStackTrace();
                throw new MojoExecutionException(e.getMessage(), e);
            }
        } else {
            getLog().info("The project is up-to-date. No build is required.");
        }
    }

}
