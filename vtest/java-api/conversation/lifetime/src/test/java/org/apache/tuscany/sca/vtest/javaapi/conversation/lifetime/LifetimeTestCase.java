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

package org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osoa.sca.ConversationEndedException;

/**
 * 
 */
public class LifetimeTestCase {

    protected static String compositeName = "lifetime.composite";
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
     * Lines 475, 476
     * <p>
     * Conversations start on the client side when one of the following occur: A
     * "@Reference" to a conversational service is injected, ... and then a
     * method of the service is called
     */
    @Test
    public void lifetime1() throws Exception {
        aService.testConversationStarted();
    }

    /**
     * Lines 477, 478, 479
     * <p>
     * Conversations start on the client side when one of the following occur
     * ... A call is made to CompositeContext.getServiceReference and then a
     * method of the service is called.
     */
    @Test
    //@Ignore("TUSCANY-2243")
    public void lifetime2() throws Exception {
        aService.testConversationStarted2();
    }

    /**
     * Line 481, 482
     * <p>
     * The client can continue an existing conversation, by: Holding the service
     * reference that was created when the conversation started
     */
    @Test
    public void lifetime3() throws Exception {
        aService.testConversationContinue();
    }

    /**
     * Line 481, 483
     * <p>
     * The client can continue an existing conversation, by: ... • Getting the
     * service reference object passed as a parameter from another service, even
     * remotely
     */
    @Test
    public void lifetime4() throws Exception {
        // aService.testConversationContinue2();
    }

    /**
     * Line 481, 484
     * <p>
     * The client can continue an existing conversation, by:<br> • Loading a
     * service reference that had been written to some form of persistent
     * storage
     */
    @Test
    public void lifetime6() throws Exception {
        aService.testConversationContinue3();
    }

    /**
     * Line 487, 488
     * <p>
     * A conversation ends, and any state associated with the conversation is
     * freed up, when: <br>
     * ...A server operation that has been annotated "@EndConveration" has been
     * called
     */
    @Test
    public void lifetime7() throws Exception {
        aService.testConversationEnd();
    }

    /**
     * Line 487, 489
     * <p>
     * A conversation ends, and any state associated with the conversation is
     * freed up, when: <br>
     * ...The server calls an "@EndsConversation" method on the "@Callback"
     * reference <br>
     */
    @Test
    public void lifetime8() throws Exception {
        aService.testConversationEnd2();
    }

    /**
     * Line 487, 490
     * <p>
     * 487 A conversation ends, and any state associated with the conversation
     * is freed up, when: <br>
     * ... The server's conversation lifetime timeout occurs
     */
    @Test
    public void lifetime9() throws Exception {
        aService.testConversationEnd3();
    }

    /**
     * Line 487, 491
     * <p>
     * A conversation ends, and any state associated with the conversation is
     * freed up, when: <br>
     * ...The client calls Conversation.end()
     */
    @Test
    public void lifetime10() throws Exception {
        aService.testConversationEnd4();
    }

    /**
     * Line 487, 492
     * <p>
     * A conversation ends, and any state associated with the conversation is
     * freed up, when: <br>
     * ...Any non-business exception is thrown by a conversational operation
     */
    @Test
    //@Ignore("TUSCANY-2283")
    public void lifetime11() throws Exception {
        aService.testConversationEnd5();
        aService.testConversationEnd9();
    }

    /**
     * Line 494, 495
     * <p>
     * If a method is invoked on a service reference after an
     * "@EndsConversation" method has been called then a new conversation will
     * automatically be started.
     */
    @Test
    public void lifetime12() throws Exception {
        aService.testConversationEnd6();
    }

    /**
     * Line 495, 496, 497
     * <p>
     * If ServiceReference.getConversationID() is called after the
     * "@EndsConversation" method: is called, but before the next conversation
     * has been started, it will return null.
     */
    @Test
    public void lifetime13() throws Exception {
        aService.testConversationEnd7();
    }

    /**
     * Line 498, 499
     * <p>
     * If a service reference is used after the service provider's conversation
     * timeout has caused the conversation to be ended, then
     * ConversationEndedException will be thrown.
     */
    @Test(expected = ConversationEndedException.class)
    public void lifetime14() throws Exception {
        aService.testConversationEnd8();
    }

}
