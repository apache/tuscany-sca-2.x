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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.osoa.sca.annotations.Requires;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
public class PolicyProcessorTestCase extends TestCase {
    private ServiceProcessor serviceProcessor;
    private PolicyProcessor policyProcessor;
    private JavaImplementation type;

    // This actually is a test for PolicyJavaInterfaceProcessor. It will get
    // invoked via the call to ImplementationProcessorServiceImpl.createService in
    // ServiceProcessor. Of course ServiceProcessor class has to be working.
    public void testSingleInterfaceWithIntentsOnInterfaceAtInterfaceLevel() throws Exception {
        serviceProcessor.visitClass(Service1.class, type);
        policyProcessor.visitClass(Service1.class, type);
        verifyIntents(Service1.class, type);
    }

    public void testMultipleInterfacesWithIntentsOnInterfaceAtInterfaceLevel() throws Exception {
        serviceProcessor.visitClass(Service2.class, type);
        policyProcessor.visitClass(Service2.class, type);
        verifyIntents(Service2.class, type);
    }

    public void testSingleInterfaceWithIntentsOnImplAtClassLevel() throws Exception {
        serviceProcessor.visitClass(Service3.class, type);
        policyProcessor.visitClass(Service3.class, type);
        verifyIntents(Service3.class, type);
    }

    public void testMultipleInterfacesWithIntentsOnImplAtClassLevel() throws Exception {
        serviceProcessor.visitClass(Service4.class, type);
        policyProcessor.visitClass(Service4.class, type);
        verifyIntents(Service4.class, type);
    }

    public void testSingleInterfaceWithIntentsOnInterfaceAtMethodLevel() throws Exception {
        serviceProcessor.visitClass(Service5.class, type);
        policyProcessor.visitClass(Service5.class, type);
        verifyIntents(Service5.class, type);
    }

    public void testSingleInterfaceWithIntentsOnServiceAndInterfaceAtImplAndInertfaceAndMethodLevel() throws Exception {
        serviceProcessor.visitClass(Service6.class, type);
        policyProcessor.visitClass(Service6.class, type);
        for (Method method : Service6.class.getDeclaredMethods()) {
            policyProcessor.visitMethod(method, type);
        }
        verifyIntents(Service6.class, type);
    }

    private void verifyIntents(Class serviceImplClass, JavaImplementation type) {
        if ( !(type instanceof PolicySetAttachPoint) ) {
            fail("No Intents on the service ");
        }
        Requires serviceImplIntentAnnotation = (Requires)serviceImplClass.getAnnotation(Requires.class);
        if (serviceImplIntentAnnotation != null) {
            String[] serviceImplIntents = serviceImplIntentAnnotation.value();
            List<Intent> requiredIntents = ((PolicySetAttachPoint)type).getRequiredIntents();
            if (serviceImplIntents.length > 0) {
                if (requiredIntents == null || requiredIntents.size() == 0) {
                    fail("No Intents on the service ");
                }
                Map<String, Intent> intentMap = new HashMap<String, Intent>();
                for (Intent intent : requiredIntents) {
                    intentMap.put(intent.getName().getLocalPart(), intent);
                }
                for (String intent : serviceImplIntents) {
                    assertTrue("ComponentType for Service class " + serviceImplClass.getName()
                        + " did not contain Service Implementation intent "
                        + intent, intentMap.containsKey(intent));
                }
            }
        }

        // This should match what was specified on @Service for a Service Implementation
        // If we use these to get the Service names and we get a null Service
        // name then it would seem that wrong values were put on the @Service annotation
        // or the wrong interfaces were specified on the implements list of the class
        // statement?
        Map<String, org.apache.tuscany.sca.assembly.Service> serviceMap = new HashMap<String, org.apache.tuscany.sca.assembly.Service>();
        for (org.apache.tuscany.sca.assembly.Service service: type.getServices()) {
            serviceMap.put(service.getName(), service);
        }
        for (Class interfaceClass : serviceImplClass.getInterfaces()) {
            Requires interfaceIntentAnnotation = (Requires)interfaceClass.getAnnotation(Requires.class);
            org.apache.tuscany.sca.assembly.Service service = serviceMap.get(interfaceClass.getSimpleName());
            if (service == null) {
                fail("No service defined for interface " + interfaceClass.getSimpleName()
                    + " on Service Implementation "
                    + serviceImplClass.getName());
            }

            if (interfaceIntentAnnotation != null) {
                String[] interfaceIntents = interfaceIntentAnnotation.value();
                List<Intent> requiredIntents = service.getRequiredIntents();
                if (interfaceIntents.length > 0) {
                    if (requiredIntents == null || requiredIntents.size() == 0) {
                        fail("No Intents on the service " + service.getName());
                    }
                    Map<String, Intent> intentMap = new HashMap<String, Intent>();
                    for (Intent intent : requiredIntents) {
                        intentMap.put(intent.getName().getLocalPart(), intent);
                    }
                    for (String intent : interfaceIntents) {
                        assertTrue("Interface " + service.getName()
                            + " did not contain Service Interface intent "
                            + intent, intentMap.containsKey(intent));
                    }
                }
            }

            /*for (Method method : interfaceClass.getDeclaredMethods()) {
                Requires methodIntentAnnotation = method.getAnnotation(Requires.class);

                // Verify that each of the Intents on each of the Service
                // Interface Methods exist on their associated operation.
                if (methodIntentAnnotation != null) {
                    String[] methodIntents = methodIntentAnnotation.value();
                    if (methodIntents.length > 0) {
                        List<Intent> requiredIntents = service.getRequiredIntents();
                        if (requiredIntents.size() == 0) {
                            fail("No Intents on operation " + method.getName());
                        }
                        for (String intent : methodIntents) {
                            boolean found = false;
                            for (Intent requiredIntent: requiredIntents) {
                                if (requiredIntent.getName().getLocalPart().equals(intent)) {
                                    for (Operation operation: requiredIntent.getOperations()) {
                                        if (operation.getName().equals(method.getName())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if (found)
                                    break;
                            }
                            assertTrue("Operation " + method.getName()
                                + " did not contain Service Interface method intent "
                                + intent, found);
                        }
                    }
                }
            }
            */
            for (Method method : serviceImplClass.getDeclaredMethods()) {
                Requires methodIntentAnnotation = method.getAnnotation(Requires.class);

                // Verify that each of the Intents on each of the Service
                // Implementation Methods exist on their associated
                // operation.
                if (methodIntentAnnotation != null) {
                    String[] methodIntents = methodIntentAnnotation.value();
                    if (methodIntents.length > 0) {
                        List<Intent> requiredIntents = ((PolicySetAttachPoint)type).getRequiredIntents();
                        if (requiredIntents.size() == 0) {
                            fail("No Intents on operation " + method.getName());
                        }
                        /*for (String intent : methodIntents) {
                            boolean found = false;
                            for (Intent requiredIntent: requiredIntents) {
                                if (requiredIntent.getName().getLocalPart().equals(intent)) {
                                    for (Operation operation: requiredIntent.getOperations()) {
                                        if (operation.getName().equals(method.getName())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if (found)
                                    break;
                            }
                            assertTrue("Operation " + method.getName()
                                + " did not contain Service Interface method intent "
                                + intent, found);
                        }*/
                    }
                }
            }
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        serviceProcessor = new ServiceProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory());
        policyProcessor = new PolicyProcessor(new DefaultAssemblyFactory(), new DefaultPolicyFactory());
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        type = javaImplementationFactory.createJavaImplementation();
    }

    // @Remotable
    @Requires( {"transaction.global"})
    private interface Interface1 {
        int method1();

        int method2();

        int method3();

        int method4();
    }

    @Service(Interface1.class)
    private class Service1 implements Interface1 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }
    }

    // @Remotable
    @Requires( {"transaction.local"})
    private interface Interface2 {
        int method5();

        int method6();
    }

    @Service(interfaces = {Interface1.class, Interface2.class})
    private class Service2 implements Interface1, Interface2 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }

        public int method5() {
            return 0;
        }

        public int method6() {
            return 0;
        }
    }

    // @Remotable
    private interface Interface3 {
        int method1();

        int method2();

        int method3();

        int method4();
    }

    @Service(Interface3.class)
    @Requires( {"transaction.global"})
    private class Service3 implements Interface3 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }
    }

    // @Remotable
    private interface Interface4 {
        int method5();

        int method6();
    }

    @Service(interfaces = {Interface3.class, Interface4.class})
    @Requires( {"transaction.local"})
    private class Service4 implements Interface3, Interface4 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }

        public int method5() {
            return 0;
        }

        public int method6() {
            return 0;
        }
    }

    private interface Interface5 {
        @Requires( {"transaction.global"})
        int method1();

        @Requires( {"transaction.local"})
        int method2();
    }

    @Service(Interface5.class)
    private class Service5 implements Interface5 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }
    }

    @Requires( {"transaction.global.Interface6"})
    private interface Interface6 {
        @Requires( {"transaction.global.Interface6.method1"})
        int method1();

        @Requires( {"transaction.local.Interface6.method2"})
        int method2();
    }

    @Service(Interface6.class)
    @Requires( {"transaction.global.Service6"})
    private class Service6 implements Interface6 {
        @Requires( {"transaction.global.Service6.method1"})
        public int method1() {
            return 0;
        }

        @Requires( {"transaction.global.Service6.method1"})
        public int method2() {
            return 0;
        }
    }

}
