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

package org.apache.tuscany.sca.itest.conversational;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConversationLifetimeTestCase {

    private SCADomain domain;

    @Before
    public void setUp() throws Exception {
        domain = SCADomain.newInstance("conversationLifetime.composite");

    }

    @After
    public void tearDown() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    
    /**
     * Verify that user provided conversation ID is returned from Conversation.getConversationID()
     */
    @Test
    public void testSetUserSuppliedConversationID() {
        CService service = domain.getService(CService.class, "ConversationalCComponent");
        service.setUserConversationIDWithD("A user set conversation ID");
        service.getStateOnD();
        Assert.assertEquals(service.getUserConversationIDWithD(), service.getConversationIDWithD());
        Assert.assertEquals(service.getConversationIDWithD(),"A user set conversation ID");
   }
    

    /**
     * Verify that a new conversation is started if conversational service method is called subsequent
     * to a call to Conversation.end()
     */
    @Test
    public void explicitEnd() {
        CService service = domain.getService(CService.class, "ConversationalCComponent");
        service.getStateOnD();
        Object firstID = service.getConversationIDWithD();
        service.endConversationWithD();
        service.getStateOnD();
        Assert.assertNotSame(firstID, service.getConversationIDWithD());
    }
    
}
