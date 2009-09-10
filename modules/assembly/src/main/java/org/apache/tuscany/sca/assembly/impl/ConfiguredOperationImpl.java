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

package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents an Operation.
 *
 * @version $Rev$ $Date$
 */
public class ConfiguredOperationImpl extends BaseImpl implements ConfiguredOperation {
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();
    
    private String name;
    private String contractName;
    private ExtensionType type;

    /**
     * Constructs a new Operation.
     */
    protected ConfiguredOperationImpl() {
    }
    
    public List<PolicySet> getPolicySets() {
        return policySets;
    }
    
    public void setPolicySets(List<PolicySet> policySets) {
        this.policySets = policySets; 
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }
    
    public void setRequiredIntents(List<Intent> intents) {
        this.requiredIntents = intents;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }

    public ExtensionType getExtensionType() {
        return type;
    }

    public void setExtensionType(ExtensionType type) {
        this.type = type;
    }
    
    
}
