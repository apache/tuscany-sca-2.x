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

package org.apache.tuscany.sca.vtest.javaapi.annotations.endsconversation;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osoa.sca.ConversationEndedException;

/**
 * This test class tests the Service annotation described in section 1.2.1 and
 * 1.8.17
 */
public class EndsConversationTestCase {

    protected static String compositeName = "endsconversation.composite";
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
     * Lines 410,411,412,413:
     * <p>
     * A method of a conversational interface may be marked with an
     * "@EndsConversation" annotation. Once a method marked with
     * "@EndsConversation" has been called, the conversation between client and
     * service provider is at an end, which implies no further methods may be
     * called on that service within the *same* conversation.
     */
    @Test
    public void atEndsConversation1() throws Exception {
        aService.testAtEndsConversation();
    }

    /**
     * Lines 417,418,419,420:
     * <p>
     * From the errata: <br>
     * Solution: Replace lines 417-420 at the end of section 1.6.2.2 with the
     * following: "If a conversation is ended with an explicit outbound call to
     * an "@EndsConversation" method or a call to
     * ServiceReference.endConversation(), then any subsequent call to an
     * operation on the service reference will start a new conversation. If the
     * conversation ends for any other reason (e.g. a timeout occurred), then
     * until ServiceReference.getConversation().end() is called, the
     * ConversationEndedException will be thrown by any conversational
     * operation."
     * <p>
     * This tests the first section of the errata. Up to "start a new
     * conversation"
     */
    @Test
    public void atEndsConversation2() throws Exception {
        aService.testSREndConversation();
    }

    /**
     * Lines 417,418,419,420:
     * <p>
     * From the errata: <br>
     * Solution: Replace lines 417-420 at the end of section 1.6.2.2 with the
     * following: "If a conversation is ended with an explicit outbound call to
     * an "@EndsConversation" method or a call to
     * ServiceReference.endConversation(), then any subsequent call to an
     * operation on the service reference will start a new conversation. If the
     * conversation ends for any other reason (e.g. a timeout occurred), then
     * until ServiceReference.getConversation().end() is called, the
     * ConversationEndedException will be thrown by any conversational
     * operation."
     * <p>
     * This tests the second section of the errata. Starting with .. "If the
     * conversation ends for any other reason ..."
     */
    @Test(expected = ConversationEndedException.class)
    public void atEndsConversation3() throws Exception {
        aService.testTimedEnd();
    }
}
