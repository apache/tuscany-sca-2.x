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

package org.apache.tuscany.sca.itest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Iterator;

import java.math.BigInteger;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jaxb.props.ReturnCodeProperties;
import commonj.sdo.DataObject;

/**
 * This is a class which makes user of JUnit Framework, all tests are written using JUnit notation. These tests are used
 * to test different property values returned from the SCA Runtime Environment which is initialized with the composite
 * 'PropertyTest.composite'. It basically tests all types of property values returned from SCA runtime environment.
 */
public class PropertyTestCase {
    private static SCADomain domain;
    private static ABComponent abService;
    private static CDComponent cdService;
    private static ABCDComponent abcdService;
    private static PropertyComponent propertyService;



    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'a'
     */
    @Test
    public void testA() {
        assertEquals("a", abService.getA());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'b'
     */
    @Test
    public void testB() {
        assertEquals("b", abService.getB());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'c'
     */
    @Test
    public void testC() {
        assertEquals("c", cdService.getC());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'c'
     */
    @Test
    public void testC2() {
        assertEquals("c", cdService.getC2());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'd'
     */
    @Test
    public void testD() {
        assertEquals("d", cdService.getD());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'a'
     */
    @Test
    public void testF() {
        assertEquals("a", abService.getF());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'z'
     */
    @Test
    public void testZ() {
        assertEquals("z", abService.getZ());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 1.
     */
    @Test
    public void testIntValue() {
        assertEquals(1, abService.getIntValue());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 1.
     */
    @Test
    public void testDefaultValue() {
        assertEquals(1, abService.getIntValue());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 1.
     */
    @Test
    public void testDefaultValueOverride() {
        assertEquals(1, cdService.getOverrideValue());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'aValue'
     */
    @Test
    public void testNoSource() {
        assertEquals("aValue", cdService.getNoSource());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value
     *       'fileValue'
     */
    @Test
    public void testFileProperty() {
        assertEquals("fileValue", cdService.getFileProperty());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values 4 and
     *       'fileValueTwo' respectively.
     */
    @Test
    public void testManyValuesFileProperty() {
        Iterator<String> iterator = cdService.getManyValuesFileProperty().iterator();
        iterator.next();
        String secondValue = iterator.next();
        assertEquals(4, cdService.getManyValuesFileProperty().size());
        assertEquals("fileValueTwo", secondValue);
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values 'a' ,
     *       'b', 'c' and 'd' respectively.
     */
    @Test
    public void testABCD() {
        assertEquals("a", abcdService.getA());
        assertEquals("b", abcdService.getB());
        assertEquals("c", abcdService.getC());
        assertEquals("d", abcdService.getD());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values 'RTP'
     *       and '2006' respectively
     */
    @Test
    public void testDefaultProperty() {
        assertEquals("RTP", propertyService.getLocation());
        assertEquals("2006", propertyService.getYear());

    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values 'Apache' ,
     *       'Tuscany' and 'Java SCA' respectively .
     */
    @Test
    public void testManySimpleStringValues() {
        Iterator<String> iterator = abService.getManyStringValues().iterator();
        assertEquals("Apache", iterator.next());
        assertEquals("Tuscany", iterator.next());
        assertEquals("Java SCA", iterator.next());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values 123, 456
     *       and 789 respectively.
     */
    @Test
    public void testManySimpleIntegerValues() {
        Iterator<Integer> iterator = abService.getManyIntegers().iterator();
        assertEquals(123, iterator.next().intValue());
        assertEquals(456, iterator.next().intValue());
        assertEquals(789, iterator.next().intValue());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values
     *       'TestString_1' and 2 respectively.
     */
    @Test
    public void testComplexPropertyOne() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyOne();
        assertNotNull(propBean);
        assertEquals("TestString_1", propBean.getStringArray()[0]);
        assertEquals(2, propBean.numberSetArray[1].integerNumber);
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected integer number
     *       and a float number
     */
    @Test
    public void testComplexPropertyTwo() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyTwo();
        assertNotNull(propBean);
        assertEquals(10, propBean.intArray[0]);
        assertEquals((float)22, propBean.numberSetArray[1].floatNumber);
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected value first
     *       element in the string array and the float number
     */
    @Test
    public void testComplexPropertyThree() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyThree();
        assertNotNull(propBean);
        assertEquals("TestElementString_1", propBean.stringArray[0]);
        assertEquals((float)22, propBean.numberSetArray[1].floatNumber);
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       complex property value obtained using a service from the SCA runtime environment with the expected values
     *       1, 222.222 and 33 respectively.
     */
    @Test
    public void testComplexPropertyFour() {
        Object[] propBeanCollection = propertyService.getComplexPropertyFour().toArray();
        assertNotNull(propBeanCollection);
        assertEquals(1, ((ComplexPropertyBean)propBeanCollection[0]).getIntegerNumber());
        assertEquals(222.222, ((ComplexPropertyBean)propBeanCollection[1]).getDoubleNumber());
        assertEquals(33, ((ComplexPropertyBean)propBeanCollection[2]).getNumberSet().getIntegerNumber());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values
     *       'TestString_3', 'TestString_4', 100 and 200.
     */
    @Test
    public void testComplexPropertyFive() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyFive();
        assertNotNull(propBean);
        assertEquals("TestString_3", propBean.getStringArray()[0]);
        assertEquals("TestString_4", propBean.getStringArray()[1]);
        assertEquals(100, propBean.getIntArray()[0]);
        assertEquals(200, propBean.getIntArray()[1]);
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected string values
     *       'Firstly Name' , 'Middler Name' and 'Lasting Name' respectively
     */
    @Test
    public void testSDOProperty1() {
        DataObject dataObject = propertyService.getSdoProperty();
        assertNotNull(dataObject);
        assertEquals("Firstly Name", dataObject.get("firstName"));
        assertEquals("Middler Name", dataObject.getString("middleName"));
        assertEquals("Lasting Name", dataObject.getString("lastName"));
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected string values
     *       'Sdo Firstly Name', 'Sdo Middler Name' and 'Sdo Lasting Name' respectively
     */
    @Test
    public void testSDOProperty2() {
        DataObject dataObject = propertyService.getCustomerSdo();
        assertNotNull(dataObject);
        assertEquals("Sdo Firstly Name", dataObject.get("firstName"));
        assertEquals("Sdo Middler Name", dataObject.getString("middleName"));
        assertEquals("Sdo Lasting Name", dataObject.getString("lastName"));
    }
     

    @Test
    public void testGetLocationFromComponentContext() {
        String location = propertyService.getLocation();
        assertNotNull(location);
        String locationFromCC = propertyService.getLocationFromComponentContext();
        assertNotNull(locationFromCC);
        assertEquals(location, locationFromCC);
    }

    @Test
    public void testGetInjectedStringArrayProperty() {
        String[] daysOfWeek = propertyService.getDaysOfTheWeek();
        assertNotNull(daysOfWeek);

        String[] expected = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        Assert.assertTrue(Arrays.equals(expected, daysOfWeek));
    }

    @Test
    public void testGetInjectedIntegerArrayProperty() {
        Integer[] numbers = propertyService.getIntegerNumbers();
        assertNotNull(numbers);

        Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Assert.assertTrue(Arrays.equals(expected, numbers));
    }

    @Test
    public void testGetInjectedIntArrayProperty() {
        int[] numbers = propertyService.getIntNumbers();
        assertNotNull(numbers);

        int[] expected = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        Assert.assertTrue(Arrays.equals(expected, numbers));
    }

    @Test
    public void testGetInjectedSdoArrayProperty() {
        DataObject[] sdos = propertyService.getSdoArrayProperty();
        assertNotNull(sdos);

        for (int i = 1; i <= 3; i++) {
            DataObject dataObject = sdos[i - 1];
            assertEquals("Firstly Name " + i, dataObject.get("firstName"));
            assertEquals("Middler Name " + i, dataObject.getString("middleName"));
            assertEquals("Lasting Name " + i, dataObject.getString("lastName"));
        }
    }

    @Test
    public void testComplexJAXBProperty() {
        ReturnCodeProperties rc = propertyService.getComplexJAXBPropertyOne();
        assertEquals(10, rc.getA());
        assertEquals(new BigInteger("10"), rc.getB());
    }

    @Test
    public void testComplexJAXBPropertyFromFile() {
        ReturnCodeProperties rc = propertyService.getComplexJAXBPropertyTwo();
        System.out.println("SKSK: a =" + rc.getA());
        System.out.println("SKSK: b =" + rc.getB());
        assertEquals(20, rc.getA());
        assertEquals(new BigInteger("20"), rc.getB());
    }

    /**
     * Method annotated with
     * 
     * @BeforeClass is used for one time set Up, it executes before every tests. This method is used to create a test
     *              Embedded SCA Domain, to start the SCA Domain and to get a reference to 4 services namely 'abService'
     *              service, 'cdService' service, 'abcdService' service and 'propertyService' service
     */
    @BeforeClass
    public static void init() throws Exception {
        try {
            domain = SCADomain.newInstance("PropertyTest.composite");
        } catch (Exception e) {
            e.printStackTrace();
        }
        abService = domain.getService(ABComponent.class, "ABComponent");
        cdService = domain.getService(CDComponent.class, "CDComponent");
        abcdService = domain.getService(ABCDComponent.class, "ABCDComponent");
        propertyService = domain.getService(PropertyComponent.class, "PropertyComponent");
    }

    /**
     * Method annotated with
     * 
     * @AfterClass is used for one time Tear Down, it executes after every tests. This method is used to close the
     *             domain, close any previously opened connections etc
     */
    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
