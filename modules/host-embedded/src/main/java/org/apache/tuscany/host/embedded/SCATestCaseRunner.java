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
package org.apache.tuscany.host.embedded;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A helper class that can be used to run an SCA JUnit test case. The test case will run in an isolated class loader.
 * 
 * @version $Rev$ $Date$
 */
public class SCATestCaseRunner {

    private ClassLoader classLoader;
    private Class<?> testSuiteClass;
    private Object testSuite;
    private Class<?> testResultClass;
    private Class<?> testCaseClass;
    private Object testCase;

    private Class<?> beforeAnnotation;
    private Class<?> beforeClassAnnotation;
    private Class<?> afterAnnotation;
    private Class<?> afterClassAnnotation;
    private Class<?> junit4AdapterClass;
    private Class<?> junit3TestCaseClass;

    /**
     * Constructs a new TestCase runner.
     * 
     * @param testClass
     */
    public SCATestCaseRunner(Class testClass) {
        try {
            classLoader = (URLClassLoader)testClass.getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader)classLoader).getURLs();
                classLoader = new URLClassLoader(urls, classLoader.getParent());
            } else {
                classLoader = new URLClassLoader(new URL[0], classLoader);
            }

            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classLoader);

                testCaseClass = Class.forName(testClass.getName(), true, classLoader);
                testCase = testCaseClass.newInstance();

                junit3TestCaseClass = Class.forName("junit.framework.TestCase", true, classLoader);

                testSuiteClass = Class.forName("junit.framework.TestSuite", true, classLoader);
                Constructor testSuiteConstructor = testSuiteClass.getConstructor(Class.class);
                testSuite = testSuiteConstructor.newInstance(testCaseClass);

                testResultClass = Class.forName("junit.framework.TestResult", true, classLoader);

                try {
                    beforeAnnotation = Class.forName("org.junit.Before", true, classLoader);
                    afterAnnotation = Class.forName("org.junit.After", true, classLoader);
                    beforeClassAnnotation = Class.forName("org.junit.BeforeClass", true, classLoader);
                    afterClassAnnotation = Class.forName("org.junit.AfterClass", true, classLoader);
                    junit4AdapterClass = Class.forName("junit.framework.JUnit4TestAdapter", true, classLoader);
                } catch (Exception e) {
                    // Unexpected
                    throw new AssertionError(e);
                }

            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Run the test case
     */
    public void run() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            if (junit3TestCaseClass.isAssignableFrom(testCaseClass)) {
                Object testResult = testResultClass.newInstance();
                Method runMethod = testSuiteClass.getMethod("run", testResultClass);
                runMethod.invoke(testSuite, testResult);
            } else {
                Object junit4Adapter = junit4AdapterClass.getConstructor(Class.class).newInstance(testCaseClass);
                Object testResult = testResultClass.newInstance();
                Method runMethod = junit4AdapterClass.getMethod("run", testResultClass);
                runMethod.invoke(junit4Adapter, testResult);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Invoke the setUp method
     */
    public void setUp() {
        execute("setUp");
    }

    /**
     * Invoke the before methods
     */
    public void before() {
        execute(beforeAnnotation);
    }

    /**
     * Invoke the beforeClass methods
     */
    public void beforeClass() {
        execute(beforeClassAnnotation);
    }

    /**
     * Invoke the tearDown method
     */
    public void tearDown() {
        execute("tearDown");
    }

    /**
     * Invoke the after methods
     */
    public void after() {
        execute(afterAnnotation);
    }

    /**
     * Invoke the afterClass methods
     */
    public void afterClass() {
        execute(afterClassAnnotation);
    }

    /**
     * Invoke the specified test method.
     */
    public void run(String methodName) {
        execute(methodName);
    }

    /**
     * Invoke the methods annotated with the specified annotation.
     */
    private void execute(Class<?> annotationClass) {
        if (annotationClass == null) {
            throw new RuntimeException(new NoSuchMethodException());
        }
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            for (Method method : testCaseClass.getDeclaredMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType() == annotationClass) {
                        method.invoke(testCase);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Invoke the specified method
     */
    private void execute(String methodName) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            Method setUpMethod = testCaseClass.getDeclaredMethod(methodName);
            setUpMethod.setAccessible(true);
            setUpMethod.invoke(testCase);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

}
