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

package org.apache.tuscany.sca.vtest.javaapi.annotations.scope.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.IService;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Scope;

@Service(IService.class)
@Scope("CONVERSATION")
public class IServiceImpl implements IService {

	public static int instanceCounter = 0;

	public static int initCalledCounter = 0;

	public static int destroyCalledCounter = 0;

    @ConversationID
    protected String conversationId;
    
	public int currentInstanceId = 0;
    
	public AService a1;
	
	public String p1;
	
	public String currentState = null;
	
	public boolean isInitReady = false;
	
	public IServiceImpl() {
		currentInstanceId = ++instanceCounter;
		isInitReady = false;
	}

	@Reference
	public void setA1(AService a1) {
		this.a1 = a1;
	}

	@Property
	public void setP1(String p1) {
		this.p1 = p1;
	}

	@Init
    public void initIService() throws Exception {
    	initCalledCounter++;
    	if (p1.equals("p1") && a1.getName().equals("AService"))
    		isInitReady = true;
    	System.out.println("IService" + currentInstanceId + "->initIService");
    }

    @Destroy
    public void destroyIService() {
    	destroyCalledCounter++;
    	System.out.println("IService" + currentInstanceId + "->destroyIService");
    }
    
    public void endConversation() {
    	System.out.println("IService" + currentInstanceId + "->endConversation");
    }
    
    public String getName() {
        return "IService" + currentInstanceId;
    }
    
    public AService getA1() {
		return a1;
	}

    public String getP1() {
		return p1;
	}

    public String getCurrentState() {
		return currentState;
	}

	public String getConversationId() {
		return conversationId;
	}

	public String setCurrentState(String currentState) {
		this.currentState = "IService" + currentInstanceId + "-" + currentState;
		return this.currentState;
	}

	public boolean isInitReady() {
		return isInitReady;
	}

	public int getDestroyCalledCounter() {
		return destroyCalledCounter;
	}

	public int getInitCalledCounter() {
		return initCalledCounter;
	}

	public int getInstanceCounter() {
		return instanceCounter;
	}

}
