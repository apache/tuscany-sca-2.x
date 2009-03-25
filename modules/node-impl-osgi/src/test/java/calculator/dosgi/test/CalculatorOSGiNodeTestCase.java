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

package calculator.dosgi.test;

import static calculator.dosgi.test.OSGiTestBundles.generateCalculatorBundle;
import static calculator.dosgi.test.OSGiTestBundles.generateOperationsBundle;
import static calculator.dosgi.test.OSGiTestBundles.bundleStatus;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import calculator.dosgi.CalculatorService;

/**
 * 
 */
public class CalculatorOSGiNodeTestCase {
    private static EquinoxHost host;
    private static BundleContext context;
    private static Boolean client;

    public static URL getCodeLocation(final Class<?> anchorClass) {
        return AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return anchorClass.getProtectionDomain().getCodeSource().getLocation();
            }
        });
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            String prop = System.getProperty("client");
            if (prop != null) {
                client = Boolean.valueOf(prop);
            }
            Set<URL> bundles = new HashSet<URL>();

            if (client == null || client.booleanValue()) {
                System.out.println("Generating calculator.dosgi bundle...");
                bundles.add(generateCalculatorBundle());
            }

            if (client == null || !client.booleanValue()) {
                System.out.println("Generating calculator.dosgi.operations bundle...");
                bundles.add(generateOperationsBundle());
            }
            host = new EquinoxHost();
            context = host.start();
            for (URL loc : bundles) {
                host.installBundle(loc, null);
            }
            for (Bundle b : context.getBundles()) {
                if (b.getSymbolicName().equals("org.eclipse.equinox.ds") || b.getSymbolicName()
                    .startsWith("org.apache.tuscany.sca.")) {
                    try {
                        b.start();
                    } catch (Exception e) {
                        System.out.println(bundleStatus(b, false));
                        e.printStackTrace();
                    }
                    System.out.println(bundleStatus(b, false));
                }
            }
            for (Bundle b : context.getBundles()) {
                if (b.getSymbolicName().startsWith("calculator.dosgi")) {
                    b.start();
                    System.out.println(bundleStatus(b, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testOSGi() {
        if (client == null || client.booleanValue()) {
            ServiceReference ref = context.getServiceReference(CalculatorService.class.getName());
            Assert.assertNotNull(ref);
            Object service = context.getService(ref);
            Assert.assertNotNull(service);
            CalculatorService calculator = OSGiTestBundles.cast(service, CalculatorService.class);
            System.out.println("2.0 + 1.0 = " + calculator.add(2.0, 1.0));
            System.out.println("2.0 - 1.0 = " + calculator.subtract(2.0, 1.0));
            System.out.println("2.0 * 1.0 = " + calculator.multiply(2.0, 1.0));
            System.out.println("2.0 / 1.0 = " + calculator.divide(2.0, 1.0));
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (host != null) {
            if (client != null && !client.booleanValue()) {
                System.out.println("Press Enter to stop the node...");
                System.in.read();
            }
            host.stop();
            context = null;
        }
    }

}
