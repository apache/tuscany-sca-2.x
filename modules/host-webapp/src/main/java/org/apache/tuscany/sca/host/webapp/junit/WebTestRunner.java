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

package org.apache.tuscany.sca.host.webapp.junit;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import junit.textui.TestRunner;

/**
 * @version $Rev$ $Date$
 */
public class WebTestRunner implements Filter {
    private static final String JUNIT_TESTS_PATTERN = "junit.tests.pattern";
    private static final String JUNIT_TESTS_JAR = "junit.tests.jar";
    private static final String JUNIT_ENABLED = "junit.enabled";
    private static final String TESTCASE_PATTERN = ".*TestCase";
    private static final String TESTS_JAR = "/WEB-INF/test-lib/junit-tests.jar";

    private ServletContext context;
    private boolean junitEnabled = true;

    private List<String> findTestCases(String testJarPath) throws IOException {
        String filter = context.getInitParameter(JUNIT_TESTS_PATTERN);
        if (filter == null) {
            filter = TESTCASE_PATTERN;
        }
        Pattern pattern = Pattern.compile(filter);
        InputStream in = context.getResourceAsStream(testJarPath);
        List<String> tests = new ArrayList<String>();
        if (in != null) {
            JarInputStream jar = new JarInputStream(in);
            try {
                JarEntry entry = null;

                while ((entry = jar.getNextJarEntry()) != null) {
                    String name = entry.getName();

                    if (name.endsWith(".class")) {
                        String className = name.substring(0, name.length() - 6).replace('/', '.');
                        if (pattern.matcher(className).matches()) {
                            tests.add(className);
                        }
                    }
                }
            } catch (EOFException e) {
            } finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException e) {
                    }
                }
            }

        }
        return tests;
    }

    public void destroy() {
    }

    private List<String> allTestCases;
    private ClassLoader testClassLoader;

    private void init() throws IOException {
        testClassLoader = Thread.currentThread().getContextClassLoader();
        allTestCases = new ArrayList<String>();
        String testsJar = context.getInitParameter(JUNIT_TESTS_JAR);
        if (testsJar == null) {
            testsJar = TESTS_JAR;
        }
        URL url = context.getResource(testsJar);
        if (url != null) {
            allTestCases = findTestCases(testsJar);
            if (!testsJar.startsWith("/WEB-INF/lib/")) {
                // Create a new classloader to load the test jar
                testClassLoader = new URLClassLoader(new URL[] {url}, testClassLoader);
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        String query = req.getQueryString();
        PrintStream ps = new PrintStream(response.getOutputStream());

        List<String> testCases = null;
        // ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (query == null || "ALL".equals(query)) {
            testCases = this.allTestCases;
        } else {
            String[] tests = query.split(",");
            testCases = Arrays.asList(tests);
        }

        int errors = 0;
        int failures = 0;
        int runs = 0;
        response.setContentType("application/xml");
        ps.println("<?xml version=\"1.0\" encoding=\"" + "UTF-8" + "\"?>");
        ps.println("<" + XMLFormatter.TESTSUITES + ">");
        for (String testClass : testCases) {
            Class<?> test = null;
            try {
                test = Class.forName(testClass, false, testClassLoader);
            } catch (ClassNotFoundException e) {
                String st = XMLFormatter.exceptionToString(e);
                st = XMLFormatter.escape(st);
                ps.println(st);
                // ps.close();
                throw new ServletException(e);
            }
            final XMLFormatter formatter = new XMLFormatter();
            TestRunner runner = new TestRunner() {
                protected TestResult createTestResult() {
                    TestResult result = new TestResult();
                    result.addListener(formatter);
                    return result;
                }
            };
            long startTime = System.currentTimeMillis();
            TestResult result = runner.doRun(new JUnit4TestAdapter(test));
            runs += result.runCount();
            failures += result.failureCount();
            errors += result.errorCount();
            long endTime = System.currentTimeMillis();
            formatter.setTotalDuration(endTime - startTime);
            ps.println(formatter.toXML(result));
        }
        ps.println("</" + XMLFormatter.TESTSUITES + ">");
        ((HttpServletResponse)response).addIntHeader("junit.errors", errors);
        ((HttpServletResponse)response).addIntHeader("junit.failures", failures);
        ((HttpServletResponse)response).addIntHeader("junit.runs", runs);
        // ps.close();    
    }

    public void init(FilterConfig config) throws ServletException {
        context = config.getServletContext();
        // Check if the /junit path should be allowed
        String param = context.getInitParameter(JUNIT_ENABLED);
        if (param != null && param.trim().equals("false")) {
            junitEnabled = false;
        }
        try {
            init();
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    public boolean isJunitEnabled() {
        return junitEnabled;
    }

}
