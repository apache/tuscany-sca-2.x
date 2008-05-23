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

package org.apache.tuscany.sca.vtest.javaapi.annotations.property;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test class tests the Property annotation described in section 1.2.3
 * including 1.8.5 and 1.8.13
 */
public class PropertyAnnotationTestCase {

    protected static String compositeName = "property.composite";
    protected static AService aService = null;
    protected static CService cService1 = null;
    protected static CService cService2 = null;
    protected static CService cService3 = null;
    protected static CService cService4 = null;
    protected static CService cService5 = null;
    protected static CService cService6 = null;
    protected static AnotherAService anotherAService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            aService = ServiceFinder.getService(AService.class, "AComponent");
            cService1 = ServiceFinder.getService(CService.class, "CComponent1");
            cService2 = ServiceFinder.getService(CService.class, "CComponent2");
            cService3 = ServiceFinder.getService(CService.class, "CComponent3");
            cService4 = ServiceFinder.getService(CService.class, "CComponent4");
            cService5 = ServiceFinder.getService(CService.class, "CComponent5");
            cService6 = ServiceFinder.getService(CService.class, "CComponent6");
            anotherAService = ServiceFinder.getService(AnotherAService.class, "AnotherAComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();

    }

    /**
     * Lines 1343 to 1348:<br>
     * The "@Property" annotation type is used to annotate a Java class field or
     * a setter method that is used to inject an SCA property value. The type of
     * the property injected, which can be a simple Java type or a complex Java
     * type, is defined by the type of the Java class field or the type of the
     * setter method input argument.<br>
     * The "@Property" annotation may be used on protected or public fields and
     * on setter methods or on a constructor method.<br>
     * <p>
     * p1 - simple Java type injected via field<br>
     * p2 - simple Java type injected via field<br>
     * p3 - simple Java type injected via setter<br>
     * p4 - simple Java type injected via setter and required=true<br>
     * p5 - simple Java type injected via constructor parameter<br>
     * p6 - simple Java type injected via constructor parameter<br>
     * p7 - complex Java type injected via field and required=true<br>
     * p8 - complex Java type injected via field<br>
     * p9 - complex Java type injected via setter<br>
     * p10 - complex Java type injected via setter<br>
     * p11 - complex Java type injected via constructor parameter<br>
     * p12 - complex Java type injected via constructor parameter<br>
     */
    @Test
    public void atProperty1() throws Exception {
        Assert.assertEquals("p1", aService.getP1());
        Assert.assertEquals("p2", aService.getP2());
        Assert.assertEquals("p3", aService.getP3());
        Assert.assertEquals("p4", aService.getP4());
        Assert.assertEquals("p5", aService.getP5());
        Assert.assertEquals("p6", aService.getP6());
        Assert.assertEquals("p7.aString", aService.getP7AString());
        Assert.assertEquals(7, aService.getP7BInt());
        Assert.assertEquals("p8.aString", aService.getP8AString());
        Assert.assertEquals(8, aService.getP8BInt());
        Assert.assertEquals("p9.aString", aService.getP9AString());
        Assert.assertEquals(9, aService.getP9BInt());
        Assert.assertEquals("p10.aString", aService.getP10AString());
        Assert.assertEquals(10, aService.getP10BInt());
        Assert.assertEquals("p11.aString", aService.getP11AString());
        Assert.assertEquals(11, aService.getP11BInt());
        Assert.assertEquals("p12.aString", aService.getP12AString());
        Assert.assertEquals(12, aService.getP12BInt());
    }

    /**
     * Lines 1349 to 1352:<br>
     * Properties may also be injected via public setter methods even when the
     * "@Property" annotation is not present. However, the
     * 
     * @Property annotation must be used in order to inject a property onto a
     *           non-public field. In the case where there is no "@Property"
     *           annotation, the name of the property is the same as the name of
     *           the field or setter.<br>
     *           <p>
     *           p13 is an un-annotated public field which should be injected
     *           via field<br>
     */
    @Test
    public void atProperty2() throws Exception {
        Assert.assertEquals("p13", anotherAService.getP13());
    }

    /**
     * Line 1353:<br>
     * Where there is both a setter method and a field for a property, the
     * setter method is used.<br>
     * <p>
     * p14 is an un-annotated public field, it should be injected via public
     * setter<br>
     */
    @Test
    public void atProperty3() throws Exception {
        Assert.assertEquals("p14", anotherAService.getP14());
        Assert.assertTrue(anotherAService.getP14SetterIsCalled());
    }

    /**
     * Lines 1355 to 1357:<br>
     * The "@Property" annotation has the following attributes:<br>
     * <li>name (optional) – the name of the property, defaults to the name of
     * the field of the Java class</li>
     * <li>required (optional) – specifies whether injection is required,
     * defaults to false</li>
     * <p>
     * p15 - injected via field with different name "pFifteen"<br>
     * p16 - injected via setter with different name "pSixteen"<br>
     * p17 - injected via field but not defined in composite<br>
     * p18 - injected via setter but not defined in composite<br>
     * 
     * @TODO - Need to test required=true but not defined in composite (The
     *       specification does not describe the proper behaviour in this
     *       situation.)
     */
    @Test
    public void atProperty4() throws Exception {
        Assert.assertEquals("p15", aService.getP15());
        Assert.assertEquals("p16", aService.getP16());
        Assert.assertNull(aService.getP17());
        Assert.assertNull(aService.getP18());
    }

    /**
     * Lines 1369 to 1370:<br>
     * If the property is defined as an array or as a java.util.Collection, then
     * the implied component type has a property with a many attribute set to
     * true.<br>
     * <p>
     * p19 - a List and injected via field with no element<br>
     * p20 - a List and injected via setter<br>
     * p21 - an array and injected via field<br>
     */
    @Test
    public void atProperty5() throws Exception {
        Assert.assertEquals(0, aService.getP19Size());
        Assert.assertEquals(1, aService.getP20Size());
        Assert.assertEquals("p20", aService.getP20(0));
        Assert.assertEquals(3, aService.getP21Size());
        Assert.assertEquals(2, aService.getP21(0));
        Assert.assertEquals(1, aService.getP21(1));
        Assert.assertEquals(21, aService.getP21(2));
    }

    /**
     * Lines 1141 to 1162:<br>
     * 1.8.5. "@Constructor"<br>
     * ...<br>
     * The "@Constructor" annotation is used to mark a particular constructor to
     * use when instantiating a Java component implementation.<br>
     * The "@Constructor" annotation has the following attribute:<br>
     * <li>value (optional) – identifies the property/reference names that
     * correspond to each of the constructor arguments. The position in the
     * array determines which of the arguments are being named.</li>
     * <p>
     * cService1 - "@Constructor" without value and constructor arguments<br>
     * cService2 - "@Constructor" without value but with constructor arguments<br>
     * cService3 - "@Constructor" with values and constructor arguments<br>
     * cService4 - "@Constructor" with values and constructor arguments where
     * value, property and parameter names are same<br>
     * cService5 - "@Constructor" with switched values and constructor arguments<br>
     * cService6 - "@Constructor" with wrong values<br>
     */
    @Test
    public void atProperty6() throws Exception {
        Assert.assertNull(cService1.getB1Name());
        Assert.assertNull(cService1.getP2());
        Assert.assertEquals(0, cService1.getP3());
        Assert.assertNull(cService1.getP4());
        Assert.assertEquals("NoArgument", cService1.getConstructor());

        Assert.assertEquals("BService", cService2.getB1Name());
        Assert.assertEquals("p2", cService2.getP2());
        Assert.assertEquals(3, cService2.getP3());
        Assert.assertEquals("p4", cService2.getP4());
        Assert.assertEquals("AllArguments", cService2.getConstructor());

        Assert.assertEquals("BService", cService3.getB1Name());
        Assert.assertEquals("p2", cService3.getP2());
        Assert.assertEquals(3, cService3.getP3());
        Assert.assertEquals("p4", cService3.getP4());
        Assert.assertEquals("AllArguments", cService3.getConstructor());

        Assert.assertEquals("BService", cService4.getB1Name());
        Assert.assertEquals("p2", cService4.getP2());
        Assert.assertEquals(3, cService4.getP3());
        Assert.assertEquals("p4", cService4.getP4());
        Assert.assertEquals("AllArguments", cService4.getConstructor());

        Assert.assertEquals("BService", cService5.getB1Name());
        Assert.assertEquals("p4", cService5.getP2());
        Assert.assertEquals(3, cService5.getP3());
        Assert.assertEquals("p2", cService5.getP4());
        Assert.assertEquals("SwitchedValues", cService5.getConstructor());

        try {
            System.out.println(cService6.getB1Name());
            fail("Should have failed to call this service");
        } catch (Throwable t) {
        }

    }

    /**
     * Lines 1349 to 1352:<br>
     * 1.8.13. "@Property"<br>
     * ...<br>
     * Properties may also be injected via public setter methods even when the
     * "@Property" annotation is not present. However, the "@Property"
     * annotation must be used in order to inject a property onto a non-public
     * field. In the case where there is no "@Property" annotation, the name of
     * the property is the same as the name of the field or setter.
     * <p>
     * p22 is unannotated protected field which should not be injected p23 is
     * un-annotated protected which should not be injected via protected setter<br>
     */
    @Test
    @Ignore("JIRA-2289 - p23 failed")
    public void atProperty7() throws Exception {
        Assert.assertNull(anotherAService.getP22());
        Assert.assertNull(anotherAService.getP23());
        Assert.assertFalse(anotherAService.getP23SetterIsCalled());
    }

    /**
     * Lines 1349 to 1352:<br>
     * 1.8.13. "@Property"<br>
     * ...<br>
     * Properties may also be injected via public setter methods even when the
     * "@Property" annotation is not present. However, the "@Property"
     * annotation must be used in order to inject a property onto a non-public
     * field. In the case where there is no "@Property" annotation, the name of
     * the property is the same as the name of the field or setter.
     * <p>
     * p24 is un-annotated protected field which should be injected via public
     * setter<br>
     * p25 is un-annotated private field which should be injected via public
     * setter<br>
     */
    @Test
    public void atProperty8() throws Exception {
        Assert.assertEquals("p24", anotherAService.getP24());
        Assert.assertTrue(anotherAService.getP24SetterIsCalled());
        Assert.assertEquals("p25", anotherAService.getP25());
        Assert.assertTrue(anotherAService.getP25SetterIsCalled());
    }

}
