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

package org.apache.tuscany.sca.implementation.node.osgi;

import hello.HelloWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.extensibility.osgi.OSGiServiceDiscoverer;
import org.apache.tuscany.sca.implementation.node.osgi.launcher.LauncherBundleActivator;
import org.apache.tuscany.sca.node.SCANode2Factory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * 
 */
public class LauncherBundleActivatorTestCase {
    private static Felix felix;
    // private static OSGiServiceDiscoverer discoverer;
    private static LauncherBundleActivator activator;

    private final static String systemPackages =
        "org.osgi.framework; version=1.3.0," + "org.osgi.service.packageadmin; version=1.2.0, "
            + "org.osgi.service.startlevel; version=1.0.0, "
            + "org.osgi.service.url; version=1.0.0, "
            + "org.osgi.util.tracker; version=1.3.2, "
            + "javax.xml, "
            + "javax.xml.datatype, "
            + "javax.xml.namespace, "
            + "javax.xml.parsers, "
            + "javax.xml.transform, "
            + "javax.xml.transform.dom, "
            + "javax.xml.transform.sax, "
            + "javax.xml.transform.stream, "
            + "javax.xml.validation, "
            + "javax.xml.xpath, "
            // Force the classes to be imported from the system bundle
            + "javax.xml.stream, "
            + "javax.xml.stream.util, "
            + "javax.sql,"
            + "org.w3c.dom, "
            + "org.xml.sax, "
            + "org.xml.sax.ext, "
            + "org.xml.sax.helpers, "
            + "javax.security.auth, "
            + "javax.security.auth.login, "
            + "javax.security.auth.callback, "
            + "javax.naming, "
            + "javax.naming.spi, "
            + "javax.naming.directory, "
            + "javax.management, "
            + "javax.imageio, "
            + "sun.misc, "
            + "javax.net, "
            + "javax.crypto, "
            + "javax.rmi, "
            + "javax.transaction, "
            + "javax.transaction.xa";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Create a configuration property map.
        Map<String, String> configMap = new HashMap<String, String>();
        // Configure the Felix instance to be embedded.
        configMap.put(FelixConstants.EMBEDDED_EXECUTION_PROP, "true");
        // Add core OSGi packages to be exported from the class path
        // via the system bundle.
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
        // Explicitly specify the directory to use for caching bundles.
        configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, "target/.felix");
        List<BundleActivator> list = new ArrayList<BundleActivator>();

        // Now create an instance of the framework with
        // our configuration properties and activator.
        felix = new Felix(configMap, list);

        // Now start Felix instance.
        felix.start();
        BundleContext context = felix.getBundleContext();
        //        discoverer = new OSGiServiceDiscoverer(context);
        //        ServiceDiscovery.setServiceDiscoverer(discoverer);

        System.setProperty("TUSCANY_HOME", "target/tuscany");
        activator = new LauncherBundleActivator();
        activator.start(context);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (felix != null) {
            // Uninstall the bundle to clean up the cache
            activator.stop(felix.getBundleContext());
            felix.stop();
        }
    }

    @Test
    public void testLauncher() throws Exception {
        String className = SCANode2Factory.class.getName();
        Map<String, Bundle> bundles = new HashMap<String, Bundle>();

        for (Bundle b : felix.getBundleContext().getBundles()) {
            bundles.put(b.getSymbolicName(), b);
            // b.start();
            System.out.println(OSGiServiceDiscoverer.toString(b));
        }
        Bundle b1 = bundles.get("org.apache.tuscany.sca.extensibility.osgi");
        Class<?> discovererClass = b1.loadClass(OSGiServiceDiscoverer.class.getName());
        Constructor<?> ctor = discovererClass.getConstructor(BundleContext.class);
        Object discoverer = ctor.newInstance(felix.getBundleContext());

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Method getCL = discovererClass.getMethod("getClassLoader");
        ClassLoader cl = (ClassLoader)getCL.invoke(discoverer);
        Thread.currentThread().setContextClassLoader(cl);

        try {
            Class<?> serviceDiscoveryClass = b1.loadClass(ServiceDiscovery.class.getName());
            Method set = serviceDiscoveryClass.getMethod("setServiceDiscoverer", discovererClass.getInterfaces()[0]);
            set.invoke(null, discoverer);

            Bundle b2 = bundles.get("org.apache.tuscany.sca.node2.api");
            // b2.start();
            Class<?> factory = b2.loadClass(className);
            Method newInstance = factory.getMethod("newInstance");
            Object instance = newInstance.invoke(null);
            Method create =
                instance.getClass().getMethod("createSCANodeFromClassLoader", String.class, ClassLoader.class);
            Object node = create.invoke(instance, "HelloWorld.composite", getClass().getClassLoader());
            Method start = node.getClass().getMethod("start");
            start.invoke(node);
            
            Method getService = node.getClass().getMethod("getService", Class.class, String.class);
            HelloWorld hw = (HelloWorld) getService.invoke(node, HelloWorld.class, "HelloWorld");
            hw.hello("OSGi");
            
            Method stop = node.getClass().getMethod("stop");
            stop.invoke(node);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }

    }

}
