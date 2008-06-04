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
package org.apache.tuscany.sca.vtest.wsbinding.nowsdl.promoted;

import junit.framework.Assert;
import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.promoted.AService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the simplest form of the binding element without WSDL document
 * described in section section 2.3.1 and 2.3.3.1 of SCA Web Services Binding
 * V1.00
 */
public class NoWsdlPromoted1TestCase {

	protected static ServiceFinder serviceFinder;
    protected static String compositeName = "nowsdlpromoted.composite";
    
    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lines 251-253, 287-291:<br>
     * <li>soap</li>
     * This indicates that a SOAP binding is required. The SOAP binding may be
     * of any SOAP version, including multiple versions.<br>
     * <br>
     * 2.3.3.1 SOAP versions<br>
     * Where a web service binding requires a specific SOAP version, then a
     * single WSDL port and SOAP binding of the appropriate version is
     * generated. Where no specific SOAP version is required, then one or more
     * WSDL ports with associated SOAP bindings may be generated, depending on
     * the level(s) supported in the target runtime.<br>
     * <p>
     * Tests promoted services and references<br>
     */
    @Test
    public void testSoapVersion1() throws Exception {
    	
    	for (int i = 0; i < 8; i++) {
    		AService a = ServiceFinder.getService(AService.class, "AComponent" + (i+1));
        	String aName = a.getName();
        	String b1Str1 = a.getB1String("string1");
        	String b1Str2 = a.getB1String2("string2", "string3");
        	int b1Int = a.getB1Int(i * 1000 + 1000);
            System.out.println(i + " - " + aName + ": " + b1Str1 + ", " + b1Str2 + ", " + b1Int);
            Assert.assertEquals("AService", aName);
            Assert.assertEquals("string1", b1Str1);
            Assert.assertEquals("string2string3", b1Str2);
            Assert.assertEquals(i * 1000 + 1000, b1Int);
    	}
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
