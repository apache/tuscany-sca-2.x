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
package org.apache.tuscany.sca.itest.conversational.impl;

import org.apache.tuscany.sca.itest.conversational.ConversationalCallback;
import org.apache.tuscany.sca.itest.conversational.ConversationalClient;
import org.apache.tuscany.sca.itest.conversational.ConversationalService;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;


/**
 * The service used when testing stateful conversations
 *
 * @version $Rev: 537240 $ $Date: 2007-05-11 18:35:03 +0100 (Fri, 11 May 2007) $
 */
@Service(ConversationalService.class)
@Scope("CONVERSATION")
@ConversationAttributes(maxAge="10 minutes",
                        maxIdleTime="5 minutes",
                        singlePrincipal=false)
public class ConversationalServiceStatefulImpl implements ConversationalService {

    @ConversationID
    protected String conversationId;
    
   // @Callback - not working yet
    protected ConversationalCallback conversationalCallback; 
    
    // local count - accumulates during the conversation
    private int count = 0;
    
    // a member variable that records whether init processing happens
    private static int initValue = 0;
    
    // lets us check the init value after class instances have gone
    public static int getInitValue(){
        return initValue;
    }

    @Init
    public void init(){
        initValue = initValue - 5;
    }
    
    @Destroy
    public void destroy(){
        initValue = initValue + 10;
    }
    
    public void initializeCount(int count){
        this.count = count;
    }
    
    public void incrementCount(){
        count++;
    }
    
    public int retrieveCount(){
        return count;
    }
    
    public void initializeCountCallback(int count){
        initializeCount(count);
        conversationalCallback.initializeCount(count);
    }
    
    public void incrementCountCallback(){
        incrementCount();
        conversationalCallback.incrementCount();
    }
    
    public int retrieveCountCallback(){
        return conversationalCallback.retrieveCount();
    }
    
    public void endConversation(){
        count = 0;
    }
    
    public void endConversationCallback(){
        conversationalCallback.endConversation();
    }
}
