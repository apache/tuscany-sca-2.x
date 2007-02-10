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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.surefire.junit.JUnitDirectoryTestSuite;
import org.apache.maven.surefire.report.BriefFileReporter;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.apache.maven.surefire.testset.TestSetFailedException;

/**
 * @version $Rev$ $Date$
 * @goal test
 * @phase integration-test
 */
public class TuscanyITestMojo extends AbstractMojo {
    /**
     * @parameter expression="${project.build.directory}/surefire-reports"
     */
    private File reportsDirectory;

    /**
     * Whether to trim the stack trace in the reports to just the lines within
     * the test, or show the full trace.
     * 
     * @parameter expression="${trimStackTrace}" default-value="true"
     */
    private boolean trimStackTrace;

    /**
     * The directory containing generated test classes of the project being
     * tested.
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testClassesDirectory;

    /**
     * @parameter
     */
    private List includes = new ArrayList();

    /**
     * @parameter
     */
    private List excludes = new ArrayList();

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Executing tests...");

        boolean success = runSurefire();
        if (!success) {
            String msg = "There were test failures";
            throw new MojoFailureException(msg);
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

}
