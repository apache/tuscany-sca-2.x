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

import org.apache.tuscany.assembly.model.ComponentReference;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.Wire;
import org.apache.tuscany.policy.model.Intent;
import org.apache.tuscany.policy.model.PolicySet;

/**
 * Represents a wire
 *
 *  @version $Rev$ $Date$
 */
public class WireImpl extends BaseImpl implements Wire {
	private ComponentReference source;
	private ComponentService target;
	private List<Intent> requiredIntents = new ArrayList<Intent>();
	private List<PolicySet> policySets = new ArrayList<PolicySet>();

	public ComponentReference getSource() {
		return source;
	}

	public ComponentService getTarget() {
		return target;
	}

	public void setSource(ComponentReference source) {
		this.source = source;
	}

	public void setTarget(ComponentService target) {
		this.target = target;
	}

	public List<Intent> getRequiredIntents() {
		return requiredIntents;
	}

	public List<PolicySet> getPolicySets() {
		return policySets;
	}

}
