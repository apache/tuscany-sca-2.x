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
package org.apache.tuscany.sca.core.conversation;

import org.apache.tuscany.sca.core.scope.ScopedImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;


/**
 * 
 * @version $Rev$ $Date$
 */
public class ExtendedConversationImpl implements ExtendedConversation, Runnable {
	
    private final ConversationManagerImpl manager;
    private volatile Object conversationID;
    private ConversationState state;

    /**
     * syncs access to the state
     */
    private final Object stateSync = new Object();
    
    /**
     * the maximum time a conversation can exist
     */
    private long expirationTime = 0;
    
    /**
     * the maximum time this conversation can be idle
     */
    private long maxIdleTime = 0;
    
    /**
     * the maximum age of this conversation
     */
    private long maxAge = 0;
    
    /**
     * the time that this object was created 
     */
    private long creationTime;    

    /**
     * the time that this object was last referenced 
     */
    private long lastReferencedTime;
    
    /**
     * boolean to ensure expiry only occurs once
     */
    private boolean expired = false;
    
    /**
     * boolean to indicate if the conversation attributes have 
     * been set. In the case where a remote binding is used 
     * within a composite the JDKInvocationHandler can create the 
     * conversation but the conversationAttributes are not available
     * until the conversation is retrieved by the RuntimeWireInvoker
     */
    private boolean conversationAttributesInitialized = false;    
    
    /**
     * Constructor
     * @param manager the conversation manager
     * @param conversationID the conversation id associated with this conversation
     * @param state the initial state of this conversation
     * @param aMaxAge the maximum age of the conversation
     * @param aMaxIdleTime the maximum idle time
     */
    public ExtendedConversationImpl(ConversationManagerImpl manager, 
    			Object conversationID, ConversationState state) {
        super();
        
        this.creationTime = System.currentTimeMillis();
        this.lastReferencedTime = creationTime;
        this.manager = manager;
        this.conversationID = conversationID;
        this.state = state;
    }

    /**
     * will check whether this conversation has expired and update state if it has 
     * @return true if it has expired
     */
    public boolean isExpired() {
        long currentTime;
        synchronized (stateSync) {

            // if the attributes haven't been initialized then
            // this conversation object can't expire
            if (conversationAttributesInitialized == false) {
                return false;
            }

            // check state first
            if (state == ConversationState.EXPIRED) {
                return true;
            }

            // check whether the time is finished
            currentTime = System.currentTimeMillis();
            if (((this.lastReferencedTime + this.maxIdleTime) <= currentTime)
                    || (this.expirationTime <= currentTime)) {
                setState(ConversationState.EXPIRED);
                return true;
            }
        }
        scheduleNextExpiryTime(currentTime);
        return false;
    }

    /**
     * schedule next expiry time
     */
    public void scheduleNextExpiryTime(long currentTime) {
    	if ((lastReferencedTime + maxIdleTime) < expirationTime){ 
    		manager.scheduleConversation(this, (lastReferencedTime + maxIdleTime) - currentTime);
        } else {
    		manager.scheduleConversation(this, expirationTime - currentTime);
    	}
    }
    /**
     * updates the last time this conversation was referenced
     */
    public void updateLastReferencedTime() {
        this.lastReferencedTime = System.currentTimeMillis();
        if (conversationAttributesInitialized == true){
            scheduleNextExpiryTime(lastReferencedTime);
        }
    }
    
    public ConversationState getState() {
    	synchronized (stateSync){
    		return state;
    	}
    }

    public void end() {
        manager.endConversation(conversationID);
    }

    public Object getConversationID() {
        return conversationID;
    }

    /**
     * @param state the state to set
     */
    public void setState(ConversationState state) {
    	synchronized (stateSync){
    		this.state = state;
    	}
    }

    /**
     * @param conversationID the conversationID to set
     */
    public void setConversationID(Object conversationID) {
    	synchronized (stateSync){
    		if (state != ConversationState.ENDED) {
    			throw new IllegalStateException("The state of conversation " + conversationID + " " + state);
    		}
    	}
        this.conversationID = conversationID;
    }
    
    /**
     * @param maxAge the maximum age of this conversation
     */
    public void initializeConversationAttributes(RuntimeComponent targetComponent){
        if (targetComponent != null){ 
            this.maxAge = getMaxIdleTime(targetComponent.getImplementationProvider());
            this.maxIdleTime = getMaxAge(targetComponent.getImplementationProvider());
            this.expirationTime = creationTime + maxAge;
            this.conversationAttributesInitialized = true;
        }        
    }
    
    /**
     * @return true if the conversational attributes have been initialized
     */
    public boolean conversationalAttributesInitialized(){
        return this.conversationAttributesInitialized;
    }
    
    /**
     * return the max idle time
     * @param impProvider the implementation Provider to extract any ConversationAttribute details
     */
    private long getMaxIdleTime(ImplementationProvider impProvider) {
        // Check to see if the maxIdleTime has been specified using @ConversationAttributes.  
        // Implementation annotated attributes are honoured first.
        if ((impProvider != null) &&
            (impProvider instanceof ScopedImplementationProvider)) {
            ScopedImplementationProvider aScopedImpl =
                (ScopedImplementationProvider) impProvider;
            
            long maxIdleTime = aScopedImpl.getMaxIdleTime();
            if (maxIdleTime > 0) {
                return maxIdleTime;
            }
        }
        return manager.getMaxIdleTime();
    }
    
    /**
     * returns the max age
     * @param impProvider the implementation Provider to extract any ConversationAttribute details
     */
    private long getMaxAge(ImplementationProvider impProvider){

        // Check to see if the maxAge has been specified using @ConversationAttributes.  
        // Implementation annotated attributes are honoured first.
        if ((impProvider != null) &&
            (impProvider instanceof ScopedImplementationProvider)) {
            ScopedImplementationProvider aScopedImpl =
                (ScopedImplementationProvider) impProvider;

            long maxAge = aScopedImpl.getMaxAge();
            if (maxAge > 0) {
                return maxAge;
            }
        }
        return manager.getMaxAge();
    }     

    /**
     * called when expiring
     */
    public void run() {
        synchronized (stateSync){
        	if (!expired){
        		if (isExpired()) {
        		    expired = true;
        			try {
        				manager.expireConversation(getConversationID());
        			} catch (IllegalStateException ise) {
        				// ignore this.. this can occur if another thread has subsequently ended
        				// the conversation
        			}
        		}
        	}
        }
        
    }
    
}
