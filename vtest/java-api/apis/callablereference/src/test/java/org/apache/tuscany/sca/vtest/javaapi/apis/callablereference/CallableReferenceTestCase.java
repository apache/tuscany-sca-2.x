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

package org.apache.tuscany.sca.vtest.javaapi.apis.callablereference;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the CallableReference interface described in 1.7.3 of
 * the SCA Java Annotations & APIs Specification 1.0.
 */
public class CallableReferenceTestCase {

    protected static String compositeName = "callablereference.composite";
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
     * Lines 884 <br>
     * getService() - Returns a type-safe reference to the target of this
     * reference. The instance returned is guaranteed to implement the business
     * interface for this reference. The value returned is a proxy to the target
     * that implements the business interface associated with this reference.
     * 
     * @throws Exception
     */
    @Test
    public void testGetService() throws Exception {
        Assert.assertEquals("ComponentB", a.getServiceName());
    }

    /**
     * Lines 885 <br>
     * getBusinessInterface() – Returns the Java class for the business
     * interface associated with this reference.
     * 
     * @throws Exception
     */
    @Test
    public void testGetBusinessInterface() throws Exception {
        Assert.assertEquals("BComponent", a.getBusinessInterfaceName());
    }

    /**
     * Lines 886 <br>
     * isConversational() – Returns true if this reference is conversational.
     * 
     * @throws Exception
     */
    @Test
    public void testIsConversational() throws Exception {
        Assert.assertEquals(true, a.isConversational());
    }

    /**
     * Lines 887 <br>
     * getConversation() – Returns the conversation associated with this
     * reference. Returns null if no conversation is currently active.
     * 
     * @throws Exception
     */
    @Test
    public void testGetConversation() throws Exception {
        a.testConversationID();
        b.testNonNullConversation();
    }

    /**
     * Lines 888 <br>
     * getCallbackID() – Returns the callback ID.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCallbackID() throws Exception {
        // Actual test is in BComponentImpl. Below is an extra test.
        Assert.assertEquals("CallBackFromB", a.getCallbackResult());
    }

}
