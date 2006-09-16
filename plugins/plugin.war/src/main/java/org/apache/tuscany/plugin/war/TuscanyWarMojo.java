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
package org.apache.tuscany.plugin.war;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Build the tuscany war file by adding the tuscany dependencies.
 * 
 * Performs the following tasks.
 * 
 * <ul>
 *   <li>Adds the boot dependencies transitively to WEB-INF/tuscany/boot</li>
 *   <li>By default boot libraries are transitively resolved from webapp-host</li>
 *   <li>This can be overridden using the configuration/bootLibs element in the plugin</li>
 *   <li>Adds the extension artifacts specified using configuration/extensions to WEB-INF/tuscany/extensions</li>
 *   <li>If configuration/loadExtensionsDependency is set to true extension dependencies are transitivel loaded</li>
 *   <li>Extension dependencies are loaded into WEB-INF/tuscany/repository directory in a Maven repo format</li>
 * </ul>
 * @goal tuscany-war
 * @phase package
 * @version
 * 
 */
public class TuscanyWarMojo extends AbstractMojo {

    /**
     * Tuscany boot path.
     */
    private static final String BOOT_PATH = "WEB-INF/tuscany/boot/";

    /**
     * Tuscany extension path.
     */
    private static final String EXTENSION_PATH = "WEB-INF/tuscany/extensions/";

    /**
     * Tuscany repository path.
     */
    private static final String REPOSITORY_PATH = "WEB-INF/tuscany/repository/";

    /**
     * Artifact metadata source.
     * 
     * @component
     */
    private ArtifactMetadataSource metadataSource;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    protected ArtifactResolver resolver;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    private List remoteRepositories;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String outputDirectory;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter
     */
    private Dependency[] bootLibs = Dependency.getDefaultBootLibs();

    /**
     * The directory for the generated WAR.
     * 
     * @parameter
     */
    private Dependency[] extensions = new Dependency[0];

    /**
     * The name of the generated WAR.
     * 
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String warName;

    /**
     * A flag to indicate whether extension dependencies should be resolved transitively.
     * 
     * @parameter
     */
    private boolean loadExtensionDependencies;

    /**
     * WEB-INF jar files.
     */
    private Set<String> packagedLibs = new HashSet<String>();

    /**
     * Executes the MOJO.
     */
    public void execute() throws MojoExecutionException {

        JarFile originalWar = null;
        JarOutputStream newWar = null;
        File originalWarFile = null;
        File newWarFile = null;

        boolean success = false;

        try {

            originalWarFile = new File(outputDirectory, warName + ".war");
            originalWar = new JarFile(originalWarFile);

            newWarFile = new File(outputDirectory, warName + "-temp.war");
            newWar = new JarOutputStream(new FileOutputStream(newWarFile));

            copyOriginal(originalWar, newWar);

            for (Dependency dependency : bootLibs) {
                for (Artifact art : resolveDependency(dependency, true)) {
                    addArtifact(newWar, BOOT_PATH, art);
                }
            }

            for (Dependency dependency : extensions) {
                for (Artifact art : resolveDependency(dependency, loadExtensionDependencies)) {
                    if (dependency.match(art)) {
                        addArtifact(newWar, EXTENSION_PATH, art);
                    } else {
                        String groupId = art.getGroupId().replace('.', '/');
                        String path = REPOSITORY_PATH + groupId + "/" + art.getArtifactId() + "/" + art.getVersion() + "/";
                        addArtifact(newWar, path, art);
                    }

                }
            }

            success = true;

        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(newWar);
        }

        if (success) {
            if (!originalWarFile.delete()) {
                throw new MojoExecutionException("Unable to rename war file");
            }
            if (!newWarFile.renameTo(originalWarFile)) {
                throw new MojoExecutionException("Unable to rename war file");
            }
        }

    }

    /**
     * Resolves the specified dependency.
     * 
     * @param dependency Dependency to be resolved.
     * @param transitive Whether to resolve transitively.
     * @return A set of resolved artifacts.
     * @throws IOException In case of an unexpected IO error.
     * @throws ArtifactResolutionException If the artifact cannot be resolved.
     * @throws ArtifactNotFoundException If the artifact is not found.
     * @throws ArtifactMetadataRetrievalException In case of error in retrieving metadata.
     */
    private Set<Artifact> resolveDependency(Dependency dependency, boolean transitive) throws IOException, ArtifactResolutionException, ArtifactNotFoundException, ArtifactMetadataRetrievalException {

        Set<Artifact> resolvedArtifacts = new HashSet<Artifact>();

        // Resolve the artifact
        Artifact artifact = dependency.getArtifact(artifactFactory);
        resolver.resolve(artifact, remoteRepositories, localRepository);
        resolvedArtifacts.add((Artifact) artifact);

        if (!transitive) {
            return resolvedArtifacts;
        }

        // Transitively resolve all the dependencies
        ResolutionGroup resolutionGroup = metadataSource.retrieve(artifact, localRepository, remoteRepositories);
        ArtifactResolutionResult result = resolver.resolveTransitively(resolutionGroup.getArtifacts(), artifact, remoteRepositories, localRepository,
                metadataSource);

        // Add the artifacts to the deployment unit
        for (Object depArtifact : result.getArtifacts()) {
            resolvedArtifacts.add((Artifact) depArtifact);
        }
        return resolvedArtifacts;

    }

    /**
     * Adds the artifact to the war file.
     * 
     * @param newWar War file to which the artifact is added.
     * @param path Path within the war file where artifact is added.
     * @param artifact Artifact to be added.
     * @throws IOException In case of an unexpected IO error.
     */
    private void addArtifact(JarOutputStream newWar, String path, Artifact artifact) throws IOException {

        FileInputStream artifactStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            
            File artifactFile = artifact.getFile();
            if (packagedLibs.contains(artifactFile.getName())) {
                return;
            }
            artifactStream = new FileInputStream(artifactFile);
            

            newWar.putNextEntry(new JarEntry(path + artifactFile.getName()));

            File file = new File(outputDirectory, "webapp");
            file = new File(file, path);
            file.mkdirs();

            file = new File(file, artifactFile.getName());
            fileOutputStream = new FileOutputStream(file);

            IOUtils.copy(artifactStream, fileOutputStream);
            IOUtils.closeQuietly(artifactStream);

            artifactStream = new FileInputStream(artifactFile);
            IOUtils.copy(artifactStream, newWar);
            
            packagedLibs.add(artifactFile.getName());

            getLog().info("Processed " + path + artifactFile.getName());

        } finally {
            IOUtils.closeQuietly(artifactStream);
            IOUtils.closeQuietly(fileOutputStream);
        }

    }

    /**
     * Copies the original war file.
     * 
     * @param originalWar Original war file.
     * @param newWar New war file.
     * @throws IOException In case of an unexpected IO error.
     */
    private void copyOriginal(JarFile originalWar, JarOutputStream newWar) throws IOException {

        Enumeration entries = originalWar.entries();
        packagedLibs.clear();

        while (entries.hasMoreElements()) {

            JarEntry entry = (JarEntry) entries.nextElement();
            InputStream jarEntryStream = null;

            try {
                jarEntryStream = originalWar.getInputStream(entry);
                newWar.putNextEntry(entry);
                IOUtils.copy(jarEntryStream, newWar);
                String name = entry.getName();

                if (name.endsWith(".jar")) {
                    packagedLibs.add(name.substring(name.lastIndexOf("/") + 1));
                }
            } finally {
                IOUtils.closeQuietly(jarEntryStream);
            }

        }

        originalWar.close();

    }

}
