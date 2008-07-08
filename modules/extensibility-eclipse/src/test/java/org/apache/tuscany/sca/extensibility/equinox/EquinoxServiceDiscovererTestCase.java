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

package org.apache.tuscany.sca.extensibility.equinox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import junit.framework.Assert;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class EquinoxServiceDiscovererTestCase {
    private static EquinoxServiceDiscoverer discoverer;
    private static Bundle testBundle;

    private static String getState(Bundle b) {
        StringBuffer sb = new StringBuffer();
        int s = b.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append("UNINSTALLED ");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append("INSTALLED ");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append("RESOLVED ");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append("STARTING ");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append("STOPPING ");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append("ACTIVE ");
        }
        return sb.toString();

    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String args[] = {};
        Map props = new HashMap();
        EclipseStarter.setInitialProperties(props);
        BundleContext context = EclipseStarter.startup(args, null);

        InputStream is = EquinoxServiceDiscovererTestCase.class.getResourceAsStream("/test-bundle.jar");
        testBundle = context.installBundle("test-bundle", is);
        is.close();
        discoverer = new EquinoxServiceDiscoverer(context);
        File dep = new File("target/test-classes/dependency");
        List<Bundle> bundles = new ArrayList<Bundle>();
        for (File f : dep.listFiles()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            bis.mark(8192);
            JarInputStream jis = new JarInputStream(bis);
            Manifest manifest = jis.getManifest();
            if (manifest == null || manifest.getMainAttributes().getValue("Bundle-Name") == null) {
                bis.close();
                continue;
            }
            bis.reset();
            Bundle b = context.installBundle(f.getName(), bis);
            System.out.println("Installed "+b.getSymbolicName() + " [" + getState(b) + "]");
            bundles.add(b);
            is.close();
        }
        for (Bundle b : bundles) {
            b.start();
            System.out.println("Started "+b.getSymbolicName() + " [" + getState(b) + "]");
            // Get the Platform.getExtensionRegistry()
            if ("org.eclipse.core.runtime".equals(b.getSymbolicName())) {
                // The Platform class loaded by the bundle is different that the one
                // on the classpath
                Class<?> cls = b.loadClass("org.eclipse.core.runtime.Platform");
                Method m = cls.getMethod("getExtensionRegistry");
                Object reg = m.invoke(cls);
                System.out.println(reg);
            }
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (testBundle != null) {
            // Uninstall the bundle to clean up the cache
            testBundle.uninstall();
        }
        EclipseStarter.shutdown();
    }

    @Test
    public void testDiscovery() {
        Set<ServiceDeclaration> descriptors =
            discoverer.discover("org.apache.tuscany.sca.endpointresolver.EndpointResolverFactory");
        Assert.assertEquals(1, descriptors.size());
        descriptors = discoverer.discover("notthere");
        Assert.assertEquals(0, descriptors.size());
    }

}
