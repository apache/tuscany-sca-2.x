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
 * Tests "Endpoint URI resolution" for service binding.ws as per Web Services
 * Binding Specification v1.00 - Sec 2.1.1 - Lines 70 to 85.
 */
public class EndpointUriResolutionTestCase {

    protected static String compositeName = "endpoint-uri-resolution.composite";

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
     * Lines 71-78
     * <p>
     * The rules for resolving the URI at which an SCA service is hosted, or SCA
     * reference targets, when used with binding.ws (in precedence order) are:
     * 1. The URIs in the endpoint(s) of the referenced WSDL or The URI
     * specified by the wsa:Address element of the wsa:EndpointReference, 2. The
     * explicitly stated URI in the "uri" attribute of the binding.ws element,
     * which may be relative, 3. The implicit URI as defined by the Assembly
     * specification
     * <p>
     * This method tests that URI in the endpoint takes precedence.
     */
    @Test
    public void testUriInEndpoint() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent1");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 71-78
     * <p>
     * The rules for resolving the URI at which an SCA service is hosted, or SCA
     * reference targets, when used with binding.ws (in precedence order) are:
     * 1. The URIs in the endpoint(s) of the referenced WSDL or The URI
     * specified by the wsa:Address element of the wsa:EndpointReference, 2. The
     * explicitly stated URI in the "uri" attribute of the binding.ws element,
     * which may be relative, 3. The implicit URI as defined by the Assembly
     * specification
     * <p>
     * This method tests that URI in the EndpointReference takes precedence.
     */
    @Test
    public void testUriInEndpointReference() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent2");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 71-78
     * <p>
     * The rules for resolving the URI at which an SCA service is hosted, or SCA
     * reference targets, when used with binding.ws (in precedence order) are:
     * 1. The URIs in the endpoint(s) of the referenced WSDL or The URI
     * specified by the wsa:Address element of the wsa:EndpointReference, 2. The
     * explicitly stated URI in the "uri" attribute of the binding.ws element,
     * which may be relative, 3. The implicit URI as defined by the Assembly
     * specification
     * <p>
     * This method tests that explicitly stated URI in the "uri" attribute of
     * binding.ws takes precedence.
     */
    @Test
    public void testUriInBindingWs() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent3");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 71-78
     * <p>
     * The rules for resolving the URI at which an SCA service is hosted, or SCA
     * reference targets, when used with binding.ws (in precedence order) are:
     * 1. The URIs in the endpoint(s) of the referenced WSDL or The URI
     * specified by the wsa:Address element of the wsa:EndpointReference, 2. The
     * explicitly stated URI in the "uri" attribute of the binding.ws element,
     * which may be relative, 3. The implicit URI as defined by the Assembly
     * specification
     * <p>
     * This method tests that implicit URI is used.
     */
    @Ignore("TUSCANY-2523 - Port 8080 conflicts with Continuum") 
    @Test
    public void testImplicitUri() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent4");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 71-78
     * <p>
     * The rules for resolving the URI at which an SCA service is hosted, or SCA
     * reference targets, when used with binding.ws (in precedence order) are:
     * 1. The URIs in the endpoint(s) of the referenced WSDL or The URI
     * specified by the wsa:Address element of the wsa:EndpointReference, 2. The
     * explicitly stated URI in the "uri" attribute of the binding.ws element,
     * which may be relative, 3. The implicit URI as defined by the Assembly
     * specification
     * <p>
     * This method tests that explicitly stated URI in the "uri" attribute of
     * the binding.ws is used in the absence of a wsdlElement.
     */
    @Test
    public void testNoWsdlElement() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent5");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 79-83
     * <p>
     * The URI in the WSDL endpoint or in the wsa:Address of an EPR may be a
     * relative URI, in which case it is relative to the URI defined in (2) or
     * (3). The wsa:Address element can be the empty relative URI, in which case
     * it uses the URI defined in (2) or (3) directly. This allows the EPR
     * writer to specify reference parameters, metadata and other EPR contents
     * while allowing the URI to be chosen by the deployer.
     * <p>
     * This method tests that the relative URI specified in WSDL endpoint is
     * used along with the explicit URI specified in the "uri" attribute of
     * binding.ws element.
     */
    @Test
    public void testRelativeUriInWsdl() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent6");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 79-83
     * <p>
     * The URI in the WSDL endpoint or in the wsa:Address of an EPR may be a
     * relative URI, in which case it is relative to the URI defined in (2) or
     * (3). The wsa:Address element can be the empty relative URI, in which case
     * it uses the URI defined in (2) or (3) directly. This allows the EPR
     * writer to specify reference parameters, metadata and other EPR contents
     * while allowing the URI to be chosen by the deployer.
     * <p>
     * This method tests that the relative URI specified in wsa:Address is used
     * along with the explicit URI specified in the "uri" attribute of
     * binding.ws element.
     */
    @Test
    public void testRelativeUriInAddress() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent7");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    /**
     * Lines 79-83
     * <p>
     * The URI in the WSDL endpoint or in the wsa:Address of an EPR may be a
     * relative URI, in which case it is relative to the URI defined in (2) or
     * (3). The wsa:Address element can be the empty relative URI, in which case
     * it uses the URI defined in (2) or (3) directly. This allows the EPR
     * writer to specify reference parameters, metadata and other EPR contents
     * while allowing the URI to be chosen by the deployer.
     * <p>
     * This method tests that when wsa:Address is empty, the explicit URI
     * specified in the "uri" attribute of binding.ws element is used.
     */
    @Test
    public void testEmptyAddressElement() throws Exception {
        AClientService aClient = ServiceFinder.getService(AClientService.class, "AClientComponent8");
        Assert.assertEquals("Hello Pandu", aClient.getGreetingsForward("Pandu"));
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
