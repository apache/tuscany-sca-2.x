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
 * Goal which touches a timestamp file.
 *
 * @goal tuscany-war
 * @phase package
 * @version
 *
 * TODO Check timestamp for copying resources from the repo
 */
public class TuscanyWarMojo extends AbstractMojo {

    /**
     * WEB-INF lib jars.
     */
    private static final String WEB_INF_LIB = "WEB-INF/lib";

    /**
     * Tuscany boot path.
     */
    private static final String BOOT_PATH = "WEB-INF/tuscany/boot/";

    /**
     * Tuscany boot path.
     */
    private static final String EXTENSION_PATH = "WEB-INF/tuscany/extensions/";

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
    private Dependency[] extensions;

    /**
     * The name of the generated WAR.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String warName;

    /**
     * WEB-INF jar files.
     */
    private Set<String> packagedLibs = new HashSet<String>();

    /**
     * Executes the task.
     *
     * The plugin executes the following tasks.
     *
     * <ul>
     * <li>Adds the specified boot libraries to WEB-INF/tuscany/boot directory</li>
     * <li>Adds the specified extension artifacts to WEB-INF/tuscany/extensions</li>
     * <li>Checks for the tuscany context listener in WEB-INF/web.xml</li>
     * <li>Adds the context listener if not present</li>
     * <ul>
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
                addTuscanyDependency(newWar, dependency, BOOT_PATH);
            }

            if (extensions != null) {
                for (Dependency dependency : extensions) {
                    addTuscanyDependency(newWar, dependency, EXTENSION_PATH);
                }
            }

            success = true;

        } catch (Exception ex) {
            ex.printStackTrace();
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
     * Adds the tuscany dependency.
     *
     * @param newWar
     *            New WAR file.
     * @param dependency
     *            Dependency to be added.
     * @param path
     *            Path to the dependency.
     * @throws ArtifactResolutionException
     *             If artifact is not resolved.
     * @throws ArtifactNotFoundException
     *             If artifact is not found.
     * @throws IOException
     *             In case of an IO error.
     * @throws ArtifactMetadataRetrievalException
     */
    private void addTuscanyDependency(JarOutputStream newWar, Dependency dependency, String path) throws ArtifactResolutionException,
            ArtifactNotFoundException, IOException, ArtifactMetadataRetrievalException {

        Artifact artifact = dependency.getArtifact(artifactFactory);

        // Resolve the artifact
        resolver.resolve(artifact, remoteRepositories, localRepository);

        // Transitively resolve all the dependencies
        ResolutionGroup resolutionGroup = metadataSource.retrieve(artifact, localRepository, remoteRepositories);
        ArtifactResolutionResult result = resolver.resolveTransitively(resolutionGroup.getArtifacts(), artifact, remoteRepositories, localRepository,
                metadataSource);

        // Add the artifacts to the deployment unit
        for (Artifact depArtifact : (Set<Artifact>) result.getArtifacts()) {
            addArtifact(newWar, path, depArtifact);
        }

        addArtifact(newWar, path, artifact);

    }

    /**
     * Adds the artifact to the deployment unit.
     *
     * @param newWar
     *            WAR to which the artifact is added.
     * @param path
     *            Path wothin the WAR file.
     * @param artifact
     *            ARtifact that is being added.
     * @throws IOException
     *             In case of an IO error.
     * @throws ArtifactResolutionException
     *             If the artifact cannot be resolved.
     * @throws ArtifactNotFoundException
     *             If the artifact is not found.
     */
    private void addArtifact(JarOutputStream newWar, String path, Artifact artifact) throws IOException, ArtifactResolutionException,
            ArtifactNotFoundException {

        FileInputStream artifactStream = null;
        FileOutputStream fileOutputStream = null;

        try {

            File artifactFile = artifact.getFile();
            artifactStream = new FileInputStream(artifactFile);
            if (packagedLibs.contains(artifactFile.getName())) {
                return;
            }

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

            getLog().info("Processed " + path + artifactFile.getName());

        } finally {
            IOUtils.closeQuietly(artifactStream);
            IOUtils.closeQuietly(fileOutputStream);
        }

    }

    /**
     * Copy the contents of the original WAR to a temporary WAR.
     *
     * @param originalWar
     *            Original WAR file.
     * @param newWar
     *            New war file.
     * @throws IOException
     *             Thrown in case of an IO error.
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

                if (name.endsWith(".jar") && (name.startsWith(WEB_INF_LIB) || name.startsWith(BOOT_PATH) || name.startsWith(EXTENSION_PATH))) {
                    packagedLibs.add(name.substring(name.lastIndexOf("/") + 1));
                }
            } finally {
                IOUtils.closeQuietly(jarEntryStream);
            }

        }

        originalWar.close();

    }

}
