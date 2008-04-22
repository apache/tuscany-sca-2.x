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

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.LService;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Scope;

@Service(LService.class)
@Scope("REQUEST")
public class LServiceImpl implements LService {

	public static int instanceCounter = 0;

	public static int initCalledCounter = 0;

	public static int destroyCalledCounter = 0;

	public int currentInstanceId = 0;
	
	public String currentState = null;
	
	public LServiceImpl() {
		currentInstanceId = ++instanceCounter;
	}

	@Init
    public void initLService() throws Exception {
    	initCalledCounter++;
    	System.out.println("LService" + currentInstanceId + "->initLService");
    }

    @Destroy
    public void destroyLService() {
    	destroyCalledCounter++;
    	System.out.println("LService" + currentInstanceId + "->destroyLService");
    }
    
    public String getName() {
        return "LService" + currentInstanceId;
    }
    
    public String getCurrentState() {
		return currentState;
	}

	public String setCurrentState(String currentState) {
		this.currentState = "LService" + currentInstanceId + "-" + currentState;
		return this.currentState;
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
