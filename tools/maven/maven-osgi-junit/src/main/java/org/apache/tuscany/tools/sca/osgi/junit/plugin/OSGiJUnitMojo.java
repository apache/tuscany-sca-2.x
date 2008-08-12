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
package org.apache.tuscany.tools.sca.osgi.junit.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.tuscany.sca.node.osgi.launcher.FelixOSGiHost;
import org.apache.tuscany.sca.node.osgi.launcher.LauncherBundleActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @version $Rev$ $Date$
 * @goal test
 * @phase integration-test
 * @requiresDependencyResolution test
 * @description Run the unit test with OSGi
 */
public class OSGiJUnitMojo extends AbstractMojo {
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
    protected String osgiRuntime;

    protected Artifact getArtifact(String groupId, String artifactId) throws MojoExecutionException {
        Artifact artifact;
        VersionRange vr;
        try {
            vr = VersionRange.createFromVersionSpec(project.getVersion());
        } catch (InvalidVersionSpecificationException e1) {
            vr = VersionRange.createFromVersion(project.getVersion());
        }
        artifact = factory.createDependencyArtifact(groupId, artifactId, vr, "jar", null, Artifact.SCOPE_TEST);

        try {
            resolver.resolve(artifact, remoteRepos, local);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Unable to resolve artifact.", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Unable to find artifact.", e);
        }

        return artifact;
    }

    public void execute() throws MojoExecutionException {
        if (project.getPackaging().equals("pom")) {
            return;
        }

        Log log = getLog();
        List<URL> jarFiles = new ArrayList<URL>();
        for (Object o : project.getArtifacts()) {
            Artifact a = (Artifact)o;
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Adding: " + a);
                }
                jarFiles.add(a.getFile().toURI().toURL());
            } catch (MalformedURLException e) {
                getLog().error(e);
            }
        }

        /*
         * Add org.apache.tuscany.sca:tuscany-extensibility-osgi module
         */
        String aid = "equinox".equals(osgiRuntime) ? "tuscany-extensibility-equinox" : "tuscany-extensibility-osgi";
        Artifact ext = getArtifact("org.apache.tuscany.sca", aid);
        try {
            URL url = ext.getFile().toURI().toURL();
            if (!jarFiles.contains(url)) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding: " + ext);
                }
                jarFiles.add(url);
            }
        } catch (MalformedURLException e) {
            getLog().error(e);
        }

        //        String home = new File(basedir, "target/tuscany").toString();
        //        System.setProperty("TUSCANY_HOME", home);
        //        getLog().info(home);
        try {
            FelixOSGiHost host = new FelixOSGiHost();
            host.setActivator(new LauncherBundleActivator(jarFiles));
            BundleContext context = host.start();

            for (Bundle b : context.getBundles()) {
                if (getLog().isDebugEnabled()) {
                    getLog().debug(LauncherBundleActivator.toString(b, false));
                }
            }

            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            URL[] urls =
                new URL[] {new File(project.getBuild().getOutputDirectory()).toURI().toURL(),
                           new File(project.getBuild().getTestOutputDirectory()).toURI().toURL()};

            URLClassLoader cl = new URLClassLoader(urls, tccl);
            Thread.currentThread().setContextClassLoader(cl);
            try {
                runAllTestsFromDirs(cl, project.getBuild().getTestOutputDirectory());
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
            host.stop();
        } catch (Throwable e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        //        finally {
        //            System.clearProperty("TUSCANY_HOME");
        //        }

    }

    public void getTestCases(File dir, String prefix, HashSet<String> testCaseSet) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String newPrefix = prefix == null ? file.getName() : prefix + "." + file.getName();
                getTestCases(file, newPrefix, testCaseSet);
            } else if (file.getName().endsWith("TestCase.class")) {
                String name = file.getName();
                name = name.substring(0, name.length() - 6); // remove .class
                name = (prefix == null) ? name : prefix + "." + name;

                testCaseSet.add(name);
            }
        }
    }

    public void runAllTestsFromDirs(ClassLoader testClassLoader, String testDir) throws Exception {

        int failures = 0;
        HashSet<String> testCaseSet = new HashSet<String>();
        getTestCases(new File(testDir), null, testCaseSet);
        for (String className : testCaseSet) {
            Class testClass = testClassLoader.loadClass(className);
            failures += runTestCase(testClassLoader, testClass);
        }

        Assert.assertEquals(0, failures);

    }

    /**
     * Use java reflection to call JUNIT as the JUNIT might be in the bundles
     * @param testClassLoader
     * @param testClass
     * @return
     * @throws Exception
     */
    public int runTestCase(ClassLoader testClassLoader, Class testClass) throws Exception {

        if (testClass.getName().endsWith("TestCase")) {
            getLog().info("Running: " + testClass.getName());
            Class coreClass = Class.forName("org.junit.runner.JUnitCore", true, testClassLoader);
            Object core = coreClass.newInstance();
            Class reqClass = Class.forName("org.junit.runner.Request", true, testClassLoader);
            Method aClass = reqClass.getMethod("aClass", Class.class);
            Object req = aClass.invoke(null, testClass);
            Method run = coreClass.getMethod("run", reqClass);
            Object result = run.invoke(core, req);
            Object runs = result.getClass().getMethod("getRunCount").invoke(result);
            Object ignores = result.getClass().getMethod("getIgnoreCount").invoke(result);
            List failureList = (List)result.getClass().getMethod("getFailures").invoke(result);

            int failures = 0, errors = 0;
            Class errorClass = Class.forName("junit.framework.AssertionFailedError", true, testClassLoader);
            for (Object f : failureList) {
                Object ex = f.getClass().getMethod("getException").invoke(f);
                if (errorClass.isInstance(ex)) {
                    failures++;
                } else {
                    errors++;
                }
                getLog().error((Throwable)ex);
            }

            getLog().info("Test Runs: " + runs
                + ", Failures: "
                + failures
                + ", Errors: "
                + errors
                + ", Ignores: "
                + ignores);

            return failureList.size();

        }
        return 0;

    }

}
