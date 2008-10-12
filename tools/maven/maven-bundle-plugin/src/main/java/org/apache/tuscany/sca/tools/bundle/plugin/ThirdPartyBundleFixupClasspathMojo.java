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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

/**
 * @version $Rev$ $Date$
 * @goal fixup-pde-classpath
 * @phase process-resources
 * @requiresDependencyResolution test
 * @description Adjust third party bundle classpath
 */
public class ThirdPartyBundleFixupClasspathMojo extends AbstractMojo {
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
    private File basedir;

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        if (project.getPackaging().equals("pom")) {
            return;
        }

        try {
            // Adjust .classpath, make all classpath entries point to the lib directory
            File classpath = new File(basedir, ".classpath");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(classpath)));
            StringWriter buffer = new StringWriter();
            PrintWriter printer = new PrintWriter(buffer); 
            for (;;) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                int i = line.indexOf("kind=\"var\"");
                if (i != -1) {
                    line = line.substring(0, i) + "kind=\"lib\" exported=\"true\"" + line.substring(i+10);
                    
                    i = line.indexOf("path=\"");
                    if (i != -1) {
                        int e = line.indexOf("\"", i+6);
                        String path = line.substring(i + 6, e);
                        int s = path.lastIndexOf("/");
                        if (s != -1) {
                            path = "lib" + path.substring(s);
                            line = line.substring(0, i) + "path=\"" + path + line.substring(e);
                        }
                    }
                }
                printer.println(line);
            }
            reader.close();
            Writer writer = new OutputStreamWriter(new FileOutputStream(classpath));
            writer.write(buffer.toString());
            writer.close();
            
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

}
