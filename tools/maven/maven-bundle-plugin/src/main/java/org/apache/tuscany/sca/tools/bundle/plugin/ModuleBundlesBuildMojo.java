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
     * Set to true to generate a PDE target platform configuration.
     * 
     *  @parameter
     */
    private boolean generateTargetPlatform;
    
    /**
     * Set to true to generate a plugin.xml.
     * 
     *  @parameter
     */
    private boolean generatePlugin;

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
            
            // Build sets of exclude directories and groupids
            Set<String> excludedFileNames = new HashSet<String>(); 
            if (excludeDirectories != null) {
                for (File f: excludeDirectories) {
                    for (String n: f.list()) {
                        excludedFileNames.add(n);
                    }
                }
            }
            Set<String> excludedGroupIds = new HashSet<String>();
            if (excludeGroupIds != null) {
                for (String g: excludeGroupIds) {
                    excludedGroupIds.add(g);
                }
            }

            // Process all the dependency artifacts
            Set<String> bundleSymbolicNames = new HashSet<String>();
            for (Object o : project.getArtifacts()) {
                Artifact artifact = (Artifact)o;

                // Only consider Compile and Runtime dependencies
                if (!(Artifact.SCOPE_COMPILE.equals(artifact.getScope()) || Artifact.SCOPE_RUNTIME.equals(artifact.getScope()))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping artifact: " + artifact);
                    }
                    continue;
                }
                
                // Only consider JAR and WAR files
                if (!"jar".equals(artifact.getType()) && !"war".equals(artifact.getType())) {
                    continue;
                }
                
                // Exclude artifact if its groupId is excluded
                if (excludedGroupIds.contains(artifact.getGroupId())) {
                    log.debug("Artifact groupId is excluded: " + artifact);
                    continue;
                }
                
                File artifactFile = artifact.getFile();
                if (!artifactFile.exists()) {
                    log.warn("Artifact doesn't exist: " + artifact);
                    continue;
                }
                
                // Exclude artifact if its file name is excluded
                if (excludedFileNames.contains(artifactFile.getName())) {
                    log.debug("Artifact file is excluded: " + artifact);
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
                    
                    // Copy an OSGi bundle as is 
                    log.info("Adding OSGi bundle artifact: " + artifact);
                    copyFile(artifactFile, root);
                    bundleSymbolicNames.add(bundleName);
                    
                } else if ("war".equals(artifact.getType())) {
                    
                    // Copy a WAR as is 
                    log.info("Adding WAR artifact: " + artifact);
                    copyFile(artifactFile, root);
                    bundleSymbolicNames.add(bundleName);
                    
                } else {
                    
                    // Create a bundle directory for a non-OSGi JAR
                    log.info("Adding JAR artifact: " + artifact);
                    String version = BundleUtil.version(artifactFile.getPath());

                    Set<File> jarFiles = new HashSet<File>();
                    jarFiles.add(artifactFile);
                    String symbolicName = (artifact.getGroupId() + "." + artifact.getArtifactId()).replace('-', '.');
                    Manifest mf = BundleUtil.libraryManifest(jarFiles, symbolicName + "_" + version, symbolicName, version, null);
                    File dir = new File(root, artifactFile.getName().substring(0, artifactFile.getName().length() - 4));
                    File file = new File(dir, "META-INF");
                    file.mkdirs();
                    file = new File(file, "MANIFEST.MF");

                    FileOutputStream fos = new FileOutputStream(file);
                    write(mf, fos);
                    fos.close();
                    copyFile(artifactFile, dir);
                    bundleSymbolicNames.add(symbolicName);
                }
            }
            
            // Generate a PDE target
            if (generateTargetPlatform) {
                File target = new File(project.getBasedir(), "tuscany.target");
                FileOutputStream targetFile = new FileOutputStream(target);
                writeTarget(new PrintStream(targetFile), bundleSymbolicNames);
                targetFile.close();
            }
            
            // Generate a plugin.xml referencing the PDE target
            if (generatePlugin) {
                File pluginxml = new File(project.getBasedir(), "plugin.xml");
                FileOutputStream pluginXMLFile = new FileOutputStream(pluginxml);
                writePluginXML(new PrintStream(pluginXMLFile));
                pluginXMLFile.close();
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

    private static void writeTarget(PrintStream ps, Set<String> ids) {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<?pde version=\"3.2\"?>");

        ps.println("<target name=\"Apache Tuscany Eclipse Target\">");
        ps.println("<location path=\"${project_loc}/eclipse\"/>");

        ps.println("<content>");
        ps.println("<plugins>");
        for (String id : ids) {
            ps.println("<plugin id=\"" + id + "\"/>");
        }
        ps.println("</plugins>");
        ps.println("<features>");
        ps.println("</features>");
        ps.println("<extraLocations>");
        // Not sure why the extra path needs to the plugins folder
        ps.println("<location path=\"${eclipse_home}/plugins\"/>"); 
        ps.println("</extraLocations>");
        ps.println("</content>");

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
