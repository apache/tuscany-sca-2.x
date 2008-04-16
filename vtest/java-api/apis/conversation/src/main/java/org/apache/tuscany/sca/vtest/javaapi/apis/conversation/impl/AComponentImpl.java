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

package org.apache.tuscany.sca.vtest.javaapi.apis.conversation.impl;

import org.apache.tuscany.sca.vtest.javaapi.apis.conversation.AComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.conversation.BComponent;
import org.junit.Assert;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Conversation;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AComponent.class)
@Scope("CONVERSATION")
public class AComponentImpl implements AComponent {

    protected ComponentContext componentContext;
    
    @Reference
    protected CallableReference<BComponent> bReference;

    @ConversationID
    protected String cid;

    public String getName() {
        return "ComponentA";
    }

    @Context
    public void setComponentContext(ComponentContext context) {
        this.componentContext = context;
    }

    public void testConversation() {
        ServiceReference<BComponent> bSR = componentContext.getServiceReference(BComponent.class, "bReference");
        bSR.setConversationID("AConversationID");
        bSR.getService().testCustomConversationID();

        Conversation bc = bSR.getConversation();
        Assert.assertEquals("AConversationID", bc.getConversationID());
        bc.end();

        Assert.assertEquals("ComponentB", bReference.getService().getName());
        bReference.getService().testGeneratedConversationID(bReference.getConversation().getConversationID());
        bReference.getConversation().end();

        Assert.assertEquals(1, BComponentImpl.customInitCount);
        Assert.assertEquals(1, BComponentImpl.customDestroyCount);
    }

}
