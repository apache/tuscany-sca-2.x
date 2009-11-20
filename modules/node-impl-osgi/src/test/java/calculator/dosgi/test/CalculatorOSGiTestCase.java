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

import static calculator.dosgi.test.OSGiTestBundles.bundleStatus;
import static calculator.dosgi.test.OSGiTestBundles.generateCalculatorBundle;
import static calculator.dosgi.test.OSGiTestBundles.generateOperationsBundle;

import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import calculator.dosgi.CalculatorService;

/**
 *
 */
public class CalculatorOSGiTestCase {
    private static EquinoxHost host;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            host = new EquinoxHost();
            BundleContext context = host.start();
            Bundle calculatorBundle = context.installBundle("reference:" + generateCalculatorBundle().toString());
            Bundle operationsBundle = context.installBundle("reference:" + generateOperationsBundle().toString());

            for (Bundle b : context.getBundles()) {
                if (b.getSymbolicName().equals("org.eclipse.equinox.ds")) {
                    System.out.println(bundleStatus(b, false));
                    b.start();
                    System.out.println(bundleStatus(b, false));
                }
            }

            calculatorBundle.start();
            System.out.println(bundleStatus(calculatorBundle, false));
            operationsBundle.start();
            System.out.println(bundleStatus(operationsBundle, false));

            // Sleep for 1 sec so that the DS is available
            Thread.sleep(1000);
            // Use the DS version
            String filter = "(component.name=CalculatorComponent)";
            System.out.println(filter);
            ServiceReference ref =
                calculatorBundle.getBundleContext().getServiceReferences(CalculatorService.class.getName(), filter)[0];
            CalculatorService calculator = OSGiTestBundles.cast(context.getService(ref), CalculatorService.class);
            System.out.println("2.0 + 1.0 = " + calculator.add(2.0, 1.0));
            System.out.println("2.0 - 1.0 = " + calculator.subtract(2.0, 1.0));
            System.out.println("2.0 * 1.0 = " + calculator.multiply(2.0, 1.0));
            System.out.println("2.0 / 1.0 = " + calculator.divide(2.0, 1.0));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testOSGi() {

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (host != null) {
            host.stop();
        }
    }

}
