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
package org.apache.tuscany.sca.vtest.wsbinding.wsdlelement.port;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.apache.tuscany.sca.vtest.wsbinding.wsdlelement.port.AService;
import org.apache.tuscany.sca.vtest.wsbinding.wsdlelement.port.DService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the simplest form of the binding element wsdl.port with WSDL 
 * document described in section section 2.1 of SCA Web Services Binding
 * V1.00<br>
 */
public class WsdlElementPortTestCase {

	protected static ServiceFinder serviceFinder;
    protected static String compositeName = "wsdlelement.port.composite";
    protected static AService a1;
    protected static AService a2;
    protected static AService a3;
    protected static AService a4;
    protected static DService d;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            a1 = ServiceFinder.getService(AService.class, "AComponent1");
            a2 = ServiceFinder.getService(AService.class, "AComponent2");
            a3 = ServiceFinder.getService(AService.class, "AComponent3");
            a4 = ServiceFinder.getService(AService.class, "AComponent4");
            d = ServiceFinder.getService(DService.class, "DComponent1");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lines 42-45:<br>
     * Port (WSDL 1.1):<br>
     * <WSDL-namespace-URI>#wsdl.port(<service-name>/<port-name>)
     * In this case, the identified port in the WSDL 1.1 Service must have an
     * equivalent PortType with the SCA service or reference.<br>
     * <br>
     * Test wsdl.port(BService/BService1SOAP)<br>
     */
    @Test
    public void testWsdlElementPort1() throws Exception {
    	String aName = a1.getName();
    	String b1Str1 = a1.getB1String("string1");
    	String b1Str2 = a1.getB1String2("string2", "string3");
    	int b1Int = a1.getB1Int(1000);
    	
        System.out.println(aName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(1000, b1Int);
    }

    /**
     * Lines 42-45:<br>
     * Port (WSDL 1.1):<br>
     * <br>
     * Tests wsdl.port(BService/BService2SOAP) with BService where the port
     * does not define getString2, so exceptions are expected.<br>
     */
    @Test
    public void testWsdlElementPort2() throws Exception {
        String aName = a2.getName();
        System.out.println(aName + ": ");
    	try {
        	a2.getB1String("string1");
    		fail("Should not be able to call getString");
    	} catch (Exception e) {
            System.out.println("  Exception is expected: " + e.toString());
    	}
    	try {
    		a2.getB1String2("string2", "string3");
    		fail("Should not be able to call getString2");
    	} catch (Exception e) {
    		System.out.println("  Exception is expected: " + e.toString());
    	}
    	try {
        	a2.getB1Int(2000);
    		fail("Should not be able to call getInt");
    	} catch (Exception e) {
    		System.out.println("  Exception is expected: " + e.toString());
    	}
    }

    /**
     * Lines 42-45:<br>
     * Port (WSDL 1.1):<br>
     * <br>
     * Tests wsdl.port(BService/BService2SOAP) with BService2<br>
     */
    @Test
    public void testWsdlElementPort3() throws Exception {
    	String aName = a3.getName();
    	String b2Str1 = a3.getB2String("string1");
    	int b2Int = a3.getB2Int(1000);
    	
        System.out.println(aName + ": " + b2Str1 + ", " + b2Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b2Str1);
        Assert.assertEquals(1000, b2Int);
    }

    /**
     * Lines 42-45:<br>
     * Port (WSDL 1.1):<br>
     * <br>
=     * Tests two references using wsdl.port.<br> 
     */
    @Test
    public void testWsdlElementPort4() throws Exception {
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

    /**
     * Lines 42-45:<br>
     * Port (WSDL 1.1):<br>
     * <br>
=     * Tests wsdl.port(BService/BService3SOAP12) which uses SOAP 1.2<br> 
     */
    @Test
    public void testWsdlElementPort5() throws Exception {
    	String aName = a4.getName();
    	String b1Str1 = a4.getB1String("string1");
    	String b1Str2 = a4.getB1String2("string2", "string3");
    	int b1Int = a4.getB1Int(5000);
    	
        System.out.println(aName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(5000, b1Int);
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
