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
package org.apache.tuscany.assembly.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.model.Interface;
import org.apache.tuscany.assembly.model.Operation;
import org.apache.tuscany.policy.model.Intent;

public abstract class InterfaceImpl extends BaseImpl implements Interface {
	
	private boolean conversational;
	private boolean remotable;
	private List<Intent> requiredIntents = new ArrayList<Intent>();
	private List<Operation> operations = new ArrayList<Operation>();

	public boolean isConversational() {
		return conversational;
	}

	public boolean isRemotable() {
		return remotable;
	}

	public void setConversational(boolean conversational) {
		this.conversational = conversational;
	}

	public void setRemotable(boolean local) {
		this.remotable = local;
	}

	public List<Intent> getRequiredIntents() {
		return requiredIntents;
	}

	public List<Operation> getOperations() {
		return operations;
	}
}
