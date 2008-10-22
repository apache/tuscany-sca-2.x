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
 * @version $Rev$ $Date$
 * @goal build-thirdparty-distro
 * @phase generate-resources
 * @requiresDependencyResolution test
 * @description Build an OSGi bundle for third party dependencies
 */
public class ThirdPartyBundleDistroMojo extends AbstractMojo {
    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        if (project.getPackaging().equals("pom")) {
            return;
        }
        try {

            File root = new File(project.getBasedir(), "eclipse/plugins/");
            root.mkdirs();

            Set<String> ids = new HashSet<String>();
            String projectGroupId = project.getGroupId();
            for (Object o : project.getArtifacts()) {
                Artifact artifact = (Artifact)o;

                if (!(Artifact.SCOPE_COMPILE.equals(artifact.getScope()) || Artifact.SCOPE_RUNTIME.equals(artifact
                    .getScope()))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping artifact: " + artifact);
                    }
                    continue;
                }
                if (!"jar".equals(artifact.getType())) {
                    continue;
                }
                if (projectGroupId.equals(artifact.getGroupId())) {
                    continue;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Artifact: " + artifact);
                }
                String bundleName = null;
                try {
                    bundleName = BundleUtil.getBundleSymbolicName(artifact.getFile());
                } catch (IOException e) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }
                File artifactFile = artifact.getFile();
                if (!artifactFile.exists()) {
                    log.warn("Artifact doesn't exist: " + artifact);
                    continue;
                }

                if (bundleName != null) {
                    log.info("Adding third party bundle: " + artifact);
                    copyFile(artifactFile, root);
                    ids.add(bundleName);
                } else {
                    log.info("Adding third party jar: " + artifact);
                    String version = BundleUtil.version(artifactFile.getPath());

                    Set<File> jarFiles = new HashSet<File>();
                    jarFiles.add(artifactFile);
                    String symbolicName = (artifact.getGroupId() + "." + artifact.getArtifactId()).replace('-', '.');
                    Manifest mf =
                        BundleUtil.libraryManifest(jarFiles, symbolicName + "_" + version, symbolicName, version, null);
                    File dir = new File(root, artifactFile.getName().substring(0, artifactFile.getName().length() - 4));
                    File file = new File(dir, "META-INF");
                    file.mkdirs();
                    file = new File(file, "MANIFEST.MF");

                    FileOutputStream fos = new FileOutputStream(file);
                    write(mf, fos);
                    fos.close();
                    copyFile(artifactFile, dir);
                    ids.add(symbolicName);
                }
            }
            
            File target = new File(project.getBasedir(), "tuscany.target");
            FileOutputStream targetFile = new FileOutputStream(target);
            writeTarget(new PrintStream(targetFile), ids);
            targetFile.close();
            
            File pluginxml = new File(project.getBasedir(), "plugin.xml");
            FileOutputStream pluginXMLFile = new FileOutputStream(pluginxml);
            writePluginXML(new PrintStream(pluginXMLFile));
            pluginXMLFile.close();
            

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
