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
package org.apache.tuscany.tools.sca.tuscany.bundle.plugin;

import static org.apache.tuscany.tools.sca.tuscany.bundle.plugin.LibraryBundleUtil.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

/**
 * @version $Rev$ $Date$
 * @goal build
 * @phase process-sources
 * @requiresDependencyResolution test
 * @description Build a virtual bundle for 3rd party dependencies
 */
public class LibraryBundleMojo extends AbstractMojo {
    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The basedir of the project.
     * 
     * @parameter expression="${basedir}"
     * @required @readonly
     */
    private File basedir;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    private org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    private java.util.List remoteRepos;

    /**
     * @parameter
     */
    private boolean copyJars = false;

    /**
     * Dependency tree builder
     * 
     * @component
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * Artifact factory
     * 
     * @component
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;    
    
    /**
     * @component
     */
    private ArtifactCollector collector;

    /**
     * The local repository
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * The remote repositories
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     */
    private List remoteRepositories;
    
    /**
     * Artifact resolver
     * 
     * @component
     */
    private ArtifactResolver artifactResolver;

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        if (project.getPackaging().equals("pom")) {
            return;
        }

        DependencyTree dependencyTree;
        try {
            dependencyTree = dependencyTreeBuilder.buildDependencyTree(project, 
                                                                                  localRepository, artifactFactory,
                                                                                  artifactMetadataSource, collector );
                                                                          
        } catch (DependencyTreeBuilderException e) {
            throw new MojoExecutionException("Could not build dependency tree", e);
        }
        
        Set<File> jarFiles = new HashSet<File>();
        for (Object o : dependencyTree.getArtifacts()) {
            Artifact artifact = (Artifact)o;

            if (!(Artifact.SCOPE_COMPILE.equals(artifact.getScope()) || Artifact.SCOPE_RUNTIME.equals(artifact.getScope()))) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping artifact: " + artifact);
                }
                continue;
            }
            if (!"jar".equals(artifact.getType())) {
                continue;
            }
            if ("org.apache.tuscany.sca".equals(artifact.getGroupId())) {
                continue;
            }
            
            VersionRange versionRange = artifact.getVersionRange();
            if (versionRange == null)
                versionRange = VersionRange.createFromVersion(artifact.getVersion());
            Artifact dependencyArtifact = artifactFactory.createDependencyArtifact(artifact.getGroupId(), 
                                                                                   artifact.getArtifactId(), 
                                                                                   versionRange, 
                                                                                   artifact.getType(), 
                                                                                   artifact.getClassifier(), 
                                                                                   artifact.getScope());
                                                                           
           try {
               artifactResolver.resolve(dependencyArtifact, remoteRepositories, localRepository);
           } catch (ArtifactResolutionException e) {
               log.warn("Artifact " + artifact + " could not be resolved.");
           } catch (ArtifactNotFoundException e) {
               log.warn("Artifact " + artifact + " could not be found.");
           }
           artifact = dependencyArtifact;

            if (log.isDebugEnabled()) {
                log.debug("Artifact: " + artifact);
            }
            String bundleName = null;
            try {
                bundleName = LibraryBundleUtil.getBundleName(artifact.getFile());
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            if (bundleName == null) {
                if (artifact.getFile().exists()) {
                    log.info("Adding third party jar: " + artifact);
                    jarFiles.add(artifact.getFile());
                } else {
                    log.warn("Third party jar not found: " + artifact);
                }
            }
        }

        try {
            String version = project.getVersion();
            if (version.endsWith(Artifact.SNAPSHOT_VERSION)) {
                version = version.substring(0, version.length() - Artifact.SNAPSHOT_VERSION.length() - 1);
            }

            Manifest mf = LibraryBundleUtil.libraryManifest(jarFiles, project.getName(), version, copyJars);
            File file = new File(project.getBasedir(), "META-INF");
            file.mkdir();
            file= new File(file, "MANIFEST.MF");
            if (log.isDebugEnabled()) {
                log.debug("Generating " + file);
            }

            FileOutputStream fos = new FileOutputStream(file);
            write(mf, fos);
            fos.close();

            if (copyJars) {
                File lib = new File(project.getBasedir(), "lib");
                if (lib.isDirectory()) {
                    for (File c : lib.listFiles()) {
                        c.delete();
                    }
                }
                lib.mkdir();
                byte[] buf = new byte[4096];
                for (File jar : jarFiles) {
                    File jarFile = new File(lib, jar.getName());
                    if (log.isDebugEnabled()) {
                        log.debug("Copying " + jar + " to " + jarFile);
                    }
                    FileInputStream in = new FileInputStream(jar);
                    FileOutputStream out = new FileOutputStream(jarFile);
                    for (;;) {
                        int len = in.read(buf);
                        if (len > 0) {
                            out.write(buf, 0, len);
                        } else {
                            break;
                        }
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

}
