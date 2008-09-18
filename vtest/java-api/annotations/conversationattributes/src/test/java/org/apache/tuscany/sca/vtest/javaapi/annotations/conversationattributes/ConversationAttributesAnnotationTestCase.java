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

package org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osoa.sca.ConversationEndedException;

/**
 * This test class tests the Service annotation described in section 1.2.1 and
 * 1.8.17
 */
public class ConversationAttributesAnnotationTestCase {

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
     * Line 1665, 1666
     * <p>
     * maxIdleTime (optional) - The maximum time that can pass between
     * operations within a single conversation. If more time than this passes,
     * then the container may end the conversation.
     */
    @Test(expected = ConversationEndedException.class)
    public void maxIdle() throws Exception {
        aService.testMaxIdle();
    }

    /**
     * Line 1667, 1668
     * <p>
     * maxAge (optional) - The maximum time that the entire conversation can
     * remain active. If more time than this passes, then the container may end
     * the conversation.
     */
    @Test(expected = ConversationEndedException.class)
    public void maxAge() throws Exception {
        aService.testMaxAge();
    }

    /**
     * Line 1669, 1670
     * <p>
     * singlePrincipal (optional) – If true, only the principal (the user) that
     * started the conversation has authority to continue the conversation.
     * The default value is false.
     */
    @Ignore("TUSCANY-2608")
    @Test(expected = Exception.class)
    public void singlePrincipal() throws Exception {
        aService.testSinglePrincipal();
    }
}
