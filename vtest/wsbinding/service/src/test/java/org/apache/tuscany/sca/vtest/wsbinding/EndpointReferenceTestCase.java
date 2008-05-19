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
import org.junit.Test;

/**
 * Tests EndpointReference element specified in service binding.ws as per Web
 * Services Binding Specification v1.00 - Sec 2.1 - Lines 61 to 65.
 */
public class EndpointReferenceTestCase {

    protected static String compositeName = "endpointreference.composite";

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
     * Lines 61-65
     * <p>
     * /binding.ws/wsa:EndpointReference – optional WS-Addressing [6]
     * EndpointReference that specifies the endpoint for the service or
     * reference. When this element is present along with the wsdlElement
     * attribute on the parent element, the wsdlElement attribute value MUST be
     * of the ‘Binding’ form as specified above, i.e. <WSDL-namespace-
     * URI>#wsdl.binding(<binding-name>).
     */
    @Test
    public void testWsdlBinding() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
