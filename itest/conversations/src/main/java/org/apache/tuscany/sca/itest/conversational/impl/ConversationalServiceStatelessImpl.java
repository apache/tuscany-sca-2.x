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

import java.util.HashMap;

import org.apache.tuscany.sca.itest.conversational.ConversationalCallback;
import org.apache.tuscany.sca.itest.conversational.ConversationalClient;
import org.apache.tuscany.sca.itest.conversational.ConversationalService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Conversation;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Context;
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
public class ConversationalServiceStatelessImpl implements ConversationalService {
    
    @ConversationID
    protected String conversationId;
    
    @Callback
    protected ConversationalCallback conversationalCallback; 
    
    // static area in which to hold conversational data
    private static HashMap<String, Integer> conversationalState = new HashMap<String, Integer>();
   
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
        Integer conversationalCount = new Integer(count); 
        conversationalState.put(conversationId, conversationalCount);
    }
    
    public void incrementCount(){
        calls.append("incrementCount,");
        Integer conversationalCount = conversationalState.get(conversationId);
        conversationalCount++;
        conversationalState.put(conversationId, conversationalCount);
    }
    
    public int retrieveCount(){
        calls.append("retrieveCount,");
        Integer count = conversationalState.get(conversationId);
        if (count != null){
            return count.intValue();
        } else {
            return -999;
        }
    }
    
    public void businessException() throws Exception {
        throw new Exception("Business Exception");
    }    
    
    public void initializeCountCallback(int count){
        calls.append("initializeCountCallback,");
        initializeCount(count);
        conversationalCallback.initializeCount(count);
    }
    
    public void incrementCountCallback(){
        calls.append("incrementCountCallback,");
        incrementCount();
        conversationalCallback.incrementCount();
    }
    
    public int retrieveCountCallback(){
        calls.append("retrieveCountCallback,");
        return conversationalCallback.retrieveCount();
    }
    
    public void businessExceptionCallback() throws Exception {
        calls.append("businessExceptionCallback,");        
        conversationalCallback.businessException();
    }
    
    public String endConversation(){
        calls.append("endConversation,");
        conversationalState.remove(conversationId);
        return conversationId;
    }
    
    public String endConversationCallback(){
        calls.append("endConversationCallback,");       
        return conversationalCallback.endConversation();
    }   
}
