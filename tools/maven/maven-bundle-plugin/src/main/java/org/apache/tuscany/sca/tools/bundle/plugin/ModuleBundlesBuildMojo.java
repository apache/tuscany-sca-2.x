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
package org.apache.tuscany.sca.tools.bundle.plugin;

import static org.apache.tuscany.sca.tools.bundle.plugin.BundleUtil.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

/**
 * A maven plugin that generates a modules directory containing OSGi bundles for all the project's module dependencies.
 * 
 * @version $Rev$ $Date$
 * @goal generate-modules
 * @phase generate-resources
 * @requiresDependencyResolution test
 * @description Generate a modules directory containing OSGi bundles for all the project's module dependencies.
 */
public class ModuleBundlesBuildMojo extends AbstractMojo {

    /**
     * The project to create a distribution for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Project builder -- builds a model from a pom.xml
     * 
     * @component role="org.apache.maven.project.MavenProjectBuilder"
     * @required
     * @readonly
     */
    private MavenProjectBuilder mavenProjectBuilder;
    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    private org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    private org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource"
     *            hint="maven"
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

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
     * Target directory.
     * 
     *  @parameter expression="${project.build.directory}/modules"
     */
    private File targetDirectory;

    /**
     * @parameter default-value="features"
     */
    private String featuresName = "features";

    /**
     * Directories containing artifacts to exclude.
     * 
     * @parameter
     */
    private File[] excludeDirectories;

    /**
     * Directories containing groupids to exclude.
     * 
     * @parameter
     */
    private String[] excludeGroupIds;

    /**
     * Directories containing groupids to include.
     * 
     * @parameter
     */
    private String[] includeGroupIds;

    /**
     * Set to true to generate configurations under a folder named as the distro 
     * 
     * @parameter default-value="true"
     */
    private boolean useDistributionName = true;

    /**
     * Set to true to generate a PDE target platform configuration.
     * 
     *  @parameter default-value="true"
     */
    private boolean generateTargetPlatform = true;

    /**
     * OSGi execution environment
     */
    private String executionEnvironment;

    /**
     * A list of Eclipse features to be added to the target definition
     * @parameter
     */
    private String[] eclipseFeatures;

    /**
     * If we use the running eclipse as the default location for the target 
     * @parameter default-value="true"
     */
    private boolean useDefaultLocation = true;

    /**
     * Set to true to generate a plugin.xml.
     * 
     *  @parameter default-value="false"
     */
    private boolean generatePlugin;

    /**
     * Generate a configuration/config.ini for equinox
     * @parameter default-value="true"
     */
    private boolean generateConfig = true;
    
    /**
     * @parameter default-value="true"
     */
    private boolean generateBundleStart = true;

    /**
     * Generete manifest.jar
     * @parameter default-value="true"
     */
    private boolean generateManifestJar = true;
    
    /**
     * @parameter default-value="tuscany-sca-manifest.jar"
     */
    private String manifestJarName = "tuscany-sca-manifest.jar";

    /**
     * @parameter default-value="tuscany-sca-equinox-manifest.jar"
     */
    private String equinoxManifestJarName = "tuscany-sca-equinox-manifest.jar";
    

    /**
     * @parameter default-value="true"
     */
    private boolean generateAntScript = true;
    
    /**
     * @parameter
     */
    private ArtifactAggregation[] artifactAggregations;
    
    private static final String XML_PI = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String ASL_HEADER =
            "<!--"
            + "\n * Licensed to the Apache Software Foundation (ASF) under one"
            + "\n * or more contributor license agreements.  See the NOTICE file"
            + "\n * distributed with this work for additional information"
            + "\n * regarding copyright ownership.  The ASF licenses this file"
            + "\n * to you under the Apache License, Version 2.0 (the"
            + "\n * \"License\"); you may not use this file except in compliance"
            + "\n * with the License.  You may obtain a copy of the License at"
            + "\n * "
            + "\n *   http://www.apache.org/licenses/LICENSE-2.0"
            + "\n * "
            + "\n * Unless required by applicable law or agreed to in writing,"
            + "\n * software distributed under the License is distributed on an"
            + "\n * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY"
            + "\n * KIND, either express or implied.  See the License for the"
            + "\n * specific language governing permissions and limitations"
            + "\n * under the License."
            + "\n-->";    

    /**
     * Group the artifacts by distribution poms
     */
    private class ProjectSet {
        // Distribution projects
        private Map<String, MavenProject> projects;
        // Key: the pom artifact id
        // Value: the names for the artifacts
        private Map<String, Set<String>> nameMap = new HashMap<String, Set<String>>();
        
        private Map<String, String> artifactToNameMap = new HashMap<String, String>();

        public ProjectSet(List<MavenProject> projects) {
            super();
            this.projects = new HashMap<String, MavenProject>();
            for (MavenProject p : projects) {
                this.projects.put(p.getArtifactId(), p);
            }
        }

        private MavenProject getProject(String artifactId) {
            return projects.get(artifactId);
        }

        private void add(Artifact artifact, String name) {
            String key = ArtifactUtils.versionlessKey(artifact);
            for (MavenProject p : projects.values()) {
                Artifact a = (Artifact)p.getArtifactMap().get(key);
                if (a != null) {
                    Set<String> names = nameMap.get(p.getArtifactId());
                    if (names == null) {
                        names = new HashSet<String>();
                        nameMap.put(p.getArtifactId(), names);
                    }
                    names.add(name);
                }
            }
            artifactToNameMap.put(key, name);
        }
    }

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        try {

            // Create the target directory
            File root;
            if (targetDirectory == null) {
                root = new File(project.getBuild().getDirectory(), "plugins/");
            } else {
                root = targetDirectory;
            }
            root.mkdirs();

            // Build sets of exclude directories and included/excluded/groupids
            Set<String> excludedFileNames = new HashSet<String>();
            if (excludeDirectories != null) {
                for (File f : excludeDirectories) {
                    if (f.isDirectory()) {
                        for (String n : f.list()) {
                            excludedFileNames.add(n);
                        }
                    }
                }
            }
            Set<String> includedGroupIds = new HashSet<String>();
            if (includeGroupIds != null) {
                for (String g : includeGroupIds) {
                    includedGroupIds.add(g);
                }
            }
            Set<String> excludedGroupIds = new HashSet<String>();
            if (excludeGroupIds != null) {
                for (String g : excludeGroupIds) {
                    excludedGroupIds.add(g);
                }
            }

            // Find all the distribution poms 
            List<MavenProject> poms = new ArrayList<MavenProject>();
            poms.add(project);
            if (useDistributionName) {
                for (Object o : project.getArtifacts()) {
                    Artifact artifact = (Artifact)o;
                    if ("pom".equals(artifact.getType()) && artifact.getGroupId().equals(project.getGroupId())
                        && artifact.getArtifactId().startsWith("tuscany-feature-")) {
                        log.info("Dependent distribution: " + artifact);
                        MavenProject pomProject = buildProject(artifact);
                        poms.add(pomProject);
                        // log.info(pomProject.getArtifactMap().toString());
                    }
                }
            }

            // Process all the dependency artifacts
            ProjectSet bundleSymbolicNames = new ProjectSet(poms);
            ProjectSet bundleLocations = new ProjectSet(poms);
            ProjectSet jarNames = new ProjectSet(poms);
            for (Object o : project.getArtifacts()) {
                Artifact artifact = (Artifact)o;

                // Only consider Compile and Runtime dependencies
                if (!(Artifact.SCOPE_COMPILE.equals(artifact.getScope()) || Artifact.SCOPE_RUNTIME.equals(artifact
                    .getScope())
                    || Artifact.SCOPE_PROVIDED.equals(artifact.getScope()) || (generateTargetPlatform && Artifact.SCOPE_TEST
                    .equals(artifact.getScope())))) {
                    log.info("Skipping artifact: " + artifact);
                    continue;
                }

                // Only consider JAR and WAR files
                if (!"jar".equals(artifact.getType()) && !"war".equals(artifact.getType())) {
                    continue;
                }

                // Exclude artifact if its groupId is excluded or if it's not included
                if (excludedGroupIds.contains(artifact.getGroupId())) {
                    log.debug("Artifact groupId is excluded: " + artifact);
                    continue;
                }
                if (!includedGroupIds.isEmpty()) {
                    if (!includedGroupIds.contains(artifact.getGroupId())) {
                        log.debug("Artifact groupId is not included: " + artifact);
                        continue;
                    }
                }

                File artifactFile = artifact.getFile();
                if (!artifactFile.exists()) {
                    log.warn("Artifact doesn't exist: " + artifact);
                    continue;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Processing artifact: " + artifact);
                }

                // Get the bundle name if the artifact is an OSGi bundle
                String bundleName = null;
                try {
                    bundleName = BundleUtil.getBundleSymbolicName(artifact.getFile());
                } catch (IOException e) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }

                if (bundleName != null) {

                    // Exclude artifact if its file name is excluded
                    if (excludedFileNames.contains(artifactFile.getName())) {
                        log.debug("Artifact file is excluded: " + artifact);
                        continue;
                    }

                    // Copy an OSGi bundle as is 
                    log.info("Adding OSGi bundle artifact: " + artifact);
                    copyFile(artifactFile, root);
                    bundleSymbolicNames.add(artifact, bundleName);
                    bundleLocations.add(artifact, artifactFile.getName());
                    jarNames.add(artifact, artifactFile.getName());

                } else if ("war".equals(artifact.getType())) {

                    // Exclude artifact if its file name is excluded
                    if (excludedFileNames.contains(artifactFile.getName())) {
                        log.debug("Artifact file is excluded: " + artifact);
                        continue;
                    }

                    // Copy a WAR as is 
                    log.info("Adding WAR artifact: " + artifact);
                    copyFile(artifactFile, root);

                } else {

                    //                    String version = BundleUtil.osgiVersion(artifact.getVersion());
                    //                    String symbolicName = (artifact.getGroupId() + "." + artifact.getArtifactId());
                    //                    String dirName = symbolicName + "_" + version;

                    String dirName = artifactFile.getName().substring(0, artifactFile.getName().length() - 4);
                    File dir = new File(root, dirName);

                    // Exclude artifact if its file name is excluded
                    if (excludedFileNames.contains(dir.getName())) {
                        log.debug("Artifact file is excluded: " + artifact);
                        continue;
                    }

                    if (artifactAggregations != null) {
                        boolean aggregated = false;
                        for (ArtifactAggregation group : artifactAggregations) {
                            if (group.matches(artifact)) {
                                group.getArtifacts().add(artifact);
                                aggregated = true;
                                break;
                            }
                        }
                        if (aggregated) {
                            continue;
                        }
                    }

                    // Create a bundle directory for a non-OSGi JAR
                    log.info("Adding JAR artifact: " + artifact);
                    String version = BundleUtil.osgiVersion(artifact.getVersion());

                    Set<File> jarFiles = new HashSet<File>();
                    jarFiles.add(artifactFile);
                    String symbolicName = (artifact.getGroupId() + "." + artifact.getArtifactId());
                    Manifest mf = BundleUtil.libraryManifest(jarFiles, symbolicName, symbolicName, version, null);
                    File file = new File(dir, "META-INF");
                    file.mkdirs();
                    file = new File(file, "MANIFEST.MF");

                    FileOutputStream fos = new FileOutputStream(file);
                    write(mf, fos);
                    fos.close();
                    copyFile(artifactFile, dir);
                    bundleSymbolicNames.add(artifact, symbolicName);
                    bundleLocations.add(artifact, dir.getName());
                    jarNames.add(artifact, dirName + "/" + artifactFile.getName());
                }
            }

            if (artifactAggregations != null) {
                for (ArtifactAggregation group : artifactAggregations) {
                    if (group.getArtifacts().isEmpty()) {
                        continue;
                    }
                    String symbolicName = group.getSymbolicName();
                    String version = group.getVersion();
                    File dir = new File(root, symbolicName + "-" + version);
                    dir.mkdir();
                    Set<File> jarFiles = new HashSet<File>();
                    Artifact artifact = null;
                    for (Artifact a : group.getArtifacts()) {
                        log.info("Aggragating JAR artifact: " + a);
                        artifact = a;
                        jarFiles.add(a.getFile());
                        copyFile(a.getFile(), dir);
                        jarNames.add(a, symbolicName + "-" + version + "/" + a.getFile().getName());
                    }
                    Manifest mf = BundleUtil.libraryManifest(jarFiles, symbolicName, symbolicName, version, null);
                    File file = new File(dir, "META-INF");
                    file.mkdirs();
                    file = new File(file, "MANIFEST.MF");

                    FileOutputStream fos = new FileOutputStream(file);
                    write(mf, fos);
                    fos.close();
                    bundleSymbolicNames.add(artifact, symbolicName);
                    bundleLocations.add(artifact, dir.getName());
                }
            }

            // Generate a PDE target
            if (generateTargetPlatform) {
                generatePDETarget(bundleSymbolicNames, root, log);
            }

            // Generate a plugin.xml referencing the PDE target
            if (generatePlugin) {
                File pluginxml = new File(project.getBasedir(), "plugin.xml");
                FileOutputStream pluginXMLFile = new FileOutputStream(pluginxml);
                writePluginXML(new PrintStream(pluginXMLFile));
                pluginXMLFile.close();
            }

            if (generateConfig) {
                generateEquinoxConfig(bundleLocations, root, log);
            }

            if (generateManifestJar) {
                generateManifestJar(jarNames, root, log);
                generateEquinoxLauncherManifestJar(jarNames, root, log);
            }
            
            if (generateAntScript) {
                generateANTPath(jarNames, root, log);
            }

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

    private void generateANTPath(ProjectSet jarNames, File root, Log log) throws FileNotFoundException, IOException {
        for (Map.Entry<String, Set<String>> e : jarNames.nameMap.entrySet()) {
            Set<String> jars = e.getValue();
            File feature = new File(root, "../" + featuresName + "/" + (useDistributionName ? trim(e.getKey()) : ""));
            feature.mkdirs();
            File antPath = new File(feature, "build-path.xml");
            log.info("Generating ANT build path: " + antPath.getCanonicalPath());
            FileOutputStream fos = new FileOutputStream(antPath);
            PrintStream ps = new PrintStream(fos);
            // ps.println(XML_PI);
            ps.println(ASL_HEADER);
            String name = trim(e.getKey());
            ps.println("<project name=\"tuscany."+name+"\">");
            ps.println("  <property name=\"tuscany.distro\" value=\"" + name + "\"/>");
            ps.println("  <property name=\"tuscany.manifest\" value=\"" + new File(feature, manifestJarName).getCanonicalPath()
                + "\"/>");
            ps.println("  <path id=\"" + "tuscany.path" + "\">");
            ps.println("    <fileset dir=\"" + root.getCanonicalPath() + "\">");
            for (String jar : jars) {
                ps.println("      <include name=\"" + jar + "\"/>");
            }
            ps.println("    </fileset>");
            ps.println("  </path>");
            ps.println("</project>");
        }
    }

    private void generateManifestJar(ProjectSet jarNames, File root, Log log) throws FileNotFoundException, IOException {
        for (Map.Entry<String, Set<String>> e : jarNames.nameMap.entrySet()) {
            MavenProject pom = jarNames.getProject(e.getKey());
            Set<String> jars = e.getValue();
            File feature = new File(root, "../" + featuresName + "/" + (useDistributionName ? trim(e.getKey()) : ""));
            feature.mkdirs();
            File mfJar = new File(feature, manifestJarName);
            log.info("Generating manifest jar: " + mfJar.getCanonicalPath());
            FileOutputStream fos = new FileOutputStream(mfJar);
            Manifest mf = new Manifest();
            StringBuffer cp = new StringBuffer();
            String path = (useDistributionName ? "../../" : "../") + root.getName();
            for (String jar : jars) {
                cp.append(path).append('/').append(jar).append(' ');
            }
            if (cp.length() > 0) {
                cp.deleteCharAt(cp.length() - 1);
            }
            Attributes attrs = mf.getMainAttributes();
            attrs.putValue("Manifest-Version", "1.0");
            attrs.putValue("Implementation-Title", pom.getName());
            attrs.putValue("Implementation-Vendor", "The Apache Software Foundation");
            attrs.putValue("Implementation-Vendor-Id", "org.apache");
            attrs.putValue("Implementation-Version", pom.getVersion());
            attrs.putValue("Class-Path", cp.toString());
            attrs.putValue("Main-Class", "org.apache.tuscany.sca.node.launcher.NodeMain");
            JarOutputStream jos = new JarOutputStream(fos, mf);
            addFileToJar(jos, "META-INF/LICENSE", getClass().getResource("LICENSE.txt"));
            addFileToJar(jos, "META-INF/NOTICE", getClass().getResource("NOTICE.txt"));
            jos.close();
        }
    }

    private void generateEquinoxLauncherManifestJar(ProjectSet jarNames, File root, Log log) throws Exception {
        String equinoxLauncher = "org.apache.tuscany.sca:tuscany-node-launcher-equinox";
        Artifact artifact = (Artifact)project.getArtifactMap().get(equinoxLauncher);
        if (artifact == null) {
            return;
        }
        Set artifacts = resolveTransitively(artifact).getArtifacts();
        File feature = new File(root, "../" + featuresName + "/");
        feature.mkdirs();
        File mfJar = new File(feature, equinoxManifestJarName);
        log.info("Generating equinox manifest jar: " + mfJar.getCanonicalPath());
        FileOutputStream fos = new FileOutputStream(mfJar);
        Manifest mf = new Manifest();
        StringBuffer cp = new StringBuffer();
        String path = "../" + root.getName();

        for (Object o : artifacts) {
            Artifact a = (Artifact)o;
            if (!Artifact.SCOPE_TEST.equals(a.getScope())) {
                String id = ArtifactUtils.versionlessKey(a);
                String jar = jarNames.artifactToNameMap.get(id);
                if (jar != null) {
                    cp.append(path).append('/').append(jar).append(' ');
                }
            }
        }
        if (cp.length() > 0) {
            cp.deleteCharAt(cp.length() - 1);
        }
        Attributes attrs = mf.getMainAttributes();
        attrs.putValue("Manifest-Version", "1.0");
        attrs.putValue("Implementation-Title", artifact.getId());
        attrs.putValue("Implementation-Vendor", "The Apache Software Foundation");
        attrs.putValue("Implementation-Vendor-Id", "org.apache");
        attrs.putValue("Implementation-Version", artifact.getVersion());
        attrs.putValue("Class-Path", cp.toString());
        attrs.putValue("Main-Class", "org.apache.tuscany.sca.node.equinox.launcher.NodeMain");
        JarOutputStream jos = new JarOutputStream(fos, mf);
        addFileToJar(jos, "META-INF/LICENSE", getClass().getResource("LICENSE.txt"));
        addFileToJar(jos, "META-INF/NOTICE", getClass().getResource("NOTICE.txt"));
        jos.close();
    }

    private void generateEquinoxConfig(ProjectSet bundleLocations, File root, Log log) throws IOException {
        for (Map.Entry<String, Set<String>> e : bundleLocations.nameMap.entrySet()) {
            Set<String> locations = e.getValue();
            File feature = new File(root, "../" + featuresName + "/" + (useDistributionName ? trim(e.getKey()) : ""));
            File config = new File(feature, "configuration");
            config.mkdirs();
            File ini = new File(config, "config.ini");
            log.info("Generating configuation: " + ini.getCanonicalPath());
            FileOutputStream fos = new FileOutputStream(ini);
            PrintStream ps = new PrintStream(fos);
            ps.print("osgi.bundles=");
            for (String f : locations) {
                ps.print(f);
                if (generateBundleStart) {
                    ps.print("@:start,");
                } else {
                    ps.println(",");
                }
            }
            ps.println();
            ps.println("eclipse.ignoreApp=true");
            // Do not shutdown
            ps.println("osgi.noShutdown=true");
            // Start a default console
            ps.println("osgi.console=");
            ps.close();
        }
    }

    private void generatePDETarget(ProjectSet bundleSymbolicNames, File root, Log log) throws FileNotFoundException,
        IOException {
        for (Map.Entry<String, Set<String>> e : bundleSymbolicNames.nameMap.entrySet()) {
            Set<String> bundles = e.getValue();
            String name = trim(e.getKey());
            File feature = new File(root, "../" + featuresName + "/" + (useDistributionName ? name : ""));
            feature.mkdirs();
            File target = new File(feature, "tuscany.target");
            log.info("Generating target definition: " + target.getCanonicalPath());
            FileOutputStream targetFile = new FileOutputStream(target);
            if (!bundles.contains("org.eclipse.osgi")) {
                bundles.add("org.eclipse.osgi");
            }
            writeTarget(new PrintStream(targetFile), name, bundles, eclipseFeatures);
            targetFile.close();
        }
    }

    private MavenProject buildProject(Artifact artifact) throws ProjectBuildingException,
        InvalidDependencyVersionException, ArtifactResolutionException, ArtifactNotFoundException {
        MavenProject pomProject =
            mavenProjectBuilder.buildFromRepository(artifact, this.remoteRepos, this.local);
        if (pomProject.getDependencyArtifacts() == null) {
            pomProject.setDependencyArtifacts(pomProject
                .createArtifacts(factory,
                                 null, // Artifact.SCOPE_TEST,
                                 new ScopeArtifactFilter(Artifact.SCOPE_TEST)));
        }
        ArtifactResolutionResult result =
            resolver.resolveTransitively(pomProject.getDependencyArtifacts(),
                                         pomProject.getArtifact(),
                                         remoteRepos,
                                         local,
                                         artifactMetadataSource);
        pomProject.setArtifacts(result.getArtifacts());
        return pomProject;
    }
    
    private ArtifactResolutionResult resolveTransitively(Artifact artifact) throws ArtifactResolutionException, ArtifactNotFoundException {
        Artifact originatingArtifact = factory.createBuildArtifact("dummy", "dummy", "1.0", "jar");

        return resolver.resolveTransitively(Collections.singleton(artifact),
                                            originatingArtifact,
                                            local,
                                            remoteRepos,
                                            artifactMetadataSource,
                                            null);
    }
    
    /**
     * Convert tuscany-feature-xyz to feature-xyz
     * @param artifactId
     * @return
     */
    private String trim(String artifactId) {
        if (artifactId.startsWith("tuscany-feature-")) {
            return artifactId.substring("tuscany-feature-".length());
        } else {
            return artifactId;
        }
    }

    private static void copyFile(File jar, File dir) throws FileNotFoundException, IOException {
        byte[] buf = new byte[4096];
        File jarFile = new File(dir, jar.getName());
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
    
    private static void addFileToJar(JarOutputStream out, String entryName, URL file) throws FileNotFoundException, IOException {
        byte[] buf = new byte[4096];
        InputStream in = file.openStream();
        out.putNextEntry(new ZipEntry(entryName));
        for (;;) {
            int len = in.read(buf);
            if (len > 0) {
                out.write(buf, 0, len);
            } else {
                break;
            }
        }
        in.close();
        out.closeEntry();
    }    

    private void writeTarget(PrintStream ps, String pom, Set<String> ids, String[] features) {
        ps.println(XML_PI);
        ps.println("<?pde version=\"3.2\"?>");
        ps.println(ASL_HEADER);

        ps.println("<target name=\"Eclipse Target - " + pom + "\">");

        if (executionEnvironment != null) {
            ps.println("  <targetJRE>");
            ps.println("    <execEnv>" + executionEnvironment + "</execEnv>");
            ps.println("  </targetJRE>");
        }

        if (useDefaultLocation) {
            ps.println("  <location useDefault=\"true\"/>");
        } else {
            ps.println("  <location path=\"" + targetDirectory + "\"/>");
        }

        // ps.println("<content useAllPlugins=\"true\">");
        ps.println("  <content>");
        ps.println("    <plugins>");
        for (String id : ids) {
            ps.println("      <plugin id=\"" + id + "\"/>");
        }
        ps.println("    </plugins>");
        ps.println("    <features>");
        if (features != null) {
            for (String f : features) {
                ps.println("      <feature id=\"" + f + "\"/>");
            }
        }
        ps.println("    </features>");
        if (useDefaultLocation) {
            ps.println("    <extraLocations>");
            // Not sure why the extra path needs to the plugins folder
            ps.println("      <location path=\"" + targetDirectory + "\"/>");
            ps.println("    </extraLocations>");
        }
        ps.println("  </content>");

        ps.println("</target>");

    }

    private static void writePluginXML(PrintStream ps) {
        ps.println(XML_PI);
        ps.println("<?pde version=\"3.2\"?>");
        ps.println(ASL_HEADER);
        ps.println("<plugin>");
        ps.println("<extension point = \"org.eclipse.pde.core.targets\">");
        ps.println("<target");
        ps.println("id=\"org.apache.tuscany.sca.target\"");
        ps.println("name=\"Apache Tuscany Eclipse Target\"");
        ps.println("path=\"tuscany.target\"/>");
        ps.println("</extension>");
        ps.println("</plugin>");
    }
}
