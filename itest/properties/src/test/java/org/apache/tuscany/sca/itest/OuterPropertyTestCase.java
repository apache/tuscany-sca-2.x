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

import java.util.Iterator;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is a class which makes user of JUnit Framework, all tests are written using JUnit notation. These tests are used
 * to test different property values returned from the SCA Runtime Environment which is initialized with the composite
 * 'OuterPropertyTest.composite'. It basically tests the Simple type of property and Complex type of property from the
 * SCA Runtime Environment.
 */
public class OuterPropertyTestCase {

    private static SCADomain domain;
    private static ABComponent outerABService;

    /**
     * Method annotated with
     * 
     * @BeforeClass is used for one time set Up, it executes before every tests. This method is used to create a test
     *              Embedded SCA Domain, to start the SCA Domain and to get a reference to the 'outerABService' service
     */
    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("OuterPropertyTest.composite");
        outerABService = domain.getService(ABComponent.class, "OuterComponent");
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

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'Overriden
     *       A'
     */
    @Test
    public void testOverridenA() {
        assertEquals("Overriden A", outerABService.getA());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'Overriden
     *       B'
     */
    @Test
    public void testOverridenB() {
        assertEquals("Overriden B", outerABService.getB());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'Overriden
     *       A'
     */
    @Test
    public void testOverridenF() {
        assertEquals("Overriden A", outerABService.getF());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 'Overriden
     *       Z'
     */
    @Test
    public void testOverridenZ() {
        assertEquals("Overriden Z", outerABService.getZ());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 125
     */
    @Test
    public void testOverridenIntValue() {
        assertEquals(125, outerABService.getIntValue());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using a service from the SCA runtime environment with the expected value 125
     */
    @Test
    public void testDefaultValue() {
        assertEquals(125, outerABService.getIntValue());
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
        Iterator<String> iterator = outerABService.getManyStringValues().iterator();
        assertEquals("Apache", iterator.next());
        assertEquals("Tuscany", iterator.next());
        assertEquals("Java SCA", iterator.next());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property values obtained using a service from the SCA runtime environment with the expected values 123, 456
     *       and 789
     */
    @Test
    public void testManySimpleIntegerValues() {
        Iterator<Integer> iterator = outerABService.getManyIntegers().iterator();
        assertEquals(123, iterator.next().intValue());
        assertEquals(456, iterator.next().intValue());
        assertEquals(789, iterator.next().intValue());
    }
}
