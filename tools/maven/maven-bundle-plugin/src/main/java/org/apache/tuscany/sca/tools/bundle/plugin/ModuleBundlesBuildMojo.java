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
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

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
     * Target directory.
     * 
     *  @parameter
     */
    private File targetDirectory;

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
     * Set to true to generate a PDE target platform configuration.
     * 
     *  @parameter
     */
    private boolean generateTargetPlatform;
    
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
     * @parameter
     */
    private boolean useDefaultLocation = true;

    /**
     * Set to true to generate a plugin.xml.
     * 
     *  @parameter
     */
    private boolean generatePlugin;
    
    /**
     * Generate a configuration/config.ini for equinox
     * @parameter
     */
    private boolean generateConfig;
    
    /**
     * Generete startup/-manifest.jar
     * @parameter
     */
    private boolean generateManifestJar;

    /**
     * @parameter
     */
    private ArtifactAggregation[] artifactAggregations;

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

            // Process all the dependency artifacts
            Set<String> bundleSymbolicNames = new HashSet<String>();
            Set<String> bundleLocations = new HashSet<String>();
            Set<String> jarNames = new HashSet<String>();
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
                    bundleSymbolicNames.add(bundleName);
                    bundleLocations.add(artifactFile.getName());
                    jarNames.add(artifactFile.getName());

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
                    bundleSymbolicNames.add(symbolicName);
                    bundleLocations.add(dir.getName());
                    jarNames.add(dirName + "/" + artifactFile.getName());
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
                    for (Artifact a : group.getArtifacts()) {
                        log.info("Aggragating JAR artifact: " + a);
                        jarFiles.add(a.getFile());
                        copyFile(a.getFile(), dir);
                        jarNames.add(symbolicName + "-" + version + "/" + a.getFile().getName());
                      }
                    Manifest mf = BundleUtil.libraryManifest(jarFiles, symbolicName, symbolicName, version, null);
                    File file = new File(dir, "META-INF");
                    file.mkdirs();
                    file = new File(file, "MANIFEST.MF");

                    FileOutputStream fos = new FileOutputStream(file);
                    write(mf, fos);
                    fos.close();
                    bundleSymbolicNames.add(symbolicName);
                    bundleLocations.add(dir.getName());
                }
            }

            // Generate a PDE target
            if (generateTargetPlatform) {
                File target = new File(project.getBuild().getDirectory(), project.getArtifactId() + ".target");
                log.info("Generating target definition: " + target);
                FileOutputStream targetFile = new FileOutputStream(target);
                if (!bundleSymbolicNames.contains("org.eclipse.osgi")) {
                    bundleSymbolicNames.add("org.eclipse.osgi");
                }
                writeTarget(new PrintStream(targetFile), bundleSymbolicNames, eclipseFeatures);
                targetFile.close();
            }

            // Generate a plugin.xml referencing the PDE target
            if (generatePlugin) {
                File pluginxml = new File(project.getBasedir(), "plugin.xml");
                FileOutputStream pluginXMLFile = new FileOutputStream(pluginxml);
                writePluginXML(new PrintStream(pluginXMLFile));
                pluginXMLFile.close();
            }
            
            if(generateConfig) {
                File config = new File(root, "configuration");
                config.mkdir();
                File ini = new File(config, "config.ini");
                log.info("Generating configuation: " + ini);
                FileOutputStream fos = new FileOutputStream(ini); 
                PrintStream ps = new PrintStream(fos);
                ps.print("osgi.bundles=");
                for(String f: bundleLocations) {
                    ps.print(f);
                    ps.print("@:start,");
                }
                ps.println();
                ps.println("eclipse.ignoreApp=true");
                ps.close();
            }
            
            if (generateManifestJar) {
                File startup = new File(new File(project.getBuild().getDirectory()), "startup");
                startup.mkdir();
                File mfJar = new File(startup, project.getArtifactId() + "-manifest.jar");
                log.info("Generating manifest jar: " + mfJar);
                FileOutputStream fos = new FileOutputStream(mfJar);
                Manifest mf = new Manifest();
                StringBuffer cp = new StringBuffer();
                for (String jar : jarNames) {
                    cp.append(jar).append(',');
                }
                if (cp.length() > 0) {
                    cp.deleteCharAt(cp.length() - 1);
                }
                Attributes attrs = mf.getMainAttributes();
                attrs.putValue("Manifest-Version", "1.0");
                attrs.putValue("Implementation-Title", project.getName());
                attrs.putValue("Implementation-Vendor", "The Apache Software Foundation");
                attrs.putValue("Implementation-Vendor-Id", "org.apache");
                attrs.putValue("Implementation-Version", project.getVersion());
                attrs.putValue("Class-Path", cp.toString());
                JarOutputStream jos = new JarOutputStream(fos, mf);
                jos.close();
            }

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
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

    private void writeTarget(PrintStream ps, Set<String> ids, String[] features) {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<?pde version=\"3.2\"?>");

        ps.println("<target name=\"Eclipse Target - " + project.getArtifactId() + "\">");

        if (executionEnvironment != null) {
            ps.println("  <targetJRE>");
            ps.println("    <execEnv>" + executionEnvironment + "</execEnv>");
            ps.println("  </targetJRE>");
        }

        if(useDefaultLocation) {
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
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<?pde version=\"3.2\"?>");
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
