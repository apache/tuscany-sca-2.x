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

package org.apache.tuscany.sca.extensibility.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
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
public class OSGiServiceDiscovererTestCase {
    private static Felix felix;
    private static OSGiServiceDiscoverer discoverer;
    private static Bundle testBundle;

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
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
                      "org.osgi.framework; version=1.3.0," + "org.osgi.service.packageadmin; version=1.2.0,"
                          + "org.osgi.service.startlevel; version=1.0.0,"
                          + "org.osgi.service.url; version=1.0.0");
        // Explicitly specify the directory to use for caching bundles.
        configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, "target/.felix");
        List<BundleActivator> list = new ArrayList<BundleActivator>();

        // Now create an instance of the framework with
        // our configuration properties and activator.
        felix = new Felix(configMap, list);

        // Now start Felix instance.
        felix.start();
        BundleContext context = felix.getBundleContext();
        InputStream is = OSGiServiceDiscovererTestCase.class.getResourceAsStream("/test-bundle.jar");
        testBundle = context.installBundle("test-bundle", is);
        is.close();
        discoverer = new OSGiServiceDiscoverer(context);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (felix != null) {
            // Uninstall the bundle to clean up the cache
            testBundle.uninstall();
            felix.stop();
        }
    }

    @Test
    public void testDiscovery() {
        Set<ServiceDeclaration> descriptors =
            discoverer.discover("test.TestService", false);
        Assert.assertEquals(1, descriptors.size());
        descriptors = discoverer.discover("notthere", false);
        Assert.assertEquals(0, descriptors.size());
    }

    @Test
    public void testDiscoveryFirst() {
        Set<ServiceDeclaration> descriptors =
            discoverer.discover("test.TestService", true);
        Assert.assertEquals(1, descriptors.size());
        descriptors = discoverer.discover("notthere", true);
        Assert.assertEquals(0, descriptors.size());
    }    
    
    @Test
    public void testClassLoader () throws IOException {
        Enumeration<URL> resources = discoverer.getContextClassLoader().getResources("META-INF/services/test.TestService");
        List<URL> list = Collections.list(resources);
        Assert.assertEquals(1, list.size());
    }
}
