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

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.IService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.HService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(HService.class)
public class HServiceImpl implements HService {

	@Reference
	public IService i1;
	
	public String getName() {
        return "HService";
    }

	public String test() {

		String serviceName = i1.getName();
		String conversationIdBefore = i1.getConversationId();
			
		for (int i = 0; i < 10; i++) {
			String newState = "state-" + i;
			i1.setCurrentState(newState);
			String currentState = i1.getCurrentState();
			if (currentState == null || !currentState.equals(serviceName + "-" + newState)) {
				i1.endConversation();
				return "CurrentStateLost - " + currentState;
			}
		}

		boolean isInitReady = i1.isInitReady();

		String conversationIdAfter = i1.getConversationId();

		i1.endConversation();

		if (!isInitReady)
			return "InitNotReady";

		if (!conversationIdBefore.equals(conversationIdAfter))
			return "DifferentConversationId";
		
		return "None";
	}

	public String testCounters(int n) {
		int instanceCounter = i1.getInstanceCounter();
		int initCalledCounter = i1.getInitCalledCounter();
		int destroyCalledCounter = i1.getDestroyCalledCounter();
		
        System.out.println("instanceCounter=" + instanceCounter);
        System.out.println("initCalledCounter=" + initCalledCounter);
        System.out.println("destroyCalledCounter=" + destroyCalledCounter);

		if (instanceCounter != n + 1) 
			return "IncorrectInstanceCounter";
		if (initCalledCounter != n + 1)
			return "IncorrectInitCalledCounter";
		if (destroyCalledCounter != n)
			return "IncorrectDestroyCalledCounter";
		
		return "None";
	}

}
