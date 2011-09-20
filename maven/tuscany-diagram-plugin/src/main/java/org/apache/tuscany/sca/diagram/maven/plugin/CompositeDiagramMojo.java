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
package org.apache.tuscany.sca.diagram.maven.plugin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Maven Mojo to generate the diagrams for SCA composites 
 * 
 * 
 * @goal generate
 * @requiresDependencyResolution runtime
 * @phase generate-sources
 * @description 
 */
public class CompositeDiagramMojo extends AbstractMojo {

    /**
     * @parameter expression="${composites}" 
     */
    private File[] composites;

    /**
     * @parameter expression="${project.basedir}/src/main/resources"
     */
    private File compositeDirectory;

    /**
     * @parameter expression="${project.build.directory}/composite-diagrams"
     */
    private File outputDirectory;

    /**
     * @parameter The base URL for the clickable blocks
     */
    private String baseURL;

    /**
     * @parameter
     */
    private boolean generateSVG = true;

    /**
     * @parameter 
     */
    private boolean generateJPG = false;

    /**
     * @parameter 
     */
    private boolean generateHTML = false;

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<File> compositeFiles = null;
        if (composites == null || composites.length == 0) {
            compositeFiles = listCompositeFiles();
        } else {
            compositeFiles = Arrays.asList(composites);
        }

        outputDirectory.mkdirs();
        for (File f : compositeFiles) {
            try {
                getLog().info("Generating diagram for " + f);
                org.apache.tuscany.sca.diagram.main.Main.generate(outputDirectory,
                                                                  baseURL,
                                                                  generateSVG,
                                                                  generateHTML,
                                                                  generateJPG,
                                                                  f.getAbsolutePath());
            } catch (Exception e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

    }

    private List<File> listCompositeFiles() {
        List<File> compositeFiles = new ArrayList<File>();
        // getLog().info(compositeDirectory.getAbsolutePath());
        listCompositeFiles(compositeDirectory, compositeFiles);
        return compositeFiles;
    }

    private void listCompositeFiles(File dir, List<File> files) {

        if (!dir.isDirectory()) {
            return;
        }
        File[] list = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".composite") || pathname.isDirectory();
            }
        });

        for (File f : list) {
            if (f.isFile()) {
                files.add(f);
            } else {
                listCompositeFiles(f, files);
            }
        }
    }

}
