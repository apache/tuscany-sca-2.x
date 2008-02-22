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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.tuscany.sca.test.osgi.runtime.impl.OSGiTestRuntime;
import org.apache.tuscany.sca.test.util.OSGiRuntimeLoader;
import org.apache.tuscany.sca.test.util.TuscanyLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/*
 * Test Tuscany running in an OSGi container
 * Harness can be used to run Tuscany samples with Tuscany running in OSGi
 * These tests are not intended to be run as part of the build
 */
public class OSGiTuscanyTestHarness {
    
    
    private static String[] SAMPLE_DIRECTORIES = {
        
            "osgi-supplychain",
            "binding-notification-broker",
            "binding-notification-consumer",
            "binding-notification-producer",
            "calculator",            
            "calculator-rmi-reference",
            "calculator-webapp",
            "calculator-ws-webapp",
            "chat-webapp",
            "feed-aggregator",
            "feed-aggregator-webapp",
            "helloworld-dojo-webapp",
            "helloworld-jsonrpc-webapp",
            "helloworld-ws-service-secure",
            "helloworld-ws-sdo-webapp", 
            "implementation-composite",
            "implementation-notification", 
            "loanapplication", 
            "simple-bigbank", 
            "simple-callback",
            "store", 
            "supplychain", 
            "web-resource" 
            

            // FIXME: The following tests dont currently work when Tuscany is running inside
            //        OSGi. Known problems include classloading in some tests, classloading
            //        in the new domain API, URL handling for bundle URLs
            
            // "binding-echo", 
            // "binding-echo-extension",
            // "calculator-distributed", 
            // "calculator-implementation-policies",
            // "calculator-rmi-service",
            // "calculator-script", 
            // "callback-ws-client",
            // "callback-ws-service", 
            // "databinding-echo",
            // "domain-webapp",
            // "helloworld-bpel",
            // "helloworld-ws-service",
            // "helloworld-ws-reference",
            // "helloworld-ws-reference-jms,
            // "helloworld-ws-reference-secure",
            // "helloworld-ws-sdo", 
            // "implementation-crud",
            // "implementation-crud-extension", 
            // "implementation-pojo-extension",
            // "quote-xquery",
            // "simple-bigbank-spring", 
            // "simple-callback-ws",
            
            };
    

    private OSGiTestRuntime osgiRuntime;
    private Bundle tuscanyRuntime;
    private BundleContext bundleContext;

    @Before
    public void setUp() throws Exception {
        
        osgiRuntime = OSGiRuntimeLoader.startOSGiTestRuntime();
        bundleContext = osgiRuntime.getBundleContext();
    }
    

    @After
    public void tearDown() throws Exception {

        if (tuscanyRuntime != null) {
            tuscanyRuntime.stop();
            tuscanyRuntime.uninstall();
        }
        OSGiRuntimeLoader.shutdownOSGiRuntime();
    }
    

    @Test
    public void runTests() throws Exception {
        
        for (String testDir : SAMPLE_DIRECTORIES) {
            runTest("../../../samples/" + testDir);
        }
    }
     
   
    public void runTest(String testDir) throws Exception {
        
        System.out.println("Run tests from : " + testDir);

        tuscanyRuntime = TuscanyLoader.loadTuscanyIntoOSGi(bundleContext);
        
        Bundle testBundle = createAndInstallBundle(
                 "file:" + testDir + "/target/classes",        // Bundle location: used to get File URLs for DefaultSCADomain
                 "target/test-classes/META-INF/MANIFEST.MF",   // Test bundle manifest file
                 new String[]{
                    testDir + "/target/classes",
                    testDir + "/target/test-classes",
                    "target/test-classes"
                 });
    
        testBundle.start();
        
        Class<?> testClass = testBundle.loadClass(this.getClass().getName());
        Method testMethod = testClass.getMethod("runAllTestsFromBundle", Bundle.class);
        Object testObject = testClass.newInstance();
        testMethod.invoke(testObject, testBundle);
        
        testBundle.stop();
        testBundle.uninstall();
    }
    
    // Create and install a bundle with the specified manifest file
    // The bundle contains all files from the list of directories specified
    public Bundle createAndInstallBundle(String bundleLocation, String manifestFileName,
            String[] dirNames) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        File manifestFile = new File(manifestFileName);
        Manifest manifest = new Manifest();
        manifest.read(new FileInputStream(manifestFile));

        JarOutputStream jarOut = new JarOutputStream(out, manifest);
        
        for (int i = 0; i < dirNames.length; i++) {
            File dir = new File(dirNames[i]);
            addFilesToJar(dir, dirNames[i], jarOut);
        }

        jarOut.close();
        out.close();

        ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());
        return bundleContext.installBundle(bundleLocation, inStream);

    }
     
    // Add all the files from a build directory into a jar file
    // This method is used to create bundles on the fly
    private void addFilesToJar(File dir, String rootDirName, JarOutputStream jarOut) throws Exception {
        
        if (dir.getName().equals(".svn"))
            return;
        
        File[] files = dir.listFiles();
        
        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                addFilesToJar(files[i], rootDirName, jarOut);
                continue;
            }
            if (files[i].getName().endsWith("MANIFEST.MF"))
                continue;

            String entryName = files[i].getPath().substring(rootDirName.length()+1);
            entryName = entryName.replaceAll("\\\\", "/");
            ZipEntry ze = new ZipEntry(entryName);

            try {
                jarOut.putNextEntry(ze);
                FileInputStream file = new FileInputStream(files[i]);
                byte[] fileContents = new byte[file.available()];
                file.read(fileContents);
                jarOut.write(fileContents);
            } catch (Exception e) {
                // Ignore duplicate entry errors 
            }
        }
    }
    

    public void runAllTestsFromBundle(Bundle bundle) throws Exception {
        
        TestResult testResult = new TestResult();
        Enumeration entries = bundle.findEntries("/", "*TestCase.class", true);
        while (entries.hasMoreElements()) {
            URL entry = (URL)entries.nextElement();
            String className = entry.getFile();
            className = className.substring(1, className.length()-6); // remove leading / and trailing .class
            className = className.replaceAll("/", ".");
            Class testClass = bundle.loadClass(className);
            boolean isJunitTest = TestCase.class.isAssignableFrom(testClass);
            if (testClass.getName().endsWith("TestCase") &&
                    !testClass.getPackage().getName().startsWith("org.apache.tuscany.sca.test.osgi")) {
                Object test = (Object)testClass.newInstance();

                System.out.println("Running test " + test + " ");
                int ran = 0;
                int failed = 0;
                ArrayList<Method> testMethods = new ArrayList<Method>();
                Method setupMethod = null;
                Method tearDownMethod = null;
                Method setupClassMethod = null;
                Method tearDownClassMethod = null;
                Method[] methods = testClass.getDeclaredMethods();
                for (final Method method : methods) {
                    if ((isJunitTest && method.getName().startsWith("test"))
                                || method.getAnnotation(Test.class) != null) {
                        testMethods.add(method);
                        
                    }    
                    else if ((isJunitTest && method.getName().equals("setUp"))
                        || method.getAnnotation(Before.class) != null) {
                        
                        setupMethod = method;
                        AccessController.doPrivileged(new PrivilegedAction<Object>() {
                            public Object run() {
                                method.setAccessible(true);
                                return null;
                            }
                        });
                        
                    }
                    else if ((isJunitTest && method.getName().equals("tearDown"))
                            || method.getAnnotation(After.class) != null) {
                            
                        tearDownMethod = method;
                        AccessController.doPrivileged(new PrivilegedAction<Object>() {
                            public Object run() {
                                method.setAccessible(true);
                                return null;
                            }
                        });
                        
                    }
                    else if (method.getAnnotation(BeforeClass.class) != null) {
                            
                            setupClassMethod = method;
                            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                                public Object run() {
                                    method.setAccessible(true);
                                    return null;
                                }
                            });
                            
                        }
                        else if (method.getAnnotation(AfterClass.class) != null) {
                                
                            tearDownClassMethod = method;
                            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                                public Object run() {
                                    method.setAccessible(true);
                                    return null;
                                }
                            });
                            
                        }
                }
                try {
                    if (setupClassMethod != null)
                        setupClassMethod.invoke(null);
                    for (Method testMethod : testMethods) {
                        
                        ran++;
                        failed++;
                        try {
                            if (setupMethod != null)
                                setupMethod.invoke(test);
                            
                            testMethod.invoke(test);
                            failed--;
                        
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        } finally {
                            if (tearDownMethod != null)
                                tearDownMethod.invoke(test);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    
                    System.out.println("Ran: " + ran + ", Passed: " + (ran-failed) + ", Failed: " + failed);
                    if (tearDownClassMethod != null)
                        tearDownClassMethod.invoke(null);
                }
            }    

        }    
        
        Assert.assertEquals(0, testResult.errorCount());

    }
}
