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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.surefire.report.BriefFileReporter;
import org.apache.maven.surefire.report.Reporter;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.apache.maven.surefire.testset.TestSetFailedException;

import org.apache.tuscany.api.TuscanyRuntimeException;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.sca.plugin.itest.implementation.junit.ImplementationJUnit;

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
            SurefireTestSuite testSuite;
            log.info("Deploying test SCDL from " + testScdl);
            try {
                // fixme this should probably be an isolated classloader
                ClassLoader testClassLoader = createTestClassLoader(getClass().getClassLoader());
                URI name = URI.create("itest://localhost/testDomain/");
                CompositeImplementation impl = new CompositeImplementation();
                impl.setScdlLocation(testScdl.toURI().toURL());
                impl.setClassLoader(testClassLoader);

                ComponentDefinition<CompositeImplementation> definition =
                    new ComponentDefinition<CompositeImplementation>(name, impl);
                Component testComponent = runtime.deployTestScdl(definition);
                testSuite = createTestSuite(runtime, definition, testComponent);
                testComponent.start();
            } catch (Exception e) {
                throw new MojoExecutionException("Error deploying test component " + testScdl, e);
            }
            log.info("Executing tests...");

            boolean success = runSurefire(testSuite);
            if (!success) {
                String msg = "There were test failures";
                throw new MojoFailureException(msg);
            }
        } finally {
            log.info("Stopping Tuscany...");
            try {
                runtime.destroy();
            } catch (TuscanyRuntimeException e) {
                log.error("Error stopping Tuscany runtime", e);
            }
        }
    }

    public boolean runSurefire(SurefireTestSuite testSuite) throws MojoExecutionException {
        try {
            Properties status = new Properties();
            boolean success = run(testSuite, status);
            getLog().info("Test results: "+status);
            return success;
        } catch (ReporterException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (TestSetFailedException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean run(SurefireTestSuite suite, Properties status) throws ReporterException, TestSetFailedException {
        int totalTests = suite.getNumTests();

        Reporter reporter = new BriefFileReporter(reportsDirectory, trimStackTrace);
        List<Reporter> reports = Collections.singletonList(reporter);
        ReporterManager reporterManager = new ReporterManager(reports);
        reporterManager.initResultsFromProperties(status);

        reporterManager.runStarting(totalTests);

        if (totalTests == 0) {
            reporterManager.writeMessage("There are no tests to run.");
        } else {
            suite.execute(reporterManager, null);
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

    protected SurefireTestSuite createTestSuite(MavenEmbeddedRuntime runtime,
                                                ComponentDefinition<CompositeImplementation> definition,
                                                Component testComponent) throws MojoExecutionException {
        SCATestSuite suite = new SCATestSuite();
        URI uriBase = testComponent.getUri();

        CompositeImplementation impl = definition.getImplementation();
        CompositeComponentType<?,?,?> componentType = impl.getComponentType();
        Map<String, ComponentDefinition<? extends Implementation<?>>> components = componentType.getComponents();
        for (Map.Entry<String, ComponentDefinition<? extends Implementation<?>>> entry : components.entrySet()) {
            String name = entry.getKey();
            ComponentDefinition<? extends Implementation<?>> junitDefinition = entry.getValue();
            Implementation<?> implementation = junitDefinition.getImplementation();
            if (ImplementationJUnit.class.isAssignableFrom(implementation.getClass())) {
                URI testSetName = uriBase.resolve(name);
                SCATestSet testSet = createTestSet(runtime, testSetName, junitDefinition);
                suite.add(testSet);
            }
        }
        return suite;
    }

    protected SCATestSet createTestSet(MavenEmbeddedRuntime runtime,
                                       URI name,
                                       ComponentDefinition definition) throws MojoExecutionException {
        ImplementationJUnit impl = (ImplementationJUnit) definition.getImplementation();
        PojoComponentType componentType = impl.getComponentType();
        Map services = componentType.getServices();
        JavaMappedService testService = (JavaMappedService) services.get("testService");
        if (testService == null) {
            throw new MojoExecutionException("No testServic defined on component: " + definition.getUri());
        }
        Map<String, ? extends Operation<?>> operations = testService.getServiceContract().getOperations();
        return new SCATestSet(runtime, name, operations.values());
    }
}
