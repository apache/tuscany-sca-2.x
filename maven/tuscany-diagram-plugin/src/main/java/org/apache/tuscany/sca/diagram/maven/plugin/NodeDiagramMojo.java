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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;

/**
 * Maven Mojo to generate the diagrams for Tuscany Node
 * 
 * 
 * @goal generateNodeDiagram
 * @requiresDependencyResolution runtime
 * @phase process-classes
 * @description 
 */
public class NodeDiagramMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.directory}/composite-diagrams"
     */
    private File outputDirectory;

    /**
     * @parameter The node.xml
     */
    private File nodeConfiguration;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File contribution;

    /**
     * @parameter expression="${project.runtimeClasspathElements}"
     */
    private List<String> runtimeClasspathElements;

    /**
     * @parameter The root folder of a web application
     */
    private File webApplication;

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
        // getLog().info(String.valueOf(runtimeClasspathElements));
        try {
            NodeFactory factory = NodeFactory.getInstance();
            NodeConfiguration configuration = null;
            if (nodeConfiguration != null) {
                getLog().info("Loading node configuration: " + nodeConfiguration);
                configuration =
                    factory
                        .loadConfiguration(new FileInputStream(nodeConfiguration), nodeConfiguration.toURI().toURL());
            } else if (contribution != null) {
                getLog().info("Loading contribution: " + contribution);
                configuration = factory.createNodeConfiguration().addContribution(contribution.toURI().toURL());
            }

            URL[] paths = buildClasspath();
            ClassLoader classLoader = new URLClassLoader(paths, Thread.currentThread().getContextClassLoader());
            outputDirectory.mkdirs();
            String svg = org.apache.tuscany.sca.diagram.main.Main.generateDiagram(configuration, classLoader, baseURL);
            File svgFile = new File(outputDirectory, "node.svg");
            FileWriter fw = new FileWriter(svgFile);
            fw.write(svg);
            fw.close();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

    private URL[] buildClasspath() throws IOException {
        List<URL> urls = new ArrayList<URL>();
        if (webApplication != null) {
            File root = webApplication;
            File classes = new File(root, "WEB-INF/classes");
            urls.add(classes.toURI().toURL());

            File lib = new File(root, "WEB-INF/lib");
            for (File jar : listJarFiles(lib)) {
                urls.add(jar.toURI().toURL());
            }
        }
        for (String path : runtimeClasspathElements) {
            urls.add(new File(path).toURI().toURL());
        }
        return urls.toArray(new URL[urls.size()]);
    }

    private List<File> listJarFiles(File lib) {
        List<File> compositeFiles = new ArrayList<File>();
        // getLog().info(compositeDirectory.getAbsolutePath());
        listJarFiles(lib, compositeFiles);
        return compositeFiles;
    }

    private void listJarFiles(File dir, List<File> files) {

        if (!dir.isDirectory()) {
            return;
        }
        File[] list = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".jar");
            }
        });

        for (File f : list) {
            if (f.isFile()) {
                files.add(f);
            }
            //            else {
            //                listJarFiles(f, files);
            //            }
        }
    }

}
