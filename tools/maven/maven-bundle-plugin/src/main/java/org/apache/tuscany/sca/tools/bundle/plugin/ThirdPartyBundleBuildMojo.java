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
import java.io.FileOutputStream;
import java.io.IOException;
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
 * @goal assemble-thirdparty-bundle
 * @phase generate-resources
 * @requiresDependencyResolution test
 * @description Build an OSGi bundle for third party dependencies
 */
public class ThirdPartyBundleBuildMojo extends AbstractMojo {
    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The bundle symbolic name
     *
     * @parameter
     */
    private String symbolicName;
    
    public void execute() throws MojoExecutionException {
        Log log = getLog();

        if (project.getPackaging().equals("pom")) {
            return;
        }

        String projectGroupId = project.getGroupId();
        Set<File> jarFiles = new HashSet<File>();
        for (Object o : project.getArtifacts()) {
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
            if (bundleName == null || true) {
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

            Manifest mf = BundleUtil.libraryManifest(jarFiles, project.getName(), symbolicName, version, "lib");
            File file = new File(project.getBasedir(), "META-INF");
            file.mkdir();
            file= new File(file, "MANIFEST.MF");
            if (log.isDebugEnabled()) {
                log.debug("Generating " + file);
            }

            FileOutputStream fos = new FileOutputStream(file);
            write(mf, fos);
            fos.close();

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
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

}
