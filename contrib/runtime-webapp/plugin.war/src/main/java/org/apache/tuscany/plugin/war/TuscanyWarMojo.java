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

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipException;

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
 *   <li>The version of boot libraries can be specified using configuration/runTimeVersion element</li>
 *   <li>Boot libraries can be overridden using the configuration/bootLibs element in the plugin</li>
 *   <li>Adds the extension artifacts specified using configuration/extensions to WEB-INF/tuscany/extensions</li>
 *   <li>If configuration/loadExtensionsDependency is set to true extension dependencies are transitivel loaded</li>
 *   <li>Extension dependencies are loaded into WEB-INF/tuscany/repository directory in a Maven repo format</li>
 *   <li>Extension dependency metadata is written to WEB-INF/tuscany/repository/dependency.metadata file</li>
 * </ul>
 * @goal tuscany-war
 * @phase package
 * 
 * @version $Rev$ $Date$
 * 
 */
public class TuscanyWarMojo extends AbstractMojo {

    /**
     * Tuscany path.
     */
    private static final String TUSCANY_PATH = "WEB-INF/tuscany/";

    /**
     * Tuscany boot path.
     */
    private static final String BOOT_PATH = TUSCANY_PATH + "boot/";

    /**
     * Tuscany extension path.
     */
    private static final String EXTENSION_PATH = TUSCANY_PATH + "extensions/";

    /**
     * Tuscany repository path.
     */
    private static final String REPOSITORY_PATH = TUSCANY_PATH + "repository/";

    /**
     * Artifact metadata source.
     * 
     * @component
     */
    public ArtifactMetadataSource metadataSource;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    public ArtifactFactory artifactFactory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    public ArtifactResolver resolver;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    public ArtifactRepository localRepository;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    public List remoteRepositories;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    public String outputDirectory;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter
     */
    public Dependency[] bootLibs;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter
     */
    public Dependency[] extensions = new Dependency[0];

    /**
     * The directory for the generated WAR.
     * 
     * @parameter
     */
    public Dependency[] dependencies = new Dependency[0];

    /**
     * The name of the generated WAR.
     * 
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    public String warName;

    /**
     * A flag to indicate whether extension dependencies should be resolved transitively.
     * 
     * @parameter
     */
    public boolean loadExtensionDependencies;

    /**
     * The default version of the runtime to use.
     * 
     * @parameter
     */
    public String runTimeVersion;
    
    /**
     * WEB-INF jar files.
     */
    public Set<String> packagedLibs = new HashSet<String>();

    /**
     * Transitive dependencies for extensions.
     */
    public Map<String, Set<String>> transDepenedencyMap = new HashMap<String, Set<String>>();

    /**
     * Executes the MOJO.
     */
    public void execute() throws MojoExecutionException {

        JarFile originalWar;
        JarOutputStream newWar = null;
        File originalWarFile = null;
        File newWarFile = null;

        boolean success = false;

        if (runTimeVersion == null) {
            try {
                runTimeVersion = getPluginVersion();
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

        if (bootLibs == null) {
            Dependency dependancy = new Dependency("org.apache.tuscany.sca.runtime.webapp",
                                                   "webapp-host",
                                                   runTimeVersion);
            bootLibs = new Dependency[] {dependancy};
        }

        try {
            originalWarFile = new File(outputDirectory, warName + ".war");
            originalWar = new JarFile(originalWarFile);

            newWarFile = new File(outputDirectory, warName + "-temp.war");
            newWar = new JarOutputStream(new FileOutputStream(newWarFile));

            copyOriginal(originalWar, newWar);

            addEntry(newWar, TUSCANY_PATH);
            addEntry(newWar, BOOT_PATH);
            addEntry(newWar, EXTENSION_PATH);
            addEntry(newWar, REPOSITORY_PATH);

            for (Dependency dependency : bootLibs) {
                for (Artifact art : resolveArtifact(dependency.getArtifact(artifactFactory), true)) {
                    addArtifact(newWar, BOOT_PATH, art);
                }
            }

            for (Dependency dependency : extensions) {
                for (Artifact art : resolveArtifact(dependency.getArtifact(artifactFactory), loadExtensionDependencies)) {
                    if (dependency.match(art)) {
                        addArtifact(newWar, EXTENSION_PATH, art);
                    }

                    // Load dependencies even for the extension itself
                    if (loadExtensionDependencies) {
                        loadTransitiveDependencies(newWar, art);
                    }

                }
            }

            for (Dependency dependency : dependencies) {
                for (Artifact art : resolveArtifact(dependency.getArtifact(artifactFactory), loadExtensionDependencies)) {
                    loadTransitiveDependencies(newWar, art);
                }
            }

            writeDependencyMetadata(newWar);

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
     * Adds an entry to the JAR failing safe for duplicate.
     * 
     * @param jar JAR to which the entry is added.
     * @param entry Entry to be added.
     * @return True if added successfully.
     * @throws IOException In case of an IO error.
     */
    private boolean addEntry(JarOutputStream jar, String entry) throws IOException {
        try {
            jar.putNextEntry(new JarEntry(entry));
            return true;
        } catch (ZipException duplicateEntry) {
            getLog().info(duplicateEntry.getMessage());
            return false;
        }
    }

    /**
     * Writes the dependency metadata.
     * @param newWar WAR to which the metadata is written.
     * @throws IOException In case of an IO error.
     */
    private void writeDependencyMetadata(JarOutputStream newWar) throws IOException {

        FileOutputStream depMapOutStream = null;
        FileInputStream depMapInStream = null;

        try {
            String metadataFile = "dependency.metadata";

            File file = new File(outputDirectory, "webapp");
            file = new File(file, REPOSITORY_PATH);
            file.mkdirs();

            file = new File(file, metadataFile);
            file.createNewFile();

            depMapOutStream = new FileOutputStream(file);
            XMLEncoder xmlEncoder = new XMLEncoder(depMapOutStream);
            xmlEncoder.writeObject(transDepenedencyMap);
            xmlEncoder.close();

            if (addEntry(newWar, REPOSITORY_PATH + metadataFile)) {
                depMapInStream = new FileInputStream(file);
                IOUtils.copy(depMapInStream, newWar);
            }

        } finally {
            IOUtils.closeQuietly(depMapOutStream);
            IOUtils.closeQuietly(depMapInStream);
        }

    }

    /**
     * Builds the transitive dependencies for artifacts.
     * 
     * @param newWar WARto which the artifacts are added.
     * @param art Extension artifact.
     * @throws IOException In case of an unexpected IO error.
     * @throws ArtifactResolutionException If the artifact cannot be resolved.
     * @throws ArtifactNotFoundException If the artifact is not found.
     * @throws ArtifactMetadataRetrievalException In case of error in retrieving metadata.
     */
    private void loadTransitiveDependencies(JarOutputStream newWar, Artifact art) throws IOException, ArtifactResolutionException,
            ArtifactNotFoundException, ArtifactMetadataRetrievalException {

        String artPath = art.getGroupId() + "/" + art.getArtifactId() + "/" + art.getVersion() + "/";
        String path = REPOSITORY_PATH + artPath;
        addArtifact(newWar, path, art);

        Set<String> transDepenedenyList = new HashSet<String>();
        transDepenedencyMap.put(artPath, transDepenedenyList);

        // Get the transitive dependencies for each dependency.
        for (Artifact transArt : resolveArtifact(art, true)) {
            String transArtPath = transArt.getGroupId() + "/" + transArt.getArtifactId() + "/" + transArt.getVersion() + "/";
            if (addArtifact(newWar, REPOSITORY_PATH + transArtPath, transArt)) {
                transDepenedenyList.add(transArtPath + transArt.getFile().getName());
            }
        }

    }

    /**
     * Resolves the specified artifact.
     * 
     * @param artifact Artifact to be resolved.
     * @param transitive Whether to resolve transitively.
     * @return A set of resolved artifacts.
     * @throws IOException In case of an unexpected IO error.
     * @throws ArtifactResolutionException If the artifact cannot be resolved.
     * @throws ArtifactNotFoundException If the artifact is not found.
     * @throws ArtifactMetadataRetrievalException In case of error in retrieving metadata.
     */
    private Set<Artifact> resolveArtifact(Artifact artifact, boolean transitive) throws IOException, ArtifactResolutionException,
            ArtifactNotFoundException, ArtifactMetadataRetrievalException {

        Set<Artifact> resolvedArtifacts = new HashSet<Artifact>();

        // Resolve the artifact
        resolver.resolve(artifact, remoteRepositories, localRepository);
        resolvedArtifacts.add(artifact);

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
     * @return true if the artifact was added
     */
    private boolean addArtifact(JarOutputStream newWar, String path, Artifact artifact) throws IOException {

        FileInputStream artifactStream = null;
        FileOutputStream fileOutputStream = null;

        try {

            File artifactFile = artifact.getFile();
            // For extensions, we'll add it even the packagedLibs has it
            if ((!EXTENSION_PATH.equals(path)) && packagedLibs.contains(artifactFile.getName())) {
                return false;
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

            return true;

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

    private String getPluginVersion() throws IOException {
        Properties pomProperties = new Properties();
        String propFile = "/META-INF/maven/org.apache.tuscany/tuscany-war-plugin/pom.properties";
        InputStream is = getClass().getResourceAsStream(propFile);
        try {
            pomProperties.load(is);
            return pomProperties.getProperty("version");
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
