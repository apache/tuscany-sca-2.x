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

package calculator.dosgi.operations.test;

import static calculator.dosgi.operations.test.OSGiTestUtils.bundleStatus;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import calculator.dosgi.operations.AddService;

/**
 *
 */
public class OperationsOSGiNodeTestCase {
    private static EquinoxHost host;
    private static BundleContext context;
    private static Bundle operationsBundle;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            host = new EquinoxHost();
            context = host.start();

            for (Bundle b : context.getBundles()) {
                if (b.getSymbolicName().equals("org.eclipse.equinox.ds") || b.getSymbolicName()
                    .startsWith("org.apache.tuscany.sca.")) {
                    try {
                        if (b.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
                            // Start the non-fragment bundle
                            b.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(bundleStatus(b, false));
                }
                if ("calculator.dosgi.dynamic.operations".equals(b.getSymbolicName())) {
                    operationsBundle = b;
                }
            }

            if (operationsBundle != null) {
                operationsBundle.start();
                System.out.println(bundleStatus(operationsBundle, false));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testOSGi() throws Exception {
        Registry registry = LocateRegistry.getRegistry(8085);
        Object add = registry.lookup("AddService");
        AddService addService = OSGiTestUtils.cast(add, AddService.class);
        double sum = addService.add(1.0, 2.0);
        Assert.assertEquals(3.0, sum, 0.0);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (host != null) {
            host.stop();
            context = null;
        }
    }

}
