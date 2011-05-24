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

package org.apache.tuscany.sca.binding.comet.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.atmosphere.cpr.Broadcaster;

import com.google.gson.Gson;

public class CometComponentContext {

	public static Map<String, Broadcaster> broadcasters = new ConcurrentHashMap<String, Broadcaster>();
	public static Gson gson = new Gson();
	private Map<String, RuntimeEndpoint> endpoints;
	private Map<String, Operation> operations;

	public CometComponentContext() {
		endpoints = new ConcurrentHashMap<String, RuntimeEndpoint>();
		operations = new ConcurrentHashMap<String, Operation>();
	}

	public void addEndpoint(String key, RuntimeEndpoint endpoint) {
		endpoints.put(key, endpoint);
	}

	public void addOperation(String key, Operation operation) {
		operations.put(key, operation);
	}

	public RuntimeEndpoint getEndpoint(String key) {
		return endpoints.get(key);
	}

	public Operation getOperation(String key) {
		return operations.get(key);
	}

}
