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

package org.apache.tuscany.sca.vtest.javaapi.apis.exception;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test class tests the Exceptions described in 1.7.6, 1.7.7, 1.7.8, and 1.7.9 of the SCA Java Annotations & APIs Specification 1.0.
 * This also covers 1.5 of the specification. 
 */
public class ExceptionTestCase {

    protected static SCADomain domain;
    protected static String compositeName = "exception.composite";
    protected static AComponent a;
    protected static BComponent b;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            domain = SCADomain.newInstance(compositeName);
            a = domain.getService(AComponent.class, "AComponent");
            b = domain.getService(BComponent.class, "BComponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        if (domain != null) {
            domain.close();
        }
    }

    /**
     * Lines 951 <br>
     * NoRegisteredCallbackException.
     *  
     * @throws Exception
     */
    @Test
    public void testNoRegisteredCallbackException() throws Exception {
        a.testCallBack();
    }

    /**
     * Lines 960 <br>
     * ServiceRuntimeException - This exception signals problems in the management of SCA component execution.
     * 
     * @throws Exception
     */
    @Test
    public void testServiceRuntimeException() throws Exception {
        Assert.assertTrue(a.testServiceRuntimeException());        
    }

    /**
     * Lines 970 <br>
     * ServiceUnavailableException – This exception signals problems in the interaction with remote services.
     * 
     * @throws Exception
     */
    @Test
    //@Ignore
    public void testServiceUnavailableException() throws Exception {
        domain.getComponentManager().stopComponent("AComponent");

        try {
            a.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AComponent ac = domain.getService(AComponent.class, "AComponent");
            ac.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        domain.getComponentManager().startComponent("AComponent");
    }

    /**
     * Lines 983 <br>
     * ConversationEndedException.
     * 
     * @throws Exception
     */
    @Test
    public void testConversationEndedException() throws Exception {
        a.testConversation();
    }

    /**
     * Lines 360-361 <br>
     * Business exceptions are thrown by the implementation of the called service method,
     * and are defined as checked exceptions on the interface that types the service.
     * 
     * @throws Exception
     */
    @Test
    public void testCheckedException() throws Exception {
        Assert.assertTrue(a.testCheckedException());
    }

}
