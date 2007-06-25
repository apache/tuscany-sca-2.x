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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents a policy set.
 * 
 * @version $Rev$ $Date$
 */
public class PolicySetImpl implements PolicySet {

    private QName name;
    private List<Operation> operations = new ArrayList<Operation>();
    private List<QName> appliesTo;
    private List<Intent> providedIntents;
    private List<PolicySet> referencedPolicySets;
    private List<Object> policies;
    private boolean unresolved;
    
    protected PolicySetImpl() {
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public List<QName> getAppliesTo() {
        return appliesTo;
    }

    public List<Intent> getProvidedIntents() {
        return providedIntents;
    }

    public List<PolicySet> getReferencedPolicySets() {
        return referencedPolicySets;
    }

    public List<Object> getPolicies() {
        return policies;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }
}
