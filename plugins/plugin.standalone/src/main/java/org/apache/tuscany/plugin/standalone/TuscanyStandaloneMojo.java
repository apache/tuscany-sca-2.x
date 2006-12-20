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
package org.apache.tuscany.plugin.standalone;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
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
import org.apache.maven.project.MavenProject;

/**
 * Build the tuscany war file by adding the tuscany dependencies. Performs the
 * following tasks.
 * <ul>
 * <li>Adds the boot dependencies transitively to tuscany/boot</li>
 * <li>By default boot libraries are transitively resolved from app-host</li>
 * <li>This can be overridden using the configuration/bootLibs element in the
 * plugin</li>
 * <li>Adds the extension artifacts specified using configuration/extensions to
 * tuscany/extensions</li>
 * <li>If configuration/loadExtensionsDependency is set to true extension
 * dependencies are transitivel loaded</li>
 * <li>Extension dependencies are loaded into tuscany/repository directory in a
 * Maven repo format</li>
 * <li>Extension dependency metadata is written to
 * tuscany/repository/dependency.metadata file</li>
 * </ul>
 * <p>
 * A sample usage for this plugin:
 * <p>
 * <code>
 * &lt;plugin&gt;<br/>
 * &lt;groupId&gt;org.apache.tuscany.sca.plugins&lt;/groupId&gt;<br/>
 * &lt;artifactId&gt;tuscany-standalone-plugin&lt;/artifactId&gt;<br/>
 * &lt;executions&gt;<br/>
 * &lt;execution&gt;<br/>
 * &lt;id&gt;tuscany-standalone&lt;/id&gt;<br/>
 * &lt;goals&gt;<br/>
 * &lt;goal&gt;tuscany-standalone&lt;/goal&gt;<br/>
 * &lt;/goals&gt;<br/>
 * &lt;/execution&gt;<br/>
 * &lt;/executions&gt;<br/>
 * &lt;configuration&gt;<br/>
 * &lt;loadExtensionDependencies&gt;true&lt;/loadExtensionDependencies&gt;<br/>
 * &lt;overwrite&gt;true&lt;/overwrite&gt;<br/>
 * &lt;extensions&gt;<br/>
 * &lt;dependency&gt;<br/>
 * &lt;groupId&gt;org.apache.tuscany.sca.services.bindings&lt;/groupId&gt;<br/>
 * &lt;artifactId&gt;axis2&lt;/artifactId&gt;<br/>
 * &lt;version&gt;${tuscanyVersion}&lt;/version&gt;<br/>
 * &lt;/dependency&gt;<br/>
 * &lt;dependency&gt;<br/>
 * &lt;groupId&gt;org.apache.tuscany.sca.services.databinding&lt;/groupId&gt;<br/>
 * &lt;artifactId&gt;databinding-sdo&lt;/artifactId&gt;<br/>
 * &lt;version&gt;${tuscanyVersion}&lt;/version&gt;<br/>
 * &lt;/dependency&gt;<br/>
 * &lt;/extensions&gt;<br/>
 * &lt;/configuration&gt;<br/>
 * &lt;/plugin&gt;<br/>
 * </code>
 * 
 * @goal tuscany-standalone
 * @phase install
 * @version $Rev$ $Date$
 */
public class TuscanyStandaloneMojo extends AbstractMojo {

    private static final String APP_PATH = "app";

    /**
     * Tuscany path.
     */
    private static final String TUSCANY_PATH = "/";

    /**
     * Tuscany launcher path.
     */
    private static final String LAUNCHER_PATH = TUSCANY_PATH + "bin/";

    /**
     * Tuscany lib path.
     */
    private static final String LIB_PATH = TUSCANY_PATH + "lib/";

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
     * The directory for the generated ZIP.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String outputDirectory;

    /**
     * The directory for the generated ZIP.
     * 
     * @parameter
     */
    private Dependency[] bootLibs = Dependency.getDefaultBootLibs();

    /**
     * The directory for the generated ZIP.
     * 
     * @parameter
     */
    private Dependency[] extensions = new Dependency[0];

    /**
     * The directory for the generated WAR.
     * 
     * @parameter
     */
    private Dependency[] dependencies = new Dependency[0];

    /**
     * /** The name of the generated ZIP.
     * 
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String zipName;

    /**
     * A flag to indicate whether extension dependencies should be resolved
     * transitively.
     * 
     * @parameter
     */
    private boolean loadExtensionDependencies;

    /**
     * The artifactId for the project
     * 
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    private MavenProject project;

    /**
     * Overwrite exsiting artifact?
     * 
     * @parameter
     */
    private boolean overwrite = true;

    /**
     * WEB-INF jar files.
     */
    private Set<String> packagedLibs = new HashSet<String>();

    /**
     * Transitive dependencies for extensions.
     */
    private Map transDepenedencyMap = new HashMap();

    private Dependency launcher =
        new Dependency("org.apache.tuscany.sca.runtime.standalone", "launcher", "1.0-incubator-SNAPSHOT");

    /**
     * Executes the MOJO.
     */
    public void execute() throws MojoExecutionException {

        JarOutputStream newZip = null;
        File newZipFile = null;

        try {

            File originalJarFile = new File(outputDirectory, zipName + ".jar");

            newZipFile = new File(outputDirectory, zipName + ".zip");
            newZip = new JarOutputStream(new FileOutputStream(newZipFile));

            // copyOriginal(originalZip, newZip);

            // addEntry(newZip, TUSCANY_PATH);
            addEntry(newZip, BOOT_PATH);
            addEntry(newZip, EXTENSION_PATH);
            addEntry(newZip, REPOSITORY_PATH);

            newZip.putNextEntry(new JarEntry(LIB_PATH + originalJarFile.getName()));
            InputStream jar = new FileInputStream(originalJarFile);
            IOUtils.copy(jar, newZip);
            IOUtils.closeQuietly(jar);

            File file = new File(outputDirectory, APP_PATH);
            file = new File(file, LIB_PATH);
            file.mkdirs();

            file = new File(file, originalJarFile.getName());
            if (overwrite || (!file.exists())) {
                jar = new FileInputStream(originalJarFile);
                OutputStream fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(jar, fileOutputStream);
                IOUtils.closeQuietly(jar);
                IOUtils.closeQuietly(fileOutputStream);
            }

            for (Artifact art : resolveArtifact(project.getArtifact(), true)) {
                if (art == project.getArtifact()) {
                    continue;
                }
                // Only add artifacts with scope "compile" or "runtime"
                String scope = art.getScope();
                if (scope == null || scope.equals(Artifact.SCOPE_COMPILE)
                    || scope.equals(Artifact.SCOPE_RUNTIME)) {
                    addArtifact(newZip, LIB_PATH, art);
                }
            }

            for (Artifact art : resolveArtifact(launcher.getArtifact(artifactFactory), true)) {
                // Only add artifacts with scope "compile" or "runtime"
                String scope = art.getScope();
                if (scope == null || scope.equals(Artifact.SCOPE_COMPILE)
                    || scope.equals(Artifact.SCOPE_RUNTIME)) {
                    if (art.getArtifactId().contains("launcher")) {
                        addArtifact(newZip, LAUNCHER_PATH, art);
                    } else {
                        addArtifact(newZip, LIB_PATH, art);
                    }
                }
            }

            for (Dependency dependency : bootLibs) {
                for (Artifact art : resolveArtifact(dependency.getArtifact(artifactFactory), true)) {
                    addArtifact(newZip, BOOT_PATH, art);
                }
            }

            for (Dependency dependency : extensions) {
                for (Artifact art : resolveArtifact(dependency.getArtifact(artifactFactory),
                                                    loadExtensionDependencies)) {
                    if (dependency.match(art)) {
                        addArtifact(newZip, EXTENSION_PATH, art);
                    }

                    // Load dependencies even for the extension itself
                    if (loadExtensionDependencies) {
                        loadTransitiveDependencies(newZip, art);
                    }

                }
            }

            for (Dependency dependency : dependencies) {
                for (Artifact art : resolveArtifact(dependency.getArtifact(artifactFactory),
                                                    loadExtensionDependencies)) {
                    loadTransitiveDependencies(newZip, art);
                }
            }

            writeDependencyMetadata(newZip);

        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(newZip);
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
     * 
     * @param newZip ZIP to which the metadata is written.
     * @throws IOException In case of an IO error.
     */
    private void writeDependencyMetadata(JarOutputStream newZip) throws IOException {

        FileOutputStream depMapOutStream = null;
        FileInputStream depMapInStream = null;

        try {
            String metadataFile = "dependency.metadata";

            File file = new File(outputDirectory, APP_PATH);
            file = new File(file, REPOSITORY_PATH);
            file.mkdirs();

            file = new File(file, metadataFile);
            if ((!overwrite) && file.exists()) {
                // Try to merge the dependency map
                InputStream is = new FileInputStream(file);
                XMLDecoder decoder = new XMLDecoder(is);
                Map map = (Map)decoder.readObject();
                decoder.close();
                for (Object o : map.entrySet()) {
                    Map.Entry e = (Map.Entry)o;
                    Set s = (Set)transDepenedencyMap.get(e.getKey());
                    if (s != null) {
                        s.addAll((Set)e.getValue());
                    }
                }
            }

            depMapOutStream = new FileOutputStream(file);
            XMLEncoder xmlEncoder = new XMLEncoder(depMapOutStream);
            xmlEncoder.writeObject(transDepenedencyMap);
            xmlEncoder.close();

            if (addEntry(newZip, REPOSITORY_PATH + metadataFile)) {
                depMapInStream = new FileInputStream(file);
                IOUtils.copy(depMapInStream, newZip);
            }

        } finally {
            IOUtils.closeQuietly(depMapOutStream);
            IOUtils.closeQuietly(depMapInStream);
        }

    }

    /**
     * Builds the transitive dependencies for artifacts.
     * 
     * @param newZip WARto which the artifacts are added.
     * @param art Extension artifact.
     * @throws IOException In case of an unexpected IO error.
     * @throws ArtifactResolutionException If the artifact cannot be resolved.
     * @throws ArtifactNotFoundException If the artifact is not found.
     * @throws ArtifactMetadataRetrievalException In case of error in retrieving
     *             metadata.
     */
    private void loadTransitiveDependencies(JarOutputStream newWar, Artifact art) throws IOException,
        ArtifactResolutionException, ArtifactNotFoundException, ArtifactMetadataRetrievalException {

        String artPath = art.getGroupId() + "/" + art.getArtifactId() + "/" + art.getVersion() + "/";
        String path = REPOSITORY_PATH + artPath;
        addArtifact(newWar, path, art);

        Set<String> transDepenedenyList = new HashSet<String>();
        transDepenedencyMap.put(artPath, transDepenedenyList);

        // Get the transitive dependencies for each dependency.
        for (Artifact transArt : resolveArtifact(art, true)) {
            String transArtPath =
                transArt.getGroupId() + "/" + transArt.getArtifactId() + "/" + transArt.getVersion() + "/";
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
     * @throws ArtifactMetadataRetrievalException In case of error in retrieving
     *             metadata.
     */
    private Set<Artifact> resolveArtifact(Artifact artifact, boolean transitive) throws IOException,
        ArtifactResolutionException, ArtifactNotFoundException, ArtifactMetadataRetrievalException {

        Set<Artifact> resolvedArtifacts = new HashSet<Artifact>();

        if (artifact != project.getArtifact()) {
            // Resolve the artifact
            resolver.resolve(artifact, remoteRepositories, localRepository);
            resolvedArtifacts.add((Artifact)artifact);
        }        

        if (!transitive) {
            return resolvedArtifacts;
        }

        // Transitively resolve all the dependencies
        ResolutionGroup resolutionGroup =
            metadataSource.retrieve(artifact, localRepository, remoteRepositories);
        ArtifactResolutionResult result =
            resolver.resolveTransitively(resolutionGroup.getArtifacts(),
                                         artifact,
                                         remoteRepositories,
                                         localRepository,
                                         metadataSource);

        // Add the artifacts to the deployment unit
        for (Object depArtifact : result.getArtifacts()) {
            resolvedArtifacts.add((Artifact)depArtifact);
        }
        return resolvedArtifacts;

    }

    /**
     * Adds the artifact to the zip file.
     * 
     * @param newZip Zip file to which the artifact is added.
     * @param path Path within the zip file where artifact is added.
     * @param artifact Artifact to be added.
     * @throws IOException In case of an unexpected IO error.
     */
    private boolean addArtifact(JarOutputStream newZip, String path, Artifact artifact) throws IOException {

        FileInputStream artifactStream = null;
        FileOutputStream fileOutputStream = null;

        try {

            File artifactFile = artifact.getFile();
            // For extensions, we'll add it even the packagedLibs has it
            if ((!EXTENSION_PATH.equals(path)) && packagedLibs.contains(artifactFile.getName())) {
                return false;
            }
            artifactStream = new FileInputStream(artifactFile);

            newZip.putNextEntry(new JarEntry(path + artifactFile.getName()));

            File file = new File(outputDirectory, APP_PATH);
            file = new File(file, path);
            file.mkdirs();

            file = new File(file, artifactFile.getName());
            if (overwrite || (!file.exists())) {
                fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(artifactStream, fileOutputStream);
                IOUtils.closeQuietly(artifactStream);
            }

            artifactStream = new FileInputStream(artifactFile);
            IOUtils.copy(artifactStream, newZip);

            packagedLibs.add(artifactFile.getName());

            getLog().info("Processed " + path + artifactFile.getName());
            return true;

        } finally {
            IOUtils.closeQuietly(artifactStream);
            IOUtils.closeQuietly(fileOutputStream);
        }

    }

}
