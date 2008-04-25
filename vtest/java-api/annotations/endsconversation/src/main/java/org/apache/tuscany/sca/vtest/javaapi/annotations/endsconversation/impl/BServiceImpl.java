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

package org.apache.tuscany.sca.vtest.javaapi.annotations.endsconversation.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.endsconversation.BService;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(BService.class)
@Scope("CONVERSATION")
@ConversationAttributes(maxIdleTime="1 seconds")
public class BServiceImpl implements BService {

    String someState;
    
    protected String conversationId;

    public void setState(String someState) {
        this.someState = someState;
    }

    @ConversationID
    public void setConversationID (String id){
       this.conversationId = id;
       System.out.println("BService conversation ID =>" + conversationId);
    }
    
    public String getState() {
        return someState;
    }
    
    public String getConversationId() {
        return conversationId;
    }

    public void endConversation() {
        System.out.println("Conversation ended");
    }

}
