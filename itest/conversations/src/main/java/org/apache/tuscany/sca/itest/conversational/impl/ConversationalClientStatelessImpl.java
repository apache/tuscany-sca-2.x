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
import org.apache.tuscany.sca.itest.conversational.ConversationalReferenceClient;
import org.apache.tuscany.sca.itest.conversational.ConversationalService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * The client for the conversational itest which presents a stateful
 * callback interface
 *
 * @version $Rev: 537240 $ $Date: 2007-05-11 18:35:03 +0100 (Fri, 11 May 2007) $
 */

@Service(interfaces={ConversationalClient.class})
public class ConversationalClientStatelessImpl implements ConversationalClient, ConversationalCallback {
    
    @Context
    protected ComponentContext componentContext;
    
    @Reference 
    protected ConversationalService conversationalService;
    
    // @Reference - not yet
    protected ConversationalReferenceClient conversationalReferenceClient;
    
    private int clientCount = 0;
    private int callbackCount = 0;
    
    // a static member variable that records the number of times this service is called
    public static StringBuffer calls = new StringBuffer();    
	
    // From ConversationalClient
    public int runConversationFromInjectedReference(){
        calls.append("runConversationFromInjectedReference,");
        conversationalService.initializeCount(1);
        conversationalService.incrementCount();
        clientCount = conversationalService.retrieveCount();
        conversationalService.endConversation();
        
        return clientCount;
    }
    public int runConversationFromServiceReference(){
        calls.append("runConversationFromServiceReference,");
        ServiceReference<ConversationalService> serviceReference = componentContext.getServiceReference(ConversationalService.class, 
                                                                                                        "conversationalService");       
        ConversationalService callableReference = serviceReference.getService();
        
        callableReference.initializeCount(1);
        callableReference.incrementCount();
        clientCount = callableReference.retrieveCount();
        callableReference.endConversation();
        
        serviceReference.getConversation().end();
        
        return clientCount;
    }   
    public int runConversationWithUserDefinedConversationId(){
        calls.append("runConversationWithUserDefinedConversationId,");
        ServiceReference<ConversationalService> serviceReference = componentContext.getServiceReference(ConversationalService.class, 
                                                                                                        "conversationalService");       
        ConversationalService callableReference = serviceReference.getService();
        
        callableReference.initializeCount(1);
        callableReference.incrementCount();
        clientCount = callableReference.retrieveCount();
        callableReference.endConversation();
        
        serviceReference.getConversation().end();
        
        return clientCount;
    }    
    public int runConversationCheckingScope(){
        calls.append("runConversationCheckingScope,");
        // run a conversation
        return runConversationFromInjectedReference();
        
        // test will then use a static method to find out how many times 
        // init/destroy were called
    }
    public int runConversationWithCallback(){
        calls.append("runConversationWithCallback,");
        conversationalService.initializeCountCallback(1);
        conversationalService.incrementCountCallback();
        clientCount = conversationalService.retrieveCountCallback();
        conversationalService.endConversationCallback();
        
        return clientCount;
    } 
    public int runConversationHavingPassedReference(){
        calls.append("runConversationHavingPassedReference,");
        return clientCount;
    }	
	public int runConversationError(){
        calls.append("runConversationError,");
        return clientCount;
    }
    public int runConversationAgeTimeout(){
        calls.append("runConversationAgeTimeout,");
        return clientCount;
    }
    public int runConversationIdleTimeout(){
        calls.append("runConversationIdleTimeout,");
        return clientCount;
    }
    public int runConversationPrincipleError(){
        calls.append("runConversationPrincipleError,");
        return clientCount;
    }
    
    
    // From ConversationalCallback
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
        callbackCount = 0;
    }
    
    public void incrementCount(){
        calls.append("incrementCount,");
        callbackCount++;
    }
    
    public int retrieveCount(){
        calls.append("retrieveCount,");
        return  callbackCount;
    }
    
    public void endConversation(){
        calls.append("endConversation,");
        
    }

}
