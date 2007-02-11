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
package org.apache.tuscany.sca.plugin.itest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.surefire.junit.JUnitDirectoryTestSuite;
import org.apache.maven.surefire.report.BriefFileReporter;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.factory.ArtifactFactory;

import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * @version $Rev$ $Date$
 * @goal test
 * @phase integration-test
 */
public class TuscanyITestMojo extends AbstractMojo {
    /**
     * @parameter expression="${project.build.directory}/surefire-reports"
     */
    public File reportsDirectory;

    /**
     * Whether to trim the stack trace in the reports to just the lines within
     * the test, or show the full trace.
     * 
     * @parameter expression="${trimStackTrace}" default-value="true"
     */
    public boolean trimStackTrace;

    /**
     * The directory containing generated test classes of the project being
     * tested.
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    public File testClassesDirectory;

    /**
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    public List testClassPath;

    /**
     * @parameter
     */
    public List includes = new ArrayList();

    /**
     * @parameter
     */
    public List excludes = new ArrayList();

    /**
     * @parameter expression="${project.build.testOutputDirectory}/itest.scdl"
     */
    public File testScdl;

    /**
     * @parameter
     */
    public URL systemScdl;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    public ArtifactResolver resolver;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.metadata.ArtifactMetadataSource}"
     * @required
     * @readonly
     */
    public ArtifactMetadataSource metadataSource;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    public ArtifactRepository localRepository;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    public List remoteRepositories;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    public ArtifactFactory artifactFactory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        log.info("Starting Tuscany...");
        MavenEmbeddedRuntime runtime = createRuntime();
        runtime.setMonitorFactory(new MavenMonitorFactory(log));
        try {
            runtime.initialize();
        } catch (InitializationException e) {
            throw new MojoExecutionException("Error initializing Tuscany runtime", e);
        }
        try {
            log.debug("Deploying test SCDL from " + testScdl);
            try {
                // fixme this should probably be an isolated classloader
                ClassLoader testClassLoader = createTestClassLoader(getClass().getClassLoader());
                runtime.deployTestScdl(testScdl, testClassLoader);
            } catch (Exception e) {
                throw new MojoExecutionException("Error deploying test component " + testScdl, e);
            }
/*
            log.info("Executing tests...");

            boolean success = runSurefire();
            if (!success) {
                String msg = "There were test failures";
                throw new MojoFailureException(msg);
            }
*/
        } finally {
            log.info("Stopping Tuscany...");
            try {
                runtime.destroy();
            } catch (TuscanyRuntimeException e) {
                log.error("Error stopping Tuscany runtime", e);
            }
        }
    }

    public boolean runSurefire() throws MojoExecutionException {
        // FIXME get classloader for tests
        ClassLoader testsClassLoader = null;
        try {
            Properties status = new Properties();
            boolean success = run(testsClassLoader, status);
            getLog().info("Test results: "+status);
            return success;
        } catch (ReporterException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (TestSetFailedException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean run(ClassLoader testsClassLoader, Properties status) throws ReporterException,
        TestSetFailedException {
        List reports = new ArrayList();
        reports.add(new BriefFileReporter(reportsDirectory, trimStackTrace));
        ReporterManager reporterManager = new ReporterManager(reports);
        reporterManager.initResultsFromProperties(status);

        List suites = new ArrayList();

        int totalTests = 0;
        SurefireTestSuite suite =
            new JUnitDirectoryTestSuite(testClassesDirectory, (ArrayList)includes, (ArrayList)excludes);
        suite.locateTestSets(testsClassLoader);

        int testCount = suite.getNumTests();
        if (testCount > 0) {
            suites.add(suite);
            totalTests += testCount;
        }
        reporterManager.runStarting(totalTests);

        if (totalTests == 0) {
            reporterManager.writeMessage("There are no tests to run.");
        } else {
            suite.execute(reporterManager, testsClassLoader);
        }

        reporterManager.runCompleted();
        reporterManager.updateResultsProperties(status);
        return reporterManager.getNumErrors() == 0 && reporterManager.getNumFailures() == 0;
    }

    protected MavenEmbeddedRuntime createRuntime() throws MojoExecutionException {
        ClassLoader hostClassLoader = getClass().getClassLoader();
        if (systemScdl == null) {
            systemScdl = hostClassLoader.getResource("META-INF/tuscany/embeddedMaven.scdl");
        }

        MavenRuntimeInfo runtimeInfo = new MavenRuntimeInfo();
        MavenEmbeddedArtifactRepository artifactRepository = new MavenEmbeddedArtifactRepository(artifactFactory,
                                                                                                 resolver,
                                                                                                 metadataSource,
                                                                                                 localRepository,
                                                                                                 remoteRepositories);
        MavenEmbeddedRuntime runtime = new MavenEmbeddedRuntime();
        runtime.setRuntimeInfo(runtimeInfo);
        runtime.setSystemScdl(systemScdl);
        runtime.setHostClassLoader(hostClassLoader);
        runtime.setArtifactRepository(artifactRepository);
        return runtime;
    }

    public ClassLoader createTestClassLoader(ClassLoader parent) {
        URL[] urls = new URL[testClassPath.size()];
        int idx = 0;
        for (Iterator i = testClassPath.iterator(); i.hasNext();) {
            File pathElement = new File((String) i.next());
            try {
                URL url = pathElement.toURI().toURL();
                getLog().debug("Adding application URL: " + url);
                urls[idx++] = url;
            } catch (MalformedURLException e) {
                // toURI should have encoded the URL
                throw new AssertionError();
            }

        }
        return new URLClassLoader(urls, parent);
    }
}
