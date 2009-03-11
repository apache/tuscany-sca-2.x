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

package calculator.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestBundles;
import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import calculator.CalculatorActivator;
import calculator.CalculatorService;
import calculator.CalculatorServiceImpl;
import calculator.operations.AddService;
import calculator.operations.AddServiceImpl;
import calculator.operations.DivideService;
import calculator.operations.DivideServiceImpl;
import calculator.operations.MultiplyService;
import calculator.operations.MultiplyServiceImpl;
import calculator.operations.OperationsActivator;
import calculator.operations.SubtractService;
import calculator.operations.SubtractServiceImpl;

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
        Set<URL> bundles = new HashSet<URL>();
        bundles.add(OSGiTestBundles
            .createBundle("target/test-classes/calculator-bundle.jar",
                          "calculator/META-INF/MANIFEST.MF",
                          new String[] {"OSGI-INF/calculator-component.xml"},
                          CalculatorService.class,
                          CalculatorServiceImpl.class,
                          CalculatorActivator.class));

        bundles.add(OSGiTestBundles.createBundle("target/test-classes/operations-bundle.jar",
                                                 "calculator/operations/META-INF/MANIFEST.MF",
                                                 new String[] {"OSGI-INF/add-component.xml",
                                                               "OSGI-INF/subtract-component.xml",
                                                               "OSGI-INF/multiply-component.xml",
                                                               "OSGI-INF/divide-component.xml"},
                                                 OperationsActivator.class,
                                                 AddService.class,
                                                 AddServiceImpl.class,
                                                 SubtractService.class,
                                                 SubtractServiceImpl.class,
                                                 MultiplyService.class,
                                                 MultiplyServiceImpl.class,
                                                 DivideService.class,
                                                 DivideServiceImpl.class));
        try {
            host = new EquinoxHost(bundles);
            BundleContext context = host.start();
            for (Bundle b : context.getBundles()) {
                b.start();
            }
            ServiceReference ref = context.getServiceReference(CalculatorService.class.getName());
            CalculatorService calculator = cast(context.getService(ref), CalculatorService.class);
            System.out.println("2.0 + 1.0 = " + calculator.add(2.0, 1.0));
            System.out.println("2.0 - 1.0 = " + calculator.subtract(2.0, 1.0));
            System.out.println("2.0 * 1.0 = " + calculator.multiply(2.0, 1.0));
            System.out.println("2.0 / 1.0 = " + calculator.divide(2.0, 1.0));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static <T> T cast(Object obj, Class<T> cls) {
        if (cls.isInstance(obj)) {
            return cls.cast(obj);
        } else {
            return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(),
                                                   new Class<?>[] {cls},
                                                   new InvocationHandlerImpl(obj)));
        }
    }

    private static class InvocationHandlerImpl implements InvocationHandler {
        private Object instance;

        public InvocationHandlerImpl(Object instance) {
            super();
            this.instance = instance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method m = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
            return m.invoke(instance, args);
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
