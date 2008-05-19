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
package org.apache.tuscany.sca.vtest.wsbinding;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests wsdlElement specified on service binding.ws as per Web Services Binding
 * Specification v1.00 - Sec 2.1 - Lines 35 to 54.
 */
public class WsdlServiceTestCase {

    protected static String compositeName = "wsdlservice.composite";

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
     * Lines 38-41
     * <p>
     * Service: <WSDL-namespace-URI>#wsdl.service(<service-name>) In this case,
     * all the endpoints in the WSDL Service that have equivalent PortTypes with
     * the SCA service or reference must be available to the SCA service or
     * reference.
     */
    @Test
    @Ignore("TUSCANY-2298")
    public void testWsdlService() throws Exception {
        AClientService aClientS11 = ServiceFinder.getService(AClientService.class, "AClientS11Component");
        Assert.assertEquals("Hello Pandu", aClientS11.getGreetingsForward("Pandu"));
        AClientService aClientS12 = ServiceFinder.getService(AClientService.class, "AClientS12Component");
        Assert.assertEquals("Hello Pandu", aClientS12.getGreetingsForward("Pandu"));
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
