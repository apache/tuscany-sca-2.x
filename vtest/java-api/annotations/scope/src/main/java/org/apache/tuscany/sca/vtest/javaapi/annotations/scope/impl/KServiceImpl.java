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

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.KService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.LService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Scope;

@Service(KService.class)
@Scope("STATELESS")
public class KServiceImpl implements KService {

	public static int instanceCounter = 0;

	public String previousL1Name = null;

	public int currentInstanceId = 0;

	@Reference
	public LService l1;
	
	public KServiceImpl() {
		currentInstanceId = ++instanceCounter;
	}

	
    public String getName() {
        return "KService"+currentInstanceId;
    }
    
    public String callL1() {

    	for (int i = 1; i < 11; i++) {
    		System.out.println(getName() + " calls LService " + i + (i == 1 ? "st time:" : (i == 2 ? "nd time:" : (i == 3 ? "rd time:" : "th time"))));

    		String serviceName = l1.getName();
    		for (int j = 0; j < 10; j++) {
    			String newState = "state-" + j;
    			l1.setCurrentState(newState);
    			String currentState = l1.getCurrentState();
    			if (!currentState.equals(serviceName + "-" + newState)) {
    				return "CurrentStateLost - " + currentState;
    			}
        	}

    		System.out.print("    Name=" + serviceName);
    		System.out.print(", #Instance=" + l1.getInstanceCounter());
    		System.out.print(", #InitCalled=" + l1.getInitCalledCounter());
    		System.out.println(", #DestroyCalled=" + l1.getDestroyCalledCounter());

        	if (previousL1Name == null) {
        		previousL1Name = serviceName;
        	} else {
        		if (!previousL1Name.equals(serviceName))
        			return "DifferentLServiceInstance";
        	}
        }
		return "None";
    }

}
