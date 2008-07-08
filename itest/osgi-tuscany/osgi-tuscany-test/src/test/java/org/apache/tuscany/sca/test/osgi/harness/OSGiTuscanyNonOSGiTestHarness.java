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
package org.apache.tuscany.sca.test.osgi.harness;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;

import junit.framework.Assert;

import org.apache.tuscany.sca.test.util.TuscanyLoader;

/*
 * Test Tuscany running in an OSGi container
 * This harness runs Tuscany samples outside OSGi with Tuscany running in OSGi
 */
public class OSGiTuscanyNonOSGiTestHarness extends OSGiTuscanyTestHarness {

    public void runTest(String... testDirs) throws Exception {

        String mainTestDir = testDirs[0];

        File testDir = new File(mainTestDir + "/target/test-classes");
        if (!testDir.exists()) {
            System.err.println("Test directory " + testDir + " does not exist");
            return;
        }

        System.out.println("Run tests from : " + mainTestDir);

        long startTime = System.currentTimeMillis();

        String[] dirs = new String[testDirs.length + 2];
        int i = 0;
        dirs[i++] = mainTestDir + "/target/test-classes";
        dirs[i++] = "target/test-classes";
        for (int j = 0; j < testDirs.length; j++) {
            dirs[i++] = testDirs[j] + "/target/classes";
        }

        tuscanyRuntime = TuscanyLoader.loadTuscanyIntoOSGi(getBundleContext());
        long endTime = System.currentTimeMillis();

        System.out.println("Loaded Tuscany, time taken = " + (endTime - startTime) + " ms");

        URL[] dirURLs = new URL[dirs.length];
        for (int j = 0; j < dirs.length; j++) {
            dirURLs[j] = new File(dirs[j]).toURI().toURL();
        }
        ClassLoader testClassLoader = new URLClassLoader(dirURLs, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(testClassLoader);

        Class<?> testClass = testClassLoader.loadClass(this.getClass().getName());
        Method testMethod = testClass.getMethod("runAllTestsFromDirs", ClassLoader.class, String[].class);
        Object testObject = testClass.newInstance();
        testMethod.invoke(testObject, testClassLoader, dirs);

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

    public void runAllTestsFromDirs(ClassLoader testClassLoader, String[] testDirs) throws Exception {

        int failures = 0;
        HashSet<String> testCaseSet = new HashSet<String>();
        for (String testDir : testDirs) {
            getTestCases(new File(testDir), null, testCaseSet);
        }
        for (String className : testCaseSet) {
            Class testClass = testClassLoader.loadClass(className);
            failures += runTestCase(testClass).getFailureCount();
        }

        Assert.assertEquals(0, failures);

    }
}
