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

package org.apache.tuscany.sca.vtest.javaapi.apis.requestcontext;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test class tests the RequestContext interface described in 1.7.2 of the
 * SCA Java Annotations & APIs Specification 1.0.
 */
public class RequestContextTestCase {

    protected static String compositeName = "requestcontext.composite";
    protected static AComponent a;
    protected static BComponent b;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            a = ServiceFinder.getService(AComponent.class, "AComponent");
            b = ServiceFinder.getService(BComponent.class, "BComponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

    /**
     * Lines 858 <br>
     * getSecuritySubject() – Returns the JAAS Subject of the current request.
     * 
     * @throws Exception
     */
    @Ignore("TUSCANY-2608")
    public void testGetSecuritySubject() throws Exception {
        Assert.assertTrue(a.isJAASSubject());
    }

    /**
     * Lines 860 <br>
     * getServiceName() – Returns the name of the service on the Java
     * implementation the request came in on.
     * 
     * @throws Exception
     */
    @Test
    public void testGetServiceName() throws Exception {
        Assert.assertEquals("AComponent", a.getServiceName());
    }

    /**
     * Lines 861, 862 <br>
     * getCallbackReference() – Returns a callable reference to the callback as
     * specified by the caller. getCallback() – Returns a proxy for the callback
     * as specified by the caller.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCallback() throws Exception {
        Assert.assertEquals("CallBackFromB", a.getCallbackResult());
    }

    /**
     * Lines 863 <br>
     * getServiceReference() – When invoked during the execution of a service
     * operation, this API MUST return a CallableReference that represents the
     * service. When invoked during the execution of a callback operation, this
     * API MUST return a CallableReference that represents the callback service.
     * 
     * @throws Exception
     */
    @Test
    public void testGetServiceReference() throws Exception {
        Assert.assertEquals("ComponentA", a.getServiceReferenceName());
        Assert.assertEquals("ComponentB", b.getServiceReferenceName());
        Assert.assertEquals("CallBackB", a.getCallbackServiceReferenceName());
    }

}
