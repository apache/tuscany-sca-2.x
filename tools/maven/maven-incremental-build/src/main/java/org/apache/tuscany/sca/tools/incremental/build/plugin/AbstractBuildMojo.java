package org.apache.tuscany.sca.tools.incremental.build.plugin;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerOutputStyle;
import org.codehaus.plexus.compiler.manager.CompilerManager;
import org.codehaus.plexus.compiler.manager.NoSuchCompilerException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SingleTargetSourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @version $Id: StaleSourceScannerTest.java 2393 2005-08-08 22:32:59Z kenney $
 */
public abstract class AbstractBuildMojo extends AbstractMojo {
    // ----------------------------------------------------------------------
    // Configurables
    // ----------------------------------------------------------------------

    /**
     * The -source argument for the Java compiler.
     *
     * @parameter expression="${maven.compiler.source}"
     * @readonly
     */
    private String source;

    /**
     * The -target argument for the Java compiler.
     *
     * @parameter expression="${maven.compiler.target}"
     * @readonly
     */
    private String target;

    /**
     * The -encoding argument for the Java compiler.
     *
     * @parameter expression="${maven.compiler.encoding}"
     * @readonly
     */
    private String encoding;

    /**
     * Sets the granularity in milliseconds of the last modification
     * date for testing whether a source needs recompilation.
     *
     * @parameter expression="${lastModGranularityMs}" default-value="0"
     * 
     */
    private int staleMillis;

    /**
     * The compiler id of the compiler to use. See this
     * <a href="non-javac-compilers.html">guide</a> for more information.
     *
     * @parameter expression="${maven.compiler.compilerId}" default-value="javac"
     * @readonly
     */
    private String compilerId;

    /**
     * Version of the compiler to use, ex. "1.3", "1.5", if fork is set to true.
     *
     * @parameter expression="${maven.compiler.compilerVersion}"
     * @readonly
     */
    private String compilerVersion;

    /**
     * Sets the executable of the compiler to use when fork is true.
     *
     * @parameter expression="${maven.compiler.executable}"
     * @readonly
     */
    private String executable;

    /**
     * The directory to run the compiler from if fork is true.
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    protected File basedir;

    /**
     * The target directory of the compiler if fork is true.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File buildDirectory;

    /**
     * Plexus compiler manager.
     *
     * @component
     * @readonly
     */
    private CompilerManager compilerManager;

    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    protected SourceInclusionScanner getSourceInclusionScanner(int staleMillis) {
        return new StaleSourceScanner(staleMillis);
    }

    protected SourceInclusionScanner getSourceInclusionScanner(String inputFileEnding) {
        Set includes = Collections.singleton("**/*." + inputFileEnding);
        return new SimpleSourceInclusionScanner(includes, Collections.EMPTY_SET);
    }

    protected boolean isPOMChanged() {
        File pom = project.getFile();
        File out = getOutputFile();
        return pom.lastModified() > out.lastModified();
    }

    protected File getOutputFile() {
        File basedir = buildDirectory;
        String finalName = project.getBuild().getFinalName();
        String classifier = project.getArtifact().getClassifier();
        if (classifier == null) {
            classifier = "";
        } else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }

        return new File(basedir, finalName + classifier + "." + project.getArtifact().getType());
    }

    /**
     * Test if the resources have been changed
     * @return
     * @throws MojoExecutionException
     */
    protected boolean isResourceChanged() throws MojoExecutionException {
        return isChanged(project.getResources(), project.getBuild().getOutputDirectory());
    }

    protected boolean isTestResourceChanged() throws MojoExecutionException {
        return isChanged(project.getTestResources(), project.getBuild().getTestOutputDirectory());
    }

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String[] DEFAULT_INCLUDES = {"**/**"};

    /**
     * Test if any of the resources have been changed
     * @param resources
     * @param outputDirectory
     * @return
     * @throws MojoExecutionException
     */
    protected boolean isChanged(List resources, String outputDirectory) throws MojoExecutionException {

        for (Iterator i = resources.iterator(); i.hasNext();) {
            Resource resource = (Resource)i.next();

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File(resource.getDirectory());

            if (!resourceDirectory.exists()) {
                continue;
            }

            // this part is required in case the user specified "../something" as destination
            // see MNG-1345
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                return true;
            }

            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir(resource.getDirectory());
            if (resource.getIncludes() != null && !resource.getIncludes().isEmpty()) {
                scanner.setIncludes((String[])resource.getIncludes().toArray(EMPTY_STRING_ARRAY));
            } else {
                scanner.setIncludes(DEFAULT_INCLUDES);
            }
            if (resource.getExcludes() != null && !resource.getExcludes().isEmpty()) {
                scanner.setExcludes((String[])resource.getExcludes().toArray(EMPTY_STRING_ARRAY));
            }

            scanner.addDefaultExcludes();
            scanner.scan();

            List includedFiles = Arrays.asList(scanner.getIncludedFiles());
            for (Iterator j = includedFiles.iterator(); j.hasNext();) {
                String name = (String)j.next();

                String destination = name;

                if (targetPath != null) {
                    destination = targetPath + "/" + name;
                }

                File source = new File(resource.getDirectory(), name);

                File destinationFile = new File(outputDirectory, destination);

                if (!destinationFile.exists()) {
                    return true;
                } else {
                    if (source.lastModified() > destinationFile.lastModified()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean isSourceChanged(Compiler compiler) throws MojoExecutionException {
        try {
            List sourceRoots = project.getCompileSourceRoots();
            File outputDir = new File(project.getBuild().getOutputDirectory());
            List classPathEntries = project.getCompileClasspathElements();
            return isChanged(compiler, sourceRoots, classPathEntries, outputDir);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected boolean isTestSourceChanged(Compiler compiler) throws MojoExecutionException {
        try {
            List sourceRoots = project.getTestCompileSourceRoots();
            File outputDir = new File(project.getBuild().getTestOutputDirectory());
            List classPathEntries = project.getTestClasspathElements();
            return isChanged(compiler, sourceRoots, classPathEntries, outputDir);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean isChanged(Compiler compiler, List sourceRoots, List classPathEntries, File outputDir)
        throws MojoExecutionException {
        List compileSourceRoots = removeEmptyCompileSourceRoots(sourceRoots);

        if (compileSourceRoots.isEmpty()) {
            getLog().info("No sources to compile");
            return false;
        }

        // ----------------------------------------------------------------------
        // Create the compiler configuration
        // ----------------------------------------------------------------------

        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

        compilerConfiguration.setOutputLocation(outputDir.getAbsolutePath());

        compilerConfiguration.setClasspathEntries(classPathEntries);

        compilerConfiguration.setSourceLocations(compileSourceRoots);

        compilerConfiguration.setOptimize(false);

        compilerConfiguration.setDebug(true);

        compilerConfiguration.setVerbose(false);

        compilerConfiguration.setShowWarnings(false);

        compilerConfiguration.setShowDeprecation(true);

        compilerConfiguration.setSourceVersion(source);

        compilerConfiguration.setTargetVersion(target);

        compilerConfiguration.setSourceEncoding(encoding);

        compilerConfiguration.setExecutable(executable);

        compilerConfiguration.setWorkingDirectory(basedir);

        compilerConfiguration.setCompilerVersion(compilerVersion);

        compilerConfiguration.setBuildDirectory(buildDirectory);

        compilerConfiguration.setOutputFileName(project.getBuild().getFinalName());

        // TODO: have an option to always compile (without need to clean)
        Set staleSources;

        boolean canUpdateTarget;

        try {
            staleSources =
                computeStaleSources(compilerConfiguration,
                                    compiler,
                                    getSourceInclusionScanner(staleMillis),
                                    sourceRoots,
                                    outputDir);

            canUpdateTarget = compiler.canUpdateTarget(compilerConfiguration);

            if (compiler.getCompilerOutputStyle().equals(CompilerOutputStyle.ONE_OUTPUT_FILE_FOR_ALL_INPUT_FILES) && !canUpdateTarget) {
                getLog().info("RESCANNING!");
                // TODO: This second scan for source files is sub-optimal
                String inputFileEnding = compiler.getInputFileEnding(compilerConfiguration);

                Set sources =
                    computeStaleSources(compilerConfiguration,
                                        compiler,
                                        getSourceInclusionScanner(inputFileEnding),
                                        sourceRoots,
                                        outputDir);

                compilerConfiguration.setSourceFiles(sources);
            } else {
                compilerConfiguration.setSourceFiles(staleSources);
            }
        } catch (CompilerException e) {
            throw new MojoExecutionException("Error while computing stale sources.", e);
        }

        return !staleSources.isEmpty();

    }

    protected Compiler getCompiler() throws MojoExecutionException {
        Compiler compiler;

        getLog().debug("Using compiler '" + compilerId + "'.");

        try {
            compiler = compilerManager.getCompiler(compilerId);
        } catch (NoSuchCompilerException e) {
            throw new MojoExecutionException("No such compiler '" + e.getCompilerId() + "'.");
        }
        return compiler;
    }

    protected Set computeStaleSources(CompilerConfiguration compilerConfiguration,
                                      Compiler compiler,
                                      SourceInclusionScanner scanner,
                                      List sourceRoots,
                                      File outputDir) throws MojoExecutionException, CompilerException {
        CompilerOutputStyle outputStyle = compiler.getCompilerOutputStyle();

        SourceMapping mapping;

        File outputDirectory;

        if (outputStyle == CompilerOutputStyle.ONE_OUTPUT_FILE_PER_INPUT_FILE) {
            mapping =
                new SuffixMapping(compiler.getInputFileEnding(compilerConfiguration), compiler
                    .getOutputFileEnding(compilerConfiguration));

            outputDirectory = outputDir;
        } else if (outputStyle == CompilerOutputStyle.ONE_OUTPUT_FILE_FOR_ALL_INPUT_FILES) {
            mapping =
                new SingleTargetSourceMapping(compiler.getInputFileEnding(compilerConfiguration), compiler
                    .getOutputFile(compilerConfiguration));

            outputDirectory = buildDirectory;
        } else {
            throw new MojoExecutionException("Unknown compiler output style: '" + outputStyle + "'.");
        }

        scanner.addSourceMapping(mapping);

        Set staleSources = new HashSet();

        for (Iterator it = sourceRoots.iterator(); it.hasNext();) {
            String sourceRoot = (String)it.next();

            File rootFile = new File(sourceRoot);

            if (!rootFile.isDirectory()) {
                continue;
            }

            try {
                Set changed = scanner.getIncludedSources(rootFile, outputDirectory);
                staleSources.addAll(changed);
            } catch (InclusionScanException e) {
                throw new MojoExecutionException("Error scanning source root: \'" + sourceRoot
                    + "\' "
                    + "for stale files to recompile.", e);
            }
        }

        return staleSources;
    }

    /**
     * @todo also in ant plugin. This should be resolved at some point so that it does not need to
     * be calculated continuously - or should the plugins accept empty source roots as is?
     */
    private static List removeEmptyCompileSourceRoots(List compileSourceRootsList) {
        List newCompileSourceRootsList = new ArrayList();
        if (compileSourceRootsList != null) {
            // copy as I may be modifying it
            for (Iterator i = compileSourceRootsList.iterator(); i.hasNext();) {
                String srcDir = (String)i.next();
                if (!newCompileSourceRootsList.contains(srcDir) && new File(srcDir).exists()) {
                    newCompileSourceRootsList.add(srcDir);
                }
            }
        }
        return newCompileSourceRootsList;
    }
}
