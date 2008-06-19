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
package org.apache.tuscany.sca.vtest.wsbinding.wsdlelement.service;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.apache.tuscany.sca.vtest.wsbinding.wsdlelement.service.AService;
import org.apache.tuscany.sca.vtest.wsbinding.wsdlelement.service.DService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the simplest form of the binding element wsdl.service with WSDL 
 * document described in section section 2.1 of SCA Web Services Binding
 * V1.00<br>
 */
public class WsdlElementServiceTestCase {

	protected static ServiceFinder serviceFinder;
    protected static String compositeName = "wsdlelement.service.composite";
    protected static AService a1;
    protected static AService a2;
    protected static AService a3;
    protected static AService a4;
    protected static AService a5;
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
            a5 = ServiceFinder.getService(AService.class, "AComponent5");
            d = ServiceFinder.getService(DService.class, "DComponent1");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lines 38-41:<br>
     * Service:<br>
     * <WSDL-namespace-URI>#wsdl.service(<service-name>)<br>
     * In this case, all the endpoints in the WSDL Service that have equivalent
     * PortTypes with the SCA service or reference must be available to the SCA
     * service or reference.<br>
     * <br>
     * Tests wsdl.service(BService)<br>
     */
    @Test
    public void testWsdlElementService1() throws Exception {
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
     * Lines 38-41:<br>
     * Service:<br>
     * <br>
     * Tests wsdl.service(BService2) with with BService where the service
     * does not define getString2, so exceptions are expected.<br>
     */
    @Test
    public void testWsdlElementService2() throws Exception {
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
     * Lines 38-41:<br>
     * Service:<br>
     * <br>
     * Tests wsdl.service(BService2) with BService2<br>
     */
    @Test
    public void testWsdlElementService3() throws Exception {
    	String aName = a3.getName();
    	String b2Str1 = a3.getB2String("string1");
    	int b2Int = a3.getB2Int(1000);
    	
        System.out.println(aName + ": " + b2Str1 + ", " + b2Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b2Str1);
        Assert.assertEquals(1000, b2Int);
    }

    /**
     * Lines 38-41:<br>
     * Service:<br>
     * <br>
     * Tests two references using wsdl.service.<br> 
     */
    @Test
    public void testWsdlElementService4() throws Exception {
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
     * Lines 38-41:<br>
     * Service:<br>
     * <br>
     * Tests mixed bindings of using wsdl.port, wsdl.service, wsdl.binding,
     * and soap 1.2
     * <br> 
     */
    @Test
    public void testWsdlElementService5() throws Exception {
    	String aName = a4.getName();
    	String b1Str1 = a4.getB1String("port");
    	String b1Str2 = a4.getB1String2("string1", "string2");
    	int b1Int = a4.getB1Int(1000);
        System.out.println(aName);
        System.out.println("  b1: " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("port", b1Str1);
        Assert.assertEquals("string1string2", b1Str2);
        Assert.assertEquals(1000, b1Int);

    	String b2Str1 = a4.getB2String("service");
    	int b2Int = a4.getB2Int(2000);
        System.out.println("  b2: " + b2Str1 + ", " + b2Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("service", b2Str1);
        Assert.assertEquals(2000, b2Int);

    	String b3Str1 = a4.getB3String("binding");
    	int b3Int = a4.getB3Int(3000);
        System.out.println("  b3: " + b3Str1 + ", " + b3Int);
        Assert.assertEquals("binding", b3Str1);
        Assert.assertEquals(3000, b3Int);
        
    	String b4Str1 = a4.getB4String("soap12");
    	String b4Str2 = a4.getB4String2("string3", "string4");
    	int b4Int = a4.getB4Int(4000);
        System.out.println("  b4: " + b4Str1 + ", " + b4Str2 + ", " + b4Int);
        Assert.assertEquals("soap12", b4Str1);
        Assert.assertEquals("string3string4", b4Str2);
        Assert.assertEquals(4000, b4Int);

    }

    /**
     * Lines 38-41:<br>
     * Service:<br>
     * <br>
     * Tests wsdl.service(BService3) which uses SOAP 1.2<br>
     */
    @Test
    public void testWsdlElementService6() throws Exception {
    	String aName = a5.getName();
    	String b1Str1 = a5.getB1String("string1");
    	String b1Str2 = a5.getB1String2("string2", "string3");
    	int b1Int = a5.getB1Int(6000);
    	
        System.out.println(aName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
        Assert.assertEquals("AService", aName);
        Assert.assertEquals("string1", b1Str1);
        Assert.assertEquals("string2string3", b1Str2);
        Assert.assertEquals(6000, b1Int);
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
