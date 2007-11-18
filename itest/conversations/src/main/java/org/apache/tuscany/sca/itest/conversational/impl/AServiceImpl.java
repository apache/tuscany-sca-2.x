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

import java.lang.reflect.Proxy;
import org.apache.tuscany.sca.core.invocation.JDKInvocationHandler;

import org.apache.tuscany.sca.itest.conversational.AService;
import org.apache.tuscany.sca.itest.conversational.BService;
import org.apache.tuscany.sca.itest.conversational.Constants;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Simple conversational Service that uses another Conversational Service
 */
@Service(AService.class)
@Scope("CONVERSATION")
public class AServiceImpl implements AService {

    /**
     * The state 
     */
    private String state = Constants.A_INITIAL_VALUE;
    
    /**
     * The reference to the other service
     */
    private BService b;
    
    /**
     * Constructor
     *
     */
    public AServiceImpl() {
        System.out.println("---> AServiceImpl constructor for " + this);
    }

    /**
     * Inject the reference to the other service
     * @param aB The other service.
     */
    @Reference(name="b")
    public void setB(BService aB) {
        System.out.println("---> Setting reference to B on " + this + " to " + aB);
        this.b = aB;
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
     * Returns the state for the other service that this service is using
     * 
     * @return The state for the other service that this service is using
     */
    public String getStateOnB() {
        return b.getState();
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
     * Sets the state for the other service that this service is using
     * 
     * @param aState The state for the other service that this service is using
     */
    public void setStateOnB(String aState) {
        b.setState(aState);
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
