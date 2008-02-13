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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
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

import junit.framework.AssertionFailedError;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @version $Rev$ $Date$
 */
public class JUnitServletFilter implements Filter {
    private static final Logger logger = Logger.getLogger(JUnitServletFilter.class.getName());

    private static final String JUNIT_TESTS_PATTERN = "junit.tests.pattern";
    private static final String JUNIT_TESTS_PATH = "junit.tests.path";
    private static final String JUNIT_ENABLED = "junit.enabled";
    private static final String TESTCASE_PATTERN = ".*TestCase";
    private static final String TESTS_JAR = "/WEB-INF/test-lib/junit-tests.jar";

    private FilterConfig config;
    private boolean junitEnabled = true;
    private Set<String> allTestCases;
    private ClassLoader testClassLoader;

    private Set<String> findTestCases(String testJarPath) throws IOException {
        Pattern pattern = getTestCasePattern();
        if (testJarPath.endsWith(".jar")) {
            return findTestCasesInJar(testJarPath, pattern);
        } else {
            return findTestCasesInDir(testJarPath, pattern);
        }
    }

    /**
     * Search test cases in a JAR
     * @param testJarPath
     * @param pattern
     * @return
     * @throws IOException
     */
    private Set<String> findTestCasesInJar(String testJarPath, Pattern pattern) throws IOException {
        InputStream in = config.getServletContext().getResourceAsStream(testJarPath);
        Set<String> tests = new HashSet<String>();
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

    private Pattern getTestCasePattern() {
        String filter = config.getInitParameter(JUNIT_TESTS_PATTERN);
        if (filter == null) {
            filter = TESTCASE_PATTERN;
        }
        Pattern pattern = Pattern.compile(filter);
        return pattern;
    }

    public void destroy() {
    }

    private void init() throws IOException {
        testClassLoader = Thread.currentThread().getContextClassLoader();
        allTestCases = new HashSet<String>();
        String testsPath = config.getInitParameter(JUNIT_TESTS_PATH);
        if (testsPath == null) {
            testsPath = TESTS_JAR;
        }
        URL url = config.getServletContext().getResource(testsPath);
        if (url != null) {
            allTestCases = findTestCases(testsPath);
            if (!(testsPath.startsWith("/WEB-INF/lib/") || testsPath.startsWith("/WEB-INF/classes/"))) {
                // Create a new classloader to load the test jar
                testClassLoader = new URLClassLoader(new URL[] {url}, testClassLoader);
            }
        }
        
        // Configure the SCADomain implementation class
        config.getServletContext().setAttribute("SCADomain.Implementation", "org.apache.tuscany.sca.host.webapp.WebSCADomain");
    }

    /**
     * Search test cases in a directory
     * @param classesPath
     * @param pattern
     * @return
     */
    private Set<String> findTestCasesInDir(String classesPath, Pattern pattern) {
        ServletContext context = config.getServletContext();
        Set<String> tests = new HashSet<String>();
        String dir = classesPath;
        findResources(context, pattern, tests, classesPath, dir);
        return tests;
    }

    @SuppressWarnings("unchecked")
    private void findResources(ServletContext context, Pattern pattern, Set<String> tests, String root, String dir) {
        Set<String> paths = context.getResourcePaths(dir);
        if (paths != null) {
            for (String name : paths) {
                if (name.endsWith("/")) {
                    findResources(context, pattern, tests, root, name);
                }
                if (name.endsWith(".class")) {
                    String className = name.substring(root.length(), name.length() - 6).replace('/', '.');
                    if (pattern.matcher(className).matches()) {
                        tests.add(className);
                    }
                }
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {

        if (!junitEnabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        if (!req.getRequestURI().equals(req.getContextPath() + "/junit")) {
            // Intercept the /junit call
            chain.doFilter(request, response);
            return;
        }

        String query = req.getQueryString();
        PrintStream ps = new PrintStream(response.getOutputStream());

        Set<String> testCases = null;
        // ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String op = req.getParameter("op");
        if (query == null || op == null || "list".equalsIgnoreCase(op)) {
            response.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            ps.println("<html><body>");
            ps.println("<h2>Available Test Cases</h2><p>");
            ps.println("<form method=\"get\" action=\"junit\">");
            ps.println("<table border=\"1\">");
            for (String s : this.allTestCases) {
                ps.print("<tr><td>");
                ps.print("<input type=\"checkbox\" name=\"test\" value=\"" + s
                    + "\"/><a href=\"junit?op=runSelected&test="
                    + s
                    + "\">"
                    + s
                    + "</a>");
                ps.println("</td></tr>");
            }
            ps.println("</table>");
            ps.println("<p><input type=\"submit\" name=\"op\" value=\"RunSelected\"/>");
            ps.println("<input type=\"submit\" name=\"op\" value=\"RunAll\"/>");
            ps.println("</form></body></html>");
            resp.flushBuffer();
            return;
        } else {
            if ("runAll".equalsIgnoreCase(op)) {
                testCases = this.allTestCases;
            } else {
                String[] tests = req.getParameterValues("test");
                if (tests == null) {
                    tests = new String[0];
                }
                testCases = new HashSet<String>(Arrays.asList(tests));
            }
        }

        response.setContentType("application/xml");
        ps.println("<?xml version=\"1.0\" encoding=\"" + "UTF-8" + "\"?>");

        ServletContext context = config.getServletContext();
        Object domain = context.getAttribute("org.apache.tuscany.sca.SCADomain");
        URL contribution = context.getResource("/META-INF/sca-contribution.xml");

        long duration = 0L;
        int errors = 0;
        int failures = 0;
        int runs = 0;
        List<Class<?>> testClasses = new ArrayList<Class<?>>();
        List<Result> results = new ArrayList<Result>();
        for (String testClass : testCases) {
            Class<?> test = null;
            try {
                test = Class.forName(testClass, false, testClassLoader);
                if (domain != null && contribution != null) {
                    // Inject the SCADomain
                    inject(test, domain);
                }
                testClasses.add(test);
            } catch (ClassNotFoundException e) {
                String st = XMLFormatter.exceptionToString(e);
                st = XMLFormatter.escape(st);
                ps.println(st);
                // ps.close();
                throw new ServletException(e);
            }

            JUnitCore core = new JUnitCore();
            Result result = core.run(Request.aClass(test));
            results.add(result);

            duration += result.getRunTime();
            runs += result.getRunCount();

            for (Failure f : result.getFailures()) {
                if (f.getException() instanceof AssertionFailedError) {
                    failures++;
                } else {
                    errors++;
                }
            }
        }

        ps.println("<" + XMLFormatter.TESTSUITE
            + " "
            + XMLFormatter.ATTR_TESTS
            + "=\""
            + runs
            + "\" "
            + XMLFormatter.ATTR_FAILURES
            + "=\""
            + failures
            + "\" "
            + XMLFormatter.ATTR_ERRORS
            + "=\""
            + errors
            + "\" "
            + XMLFormatter.ATTR_TIME
            + "=\""
            + XMLFormatter.getDurationAsString(duration)
            + "\">");
        for (int i = 0; i < testClasses.size(); i++) {
            ps.println(XMLFormatter.toXML(results.get(i), testClasses.get(i)));
        }
        ps.println("</" + XMLFormatter.TESTSUITE + ">");

        resp.addIntHeader("junit.errors", errors);
        resp.addIntHeader("junit.failures", failures);
        resp.addIntHeader("junit.runs", runs);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.flushBuffer();
        // ps.close();    
    }

    private boolean inject(Class<?> cls, Object target) {
        for (Field f : cls.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType().isInstance(target)) {
                f.setAccessible(true);
                try {
                    f.set(null, target);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                } catch (IllegalAccessException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        // Check if the /junit path should be allowed
        String param = config.getInitParameter(JUNIT_ENABLED);
        if (param != null && param.trim().equals("false")) {
            junitEnabled = false;
            return;
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
