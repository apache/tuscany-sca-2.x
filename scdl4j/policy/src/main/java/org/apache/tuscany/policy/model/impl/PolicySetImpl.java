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
package org.apache.tuscany.policy.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.policy.model.PolicySet;
import org.apache.tuscany.sca.idl.Operation;

/**
 * Represents a policy set.
 *
 *  @version $Rev$ $Date$
 */
public class PolicySetImpl implements PolicySet {
	
	private QName name;
	private List<Operation> operations = new ArrayList<Operation>();

	public QName getName() {
		return name;
	}

	public void setName(QName name) {
		this.name = name;
	}

	public List<Operation> getOperations() {
		return operations;
	}

}
