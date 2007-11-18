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


import org.apache.tuscany.sca.itest.conversational.BService;
import org.apache.tuscany.sca.itest.conversational.Constants;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Simple conversational Service
 */
@Service(BService.class)
@Scope("CONVERSATION")
public class BServiceImpl implements BService {

    /**
     * The state for this service
     */
    private String state = Constants.B_INITIAL_VALUE;

    /**
     * Constructor
     */
    public BServiceImpl() {
        System.out.println("---> BServiceImpl constructor for " + this);
    }
    
    /**
     * Returns the state for this service.
     * 
     * @return The state for this service
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state for this service.
     * 
     * @param aState The state for this service
     */
    public void setState(String aState) {
        this.state = aState;
    }

    /**
     * Sets the conversation ID for this service
     * @param id The Conversation ID
     */
    @ConversationID
    public void setConversationID(String id) {
        System.out.println("Conversation ID for " + this + " is set to " + id);
    }
}
