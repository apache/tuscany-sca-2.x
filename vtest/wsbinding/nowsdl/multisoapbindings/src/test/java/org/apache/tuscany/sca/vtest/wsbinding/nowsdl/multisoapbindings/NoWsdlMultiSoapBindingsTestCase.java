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
package org.apache.tuscany.sca.vtest.wsbinding.nowsdl.multisoapbindings;

import junit.framework.Assert;
import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.multisoapbindings.AService;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.multisoapbindings.DService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the simplest form of the binding element without WSDL document
 * described in section section 2.2.2 and 2.3.2 of SCA Web Services
 * Binding V1.00
 */
public class NoWsdlMultiSoapBindingsTestCase {

    protected static String compositeName = "nowsdlmultisoapbindings.composite";
    protected static AService a1;
    protected static AService a2;
    protected static AService a3;
    protected static AService a4;
    protected static DService d1;
    protected static DService d2;
    protected static DService d3;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            a1 = ServiceFinder.getService(AService.class, "AComponent1");
            a2 = ServiceFinder.getService(AService.class, "AComponent2");
            a3 = ServiceFinder.getService(AService.class, "AComponent3");
            a4 = ServiceFinder.getService(AService.class, "AComponent4");
            d1 = ServiceFinder.getService(DService.class, "DComponent1");
            d2 = ServiceFinder.getService(DService.class, "DComponent2");
            d3 = ServiceFinder.getService(DService.class, "DComponent3");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lines 201-225:<br>
     * The next example shows the use of the binding element without a WSDL
     * document, with multiple SOAP bindings with non-default values. The SOAP 
     * 1.2 binding name defaults to the service name, the SOAP 1.1 binding is 
     * given an explicit name. The reference has a web service binding which 
     * uses SOAP 1.2, but otherwise uses all the defaults for SOAP binding. 
     * The reference binding name defaults to the reference name.<p>
     * Line 253:<br>
     * <li>soap</li>
     * ...<br>
     * version, including multiple versions.<br>
     * <p>
     * AComponent1 - no requires<br>
     * AComponent2 - requires = soap<br>
     * AComponent3 - requires = soap.1_1<br>
     * AComponent4 - requires = soap.1_2<br>
     */
    @Test
    public void testMultiSoapBindings1() throws Exception {

    	String aName = a1.getName();
    	String b1Str1 = a1.getB1String("string1");
    	String b1Str2 = a1.getB1String2("string2", "string3");
    	int b1Int = a1.getB1Int(1000);
    	
    	System.out.println("AComponent1: " + aName + ", " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(1000, b1Int);

    	aName = a2.getName();
    	b1Str1 = a2.getB1String("string1");
    	b1Str2 = a2.getB1String2("string2", "string3");
    	b1Int = a2.getB1Int(2000);

    	System.out.println("AComponent2: " + aName + ", " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(2000, b1Int);

    	aName = a3.getName();
    	b1Str1 = a3.getB1String("string1");
    	b1Str2 = a3.getB1String2("string2", "string3");
    	b1Int = a3.getB1Int(3000);

    	System.out.println("AComponent3: " + aName + ", " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(3000, b1Int);

    	aName = a4.getName();
    	b1Str1 = a4.getB1String("string1");
    	b1Str2 = a4.getB1String2("string2", "string3");
    	b1Int = a4.getB1Int(4000);

    	System.out.println("AComponent4: " + aName + ", " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(4000, b1Int);

    }

    /**
     * Lines 201-225, 262-264:<br>
     * The WSDL service has one or more ports for each web service binding on
     * the SCA service that has a SOAP requirement, or that refers to an
     * existing WSDL binding, depending on the requirements of the web service
     * binding. Each of those ports has a single binding.<br>
     * <p>
     * Test multiple SOAP bindings with two references which use different versions.<br>
     */
    @Test
    public void testMultiSoapBindings2() throws Exception {
    	
    	String dName = d1.getName();
    	String b1Str1 = d1.getB1String("string1");
    	String b1Str2 = d1.getB1String2("string2", "string3");
    	String c1Str1 = d1.getC1String("string5");
    	String c1Str2 = d1.getC1String2("string6", "string7");
    	int b1Int = d1.getB1Int(4000);
    	float c1Float = d1.getC1Float((float) 8.8);
    	Integer c1Integer = d1.getC1Integer(new Integer(9001));

        System.out.println(dName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int + ", " + c1Str1 + ", " + c1Str2 + ", " + c1Float + ", " + c1Integer);
        Assert.assertEquals("DService", dName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(4000, b1Int);
        Assert.assertEquals("string5", c1Str1);
        Assert.assertEquals("string6string7", c1Str2);
        Assert.assertEquals((float) 8.8, c1Float);
        Assert.assertEquals(new Integer(9001), c1Integer);
        
    	dName     = d2.getName();
    	b1Str1    = d2.getB1String("string1");
    	b1Str2    = d2.getB1String2("string2", "string3");
    	c1Str1    = d2.getC1String("string5");
    	c1Str2    = d2.getC1String2("string6", "string7");
    	b1Int     = d2.getB1Int(4000);
    	c1Float   = d2.getC1Float((float) 8.8);
    	c1Integer = d2.getC1Integer(new Integer(9002));

        System.out.println(dName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int + ", " + c1Str1 + ", " + c1Str2 + ", " + c1Float + ", " + c1Integer);
        Assert.assertEquals("DService", dName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(4000, b1Int);
        Assert.assertEquals("string5", c1Str1);
        Assert.assertEquals("string6string7", c1Str2);
        Assert.assertEquals((float) 8.8, c1Float);
        Assert.assertEquals(new Integer(9002), c1Integer);
    }

    /**
     * Lines 201-225, 262-264:<br>
     * Test multiple SOAP bindings with promoted services and references.<br>
     */
    @Test
    public void testMultiSoapBindings3() throws Exception {
    	
    	String dName = d3.getName();
    	String b1Str1 = d3.getB1String("string1");
    	String b1Str2 = d3.getB1String2("string2", "string3");
    	String c1Str1 = d3.getC1String("string5");
    	String c1Str2 = d3.getC1String2("string6", "string7");
    	int b1Int = d3.getB1Int(4000);
    	float c1Float = d3.getC1Float((float) 8.8);
    	Integer c1Integer = d3.getC1Integer(new Integer(9003));

        System.out.println(dName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int + ", " + c1Str1 + ", " + c1Str2 + ", " + c1Float + ", " + c1Integer);
        Assert.assertEquals("DService", dName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(4000, b1Int);
        Assert.assertEquals("string5", c1Str1);
        Assert.assertEquals("string6string7", c1Str2);
        Assert.assertEquals((float) 8.8, c1Float);
        Assert.assertEquals(new Integer(9003), c1Integer);
        
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
