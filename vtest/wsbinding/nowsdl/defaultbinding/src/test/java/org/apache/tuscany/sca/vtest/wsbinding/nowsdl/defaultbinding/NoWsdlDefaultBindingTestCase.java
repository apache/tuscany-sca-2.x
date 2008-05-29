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
package org.apache.tuscany.sca.vtest.wsbinding.nowsdl.defaultbinding;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.defaultbinding.AService;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.defaultbinding.DService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the simplest form of the binding element without WSDL document
 * described in section section 2.2.2 of SCA Web Services Binding V1.00
 * 
 * @TODO - will add following tests<br>
 * <li>promoted service</li>
 * <li>with complex type object</li>
 */
public class NoWsdlDefaultBindingTestCase {

	protected static ServiceFinder serviceFinder;
    protected static String compositeName = "nowsdl.composite";
    protected static AService a;
    protected static DService d;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            a = ServiceFinder.getService(AService.class, "AComponent");
            d = ServiceFinder.getService(DService.class, "DComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lines 169-181:<br>
     * 2.2.2 Examples Without a WSDL Document<br>
     * The next example shows the simplest form of the binding element without
     * WSDL document,<br>
     * ...<br>
     * which itself uses all the defaults.<br>
     */
    @Test
    public void testNoWsdl1() throws Exception {
    	String aName = a.getName();
    	String b1Str1 = a.getB1String("string1");
    	String b1Str2 = a.getB1String2("string2", "string3");
    	int b1Int = a.getB1Int(4000);
    	
        System.out.println(aName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(4000, b1Int);
    }

    /**
     * Lines 169-181:<br>
     * Test two references using binding.ws but without WSDL documents.<br> 
     */
    @Test
    public void testNoWsdl2() throws Exception {
    	String dName = d.getName();
    	String b1Str1 = d.getB1String("string1");
    	String b1Str2 = d.getB1String2("string2", "string3");
    	String c1Str1 = d.getC1String("string5");
    	String c1Str2 = d.getC1String2("string6", "string7");
    	
    	int b1Int = d.getB1Int(4000);
    	float c1Float = d.getC1Float((float) 8.8);

    	Integer c1Integer = d.getC1Integer(new Integer(9000));

        System.out.println(dName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int + ", " + c1Str1 + ", " + c1Str2 + ", " + c1Float + ", " + c1Integer);
        Assert.assertEquals("DService", dName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(4000, b1Int);
        Assert.assertEquals("string5", c1Str1);
        Assert.assertEquals("string6string7", c1Str2);
        Assert.assertEquals((float) 8.8, c1Float);
        Assert.assertEquals(new Integer(9000), c1Integer);
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
