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

import static calculator.dosgi.test.OSGiTestUtils.bundleStatus;

import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import calculator.dosgi.CalculatorService;
import calculator.rmi.OperationsRMIServer;

/**
 *
 */
public class CalculatorOSGiNodeTestCase {
    private static EquinoxHost host;
    private static BundleContext context;
    private static Bundle calculatorBundle;
    private static OperationsRMIServer rmiServer;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            rmiServer = new OperationsRMIServer();
            rmiServer.start();

            host = new EquinoxHost();
            context = host.start();

            for (Bundle b : context.getBundles()) {
                System.out.println(b);
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
                if ("calculator.dosgi".equals(b.getSymbolicName())) {
                    calculatorBundle = b;
                }
            }

            if (calculatorBundle != null) {
                calculatorBundle.start();
                System.out.println(bundleStatus(calculatorBundle, false));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testOSGi() {
        ServiceReference ref =
            calculatorBundle.getBundleContext().getServiceReference(CalculatorService.class.getName());
        Assert.assertNotNull(ref);
        Object service = context.getService(ref);
        Assert.assertNotNull(service);
        CalculatorService calculator = OSGiTestUtils.cast(service, CalculatorService.class);
        System.out.println("2.0 + 1.0 = " + calculator.add(2.0, 1.0));
        System.out.println("2.0 - 1.0 = " + calculator.subtract(2.0, 1.0));
        System.out.println("2.0 * 1.0 = " + calculator.multiply(2.0, 1.0));
        System.out.println("2.0 / 1.0 = " + calculator.divide(2.0, 1.0));
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (host != null) {
            host.stop();
            rmiServer.stop();
            host = null;
            rmiServer = null;
            context = null;
        }
    }

}
