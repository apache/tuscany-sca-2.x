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
package org.apache.tuscany.sca.policy.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.policy.ExternalAttachment;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * An implementation of ExternalAttachment
 *
 */
public class ExternalAttachmentImpl implements ExternalAttachment {
	
	private String attachTo;
	private List<Intent> intents = new ArrayList<Intent>();
	private List<PolicySet> policySets = new ArrayList<PolicySet>();
	private XPathExpression xpath;
	private boolean isUnresolved;
	

	public String getAttachTo() {
		return attachTo;
	}

	public void setAttachTo(String attachTo) {
		this.attachTo = attachTo;	
	}

	public List<PolicySet> getPolicySets() {
		return this.policySets;
	}

	public List<Intent> getIntents() {
		return this.intents;
	}

	public XPathExpression getAttachToXPathExpression() {
		return this.xpath;
	}

	public void setAttachToXPathExpression(XPathExpression expression) {
		this.xpath = expression;		
	}

	public boolean isUnresolved() {
		return this.isUnresolved;
	}

	public void setUnresolved(boolean unresolved) {
		this.isUnresolved = unresolved;		
	}

}
