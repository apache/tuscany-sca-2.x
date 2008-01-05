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
package org.apache.tuscany.sca.tools.ant.generator.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * A Maven plugin that generates an Ant build.xml file for Tuscany SCA samples.
 * 
 * Build dependencies and additional build steps like WSDL2Java for example are
 * automatically determined from the pom.xml file describing the module's Maven build.
 * 
 * @version $Rev$ $Date$
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution test
 * @description Generate Ant build script for an SCA project
 */
public class AntGeneratorMojo extends AbstractMojo {

    /**
     * The project to generate an Ant build for.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Used for resolving artifacts
     *
     * @component
     */
    private ArtifactResolver resolver;

    /**
     * Factory for creating artifact objects
     *
     * @component
     */
    private ArtifactFactory factory;

    /**
     * The local repository where the artifacts are located
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * The remote repositories where artifacts are located
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     */
    private List remoteRepositories;

    /**
     * The current user system settings for use in Maven.
     *
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;

    /**
     * The main class name.
     * @parameter
     */
    private String mainClass;
    
    /**
     * If set true then only the dependency file is created. The dependency
     * file can then be included in a hand generated build.xml file
     * @parameter
     */
    private Boolean buildDependencyFileOnly;    
    
    /**
     * The build.xml file to generate.
     * @parameter expression="${basedir}/build.xml"
     */
    private String buildFile;
    
    /**
     * The build-dependency.xml file to generate.
     * @parameter expression="${basedir}/build-dependency.xml"
     */
    private String buildDependencyFile;    
    
    public void execute() throws MojoExecutionException {
        if ((buildDependencyFileOnly != null) &&
            (buildDependencyFileOnly == true)){
            generateBuildDependencyFile();
        } else {
            generateBuildFile();
        }
    }

    /**
     * Generate Ant build dependency XML file
     */ 
    private void generateBuildDependencyFile() throws MojoExecutionException {
        
        getLog().info("Generating " + buildDependencyFile);
        
        // Open the target build-dependency.xml file
        File targetFile = new File(buildDependencyFile);
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.toString());
        }

        // Generate the Apache license header
        generateLicenseHeader(pw);

        // Generate Ant filesets representing the build dependencies
        generateBuildDependencies(pw);
        
        pw.close();
    }
    
    /**
     * Generate Ant build XML file
     */ 
    private void generateBuildFile() throws MojoExecutionException {
        
        getLog().info("Generating " + buildFile);
        
        // Open the target build.xml file
        File targetFile = new File(buildFile);
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.toString());
        }

        // Determine the project packaging
        String packaging = project.getPackaging().toLowerCase();

        // Generate the Apache license header
        generateLicenseHeader(pw);
        
        pw.println("<project name=\"" + project.getArtifactId() + "\" default=\"compile\">");
        pw.println();
        
        // Generate the compile target
        int base = project.getBasedir().toString().length() + 1;
        pw.println("    <target name=\"compile\">");
        pw.println("        <mkdir dir=\"target/classes\"/>");

        // Generate any pre-compilation tasks
        generatePreCompileTasks(pw);
        
        // Generate the compile task
        pw.println("        <javac destdir=\"target/classes\" debug=\"on\" source=\"1.5\" target=\"1.5\">");
        for (String source: (List<String>)project.getCompileSourceRoots()) {
            if (source.length() > base) {
                source = source.substring(base);
            } else {
                source = ".";
            }
            pw.println("            <src path=\"" + source + "\"/>");
        }
        pw.println("            <classpath>");
        pw.println("                <fileset refid=\"tuscany.jars\"/>");
        pw.println("                <fileset refid=\"3rdparty.jars\"/>");
        pw.println("            </classpath>");
        pw.println("        </javac>");
        pw.println("        <copy todir=\"target/classes\">");
        for (FileSet resource: (List<FileSet>)project.getResources()) {
            String source = resource.getDirectory();
            if (source.length() > base) {
                source = source.substring(base);
                
                if (source.equals(".")){
                    pw.println("            <fileset dir=\".\" includes=\"*\" excludes=\"src, target, pom.xml, build.xml\"/>");
                } else {
                    pw.println("            <fileset dir=\"" + source + "\"/>");
                }
            } else {
                if (project.getResources().size() > 1) {
                    break;
                }
                pw.println("            <fileset dir=\".\" excludes=\"**/*.java, pom.xml, build.xml, target\"/>");
                source = ".";
            }
        }
        pw.println("        </copy>");
        
        // Build a JAR
        if (packaging.equals("jar")) {
            pw.println("        <jar destfile=\"target/" + project.getArtifactId() + ".jar\" basedir=\"target/classes\">");
            pw.println("            <manifest>");
            if (mainClass != null) {
                pw.println("                <attribute name=\"Main-Class\" value=\"" + mainClass + "\"/>");
            }
            pw.println("            </manifest>");
            pw.println("        </jar>");
            
        } else if (packaging.equals("war")) {
            
            // Build a WAR
            pw.println("        <war destfile=\"target/" + project.getArtifactId() + ".war\" webxml=\"src/main/webapp/WEB-INF/web.xml\">");
            pw.println("            <fileset dir=\"src/main/webapp\"/>");
            pw.println("            <lib refid=\"tuscany.jars\"/>");
            pw.println("            <lib refid=\"3rdparty.jars\"/>");
            pw.println("            <classes dir=\"target/classes\"/>");
            pw.println("        </war>");
        }
        pw.println("    </target>");
        pw.println();
        
        
        // Generate a package target alongside the compile target 
        // Tuscany SCA samples use "package" as the target for webapps
        pw.println("    <target name=\"package\" depends=\"compile\"/>");
        pw.println();
    
        // Generate the run target
        if (mainClass != null) {
            pw.println("    <target name=\"run\">");
            pw.println("        <java classname=\"" + mainClass + "\" fork=\"true\">");
            pw.println("            <classpath>");
            pw.println("                <pathelement location=\"target/" + project.getArtifactId() + ".jar\"/>");
            pw.println("                <fileset refid=\"tuscany.jars\"/>");
            pw.println("                <fileset refid=\"3rdparty.jars\"/>");
            pw.println("            </classpath>");
            pw.println("        </java>");
            pw.println("    </target>");
            pw.println();
        }
        
        // Generate the clean target
        pw.println("    <target name=\"clean\">");
        pw.println("        <delete includeemptydirs=\"true\">");
        pw.println("            <fileset dir=\"target\"/>");
        pw.println("        </delete>");
        pw.println("    </target>");
        pw.println();

        // Generate Ant filesets representing the build dependencies
        generateBuildDependencies(pw);
        
        pw.println("</project>");
        pw.close();
    }

    /**
     * Generate Ant filesets representing the build dependencies. 
     * @param pw PrintWriter to write to
     */
    private void generateBuildDependencies(PrintWriter pw) {

        // Determine the module dependencies
        List<String> tuscanyModules = new ArrayList<String>();
        List<String> otherModules = new ArrayList<String>();
        for (Artifact artifact: (List<Artifact>)project.getRuntimeArtifacts()) {
            if (artifact.getGroupId().startsWith("org.apache.tuscany.sca")) {
                tuscanyModules.add(artifact.getFile().getName());
            } else {
                otherModules.add(artifact.getFile().getName());
            }
        }
        
        // Sort lists of modules, making output deterministic
        Collections.sort(tuscanyModules);
        Collections.sort(otherModules);

        // Generate filesets for the tuscany and 3rd party dependencies
        pw.println("    <fileset id=\"tuscany.jars\" dir=\"../../modules\">");
        for (String name: tuscanyModules) {
            pw.println("        <include name=\"" + name +"\"/>");
        }
        pw.println("    </fileset>");
        pw.println("    <fileset id=\"3rdparty.jars\" dir=\"../../lib\">");
        for (String name: otherModules) {
            pw.println("        <include name=\"" + name +"\"/>");
        }
        pw.println("    </fileset>");
        pw.println();
    }
    
    /**
     * Extract plugin execution configurations out of the Maven model. This handles
     * nested configurations with a base configuration and a collection of nested
     * configuration elements, for example: 
     * <configuration>
     *     <schemaFiles>
     *         <configuration>
     *             <fileName>x.wsdl</fileName>
     *         </configuration>
     *         <configuration>
     *             <fileName>y.wsdl</fileName>
     *         </configuration>
     *     </schemaFiles>
     *     <noNotification>true</noNotification>
     * </configuration>
     *                         
     * @param execution Maven plugin execution model
     * @return a list of maps containing the plugin configuration key value pairs
     */
    private static List<Map<String, String>> pluginConfigurations(PluginExecution execution) {
        List<Map<String, String>> configurations = new ArrayList<Map<String,String>>();
        Map<String, String> topConfiguration = new HashMap<String, String>();
        
        Xpp3Dom dom = (Xpp3Dom)execution.getConfiguration();
        for (Xpp3Dom element: dom.getChildren()) {
            if (element.getChildCount() != 0) {
                // Handle nested configuration element, create a child configuration
                // for each
                for (Xpp3Dom childConfigurationElement: element.getChildren()) {
                    Map<String, String> childConfiguration = new HashMap<String, String>();
                    for (Xpp3Dom childElement: childConfigurationElement.getChildren()) {
                        childConfiguration.put(childElement.getName(), childElement.getValue());
                    }
                    configurations.add(childConfiguration);
                }
            } else {
                // Handle top level key value pair element
                topConfiguration.put(element.getName(), element.getValue());
            }
        }
        // Return the top configuration or the child configurations merged
        // with the top one
        if (configurations.isEmpty()) {
            configurations.add(topConfiguration);
        } else {
            for (Map<String, String> configuration: configurations) {
                configuration.putAll(topConfiguration);
            }
        }
        return configurations;
    }
    
    /**
     * Generate Ant tasks for the pre-compilation generation plugins
     * used in the Maven module.
     * @param pw PrintWriter to write to
     * @return list of directories containing generated source to compile
     */
    private void generatePreCompileTasks(PrintWriter pw) {
        String baseDir = project.getBasedir().getAbsolutePath() + '/';
        
        List<Plugin> plugins = (List<Plugin>)project.getBuildPlugins();
        for (Plugin plugin: plugins) {
            
            // Generate Ant task equivalent to the Tuscany SDO plugin
            if ("org.apache.tuscany.sdo".equals(plugin.getGroupId()) && "tuscany-sdo-plugin".equals(plugin.getArtifactId())) {
                for (PluginExecution execution: (List<PluginExecution>)plugin.getExecutions()) {
                    for (Map<String, String> configuration: pluginConfigurations(execution)) {
                        
                        pw.println("        <java classname=\"org.apache.tuscany.sdo.generate.XSD2JavaGenerator\" fork=\"true\">");
                        
                        // Generate the various code generation options
                        for (Map.Entry<String, String> element: configuration.entrySet()) {
                            String key = element.getKey();
                            String value = element.getValue();
                            if (key.equals("schemaNamespace")) {
                                pw.println("            <arg value=\"-schemaNamespace\"/>");
                                pw.println("            <arg value=\"" + value + "\"/>");
                            }
                            if (key.equals("javaPackage")) {
                                pw.println("            <arg value=\"-javaPackage\"/>");
                                pw.println("            <arg value=\"" + value + "\"/>");
                            }
                            if (key.equals("prefix")) {
                                pw.println("            <arg value=\"-prefix\"/>");
                                pw.println("            <arg value=\"" + value + "\"/>");
                            }
                            if (key.equals("noInterfaces") && "true".equals(value)) {
                                pw.println("            <arg value=\"-noInterfaces\"/>");
                            }
                            if (key.equals("noNotification") && "true".equals(value)) {
                                pw.println("            <arg value=\"-noNotification\"/>");
                            }
                            if (key.equals("noContainer") && "true".equals(value)) {
                                pw.println("            <arg value=\"-noContainment\"/>");
                            }
                            if (key.equals("noUnsettable") && "true".equals(value)) {
                                pw.println("            <arg value=\"-noUnsettable\"/>");
                            }
                        }
                        
                        // Generate target directory parameter
                        String targetDirectory = configuration.get("targetDirectory");
                        if (targetDirectory == null) {
                            targetDirectory = "target/sdo-source";
                        } else if (targetDirectory.startsWith(baseDir)) {
                            targetDirectory = targetDirectory.substring(baseDir.length());
                        }
                        pw.println("            <arg value=\"-targetDirectory\"/>");
                        pw.println("            <arg value=\"" + targetDirectory + "\"/>");
                        
                        // Generate schema file parameter
                        String schemaFile = configuration.get("schemaFile");
                        if (schemaFile == null) {
                            schemaFile = configuration.get("fileName");
                        }
                        if (schemaFile != null) {
                            if (schemaFile.startsWith(baseDir)) {
                                schemaFile = schemaFile.substring(baseDir.length());
                            }
                            pw.println("            <arg value=\"" + schemaFile + "\"/>");
                        }

                        pw.println("            <classpath>");
                        pw.println("                <fileset refid=\"tuscany.jars\"/>");
                        pw.println("                <fileset refid=\"3rdparty.jars\"/>");
                        pw.println("            </classpath>");
                        pw.println("        </java>");
                    }
                }
            }
            
            // Generate Ant task equivalent to the Tuscany WSDL2Java plugin
            else if ("org.apache.tuscany.sca".equals(plugin.getGroupId()) && "tuscany-maven-wsdl2java".equals(plugin.getArtifactId())) {
                for (PluginExecution execution: (List<PluginExecution>)plugin.getExecutions()) {
                    for (Map<String, String> configuration: pluginConfigurations(execution)) {

                        pw.println("        <java classname=\"org.apache.tuscany.tools.wsdl2java.generate.WSDL2JavaGenerator\" fork=\"true\">");
                        
                        // Generate the various code generation options
                        for (Map.Entry<String, String> element: configuration.entrySet()) {
                            String key = element.getKey();
                            String value = element.getValue();
                            if (key.equals("javaPackage")) {
                                pw.println("            <arg value=\"-javaPackage\"/>");
                                pw.println("            <arg value=\"" + value + "\"/>");
                            }
                        }
                        
                        // Generate target directory parameter
                        String targetDirectory = configuration.get("targetDirectory");
                        if (targetDirectory == null) {
                            targetDirectory = "target/wsdl2java-source";
                        } else if (targetDirectory.startsWith(baseDir)) {
                            targetDirectory = targetDirectory.substring(baseDir.length());
                        }
                        pw.println("            <arg value=\"-targetDirectory\"/>");
                        pw.println("            <arg value=\"" + targetDirectory + "\"/>");
                        
                        // Generate WSDL file parameter
                        String wsdlFile = configuration.get("wsdlFile");
                        if (wsdlFile == null) {
                            wsdlFile = configuration.get("fileName");
                        }
                        if (wsdlFile != null) {
                            if (wsdlFile.startsWith(baseDir)) {
                                wsdlFile = wsdlFile.substring(baseDir.length());
                            }
                            pw.println("            <arg value=\"" + wsdlFile + "\"/>");
                        }

                        pw.println("            <classpath>");
                        pw.println("                <fileset refid=\"tuscany.jars\"/>");
                        pw.println("                <fileset refid=\"3rdparty.jars\"/>");
                        pw.println("            </classpath>");
                        pw.println("        </java>");
                    }
                }
            }
        }
    }

    /**
     * Generate license header.
     * 
     * @param pw PrintWriter to write to
     */
    private void generateLicenseHeader(PrintWriter pw) {
        pw.println("<!--");
        pw.println(" * Licensed to the Apache Software Foundation (ASF) under one");
        pw.println(" * or more contributor license agreements.  See the NOTICE file");
        pw.println(" * distributed with this work for additional information");
        pw.println(" * regarding copyright ownership.  The ASF licenses this file");
        pw.println(" * to you under the Apache License, Version 2.0 (the");
        pw.println(" * \"License\"); you may not use this file except in compliance");
        pw.println(" * with the License.  You may obtain a copy of the License at");
        pw.println(" * ");
        pw.println(" *   http://www.apache.org/licenses/LICENSE-2.0");
        pw.println(" * ");
        pw.println(" * Unless required by applicable law or agreed to in writing,");
        pw.println(" * software distributed under the License is distributed on an");
        pw.println(" * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY");
        pw.println(" * KIND, either express or implied.  See the License for the");
        pw.println(" * specific language governing permissions and limitations");
        pw.println(" * under the License.");
        pw.println("-->");
        pw.println();
    }

}