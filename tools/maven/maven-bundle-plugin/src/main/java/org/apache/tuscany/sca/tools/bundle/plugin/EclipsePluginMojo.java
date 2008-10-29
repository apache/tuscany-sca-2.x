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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * A maven plugin that generates Generate .classpath and .project
 * 
 * @version $Rev$ $Date$
 * @goal generate-pde
 * @phase generate-resources
 * @requiresDependencyResolution test
 * @description Generate .classpath and .project
 */
public class EclipsePluginMojo extends AbstractMojo {

    public static class EclipseSourceDir implements Comparable {
        private String exclude;

        private boolean filtering;

        private String include;

        private boolean isResource;

        private String output;

        private String path;

        private boolean test;

        public EclipseSourceDir(String path,
                                String output,
                                boolean isResource,
                                boolean test,
                                String include,
                                String exclude,
                                boolean filtering) {
            this.path = path;
            this.output = output;
            this.isResource = isResource;
            this.test = test;
            this.include = include;
            this.exclude = exclude;
            this.filtering = filtering;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object obj) {
            return this.path.compareTo(((EclipseSourceDir)obj).path);
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            return (obj != null) && (obj instanceof EclipseSourceDir) && this.path.equals(((EclipseSourceDir)obj).path);
        }

        /**
         * Getter for <code>exclude</code>.
         * 
         * @return Returns the exclude.
         */
        public String getExclude() {
            return this.exclude;
        }

        /**
         * Getter for <code>include</code>.
         * 
         * @return Returns the include.
         */
        public String getInclude() {
            return this.include;
        }

        /**
         * Getter for <code>output</code>.
         * 
         * @return Returns the output.
         */
        public String getOutput() {
            return this.output;
        }

        /**
         * Getter for <code>path</code>.
         * 
         * @return Returns the path.
         */
        public String getPath() {
            return this.path;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return this.path.hashCode();
        }

        /**
         * Wheter this resource should be copied with filtering.
         */
        public boolean isFiltering() {
            return filtering;
        }

        /**
         * Getter for <code>isResource</code>.
         * 
         * @return Returns the isResource.
         */
        public boolean isResource() {
            return this.isResource;
        }

        /**
         * Getter for <code>test</code>.
         * 
         * @return Returns the test.
         */
        public boolean isTest() {
            return this.test;
        }

        /**
         * Setter for <code>exclude</code>.
         * 
         * @param exclude The exclude to set.
         */
        public void setExclude(String exclude) {
            this.exclude = exclude;
        }

        /**
         * Setter for <code>include</code>.
         * 
         * @param include The include to set.
         */
        public void setInclude(String include) {
            this.include = include;
        }

        /**
         * Setter for <code>output</code>.
         * 
         * @param output The output to set.
         */
        public void setOutput(String output) {
            this.output = output;
        }

        /**
         * Setter for <code>path</code>.
         * 
         * @param path The path to set.
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * Setter for <code>test</code>.
         * 
         * @param test The test to set.
         */
        public void setTest(boolean test) {
            this.test = test;
        }
    }

    /**
     * Attribute name for source file excludes in a path.
     */
    private static final String ATTR_EXCLUDING = "excluding";

    /**
     * Attribute name for source file includes in a path.
     */
    private static final String ATTR_INCLUDING = "including";

    /**
     * Attribute for kind - Container (con), Variable (var)..etc.
     */
    private static final String ATTR_KIND = "kind";

    /**
     * Attribute for output.
     */
    private static final String ATTR_OUTPUT = "output";

    /**
     * Attribute for path.
     */
    private static final String ATTR_PATH = "path";

    /**
     * Attribute value for kind: src
     */
    private static final String ATTR_SRC = "src";

    /**
     * Element for classpathentry.
     */
    private static final String ELT_CLASSPATHENTRY = "classpathentry";
    private static final String ELT_CLASSPATH = "classpath";

    private static String getCanonicalPath(File file) throws MojoExecutionException {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private static String toRelativeAndFixSeparator(File basedir, File fileToAdd, boolean replaceSlashesWithDashes)
        throws MojoExecutionException {
        if (!fileToAdd.isAbsolute()) {
            fileToAdd = new File(basedir, fileToAdd.getPath());
        }

        String basedirpath;
        String absolutePath;

        basedirpath = getCanonicalPath(basedir);
        absolutePath = getCanonicalPath(fileToAdd);

        String relative;

        if (absolutePath.equals(basedirpath)) {
            relative = ".";
        } else if (absolutePath.startsWith(basedirpath)) {
            relative = absolutePath.substring(basedirpath.length() + 1);
        } else {
            relative = absolutePath;
        }

        relative = StringUtils.replace(relative, '\\', '/');

        if (replaceSlashesWithDashes) {
            relative = StringUtils.replace(relative, '/', '-');
            relative = StringUtils.replace(relative, ':', '-'); // remove ":" for absolute paths in windows
        }

        return relative;
    }

    /**
     * The project to create a distribution for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private EclipseSourceDir[] buildDirectoryList() throws MojoExecutionException {
        File buildOutputDirectory = new File(project.getBuild().getOutputDirectory());
        File basedir = project.getBasedir();
        File projectBaseDir = project.getFile().getParentFile();

        // avoid duplicated entries
        Set<EclipseSourceDir> directories = new TreeSet<EclipseSourceDir>();

        extractSourceDirs(directories, project.getCompileSourceRoots(), basedir, projectBaseDir, false, null);

        String relativeOutput = toRelativeAndFixSeparator(projectBaseDir, buildOutputDirectory, false);

        extractResourceDirs(directories,
                            project.getBuild().getResources(),
                            project,
                            basedir,
                            projectBaseDir,
                            false,
                            relativeOutput);

        // If using the standard output location, don't mix the test output into it.
        String testOutput = null;
        boolean useStandardOutputDir = buildOutputDirectory.equals(new File(project.getBuild().getOutputDirectory()));
        if (useStandardOutputDir) {
            getLog().debug("testOutput toRelativeAndFixSeparator " + projectBaseDir
                + " , "
                + project.getBuild().getTestOutputDirectory());
            testOutput =
                toRelativeAndFixSeparator(projectBaseDir, new File(project.getBuild().getTestOutputDirectory()), false);
            getLog().debug("testOutput after toRelative : " + testOutput);
        }

        extractSourceDirs(directories, project.getTestCompileSourceRoots(), basedir, projectBaseDir, true, testOutput);

        extractResourceDirs(directories,
                            project.getBuild().getTestResources(),
                            project,
                            basedir,
                            projectBaseDir,
                            true,
                            testOutput);

        return (EclipseSourceDir[])directories.toArray(new EclipseSourceDir[directories.size()]);
    }

    public void execute() throws MojoExecutionException {

        try {
            if ("pom".equals(project.getPackaging())) {
                return;
            }

            EclipseSourceDir[] dirs = buildDirectoryList();
            File classPathFile = new File(project.getBasedir(), ".classpath");
            writeClassPath(new PrintWriter(classPathFile, "UTF-8"), dirs);

            File projectFile = new File(project.getBasedir(), ".project");
            writeProject(new PrintWriter(projectFile, "UTF-8"));

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

    void extractResourceDirs(Set<EclipseSourceDir> directories,
                             List<Resource> resources,
                             MavenProject project,
                             File basedir,
                             File workspaceProjectBaseDir,
                             boolean test,
                             final String output) throws MojoExecutionException {
        for (Iterator<Resource> it = resources.iterator(); it.hasNext();) {
            Resource resource = it.next();

            getLog().debug("Processing resource dir: " + resource.getDirectory());

            String includePattern = null;
            String excludePattern = null;

            if (resource.getIncludes().size() != 0) {
                includePattern = StringUtils.join(resource.getIncludes().iterator(), "|");
            }

            if (resource.getExcludes().size() != 0) {
                excludePattern = StringUtils.join(resource.getExcludes().iterator(), "|");
            }

            // TODO: figure out how to merge if the same dir is specified twice
            // with different in/exclude patterns.

            File resourceDirectory = new File( /* basedir, */resource.getDirectory());

            if (!resourceDirectory.exists() || !resourceDirectory.isDirectory()) {
                getLog().debug("Resource dir: " + resourceDirectory + " either missing or not a directory.");
                continue;
            }

            String resourceDir =
                toRelativeAndFixSeparator(workspaceProjectBaseDir, resourceDirectory, !workspaceProjectBaseDir
                    .equals(basedir));
            String thisOutput = output;
            if (thisOutput != null) {
                // sometimes thisOutput is already an absolute path
                File outputFile = new File(thisOutput);
                if (!outputFile.isAbsolute()) {
                    outputFile = new File(workspaceProjectBaseDir, thisOutput);
                }
                // create output dir if it doesn't exist
                outputFile.mkdirs();

                if (!StringUtils.isEmpty(resource.getTargetPath())) {
                    outputFile = new File(outputFile, resource.getTargetPath());
                    // create output dir if it doesn't exist
                    outputFile.mkdirs();
                }

                getLog().debug("Making relative and fixing separator: { " + workspaceProjectBaseDir
                    + ", "
                    + outputFile
                    + ", false }.");
                thisOutput = toRelativeAndFixSeparator(workspaceProjectBaseDir, outputFile, false);
            }

            getLog().debug("Adding eclipse source dir: { " + resourceDir
                + ", "
                + thisOutput
                + ", true, "
                + test
                + ", "
                + includePattern
                + ", "
                + excludePattern
                + " }.");

            directories.add(new EclipseSourceDir(resourceDir, thisOutput, true, test, includePattern, excludePattern,
                                                 resource.isFiltering()));
        }
    }

    private void extractSourceDirs(Set<EclipseSourceDir> directories,
                                   List<String> sourceRoots,
                                   File basedir,
                                   File projectBaseDir,
                                   boolean test,
                                   String output) throws MojoExecutionException {
        for (Iterator<String> it = sourceRoots.iterator(); it.hasNext();) {

            File sourceRootFile = new File(it.next());

            if (sourceRootFile.isDirectory()) {
                String sourceRoot =
                    toRelativeAndFixSeparator(projectBaseDir, sourceRootFile, !projectBaseDir.equals(basedir));

                directories.add(new EclipseSourceDir(sourceRoot, output, false, test, null, null, false));
            }
        }
    }

    private void writeClassPath(PrintWriter writer, EclipseSourceDir[] dirs) throws MojoExecutionException {
        String defaultOutput =
            toRelativeAndFixSeparator(project.getBasedir(), new File(project.getBuild().getOutputDirectory()), false);

        // ----------------------------------------------------------------------
        // Source roots and resources
        // ----------------------------------------------------------------------

        // List<EclipseSourceDir>
        List<EclipseSourceDir> specialSources = new ArrayList<EclipseSourceDir>();

        // Map<String,List<EclipseSourceDir>>
        Map<String,List<EclipseSourceDir>> byOutputDir = new HashMap<String,List<EclipseSourceDir>>();

        for (int j = 0; j < dirs.length; j++) {
            EclipseSourceDir dir = dirs[j];

            // List<EclipseSourceDir>
            List<EclipseSourceDir> byOutputDirs = byOutputDir.get(dir.getOutput());
            if (byOutputDirs == null) {
                // ArrayList<EclipseSourceDir>
                byOutputDir.put(dir.getOutput() == null ? defaultOutput : dir.getOutput(), byOutputDirs =
                    new ArrayList<EclipseSourceDir>());
            }
            byOutputDirs.add(dir);
        }

        writer.println("<" + ELT_CLASSPATH + ">");
        writer.println("    <classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
        writer.println("    <classpathentry kind=\"con\" path=\"org.eclipse.pde.core.requiredPlugins\"/>");
        for (int j = 0; j < dirs.length; j++) {
            EclipseSourceDir dir = dirs[j];

            getLog().debug("Processing " + (dir.isResource() ? "re" : "")
                + "source "
                + dir.getPath()
                + ": output="
                + dir.getOutput()
                + "; default output="
                + defaultOutput);

            boolean isSpecial = false;

            // handle resource with nested output folders
            if (dir.isResource()) {
                // Check if the output is a subdirectory of the default output,
                // and if the default output has any sources that copy there.

                if (dir.getOutput() != null // resource output dir is set
                    && !dir.getOutput().equals(defaultOutput) // output dir is not default target/classes
                    && dir.getOutput().startsWith(defaultOutput) // ... but is nested
                    && byOutputDir.get(defaultOutput) != null // ???
                    && !(byOutputDir.get(defaultOutput)).isEmpty() // ???
                ) {
                    // do not specify as source since the output will be nested. Instead, mark
                    // it as a todo, and handle it with a custom build.xml file later.

                    getLog().debug("Marking as special to prevent output folder nesting: " + dir.getPath()
                        + " (output="
                        + dir.getOutput()
                        + ")");

                    isSpecial = true;
                    specialSources.add(dir);
                }
            }

            writer.print("    <" + ELT_CLASSPATHENTRY);

            writer.print(" " + ATTR_KIND + "=\"src\"");
            writer.print(" " + ATTR_PATH + "=\"" + dir.getPath() + "\"");

            if (!isSpecial && dir.getOutput() != null && !defaultOutput.equals(dir.getOutput())) {
                writer.print(" " + ATTR_OUTPUT + "=\"" + dir.getOutput() + "\"");
            }

            if (StringUtils.isNotEmpty(dir.getInclude())) {
                writer.print(" " + ATTR_INCLUDING + "=\"" + dir.getInclude() + "\"");
            }

            String excludes = dir.getExclude();

            if (dir.isResource()) {
                // automatically exclude java files: eclipse doesn't have the concept of resource directory so it will
                // try to compile any java file found in maven resource dirs
                excludes = StringUtils.isEmpty(excludes) ? "**/*.java" : excludes + "|**/*.java";
            }

            if (StringUtils.isNotEmpty(excludes)) {
                writer.print(" " + ATTR_EXCLUDING + "=\"" + excludes + "\"");
            }

            writer.println("/>");
        }
        writer.println("    <classpathentry kind=\"output\" path=\"" + defaultOutput + "\"/>");
        writer.println("</" + ELT_CLASSPATH + ">");
        writer.close();
    }

    private void writeProject(PrintWriter ps) {
        ps.println("<projectDescription>");
        ps.println("    <name>" + project.getArtifactId() + "</name>");
        ps.println("    <projects/>");
        ps.println("    <buildSpec>");
        ps.println("        <buildCommand>");
        ps.println("            <name>org.eclipse.jdt.core.javabuilder</name>");
        ps.println("        </buildCommand>");
        ps.println("        <buildCommand>");
        ps.println("            <name>org.eclipse.pde.ManifestBuilder</name>");
        ps.println("        </buildCommand>");
        ps.println("        <buildCommand>");
        ps.println("            <name>org.eclipse.pde.SchemaBuilder</name>");
        ps.println("        </buildCommand>");
        ps.println("    </buildSpec>");
        ps.println("    <natures>");
        ps.println("        <nature>org.eclipse.jdt.core.javanature</nature>");
        ps.println("        <nature>org.eclipse.pde.PluginNature</nature>");
        ps.println("    </natures>");
        ps.println("    <linkedResources/>");
        ps.println("</projectDescription>");
        ps.close();
    }

}
