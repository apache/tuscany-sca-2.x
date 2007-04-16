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

import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osoa.sca.CurrentCompositeContext;

public class PropertyTestCase {
    private static ABComponent abService;
    private static CDComponent cdService;
    private static ABCDComponent abcdService;
    private static PropertyComponent propertyService;

    @Test
    public void testA() {
        assertEquals("a", abService.getA());
    }

    @Test
    public void testB() {
        assertEquals("b", abService.getB());
    }

    @Test
    public void testC() {
        assertEquals("c", cdService.getC());
    }

    @Test
    public void testC2() {
        assertEquals("c", cdService.getC2());
    }

    @Test
    public void testD() {
        assertEquals("d", cdService.getD());
    }

    @Test
    public void testF() {
        assertEquals("a", abService.getF());
    }

    @Test
    public void testZ() {
        assertEquals("z", abService.getZ());
    }

    @Test
    public void testIntValue() {
        assertEquals(1, abService.getIntValue());
    }

    @Test
    public void testDefaultValue() {
        assertEquals(1, abService.getIntValue());
    }

    @Test
    public void testDefaultValueOverride() {
        assertEquals(1, cdService.getOverrideValue());
    }

    @Test
    public void testNoSource() {
        assertEquals("aValue", cdService.getNoSource());
    }

    @Test
    public void testFileProperty() {
        assertEquals("fileValue", cdService.getFileProperty());
    }

    @Test
    public void testManyValuesFileProperty() {
        Iterator<String> iterator = cdService.getManyValuesFileProperty().iterator();
        iterator.next();
        String secondValue = iterator.next();
        assertEquals(4, cdService.getManyValuesFileProperty().size());
        assertEquals("fileValueTwo", secondValue);
    }

    @Test
    public void testABCD() {
        assertEquals("a", abcdService.getA());
        assertEquals("b", abcdService.getB());
        assertEquals("c", abcdService.getC());
        assertEquals("d", abcdService.getD());
    }

    @Test
    public void testDefaultProperty() {
        assertEquals("RTP", propertyService.getLocation());
        assertEquals("2006", propertyService.getYear());

    }

    @Test
    public void testManySimpleStringValues() {
        Iterator<String> iterator = abService.getManyStringValues().iterator();
        assertEquals("Apache", iterator.next());
        assertEquals("Tuscany", iterator.next());
        assertEquals("Java SCA", iterator.next());
    }

    @Test
    public void testManySimpleIntegerValues() {
        Iterator<Integer> iterator = abService.getManyIntegers().iterator();
        assertEquals(123, iterator.next().intValue());
        assertEquals(456, iterator.next().intValue());
        assertEquals(789, iterator.next().intValue());
    }

    @Test
    public void testComplexPropertyOne() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyOne();
        assertNotNull(propBean);
        assertEquals("TestString_1", propBean.getStringArray()[0]);
        assertEquals(2, propBean.numberSetArray[1].integerNumber);
    }

    @Test
    public void testComplexPropertyTwo() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyTwo();
        assertNotNull(propBean);
        assertEquals(10, propBean.intArray[0]);
        assertEquals((float)22, propBean.numberSetArray[1].floatNumber);
    }

    @Test
    public void testComplexPropertyThree() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyThree();
        assertNotNull(propBean);
        assertEquals("TestElementString_1", propBean.stringArray[0]);
        assertEquals((float)22, propBean.numberSetArray[1].floatNumber);
    }

    @Test
    public void testComplexPropertyFour() {
        Object[] propBeanCollection = propertyService.getComplexPropertyFour().toArray();
        assertNotNull(propBeanCollection);
        assertEquals(1, ((ComplexPropertyBean)propBeanCollection[0]).getIntegerNumber());
        assertEquals(222.222, ((ComplexPropertyBean)propBeanCollection[1]).getDoubleNumber());
        assertEquals(33, ((ComplexPropertyBean)propBeanCollection[2]).getNumberSet().getIntegerNumber());
    }

    @BeforeClass
    public static void init() throws Exception {
        SCARuntime.start("PropertyTest.composite");
        abService = CurrentCompositeContext.getContext().locateService(ABComponent.class, "ABComponent");
        cdService = CurrentCompositeContext.getContext().locateService(CDComponent.class, "CDComponent");
        abcdService = CurrentCompositeContext.getContext().locateService(ABCDComponent.class, "ABCDComponent");
        propertyService =
            CurrentCompositeContext.getContext().locateService(PropertyComponent.class, "PropertyComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        SCARuntime.stop();
    }
}
