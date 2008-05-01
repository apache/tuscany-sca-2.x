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

package org.apache.tuscany.sca.vtest.javaapi.conversation.id.impl;

import org.apache.tuscany.sca.vtest.javaapi.conversation.id.CService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.id.CustomConversationId;
import org.junit.Assert;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(CService.class)
@Scope("CONVERSATION")
public class CServiceImpl implements CService {

    String someState;

    @ConversationID
    protected Object conversationID;


    public void setState(String someState) {
        this.someState = someState;
    }

    public String getState() {
        return someState;
    }

    public void testAnnotation() {
        Assert.assertNotNull(conversationID);
        Assert.assertTrue(conversationID instanceof CustomConversationId);
        
        Assert.assertSame(1, ((CustomConversationId)conversationID).getNumber());
        Assert.assertSame("One", ((CustomConversationId)conversationID).getName());
        System.out.println(conversationID);
    }

}
