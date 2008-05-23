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

package org.apache.tuscany.sca.vtest.javaapi.annotations.conversational;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the Service annotation described in section 1.2.1 and
 * 1.8.17
 */
public class ConversationAnnotationTestCase {

    protected static String compositeName = "conversation.composite";
    protected static AService aService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            aService = ServiceFinder.getService(AService.class, "AComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();

    }

    /**
     * Line 328:<br>
     * <p>
     * When "@Conversational" is not specified on a service interface, the
     * service contract is stateless.<br>
     * <p>
     * Line 394, 395: <br>
     * A service may be declared as conversational by marking its Java interface
     * with "@Conversational". If a service interface is not marked with
     * "@Conversational", it is stateless. <br>
     * <p>
     * BService has no "@Conversation" annotation so communication from A-> is
     * stateless
     */
    @Test
    public void atConversation1() throws Exception {
        String thisState = "This State";
        Assert.assertNotSame(thisState, aService.setThenGetB1State(thisState));
    }

    /**
     * Line 325-327:<br>
     * <p>
     * Java service interfaces may be annotated to specify whether their
     * contract is conversational as described in the Assembly Specification
     * by using the "@Conversational" annotation. A conversational service
     * indicates that requests to the service are correlated in some way
     */
    @Test
    public void atConversation2() throws Exception {
        String thisState = "This State";
        Assert.assertSame(thisState, aService.setThenGetB2State(thisState));
    }

}
