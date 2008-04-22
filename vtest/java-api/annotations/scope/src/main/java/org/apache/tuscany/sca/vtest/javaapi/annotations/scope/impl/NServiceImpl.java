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

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.NService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.OService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Scope;

@Service(NService.class)
@Scope("STATELESS")
public class NServiceImpl implements NService {

	public static int instanceCounter = 0;

	public String previousL1Name = null;

	public int currentInstanceId = 0;

	@Reference
	public OService o1;
	
	public NServiceImpl() {
		currentInstanceId = ++instanceCounter;
	}

	
    public String getName() {
        return "NService"+currentInstanceId;
    }
    
    public String callO1(int nthTime) {

    	for (int i = 1; i < 11; i++) {
    		System.out.println(getName() + " calls OService " + i + (i == 1 ? "st time:" : (i == 2 ? "nd time:" : (i == 3 ? "rd time:" : "th time"))));

    		String serviceName = o1.getName();
    		for (int j = 0; j < 10; j++) {
    			String newState = "state-" + j;
    			o1.setCurrentState(newState);
    			String currentState = o1.getCurrentState();
    			if (!currentState.equals(serviceName + "-" + newState)) {
    				return "CurrentStateLost - " + currentState;
    			}
        	}

    		System.out.print("    Name=" + serviceName);
    		System.out.print(", #Instance=" + o1.getInstanceCounter());
    		System.out.print(", #InitCalled=" + o1.getInitCalledCounter());
    		System.out.println(", #DestroyCalled=" + o1.getDestroyCalledCounter());

        	if (previousL1Name == null) {
        		previousL1Name = serviceName;
        	} else {
        		if (!previousL1Name.equals(serviceName))
        			return "DifferentOServiceInstance";
        	}
        	
        }
    	
    	if (o1.getInstanceCounter() < nthTime) {
    		return "SharedSameInstance";
    	}

    	return "None";
    }
}
