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

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.OService;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Service;
import org.oasisopen.sca.annotation.Scope;

@Service(OService.class)
@Scope("REQUEST")
public class OServiceImpl implements OService {

	public static int instanceCounter = 0;

	public static int initCalledCounter = 0;

	public static int destroyCalledCounter = 0;

	public int currentInstanceId = 0;
	
	public String currentState = null;
	
	public OServiceImpl() {
		currentInstanceId = ++instanceCounter;
	}

	@Init
    public void initOService() throws Exception {
    	initCalledCounter++;
    	System.out.println("OService" + currentInstanceId + "->initOService");
    }

    @Destroy
    public void destroyOService() {
    	destroyCalledCounter++;
    	System.out.println("OService" + currentInstanceId + "->destroyOService");
    }
    
    public String getName() {
        return "OService" + currentInstanceId;
    }
    
    public String getCurrentState() {
		return currentState;
	}

	public String setCurrentState(String currentState) {
		this.currentState = "OService" + currentInstanceId + "-" + currentState;
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
