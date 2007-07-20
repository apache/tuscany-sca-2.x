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
    
    private int clientCount;
    private int callbackCount;
	
    // From ConversationalClient
	public int runConversation(){
	    conversationalService.initializeCount(1);
	    conversationalService.incrementCount();
	    int count = conversationalService.retrieveCount();
	    conversationalService.endConversation();
	    
	    return count;
	}
	public int runConversationCallback(){
        return clientCount;
    } 
	public int runConversationFromReference(){
	    ServiceReference<ConversationalService> serviceReference = componentContext.getServiceReference(ConversationalService.class, 
	                                                                                                    "conversationalService");
	    serviceReference.setConversationID("MyConversation");
	    
	    ConversationalService callableReference = serviceReference.getService();
	        
	    callableReference.initializeCount(1);
	    callableReference.incrementCount();
	    clientCount = callableReference.retrieveCount();
	    callableReference.endConversation();
	    
	    serviceReference.getConversation().end();
	    
        return clientCount;
    }
	public int runConversationPassingReference(){
        return clientCount;
    }
	public int runConversationError(){
        return clientCount;
    }
    public int runConversationAgeTimeout(){
        return clientCount;
    }
    public int runConversationIdleTimeout(){
        return clientCount;
    }
    public int runConversationPrincipleError(){
        return clientCount;
    }
    
    
    // From ConversationalCallback
    public void init(){

    }
    
    public void destroy(){
        
    }
    
    public void initializeCount(int count){
        callbackCount = 0;
    }
    
    public void incrementCount(){
        callbackCount++;
    }
    
    public int retrieveCount(){
        return  callbackCount;
    }
    
    public void endConversation(){
        
    }

}
