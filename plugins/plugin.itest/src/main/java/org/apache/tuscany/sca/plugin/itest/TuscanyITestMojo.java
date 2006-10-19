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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.surefire.Surefire;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.report.BriefFileReporter;
import org.apache.maven.surefire.report.ReporterException;

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
     * Whether to trim the stack trace in the reports to just the lines within the test, or show the full trace.
     *
     * @parameter expression="${trimStackTrace}" default-value="true"
     */
    private boolean trimStackTrace;

    /**
     * The directory containing generated test classes of the project being tested.
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
        System.out.println("Executing tests");

        boolean success = runSurefire();
        if (!success) {
            String msg = "There were test failures";
            throw new MojoFailureException(msg);
        }
    }

    public boolean runSurefire() throws MojoExecutionException {
        Surefire surefire = new Surefire();
        ClassLoader surefireClassLoader = surefire.getClass().getClassLoader();

        List reports = new ArrayList();
        reports.add(new Object[]{BriefFileReporter.class.getName(),
            new Object[]{reportsDirectory, trimStackTrace}});

        List testSuites = new ArrayList();
        testSuites.add(new Object[]{"org.apache.maven.surefire.junit.JUnitDirectoryTestSuite",
            new Object[]{testClassesDirectory, includes, excludes}});

        ClassLoader testsClassLoader = TuscanyStartMojo.foo.get();
        try {
            return surefire.run(reports, testSuites, surefireClassLoader, testsClassLoader);
        } catch (ReporterException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (TestSetFailedException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
