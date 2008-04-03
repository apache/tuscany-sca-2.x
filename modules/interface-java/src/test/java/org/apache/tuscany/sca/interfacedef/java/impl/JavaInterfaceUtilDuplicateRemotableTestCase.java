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
package org.apache.tuscany.sca.interfacedef.java.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.OverloadedOperationException;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceImpl;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.osoa.sca.annotations.Remotable;

/**
 * This test case will test that a Component that has multiple Remotable interfaces
 * that contain methods with the same name will correctly select the right method.
 * 
 * @version $Rev$ $Date$
 */
public class JavaInterfaceUtilDuplicateRemotableTestCase extends TestCase {

    /**
     * Test to get the getTime() method from the LocalTimeService
     *
     * @throws Exception Test failed
     */
    public void testLocalTimeServiceGetTime() throws Exception {
        doTestLocalTimeServiceGetTime(LocalTimeService.class);
    }

    /**
     * Test to get the getTime() method from the LocalTimeService interface from
     * the specified class
     *
     * @param timeServiceClass The class that implements the LocalTimeService
     * @throws Exception Test failed
     */
    private void doTestLocalTimeServiceGetTime(Class timeServiceClass) throws Exception {
        // Add a getTime() method
        Operation operation = newOperation("getTime", LocalTimeService.class);

        Method method = JavaInterfaceUtil.findMethod(timeServiceClass, operation);
        assertEquals("getTime", method.getName());
        assertEquals(0, method.getParameterTypes().length);
    }

    /**
     * Test to get the getTime(String) method from the WorldTimeService
     *
     * @throws Exception Test failed
     */
    public void testWorldTimeServiceGetTime() throws Exception {
        doTestWorldTimeServiceGetTime(WorldTimeService.class);
    }

    /**
     * Test to get the getTime(String) method from the WorldTimeService interface from
     * the specified class
     *
     * @param timeServiceClass The class that implements the WorldTimeService
     * @throws Exception Test failed
     */
    private void doTestWorldTimeServiceGetTime(Class timeServiceClass) throws Exception {
        // Add a getTime(String) method
        Operation operation = newOperation("getTime", WorldTimeService.class, String.class);

        Method method = JavaInterfaceUtil.findMethod(timeServiceClass, operation);
        assertEquals("getTime", method.getName());
        assertEquals(1, method.getParameterTypes().length);
        assertEquals(String.class, method.getParameterTypes()[0]);
        }

    /**
     * Test to get the getTime(int) method from the GMTTimeService
     *
     * @throws Exception Test failed
     */
    public void testGMTTimeServiceGetTime() throws Exception {
        doTestGMTTimeServiceGetTime(GMTTimeService.class);
    }

    /**
     * Test to get the getTime(int) method from the GMTTimeService interface from
     * the specified class
     *
    * @param timeServiceClass The class that implements the WorldTimeService
     * @throws Exception Test failed
     */
    private void doTestGMTTimeServiceGetTime(Class timeServiceClass) throws Exception {
        // Add a getTime(String) method
        Operation operation = newOperation("getTime", GMTTimeService.class, Integer.TYPE);

        Method method = JavaInterfaceUtil.findMethod(timeServiceClass, operation);
        assertEquals("getTime", method.getName());
        assertEquals(1, method.getParameterTypes().length);
        assertEquals(Integer.TYPE, method.getParameterTypes()[0]);
    }


    /**
     * Test to get the getTime() method from the LocalTimeService on the
     * TimeServiceImpl class
     *
     * @throws Exception Test failed
     */
    public void testLocalTimeServiceGetTimeFromTimeServiceImpl() throws Exception {
        doTestLocalTimeServiceGetTime(TimeServiceImpl.class);
    }

    /**
     * Test to get the getTime(String) method from the WorldTimeService on the
     * TimeServiceImpl class
     *
     * @throws Exception Test failed
     */
    public void testWorldTimeServiceGetTimeFromTimeServiceImpl() throws Exception {
        doTestWorldTimeServiceGetTime(TimeServiceImpl.class);
    }

    /**
     * Test to get the getTime(int) method from the GMTTimeService
     *
     * @throws Exception Test failed
     */
    public void testGMTTimeServiceGetTimeFromTimeServiceImpl() throws Exception {
        doTestGMTTimeServiceGetTime(TimeServiceImpl.class);
    }

    /**
     * Creates a new operation with the specified name and parameter types
     *
     * @param name The name of the operation
     * @param operationInterface The interface to which the operation belongs
     * @param parameterTypes The types of the parameters for this operation
     * @return An operation with the specified name and parameter types
     */
    private static Operation newOperation(String name, Class operationInterface, Class... parameterTypes) {
        // Create and set the operation name
        Operation operation = new OperationImpl();
        operation.setName(name);

        // Make the operation remotable
        Interface iface = new InterfaceImpl();
        iface.setRemotable(true);
        operation.setInterface(iface);

        // Construct the parameters
        List<DataType> types = new ArrayList<DataType>();
        DataType<List<DataType>> inputType = new DataTypeImpl<List<DataType>>(Object[].class, types);
        for (Class parameterType : parameterTypes) {
            DataType type = new DataTypeImpl<Class>(parameterType, Object.class);
            types.add(type);
        }
        operation.setInputType(inputType);

        // Return the created operation
        return operation;
    }

    /**
     * Test case that validates that a @Remotable interface with Overloaded operations
     * is detected.
     * 
     * This test case is for TUSCANY-2194
     */
    public void testDuplicateOpeartionOnRemotableInterface()
    {
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorImpl introspector = new JavaInterfaceIntrospectorImpl(javaFactory);
        JavaInterfaceImpl javaInterface = new JavaInterfaceImpl();

        try {
            introspector.introspectInterface(javaInterface, DuplicateMethodOnRemotableInterface.class);
            Assert.fail("Should have thrown an exception as @Remotable interface has overloaded methods");
        } catch (OverloadedOperationException ex) {
            // As expected
            // Make sure that the class and method names are in the exception
            String exMsg = ex.toString();
            Assert.assertTrue("Method name missing from exception", exMsg.indexOf("aDuplicateMethod") != -1);
            Assert.assertTrue("Class name missing from exception", 
                exMsg.indexOf(DuplicateMethodOnRemotableInterface.class.getName()) != -1);
        } catch (InvalidInterfaceException ex) {
            // Should have thrown OverloadedOperationException
            Assert.fail("Should have thrown an OverloadedOperationException but threw " + ex);
        }
    }


    /**
     * Sample @Remotable interface that has an overloaded operation which is not 
     * allowed according to the SCA Assembly Specification.
     */
    @Remotable
    private interface DuplicateMethodOnRemotableInterface {
        void aNonDuplicateMethod();
        void aDuplicateMethod();
        void aDuplicateMethod(String aParam);
    }


    /**
     * Sample interface needed for the unit tests
     */
    @Remotable
    private interface LocalTimeService {

        /**
         * Gets the local time
         *
         * @return The Local Time
         */
        String getTime();
    }

    /**
     * Sample interface needed for the unit tests
     */
    @Remotable
    private interface WorldTimeService {

        /**
         * Gets the time in the specified TimeZone
         *
         * @param timeZone A Time Zone
         *
         * @return The time in the specified TimeZone
         */
        String getTime(String timeZone);
    }

    /**
     * Sample interface needed for the unit tests
     */
    @Remotable
    private interface GMTTimeService {

        /**
         * Gets the time with the specified GMT offset
         *
         * @param gmtOffset A GMT offset in hours
         *
         * @return The time with the specified GMT offset
         */
        String getTime(int gmtOffset);
    }

    /**
     * Sample implementation class that implements the three @Remotable interfaces
     */
    private class TimeServiceImpl implements LocalTimeService, WorldTimeService, GMTTimeService {
        /**
         * Gets the local time
         *
         * @return The Local Time
         */
        public String getTime() {
            return "The current local time";
        }

        /**
         * Gets the time in the specified TimeZone
         *
         * @param timeZone A Time Zone
         *
         * @return The time in the specified TimeZone
         */
        public String getTime(String timeZone) {
            return "The current time in TimeZone " + timeZone;
        }

        /**
         * Gets the time with the specified GMT offset
         *
         * @param gmtOffset A GMT offset in hours
         *
         * @return The time with the specified GMT offset
         */
        public String getTime(int gmtOffset) {
            return "The current time with GMT offset of " + gmtOffset;
        }
    }
}
