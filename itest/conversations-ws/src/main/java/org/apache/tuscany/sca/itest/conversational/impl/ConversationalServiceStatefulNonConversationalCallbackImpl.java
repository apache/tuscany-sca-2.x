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

import org.apache.tuscany.sca.itest.conversational.ConversationalServiceNonConversationalCallback;
import org.apache.tuscany.sca.itest.conversational.NonConversationalCallback;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;


/**
 * The service used when testing stateful conversations
 *
 * @version $Rev: 537240 $ $Date: 2007-05-11 18:35:03 +0100 (Fri, 11 May 2007) $
 */
@Service(ConversationalServiceNonConversationalCallback.class)
@Scope("CONVERSATION")
@ConversationAttributes(maxAge="10 minutes",
                        maxIdleTime="5 minutes",
                        singlePrincipal=false)
public class ConversationalServiceStatefulNonConversationalCallbackImpl implements ConversationalServiceNonConversationalCallback {

    @ConversationID
    protected String conversationId;
    
    @Callback
    protected NonConversationalCallback nonConversationalCallback; 
    
    // local count - accumulates during the conversation
    private int count = 0;
          
    // a static member variable that records the number of times this service is called
    public static StringBuffer calls = new StringBuffer();
   
    @Init
    public void init(){
        calls.append("init,");
    }
    
    @Destroy
    public void destroy(){
        calls.append("destroy,");
    }
    
    public void initializeCount(int count){
        calls.append("initializeCount,");       
        this.count = count;
    }
    
    public void incrementCount(){
        calls.append("incrementCount,");        
        count++;
    }
    
    public int retrieveCount(){
        calls.append("retrieveCount,"); 
        return count;
    }
    
    public void businessException() throws Exception {
        throw new Exception("Business Exception");
    }     
    
    public void initializeCountCallback(int count){
        calls.append("initializeCountCallback,"); 
        this.count = count;
        nonConversationalCallback.initializeCount(count);
    }
    
    public void incrementCountCallback(){
        calls.append("incrementCountCallback,"); 
        count++;
        nonConversationalCallback.incrementCount();
    }
    
    public int retrieveCountCallback(){
        calls.append("retrieveCountCallback,"); 
        return nonConversationalCallback.retrieveCount();
    }
    
    public void businessExceptionCallback() throws Exception {
        calls.append("businessExceptionCallback,");        
        nonConversationalCallback.businessException();
    }    
    
    public String endConversation(){
        calls.append("endConversation,"); 
        count = 0;
        return conversationId;
    }
    
    public String endConversationCallback(){
        calls.append("endConversationCallback,"); 
        return nonConversationalCallback.endConversation();
    }
}
