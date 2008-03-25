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
package org.apache.tuscany.sca.test.contribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osoa.sca.ServiceReference;

/*
 * 
 * Contribution ClassLoading integration tests
 */

//FIXME This test case needs some serious rework!
// First it is very dependent on the names of the Tuscany runtime JARs
// and this is going to be difficult to maintain
// Second its usage of reflection requires the Tuscany implementation classes
// to be made public and this breaks isolation between modules.
public class TuscanyClassloadingTestCaseFIXME {

    //	private static final int SCA_API = 1;
    //	private static final int TUSCANY_CORE_SPI = 2;
    //	private static final int TUSCANY_RUNTIME = 3;
    //	private static final int TUSCANY_EXTENSIONS = 4;
    //	private static final int TUSCANY_DEPENDENCIES = 0;

    private static final String[] scaApiJars = {"sca-api"};
    private static final String[] tuscanyCoreSpiJars =
        {"core-spi", "interface", "interface-java", "interface-wsdl", "assembly", "policy", "databinding",
         "contribution", "definitions"};
    private static final String[] tuscanyRuntimeJars =
        {

        "binding-sca-xml", "binding-sca", "assembly-java-dsl", "assembly-xml", "assembly-xsd", "contribution-impl",
         "contribution-java", "contribution-namespace", "core-databinding", "core-spring", "core", "definitions-xml",
         "domain-api", "domain-impl", "domain", "extension-helper", "host-embedded", "interface-java-xml",
         "interface-wsdl-xml", "java2wsdl", "node-api", "node-impl", "node", "osgi-runtime", "policy-logging",
         "policy-security", "policy-transaction", "policy-xml", "wsdl2java"};
    private static final String[] tuscanyExtensionJars =
        {"binding-dwr", "binding-ejb", "binding-feed", "binding-http", "binding-jms", "binding-jsonrpc",
         "binding-notification", "binding-rmi", "binding-sca-axis2", "binding-ws-axis2", "binding-ws-xml",
         "binding-ws", "databinding-axiom", "databinding-fastinfoset", "databinding-jaxb", "databinding-json",
         "databinding-saxon", "databinding-sdo-axiom", "databinding-sdo", "databinding-xmlbeans", "host-http",
         "host-jetty", "host-rmi", "host-tomcat", "host-webapp", "implementation-das.jar", "implementation-data.jar",
         "implementation-java-runtime", "implementation-java-xml", "implementation-java",
         "implementation-node-runtime", "implementation-node-xml", "implementation-node",
         "implementation-notification", "implementation-osgi", "implementation-resource", "implementation-script",
         "implementation-spring", "implementation-xquery", "contribution-osgi"};

    private Class<?> embeddedDomainClass;
    Object domain;

    @Before
    public void setUp() throws Exception {

        embeddedDomainClass = getEmbeddedDomainClass();

        Constructor c = embeddedDomainClass.getConstructor(ClassLoader.class, String.class);
        // Create an embedded domain
        domain = c.newInstance(embeddedDomainClass.getClassLoader(), "http://localhost");

        // Start the domain
        invokeNoArgsMethod(domain, "start");

    }

    @After
    public void tearDown() throws Exception {

        // Stop the domain
        invokeNoArgsMethod(domain, "stop");

    }

    /**
     * Create a ClassLoader hierarchy for Tuscany runtime
     *     Dependencies <- SCA-API <- Core-SPI+ Runtime <- Extensions
     * Load the embedded SCA domain class using the runtime ClassLoader
     * 
     * @return embedded SCA domain class
     * @throws Exception
     */
    private Class<?> getEmbeddedDomainClass() throws Exception {

        URL[] scaApiUrls;
        URL[] runtimeUrls;
        URL[] extensionUrls;
        URL[] dependencyUrls;

        // When the test is run under Eclipse, the ClassLoader for the test is
        // sun.misc.Launcher$AppClassLoader. The first code path is taken.
        // When the test is run under Maven, the ClassLoader for the test is
        // org.apache.maven.surefire.booter.IsolatedClassLoader, which is a subclass
        // of URLClassLoader. The second code path is taken.
        if (!(this.getClass().getClassLoader() instanceof URLClassLoader)) {
            String classPath = System.getProperty("java.class.path");
            String[] classPathEntries = classPath.split(System.getProperty("path.separator"));
            HashSet<String> dependentJars = new HashSet<String>();
            for (int i = 0; i < classPathEntries.length; i++) {
                dependentJars.add(classPathEntries[i]);
            }

            scaApiUrls = getTuscanyClassLoaderURLs(classPathEntries, dependentJars, scaApiJars);
            runtimeUrls =
                getTuscanyClassLoaderURLs(classPathEntries, dependentJars, tuscanyCoreSpiJars, tuscanyRuntimeJars);
            extensionUrls = getTuscanyClassLoaderURLs(classPathEntries, dependentJars, tuscanyExtensionJars);
            dependencyUrls = getTuscanyClassLoaderURLs(classPathEntries, dependentJars);
        } else {
            HashSet<URL> dependentJars = new HashSet<URL>();
            URL[] classPathEntries = ((URLClassLoader)this.getClass().getClassLoader()).getURLs();
            for (int i = 0; i < classPathEntries.length; i++) {
                dependentJars.add(classPathEntries[i]);
            }
            scaApiUrls = getTuscanyClassLoaderURLs(classPathEntries, dependentJars, scaApiJars);
            runtimeUrls =
                getTuscanyClassLoaderURLs(classPathEntries, dependentJars, tuscanyCoreSpiJars, tuscanyRuntimeJars);
            extensionUrls = getTuscanyClassLoaderURLs(classPathEntries, dependentJars, tuscanyExtensionJars);
            dependencyUrls = getTuscanyClassLoaderURLs(classPathEntries, dependentJars);

        }

        boolean useSingleClassLoader =
            (scaApiUrls == null || scaApiUrls.length == 0) || (runtimeUrls == null || runtimeUrls.length == 0)
                || (extensionUrls == null || extensionUrls.length == 0)
                || (dependencyUrls == null || dependencyUrls.length == 0);

        if (useSingleClassLoader) {
            return EmbeddedSCADomain.class;
        } else {

            ClassLoader dependencyLoader = new URLClassLoader(dependencyUrls, null);
            ClassLoader scaApiLoader = new URLClassLoader(scaApiUrls, dependencyLoader);
            ClassLoader runtimeClassLoader = new URLClassLoader(runtimeUrls, scaApiLoader);
            ClassLoader extensionClassLoader = new URLClassLoader(extensionUrls, runtimeClassLoader);

            Class<?> serviceDiscoveryClass = runtimeClassLoader.loadClass(ServiceDiscovery.class.getName());
            Method getInstanceMethod = serviceDiscoveryClass.getMethod("getInstance");
            Object serviceDiscoveryObj = getInstanceMethod.invoke(null);
            Method registerClassLoaderMethod =
                serviceDiscoveryClass.getMethod("registerClassLoader", ClassLoader.class);
            registerClassLoaderMethod.invoke(serviceDiscoveryObj, extensionClassLoader);

            Thread.currentThread().setContextClassLoader(extensionClassLoader);

            return runtimeClassLoader.loadClass(EmbeddedSCADomain.class.getName());

        }

    }

    /**
     * From the list of entries in the test ClassLoader, match
     * Tuscany jars corresponding to a ClassLoader, and return the list
     * of matching entries as URLs.
     * This method is used when the test is run under eclipse, using CLASSPATH
     * based application ClassLoader.
     * 
     * @param classPathEntries List of entries on CLASSPATH
     * @param dependentJars Complete set of jars, remove jars corresponding to this
     *                      ClassLoader from the set.
     * @param jars List of Tuscany jars corresponding to this ClassLoader
     * @return Matching URLs for the ClassLoader 
     * @throws IOException
     */
    private URL[] getTuscanyClassLoaderURLs(String[] classPathEntries, HashSet<String> dependentJars, String[]... jars)
        throws IOException {

        String pathSeparator = System.getProperty("file.separator");
        HashSet<String> classPathEntrySet;

        if (jars.length == 0)
            classPathEntrySet = dependentJars;
        else {
            classPathEntrySet = new HashSet<String>();

            for (int i = 0; i < classPathEntries.length; i++) {

                String classPathEntry = classPathEntries[i];
                for (int j = 0; j < jars.length; j++) {
                    String[] jarList = jars[j];
                    if (jarList != null) {
                        for (int k = 0; k < jarList.length; k++) {
                            String jarName = "tuscany-" + jarList[k];
                            String alternateJarName = "modules" + pathSeparator + jarList[k];
                            if (classPathEntry.indexOf(jarName) >= 0 || classPathEntry.indexOf(alternateJarName) >= 0) {
                                classPathEntrySet.add(classPathEntry);
                                dependentJars.remove(classPathEntry);
                            }
                        }
                    }
                }
            }

        }
        ArrayList<URL> urls = new ArrayList<URL>();

        for (String fileName : classPathEntrySet) {
            File file = new File((String)fileName);
            if (!file.exists()) {
                throw new FileNotFoundException(fileName);

            } else {
                urls.add(file.toURL());

            }
        }

        return (URL[])urls.toArray(new URL[urls.size()]);
    }

    /**
     * From the list of URLs of the test ClassLoader, match
     * Tuscany jars corresponding to a ClassLoader, and return the matching URLs
     * This method is used when the test is run under Maven. The test ClassLoader is
     * org.apache.maven.surefire.booter.IsolatedClassLoader, which is a subclass
     * of URLClassLoader
     * 
     * @param classPathEntries List of URLs from the test ClassLoader
     * @param dependentJars Complete set of jars, remove jars corresponding to this
     *                      ClassLoader from the set.
     * @param jars List of Tuscany jars corresponding to this ClassLoader
     * @return Matching URLs for the ClassLoader 
     * @throws IOException
     */
    private URL[] getTuscanyClassLoaderURLs(URL[] classPathEntries, HashSet<URL> dependentJars, String[]... jars)
        throws IOException {

        String pathSeparator = System.getProperty("file.separator");
        HashSet<URL> classPathEntrySet;

        if (jars.length == 0)
            classPathEntrySet = dependentJars;
        else {
            classPathEntrySet = new HashSet<URL>();

            for (int i = 0; i < classPathEntries.length; i++) {

                URL classPathEntry = classPathEntries[i];
                String classPathEntryStr = classPathEntry.getPath();
                for (int j = 0; j < jars.length; j++) {
                    String[] jarList = jars[j];
                    if (jarList != null) {
                        for (int k = 0; k < jarList.length; k++) {
                            String jarName = "tuscany-" + jarList[k];
                            String alternateJarName = "modules" + pathSeparator + jarList[k];
                            if (classPathEntryStr.indexOf(jarName) >= 0 || classPathEntryStr.indexOf(alternateJarName) >= 0) {
                                classPathEntrySet.add(classPathEntry);
                                dependentJars.remove(classPathEntry);
                            }
                        }
                    }
                }
            }

        }
        return (URL[])classPathEntrySet.toArray(new URL[classPathEntrySet.size()]);
    }

    private Object invokeNoArgsMethod(Object obj, String methodName) throws Exception {

        return obj.getClass().getMethod(methodName).invoke(obj);
    }

    private Object invokeOneArgMethod(Object obj, String methodName, Class argType, Object arg) throws Exception {

        return obj.getClass().getMethod(methodName, argType).invoke(obj, arg);
    }

    /**
     * 
     * Load Tuscany runtime using multiple ClassLoaders, and run supplychain
     * test. 
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {

        ClassLoader runtimeClassloader = embeddedDomainClass.getClassLoader();

        if (runtimeClassloader == this.getClass().getClassLoader()) {
            System.out.println("Runtime and test loaded using the same classloader " + runtimeClassloader);
        } else {
            System.out
                .println("Running test using separate Tuscany classloaders, runtime classloader=" + runtimeClassloader);
            ClassLoader apiClassLoader =
                runtimeClassloader.loadClass(ServiceReference.class.getName()).getClassLoader();
            Assert.assertTrue(apiClassLoader != runtimeClassloader);

            try {
                runtimeClassloader.loadClass("org.apache.tuscany.sca.implementation.java.JavaImplementation");
                Assert.fail("Loaded extension class incorrectly from runtimeClassLoader");
            } catch (ClassNotFoundException e) {
            }

        }

        // Contribute supplychain (as single contribution)
        Object contributionService = invokeNoArgsMethod(domain, "getContributionService");
        Method contributeMethod =
            contributionService.getClass().getMethod("contribute", String.class, URL.class, boolean.class);

        String folderName = "../contribution-classes/target/classes";
        String supplychainJarName = "CompleteSupplyChain";
        URL supplyChainContribURL = new File(folderName + "/" + supplychainJarName + ".jar").toURL();
        Object contribution = contributeMethod.invoke(contributionService, "SupplyChain", supplyChainContribURL, true);

        Object composite = ((List)invokeNoArgsMethod(contribution, "getDeployables")).get(0);
        Object domainComposite = invokeNoArgsMethod(domain, "getDomainComposite");
        List includes = (List)invokeNoArgsMethod(domainComposite, "getIncludes");
        includes.add(composite);
        //Object compositeBuilder = invokeNoArgsMethod(domain, "getCompositeBuilder");
        Object compositeActivator = invokeNoArgsMethod(domain, "getCompositeActivator");

        Class compositeClass = embeddedDomainClass.getClassLoader().loadClass(Composite.class.getName());
        invokeOneArgMethod(domain, "buildComposite", compositeClass, composite);
        invokeOneArgMethod(compositeActivator, "activate", compositeClass, composite);
        invokeOneArgMethod(compositeActivator, "start", compositeClass, composite);

        // Get customer service 
        Method getClassLoaderMethod = contribution.getClass().getMethod("getClassLoader");
        ClassLoader classLoader = (ClassLoader)getClassLoaderMethod.invoke(contribution);

        Class customerClass = classLoader.loadClass("supplychain.customer.Customer");
        Method getServiceMethod = embeddedDomainClass.getMethod("getService", Class.class, String.class);
        Object customer = getServiceMethod.invoke(domain, customerClass, "CustomerComponent");

        // Invoke purchaseGoods
        Method m = customerClass.getMethod("purchaseGoods");
        m.invoke(customer);

        m = customerClass.getMethod("outstandingOrderCount");

        int retries = 10;
        int outstandingCount = 1;
        while (retries-- > 0) {

            outstandingCount = (int)(Integer)m.invoke(customer);
            if (outstandingCount == 0)
                break;
            else
                Thread.sleep(100);
        }
        Assert.assertEquals(0, outstandingCount);

    }

}
