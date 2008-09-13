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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

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
    protected File basedir;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected java.util.List remoteRepos;

    /**
     * @parameter
     */
    protected boolean copyJars = false;

    public void execute() throws MojoExecutionException {
        if (project.getPackaging().equals("pom")) {
            return;
        }

        Log log = getLog();
        List<File> jarFiles = new ArrayList<File>();
        for (Object o : project.getArtifacts()) {
            Artifact a = (Artifact)o;
            if (!(Artifact.SCOPE_COMPILE.equals(a.getScope()) || Artifact.SCOPE_RUNTIME.equals(a.getScope()))) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping artifact: " + a);
                }
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("Artifact: " + a);
            }
            String bundleName = null;
            try {
                bundleName = LibraryBundleUtil.getBundleName(a.getFile());
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            if (bundleName == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding non-OSGi jar: " + a);
                }
                jarFiles.add(a.getFile());
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
            mf.write(fos);
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
                    int len = 0;
                    while (len > 0) {
                        len = in.read(buf);
                        if (len > 0) {
                            out.write(buf, 0, len);
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
